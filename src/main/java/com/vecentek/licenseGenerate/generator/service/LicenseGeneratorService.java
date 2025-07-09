package com.vecentek.licenseGenerate.generator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vecentek.licenseGenerate.generator.crypto.LicenseEncryptionUtil;
import com.vecentek.licenseGenerate.generator.entity.LicenseFeatures;
import com.vecentek.licenseGenerate.generator.entity.LicenseGenerationRequest;
import com.vecentek.licenseGenerate.generator.entity.LicenseGenerationResult;
import com.vecentek.licenseGenerate.generator.entity.LicenseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * License生成器服务
 */
@Service
@Slf4j
public class LicenseGeneratorService {
    
    @Autowired
    private DigitalSignatureService signatureService;
    
    /**
     * 生成License文件
     */
    public LicenseGenerationResult generateLicense(LicenseGenerationRequest request) {
        try {
            // 1. 验证请求参数
            validateRequest(request);
            
            // 2. 构建License信息
            LicenseInfo licenseInfo = buildLicenseInfo(request);
            
            // 3. 生成License内容
            String licenseContent = buildLicenseContent(licenseInfo);
            
            // 4. 数字签名
            String signature = signatureService.sign(licenseContent);
            
            // 5. 构建完整的License文件
            Map<String, Object> licenseFile = new LinkedHashMap<>();
            licenseFile.put("licenseInfo", licenseInfo);
            licenseFile.put("signature", signature);
            
            // 6. 转换为JSON格式
            String jsonContent = JSON.toJSONString(licenseFile, SerializerFeature.PrettyFormat);
            
            // 7. 加密License内容
            String encryptedContent = LicenseEncryptionUtil.encryptWithHardwareFingerprint(
                    jsonContent, request.getHardwareFingerprint());
            
            // 8. 保存生成记录
            saveLicenseGenerationRecord(request, licenseInfo);
            
            log.info("License生成成功（加密），客户: {}, 类型: {}", 
                     request.getLicensee(), request.getLicenseType());
            
            return LicenseGenerationResult.success(encryptedContent, licenseInfo);
            
        } catch (Exception e) {
            log.error("License生成失败", e);
            return LicenseGenerationResult.fail("License生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用私钥字符串生成License
     */
    public LicenseGenerationResult generateLicense(LicenseGenerationRequest request, String privateKey) {
        try {
            // 1. 验证请求参数
            validateRequest(request);
            
            // 2. 构建License信息
            LicenseInfo licenseInfo = buildLicenseInfo(request);
            
            // 3. 生成License内容
            String licenseContent = buildLicenseContent(licenseInfo);
            
            // 4. 使用指定私钥进行数字签名
            String signature = signatureService.sign(licenseContent, privateKey);
            
            // 5. 构建完整的License文件
            Map<String, Object> licenseFile = new LinkedHashMap<>();
            licenseFile.put("licenseInfo", licenseInfo);
            licenseFile.put("signature", signature);
            
            // 6. 转换为JSON格式
            String jsonContent = JSON.toJSONString(licenseFile, SerializerFeature.PrettyFormat);
            
            // 7. 加密License内容
            String encryptedContent = LicenseEncryptionUtil.encryptWithHardwareFingerprint(
                    jsonContent, request.getHardwareFingerprint());
            
            log.info("License生成成功（加密），客户: {}, 类型: {}", 
                     request.getLicensee(), request.getLicenseType());
            
            return LicenseGenerationResult.success(encryptedContent, licenseInfo);
            
        } catch (Exception e) {
            log.error("License生成失败", e);
            return LicenseGenerationResult.fail("License生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建License信息
     */
    private LicenseInfo buildLicenseInfo(LicenseGenerationRequest request) {
        LicenseFeatures features = LicenseFeatures.builder()
                .maxUserCount(request.getMaxUserCount())
                .enabledModules(request.getEnabledModules())
                .build();
        
        return LicenseInfo.builder()
                .version("1.0")
                .issueDate(LocalDateTime.now())
                .expiryDate(request.getExpiryDate())
                .licenseType(request.getLicenseType())
                .licensee(request.getLicensee())
                .hardwareFingerprint(request.getHardwareFingerprint())
                .features(features)
                .build();
    }
    
    /**
     * 构建License内容（用于签名的原始内容）
     */
    private String buildLicenseContent(LicenseInfo licenseInfo) {
        // 创建用于签名的标准化内容
        Map<String, Object> contentMap = new LinkedHashMap<>();
        contentMap.put("version", licenseInfo.getVersion());
        contentMap.put("issueDate", licenseInfo.getIssueDate().toString());
        contentMap.put("expiryDate", licenseInfo.getExpiryDate().toString());
        contentMap.put("licenseType", licenseInfo.getLicenseType());
        contentMap.put("licensee", licenseInfo.getLicensee());
        contentMap.put("hardwareFingerprint", licenseInfo.getHardwareFingerprint());
        contentMap.put("features", licenseInfo.getFeatures());
        
        return JSON.toJSONString(contentMap);
    }
    
    /**
     * 验证生成请求
     */
    private void validateRequest(LicenseGenerationRequest request) {
        if (!StringUtils.hasText(request.getLicensee())) {
            throw new IllegalArgumentException("被许可人不能为空");
        }
        if (!StringUtils.hasText(request.getHardwareFingerprint())) {
            throw new IllegalArgumentException("硬件指纹不能为空");
        }
        if (request.getExpiryDate() == null || request.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("过期时间不能为空且必须是未来时间");
        }
    }
    
    /**
     * 保存License生成记录
     */
    private void saveLicenseGenerationRecord(LicenseGenerationRequest request, LicenseInfo licenseInfo) {
        // 这里可以保存到数据库，用于审计和管理
        log.info("保存License生成记录：{}", request.getLicensee());
    }
    
    /**
     * 验证生成的License文件
     */
    public boolean validateGeneratedLicense(String licenseContent) {
        try {
            // 解析License文件
            Map<String, Object> licenseFile = JSON.parseObject(licenseContent, Map.class);
            
            if (!licenseFile.containsKey("licenseInfo") || !licenseFile.containsKey("signature")) {
                return false;
            }
            
            // 这里可以添加更多验证逻辑
            return true;
            
        } catch (Exception e) {
            log.error("验证License文件失败", e);
            return false;
        }
    }
} 