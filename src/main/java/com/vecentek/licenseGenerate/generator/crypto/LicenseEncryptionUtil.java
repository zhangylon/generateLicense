package com.vecentek.licenseGenerate.generator.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * License加密工具类
 * 使用AES-256-CBC加密License内容
 */
@Slf4j
public class LicenseEncryptionUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;
    
    // 默认加密密钥（实际使用中应该从安全配置获取）
    private static final String DEFAULT_SECRET_KEY = "VecentekLicenseEncryptionKey2025";
    
    /**
     * 使用默认密钥加密License内容
     */
    public static String encrypt(String content) {
        return encrypt(content, DEFAULT_SECRET_KEY);
    }
    
    /**
     * 使用指定密钥加密License内容
     */
    public static String encrypt(String content, String secretKey) {
        try {
            // 生成AES密钥
            SecretKeySpec keySpec = generateKeySpec(secretKey);
            
            // 生成随机IV
            byte[] iv = generateRandomIV();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // 创建加密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            
            // 加密内容
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.doFinal(contentBytes);
            
            // 将IV和加密内容合并，然后Base64编码
            byte[] result = new byte[IV_LENGTH + encryptedBytes.length];
            System.arraycopy(iv, 0, result, 0, IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, result, IV_LENGTH, encryptedBytes.length);
            
            return Base64.getEncoder().encodeToString(result);
            
        } catch (Exception e) {
            log.error("License内容加密失败", e);
            throw new RuntimeException("License加密失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 使用默认密钥解密License内容
     */
    public static String decrypt(String encryptedContent) {
        return decrypt(encryptedContent, DEFAULT_SECRET_KEY);
    }
    
    /**
     * 使用指定密钥解密License内容
     */
    public static String decrypt(String encryptedContent, String secretKey) {
        try {
            // Base64解码
            byte[] encryptedData = Base64.getDecoder().decode(encryptedContent);
            
            if (encryptedData.length < IV_LENGTH) {
                throw new IllegalArgumentException("加密数据长度不足");
            }
            
            // 提取IV和加密内容
            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedBytes = new byte[encryptedData.length - IV_LENGTH];
            System.arraycopy(encryptedData, 0, iv, 0, IV_LENGTH);
            System.arraycopy(encryptedData, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);
            
            // 生成AES密钥
            SecretKeySpec keySpec = generateKeySpec(secretKey);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // 创建解密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            // 解密内容
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            log.error("License内容解密失败", e);
            throw new RuntimeException("License解密失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 基于硬件指纹生成加密密钥
     */
    public static String encryptWithHardwareFingerprint(String content, String hardwareFingerprint) {
        String secretKey = generateSecretKeyFromFingerprint(hardwareFingerprint);
        return encrypt(content, secretKey);
    }
    
    /**
     * 基于硬件指纹解密License内容
     */
    public static String decryptWithHardwareFingerprint(String encryptedContent, String hardwareFingerprint) {
        String secretKey = generateSecretKeyFromFingerprint(hardwareFingerprint);
        return decrypt(encryptedContent, secretKey);
    }
    
    /**
     * 生成AES密钥规格
     */
    private static SecretKeySpec generateKeySpec(String secretKey) {
        // 使用SHA-256哈希确保密钥长度为32字节
        byte[] keyBytes = DigestUtils.md5Digest(secretKey.getBytes(StandardCharsets.UTF_8));
        // 扩展到32字节
        byte[] expandedKey = new byte[32];
        for (int i = 0; i < 32; i++) {
            expandedKey[i] = keyBytes[i % keyBytes.length];
        }
        return new SecretKeySpec(expandedKey, ALGORITHM);
    }
    
    /**
     * 生成随机IV
     */
    private static byte[] generateRandomIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    /**
     * 基于硬件指纹生成密钥
     */
    private static String generateSecretKeyFromFingerprint(String hardwareFingerprint) {
        return DEFAULT_SECRET_KEY + "_" + hardwareFingerprint;
    }
    
    /**
     * 验证License文件是否为加密格式
     */
    public static boolean isEncryptedLicense(String content) {
        try {
            // 尝试Base64解码
            byte[] decoded = Base64.getDecoder().decode(content);
            return decoded.length > IV_LENGTH;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 生成AES密钥（用于密钥管理）
     */
    public static String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_LENGTH);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("生成AES密钥失败", e);
            throw new RuntimeException("密钥生成失败", e);
        }
    }
} 