package com.vecentek.licenseGenerate.generator.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * License模板对象
 */
@Data
@Builder
public class LicenseTemplate {
    
    private String templateType;
    
    private Integer defaultDurationDays;
    
    private Integer maxToolcaseCount;
    
    private Integer maxUserCount;
    
    private Integer maxConcurrentUsers;
    
    private List<String> allowedStandards;
    
    private List<String> enabledModules;
    
    private String restrictions;
} 