package com.vecentek.licenseGenerate.generator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vecentek.licenseGenerate.generator.crypto.LicenseEncryptionUtil;
import com.vecentek.licenseGenerate.generator.entity.KeyPairResult;
import com.vecentek.licenseGenerate.generator.entity.LicenseFeatures;
import com.vecentek.licenseGenerate.generator.entity.LicenseInfo;
import com.vecentek.licenseGenerate.generator.service.LicenseKeyManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * License生成器工具类（独立运行）
 * 可以脱离Spring环境运行，用于生成License文件
 */
public class LicenseGeneratorTool {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger log = LoggerFactory.getLogger(LicenseGeneratorTool.class);

    private static Long[] allowedItemIds = new Long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L,
            14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L,
            38L, 39L, 40L, 41L, 42L, 43L, 44L, 45L, 46L, 47L, 48L, 49L, 50L, 51L, 52L, 53L, 54L, 55L, 56L, 57L, 58L, 59L, 60L, 61L,
            62L, 63L, 64L, 65L, 66L, 67L, 68L, 69L, 70L, 71L, 72L, 73L, 74L, 75L, 76L, 77L, 78L, 79L, 80L, 81L, 82L, 83L, 84L, 85L,
            86L, 87L, 88L, 89L, 90L, 91L, 92L, 93L, 94L, 95L, 96L, 97L, 98L, 99L, 100L, 101L, 102L, 103L, 104L, 105L, 106L, 107L,
            108L, 109L, 110L, 111L, 112L, 113L,114L,115L,116L, 117L, 118L, 119L, 120L, 121L, 122L, 103807441762961L, 119418510061697L};

    private static String[] allowedModules = new String[]{"/task", "/task/list", "/task/create", "/useCase", "/result", "/system"};

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("    License生成器工具 v1.0");
        System.out.println("=================================");

        try {
            showMainMenu();
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMainMenu() throws Exception {
        while (true) {
            System.out.println("\n请选择操作：");
            System.out.println("1. 生成RSA密钥对");
            System.out.println("2. 生成License文件");
            System.out.println("3. 生成硬件指纹采集脚本");
            System.out.println("4. 退出");
            System.out.print("请输入选项 (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    generateKeyPair();
                    break;
                case "2":
                    generateLicense();
                    break;
                case "3":
                    generateFingerprintScript();
                    break;
                case "4":
                    System.out.println("再见！");
                    return;
                default:
                    System.out.println("无效选项，请重新输入");
            }
        }
    }

    /**
     * 生成RSA密钥对
     */
    private static void generateKeyPair() throws Exception {
        System.out.println("\n正在生成RSA密钥对...");

        LicenseKeyManager keyManager = new LicenseKeyManager();
        KeyPairResult keyPair = keyManager.generateKeyPair();

        // 保存公钥
        String publicKeyFile = "license_public_key.txt";
        Files.write(Paths.get(publicKeyFile), keyPair.getPublicKey().getBytes());

        // 保存私钥
        String privateKeyFile = "license_private_key.txt";
        Files.write(Paths.get(privateKeyFile), keyPair.getPrivateKey().getBytes());

        System.out.println("密钥对生成成功！");
        System.out.println("公钥已保存到: " + publicKeyFile);
        System.out.println("私钥已保存到: " + privateKeyFile);
        System.out.println("⚠️  请妥善保管私钥文件，不要泄露给他人");

        System.out.println("\n公钥内容（用于配置验证系统）：");
        System.out.println("-----BEGIN PUBLIC KEY-----");
        System.out.println(keyPair.getPublicKey());
        System.out.println("-----END PUBLIC KEY-----");
    }

    /**
     * 生成License文件
     */
    private static void generateLicense() throws Exception {
        System.out.println("\n=== License文件生成 ===");

        // 输入基本信息
        System.out.print("被许可人名称: ");
        String licensee = scanner.nextLine().trim();

        System.out.print("License类型 (TRIAL/STANDARD/ENTERPRISE): ");
        String licenseType = scanner.nextLine().trim().toUpperCase();

        System.out.print("硬件指纹: ");
        String hardwareFingerprint = scanner.nextLine().trim();

        String expiryDateStr;
        LocalDateTime expiryDate;
        while (true) {
            System.out.print("过期日期（格式：yyyy-MM-dd HH:mm，例如：2025-06-16 15:00:00）: ");
            expiryDateStr = scanner.nextLine().trim();
            try {
                expiryDate = LocalDateTime.parse(expiryDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                if (expiryDate.isBefore(LocalDateTime.now())) {
                    System.out.println("错误：过期日期不能小于当前日期，请重新输入");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("日期格式错误，请按照yyyy-MM-dd HH:mm:ss格式重新输入");
            }
        }

        System.out.print("最大用户数量（默认为10）: ");
        int maxUserCount = 10;
        String maxUserCountStr = scanner.nextLine().trim();
        if (!StringUtils.isBlank(maxUserCountStr)) {
            maxUserCount = Integer.parseInt(scanner.nextLine().trim());
        }

        System.out.println("授权测试项（默认全部）：");
        String trim = scanner.nextLine().trim();
        if (StringUtils.isNotBlank(trim)) {
            String[] split = trim.split(",");
            Long[] itemIds = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                itemIds[i] = Long.parseLong(split[i]);
            }
            allowedItemIds = itemIds;
        }

        System.out.println("授权功能模块（默认全部）：");
        String trim1 = scanner.nextLine().trim();
        if (StringUtils.isNotBlank(trim1)) {
            String[] split = trim1.split(",");
            Set<String> strings = new HashSet<>();
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (("/task").equals(s)) {
                    strings.add("/task/list");
                    strings.add("/task/create");
                }
                strings.add(s);
            }
            allowedModules = strings.toArray(new String[0]);
        }

        System.out.print("是否加密License文件 (Y/N): ");
        String encryptChoice = scanner.nextLine().trim().toUpperCase();

        LicenseFeatures features = LicenseFeatures.builder()
                .maxUserCount(maxUserCount)
                .enabledModules(Arrays.asList(allowedModules))
                .allowedItemIds(Arrays.asList(allowedItemIds))
                .build();

        LicenseInfo licenseInfo = LicenseInfo.builder()
                .version("1.0")
                .issueDate(LocalDateTime.now())
                .expiryDate(expiryDate)
                .licenseType(licenseType)
                .licensee(licensee)
                .hardwareFingerprint(hardwareFingerprint)
                .features(features)
                .build();

        // 读取私钥
        System.out.print("私钥文件路径 (默认: license_private_key.txt): ");
        String privateKeyPath = scanner.nextLine().trim();
        if (privateKeyPath.isEmpty()) {
            privateKeyPath = "license_private_key.txt";
        }

        String privateKeyStr = new String(Files.readAllBytes(Paths.get(privateKeyPath)));

        // 生成签名
        String licenseContent = buildLicenseContent(licenseInfo);
        String signature = signContent(licenseContent, privateKeyStr);
        log.info("【签名内容：{}，签名：{}，私钥：{}】", licenseContent, signature, privateKeyStr);

        // 构建完整License文件
        Map<String, Object> licenseFile = new LinkedHashMap<>();
        licenseFile.put("licenseInfo", licenseInfo);
        licenseFile.put("signature", signature);

        String jsonContent = JSON.toJSONString(licenseFile, SerializerFeature.PrettyFormat);
        String encryptedContent = jsonContent;
        if ("Y".equals(encryptChoice)) {
            // 加密License内容
            encryptedContent = LicenseEncryptionUtil.encryptWithHardwareFingerprint(
                    jsonContent, hardwareFingerprint);
        }

        // 保存License文件（使用.lic扩展名）
        String fileName = "license_" + hardwareFingerprint + "_" +licensee + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".lic";
        Files.write(Paths.get(fileName), encryptedContent.getBytes(StandardCharsets.UTF_8));

        System.out.println("\nLicense文件生成成功！");
        System.out.println("文件保存位置: " + fileName);
        System.out.println("License类型: " + licenseType);
        System.out.println("被许可人: " + licensee);
        System.out.println("有效期至: " + expiryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * 生成硬件指纹采集脚本
     */
    private static void generateFingerprintScript() throws Exception {
        System.out.println("\n=== 硬件指纹采集脚本生成 ===");
        System.out.println("请选择操作系统类型：");
        System.out.println("1. Windows (.bat)");
        System.out.println("2. Linux/Unix (.sh)");
        System.out.print("请选择 (1-2): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                generateWindowsScript();
                break;
            case "2":
                generateLinuxScript();
                break;
            default:
                System.out.println("无效选项");
        }
    }

    private static void generateWindowsScript() throws Exception {
        String script = "@echo off\n" +
                "echo 开始采集硬件指纹...\n" +
                "echo.\n" +
                "echo CPU信息:\n" +
                "wmic cpu get ProcessorId,Name\n" +
                "echo.\n" +
                "echo 主板信息:\n" +
                "wmic baseboard get SerialNumber,Product\n" +
                "echo.\n" +
                "echo 网络适配器:\n" +
                "wmic path win32_networkadapter where \"netconnectionstatus=2\" get MACAddress,Name\n" +
                "echo.\n" +
                "echo 硬盘信息:\n" +
                "wmic diskdrive get SerialNumber,Model\n" +
                "echo.\n" +
                "echo 请将上述信息发送给License管理员\n" +
                "pause\n";

        Files.write(Paths.get("hardware_fingerprint_collector.bat"), script.getBytes(StandardCharsets.UTF_8));
        System.out.println("Windows采集脚本已生成: hardware_fingerprint_collector.bat");
    }

    private static void generateLinuxScript() throws Exception {
        String script = "#!/bin/bash\n" +
                "echo '开始采集硬件指纹...'\n" +
                "echo ''\n" +
                "echo '系统信息:'\n" +
                "uname -a\n" +
                "echo ''\n" +
                "echo 'CPU信息:'\n" +
                "cat /proc/cpuinfo | grep 'processor\\|model name' | head -4\n" +
                "echo ''\n" +
                "echo '网络接口:'\n" +
                "ifconfig | grep 'ether\\|HWaddr' || ip link show | grep 'link/ether'\n" +
                "echo ''\n" +
                "echo '硬盘信息:'\n" +
                "lshw -class disk 2>/dev/null | grep 'serial\\|product' || echo '需要root权限获取详细信息'\n" +
                "echo ''\n" +
                "echo '主板信息:'\n" +
                "dmidecode -s baseboard-serial-number 2>/dev/null || echo '需要root权限获取主板信息'\n" +
                "echo ''\n" +
                "echo '请将上述信息发送给License管理员'\n";

        Files.write(Paths.get("hardware_fingerprint_collector.sh"), script.getBytes(StandardCharsets.UTF_8));
        System.out.println("Linux采集脚本已生成: hardware_fingerprint_collector.sh");
        System.out.println("使用方法: chmod +x hardware_fingerprint_collector.sh && ./hardware_fingerprint_collector.sh");
    }

    /**
     * 构建License内容
     */
    private static String buildLicenseContent(LicenseInfo licenseInfo) {
        Map<String, Object> contentMap = new LinkedHashMap<>();
        contentMap.put("version", licenseInfo.getVersion());
        contentMap.put("issueDate", licenseInfo.getIssueDate().toString());
        contentMap.put("expiryDate", licenseInfo.getExpiryDate().toString());
        contentMap.put("licenseType", licenseInfo.getLicenseType());
        contentMap.put("licensee", licenseInfo.getLicensee());
        contentMap.put("hardwareFingerprint", licenseInfo.getHardwareFingerprint());
        contentMap.put("features", licenseInfo.getFeatures());

        return JSON.toJSONString(contentMap);
    }

    /**
     * 对内容进行数字签名
     */
    private static String signContent(String content, String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
} 