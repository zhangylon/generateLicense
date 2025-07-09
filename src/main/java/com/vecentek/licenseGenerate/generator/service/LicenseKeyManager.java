package com.vecentek.licenseGenerate.generator.service;

import com.vecentek.licenseGenerate.generator.entity.KeyPairResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * License密钥对管理器
 */
@Component
@Slf4j
public class LicenseKeyManager {
    
    private static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";
    
    /**
     * 生成RSA密钥对
     */
    public KeyPairResult generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            // 获取公钥和私钥
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            // 转换为Base64字符串
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            
            return KeyPairResult.builder()
                    .publicKey(publicKeyStr)
                    .privateKey(privateKeyStr)
                    .algorithm(ALGORITHM)
                    .keySize(KEY_SIZE)
                    .build();
                    
        } catch (Exception e) {
            log.error("生成RSA密钥对失败", e);
            throw new RuntimeException("密钥对生成失败", e);
        }
    }
    
    /**
     * 从字符串加载私钥
     */
    public PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }
    
    /**
     * 从字符串加载公钥
     */
    public PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }
    
    /**
     * 保存密钥对到安全存储
     */
    public void saveKeyPair(KeyPairResult keyPair) {
        try {
            // 在实际生产环境中，应该使用HSM或其他安全存储方案
            // 这里简化为日志记录
            log.info("密钥对已生成，公钥长度: {}, 私钥长度: {}", 
                     keyPair.getPublicKey().length(), keyPair.getPrivateKey().length());
            log.warn("请妥善保管私钥，并在配置文件中设置公钥");
            
        } catch (Exception e) {
            log.error("保存密钥对失败", e);
            throw new RuntimeException("密钥对保存失败", e);
        }
    }
} 