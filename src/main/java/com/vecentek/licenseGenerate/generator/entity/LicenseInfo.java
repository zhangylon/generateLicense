package com.vecentek.licenseGenerate.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * License信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseInfo {
    
    private String version;
    
    private LocalDateTime issueDate;
    
    private LocalDateTime expiryDate;
    
    private String licenseType;
    
    private String licensee;
    
    private String hardwareFingerprint;
    
    private LicenseFeatures features;
    
    private String signature;
} 