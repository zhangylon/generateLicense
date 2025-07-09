package com.vecentek.licenseGenerate.generator.service;

import com.vecentek.licenseGenerate.generator.entity.LicenseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * License模板服务
 */
@Service
@Slf4j
public class LicenseTemplateService {
    
    /**
     * 获取License模板
     */
    public LicenseTemplate getTemplate(String templateType) {
        switch (templateType.toUpperCase()) {
            case "TRIAL":
                return createTrialTemplate();
            case "STANDARD":
                return createStandardTemplate();
            case "ENTERPRISE":
                return createEnterpriseTemplate();
            default:
                throw new IllegalArgumentException("未知的模板类型: " + templateType);
        }
    }
    
    /**
     * 试用版模板
     */
    private LicenseTemplate createTrialTemplate() {
        return LicenseTemplate.builder()
                .templateType("TRIAL")
                .defaultDurationDays(30)
                .maxToolcaseCount(10)
                .maxUserCount(5)
                .maxConcurrentUsers(3)
                .allowedStandards(Arrays.asList("GB/T"))
                .enabledModules(Arrays.asList("toolcase"))
                .restrictions("试用版限制功能")
                .build();
    }
    
    /**
     * 标准版模板
     */
    private LicenseTemplate createStandardTemplate() {
        return LicenseTemplate.builder()
                .templateType("STANDARD")
                .defaultDurationDays(365)
                .maxToolcaseCount(1000)
                .maxUserCount(100)
                .maxConcurrentUsers(50)
                .allowedStandards(Arrays.asList("GB/T", "IEEE"))
                .enabledModules(Arrays.asList("toolcase", "report"))
                .restrictions("标准版功能")
                .build();
    }
    
    /**
     * 企业版模板
     */
    private LicenseTemplate createEnterpriseTemplate() {
        return LicenseTemplate.builder()
                .templateType("ENTERPRISE")
                .defaultDurationDays(365)
                .maxToolcaseCount(-1) // 无限制
                .maxUserCount(-1)     // 无限制
                .maxConcurrentUsers(-1) // 无限制
                .allowedStandards(Arrays.asList("GB/T", "IEEE", "ISO", "CUSTOM"))
                .enabledModules(Arrays.asList("toolcase", "report", "analysis", "integration"))
                .restrictions("企业版完整功能")
                .build();
    }
} 