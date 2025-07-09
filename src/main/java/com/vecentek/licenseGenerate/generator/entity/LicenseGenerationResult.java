package com.vecentek.licenseGenerate.generator.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * License生成结果对象
 */
@Data
@Builder
public class LicenseGenerationResult {
    
    private boolean success;                    // 是否成功
    private String errorMessage;               // 错误信息
    private String licenseContent;             // License文件内容
    private LicenseInfo licenseInfo;           // License信息
    private LocalDateTime generationTime;      // 生成时间
    private String generationId;               // 生成记录ID
    
    public static LicenseGenerationResult success(String licenseContent, LicenseInfo licenseInfo) {
        return LicenseGenerationResult.builder()
                .success(true)
                .licenseContent(licenseContent)
                .licenseInfo(licenseInfo)
                .generationTime(LocalDateTime.now())
                .build();
    }
    
    public static LicenseGenerationResult fail(String errorMessage) {
        return LicenseGenerationResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .generationTime(LocalDateTime.now())
                .build();
    }
} 