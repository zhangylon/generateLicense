package com.vecentek.licenseGenerate.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * License功能特性DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseFeatures {
    
    private Integer maxUserCount;
    
    private List<String> enabledModules;

    private List<Long> allowedItemIds;
} 