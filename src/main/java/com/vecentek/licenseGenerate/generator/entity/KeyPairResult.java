package com.vecentek.licenseGenerate.generator.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 密钥对结果对象
 */
@Data
@Builder
public class KeyPairResult {
    
    private String publicKey;
    
    private String privateKey;
    
    private String algorithm;
    
    private Integer keySize;
} 