package com.vecentek.licenseGenerate.generator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 远程硬件指纹采集服务
 */
@Service
@Slf4j
public class RemoteHardwareFingerprintService {
    
    /**
     * 生成Linux/Unix硬件指纹采集工具
     */
    public String generateFingerprintCollector() {
        try {
            StringBuilder script = new StringBuilder();
            
            script.append("#!/bin/bash\n");
            script.append("echo '开始采集硬件指纹...'\n");
            script.append("echo ''\n");
            script.append("echo '系统信息:'\n");
            script.append("uname -a\n");
            script.append("echo ''\n");
            script.append("echo 'CPU信息:'\n");
            script.append("cat /proc/cpuinfo | grep 'processor\\|model name' | head -4\n");
            script.append("echo ''\n");
            script.append("echo '网络接口:'\n");
            script.append("ifconfig | grep 'ether\\|HWaddr' || ip link show | grep 'link/ether'\n");
            script.append("echo ''\n");
            script.append("echo '硬盘信息:'\n");
            script.append("lshw -class disk 2>/dev/null | grep 'serial\\|product' || echo '需要root权限获取详细信息'\n");
            script.append("echo ''\n");
            script.append("echo '主板信息:'\n");
            script.append("dmidecode -s baseboard-serial-number 2>/dev/null || echo '需要root权限获取主板信息'\n");
            script.append("echo ''\n");
            script.append("echo '请将上述信息发送给License管理员'\n");
            
            return script.toString();
            
        } catch (Exception e) {
            log.error("生成硬件指纹采集工具失败", e);
            throw new RuntimeException("采集工具生成失败", e);
        }
    }
    
    /**
     * 生成Windows硬件指纹采集脚本
     */
    public String generateWindowsFingerprintCollector() {
        StringBuilder script = new StringBuilder();
        
        script.append("@echo off\n");
        script.append("echo 开始采集硬件指纹...\n");
        script.append("echo.\n");
        script.append("echo CPU信息:\n");
        script.append("wmic cpu get ProcessorId,Name\n");
        script.append("echo.\n");
        script.append("echo 主板信息:\n");
        script.append("wmic baseboard get SerialNumber,Product\n");
        script.append("echo.\n");
        script.append("echo 网络适配器:\n");
        script.append("wmic path win32_networkadapter where \"netconnectionstatus=2\" get MACAddress,Name\n");
        script.append("echo.\n");
        script.append("echo 硬盘信息:\n");
        script.append("wmic diskdrive get SerialNumber,Model\n");
        script.append("echo.\n");
        script.append("echo 请将上述信息发送给License管理员\n");
        script.append("pause\n");
        
        return script.toString();
    }
    
    /**
     * 解析客户提供的硬件信息，生成标准硬件指纹
     */
    public String parseHardwareInfo(String hardwareInfo) {
        try {
            if (hardwareInfo == null || hardwareInfo.trim().isEmpty()) {
                throw new IllegalArgumentException("硬件信息不能为空");
            }
            
            // 简化处理：提取关键硬件标识信息
            String processedInfo = extractKeyHardwareInfo(hardwareInfo);
            
            // 生成标准化的硬件指纹
            return generateStandardFingerprint(processedInfo);
            
        } catch (Exception e) {
            log.error("解析硬件信息失败", e);
            throw new RuntimeException("硬件信息解析失败", e);
        }
    }
    
    /**
     * 提取关键硬件信息
     */
    private String extractKeyHardwareInfo(String hardwareInfo) {
        StringBuilder keyInfo = new StringBuilder();
        
        String[] lines = hardwareInfo.split("\n");
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            
            // 提取MAC地址
            if (lowerLine.contains("ether") || lowerLine.contains("hwaddr") || lowerLine.contains("mac")) {
                keyInfo.append(line.trim()).append("|");
            }
            
            // 提取CPU信息
            if (lowerLine.contains("processor") || lowerLine.contains("cpu")) {
                keyInfo.append(line.trim()).append("|");
            }
            
            // 提取主板序列号
            if (lowerLine.contains("serial") || lowerLine.contains("baseboard")) {
                keyInfo.append(line.trim()).append("|");
            }
        }
        
        return keyInfo.toString();
    }
    
    /**
     * 生成标准化硬件指纹
     */
    private String generateStandardFingerprint(String hardwareInfo) {
        // 使用MD5哈希生成固定长度的指纹
        return DigestUtils.md5DigestAsHex(hardwareInfo.getBytes()).toUpperCase();
    }
    
    /**
     * 生成硬件指纹采集说明文档
     */
    public String generateCollectorInstructions() {
        StringBuilder instructions = new StringBuilder();
        
        instructions.append("硬件指纹采集说明\n");
        instructions.append("==================\n\n");
        instructions.append("请根据您的操作系统选择对应的采集方式：\n\n");
        instructions.append("Windows系统：\n");
        instructions.append("1. 下载并运行 fingerprint_collector.bat 文件\n");
        instructions.append("2. 将输出结果复制并发送给License管理员\n\n");
        instructions.append("Linux/Unix系统：\n");
        instructions.append("1. 下载并运行 fingerprint_collector.sh 文件\n");
        instructions.append("2. 执行命令：chmod +x fingerprint_collector.sh && ./fingerprint_collector.sh\n");
        instructions.append("3. 将输出结果复制并发送给License管理员\n\n");
        instructions.append("注意事项：\n");
        instructions.append("- 某些命令可能需要管理员权限\n");
        instructions.append("- 请确保网络接口处于活动状态\n");
        instructions.append("- 采集到的信息仅用于License绑定，不会泄露个人隐私\n");
        
        return instructions.toString();
    }
} 