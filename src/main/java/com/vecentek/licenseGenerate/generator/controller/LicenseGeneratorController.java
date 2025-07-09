package com.vecentek.licenseGenerate.generator.controller;

import com.vecentek.licenseGenerate.generator.entity.AjaxResult;
import com.vecentek.licenseGenerate.generator.entity.KeyPairResult;
import com.vecentek.licenseGenerate.generator.entity.LicenseGenerationRequest;
import com.vecentek.licenseGenerate.generator.entity.LicenseGenerationResult;
import com.vecentek.licenseGenerate.generator.entity.LicenseTemplate;
import com.vecentek.licenseGenerate.generator.service.LicenseGeneratorService;
import com.vecentek.licenseGenerate.generator.service.LicenseKeyManager;
import com.vecentek.licenseGenerate.generator.service.LicenseTemplateService;
import com.vecentek.licenseGenerate.generator.service.RemoteHardwareFingerprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * License生成器控制器
 */
@RestController
@RequestMapping("/api/license-generator")
@Slf4j
public class LicenseGeneratorController  {
    
    @Autowired
    private LicenseGeneratorService generatorService;
    
    @Autowired
    private LicenseKeyManager keyManager;
    
    @Autowired
    private LicenseTemplateService templateService;
    
    @Autowired
    private RemoteHardwareFingerprintService fingerprintService;
    
    /**
     * 生成License
     */
    @PostMapping("/generate")
    public AjaxResult generateLicense(@RequestBody LicenseGenerationRequest request) {
        try {
            LicenseGenerationResult result = generatorService.generateLicense(request);
            
            if (result.isSuccess()) {
                return AjaxResult.success("License生成成功", result);
            } else {
                return AjaxResult.error(result.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("License生成失败", e);
            return AjaxResult.error("License生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用自定义私钥生成License
     */
    @PostMapping("/generate-with-key")
    public AjaxResult generateLicenseWithKey(@RequestBody Map<String, Object> requestMap) {
        try {
            LicenseGenerationRequest request = parseGenerationRequest(requestMap);
            String privateKey = (String) requestMap.get("privateKey");
            
            if (privateKey == null || privateKey.trim().isEmpty()) {
                return AjaxResult.error("私钥不能为空");
            }
            
            LicenseGenerationResult result = generatorService.generateLicense(request, privateKey);
            
            if (result.isSuccess()) {
                return AjaxResult.success("License生成成功", result);
            } else {
                return AjaxResult.error(result.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("License生成失败", e);
            return AjaxResult.error("License生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成RSA密钥对
     */
    @PostMapping("/generate-keypair")
    public AjaxResult generateKeyPair() {
        try {
            KeyPairResult keyPair = keyManager.generateKeyPair();
            keyManager.saveKeyPair(keyPair);
            
            return AjaxResult.success("密钥对生成成功", keyPair);
            
        } catch (Exception e) {
            log.error("密钥对生成失败", e);
            return AjaxResult.error("密钥对生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取License模板
     */
    @GetMapping("/template/{templateType}")
    public AjaxResult getLicenseTemplate(@PathVariable String templateType) {
        try {
            LicenseTemplate template = templateService.getTemplate(templateType);
            return AjaxResult.success("模板获取成功", template);
            
        } catch (Exception e) {
            log.error("获取License模板失败", e);
            return AjaxResult.error("获取模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取硬件指纹采集工具
     */
    @GetMapping("/fingerprint-collector/{os}")
    public AjaxResult getFingerprintCollector(@PathVariable String os) {
        try {
            String script;
            if ("windows".equalsIgnoreCase(os)) {
                script = fingerprintService.generateWindowsFingerprintCollector();
            } else {
                script = fingerprintService.generateFingerprintCollector();
            }
            
            return AjaxResult.success("采集工具生成成功", script);
            
        } catch (Exception e) {
            log.error("生成硬件指纹采集工具失败", e);
            return AjaxResult.error("生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 解析硬件信息
     */
    @PostMapping("/parse-hardware")
    public AjaxResult parseHardwareInfo(@RequestBody Map<String, String> request) {
        try {
            String hardwareInfo = request.get("hardwareInfo");
            String fingerprint = fingerprintService.parseHardwareInfo(hardwareInfo);
            
            Map<String, String> result = new HashMap<>();
            result.put("hardwareFingerprint", fingerprint);
            
            return AjaxResult.success("硬件指纹生成成功", result);
            
        } catch (Exception e) {
            log.error("解析硬件信息失败", e);
            return AjaxResult.error("解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证生成的License文件
     */
    @PostMapping("/validate")
    public AjaxResult validateLicense(@RequestBody Map<String, String> request) {
        try {
            String licenseContent = request.get("licenseContent");
            boolean valid = generatorService.validateGeneratedLicense(licenseContent);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", valid);
            
            return AjaxResult.success("验证完成", result);
            
        } catch (Exception e) {
            log.error("License验证失败", e);
            return AjaxResult.error("验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 解析生成请求
     */
    private LicenseGenerationRequest parseGenerationRequest(Map<String, Object> requestMap) {
        // 这里简化处理，实际项目中可以使用JSON转换工具
        return LicenseGenerationRequest.builder()
                .licensee((String) requestMap.get("licensee"))
                .licenseType((String) requestMap.get("licenseType"))
                .hardwareFingerprint((String) requestMap.get("hardwareFingerprint"))
                // 其他字段的解析...
                .build();
    }
} 