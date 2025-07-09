package com.vecentek.licenseGenerate.generator.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * License生成请求对象
 */
@Data
@Builder
public class LicenseGenerationRequest {
    
    // 基本信息
    private String licensee;                    // 被许可人
    private String licenseType;                 // 许可证类型
    private LocalDateTime expiryDate;           // 过期时间
    private String hardwareFingerprint;         // 硬件指纹
    
    // 功能限制
    private Integer maxUserCount;               // 最大用户数
    private List<String> enabledModules;        // 启用的模块
    
    // 申请信息
    private String applicant;                   // 申请人
    private String applicationReason;           // 申请原因
    private String contactInfo;                 // 联系信息
    
    // 审批信息
    private String approver;                    // 审批人
    private LocalDateTime approvalTime;         // 审批时间
    private String approvalComments;            // 审批意见
} 