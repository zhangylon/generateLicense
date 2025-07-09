package com.vecentek.licenseGenerate.generator.service;

import com.vecentek.licenseGenerate.generator.crypto.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

/**
 * 数字签名服务
 */
@Service
@Slf4j
public class DigitalSignatureService {
    
    @Autowired
    private LicenseKeyManager keyManager;
    
    @Value("${license.generator.private-key:}")
    private String privateKeyConfig;
    
    /**
     * 对License内容进行数字签名
     */
    public String sign(String content, String privateKeyStr) {
        try {
            // 加载私钥
            PrivateKey privateKey = keyManager.loadPrivateKey(privateKeyStr);
            
            // 创建签名对象
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            
            // 生成签名
            byte[] signatureBytes = signature.sign();
            
            // 转换为Base64字符串
            return Base64.getEncoder().encodeToString(signatureBytes);
            
        } catch (Exception e) {
            log.error("数字签名失败", e);
            throw new RuntimeException("数字签名失败", e);
        }
    }
    
    /**
     * 使用配置的私钥进行签名
     */
    public String sign(String content) {
        if (privateKeyConfig == null || privateKeyConfig.trim().isEmpty()) {
            throw new RuntimeException("未配置私钥，无法进行签名");
        }
        return sign(content, privateKeyConfig);
    }
    
    /**
     * 验证签名（用于自检）
     */
    public boolean verify(String content, String signatureStr, String publicKey) {
        try {
            return RSAUtil.verifySignature(content, signatureStr, publicKey);
        } catch (Exception e) {
            log.error("签名验证失败", e);
            return false;
        }
    }
} 