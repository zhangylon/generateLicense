## 项目说明

本项目提供一个可独立运行的 License 生成器工具，用于：
- 生成 RSA 密钥对（公钥/私钥）
- 生成绑定硬件指纹的 License 文件（支持加密）
- 生成硬件指纹采集脚本（Windows/Linux）

核心入口类：`com.vecentek.licenseGenerate.generator.LicenseGeneratorTool`

---

## 运行

方式一：IDE 直接运行
- 打开 `LicenseGeneratorTool.java`
- 右键 `main` 方法运行

启动后将看到主菜单：
- 1. 生成RSA密钥对
- 2. 生成License文件
- 3. 生成硬件指纹采集脚本
- 4. 退出

输入对应数字回车即可使用。

---

## 使用介绍
- 1、合规一体化工具需要控制过期时间，功能模块与测试项，EN18031工具只控制过期时间
- 2、两个工具均可以用此功能生成对应的license文件，如果工具此前没有上传过license文件，则需要生成未加密的文件，如果工具需要更新license，则需要生成加密文件
- 3、生成license文件之前需要项目经理提供客户名称，硬件码，授权到期时间，并要求项目经理在企业微信文档【https://doc.weixin.qq.com/sheet/e3_ASYAAwYmAPwbktQFErDTNuB8VXjks?scode=AMYAeweRAAs6bit9ng】 填写好相应信息作为留存记录，如果是测试环境生成可不作此要求
- 4、合规一体化工具若新增了测试项，则需要在
~~~ java
private static Long[] allowedItemIds = new Long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L,
            14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L,
            38L, 39L, 40L, 41L, 42L, 43L, 44L, 45L, 46L, 47L, 48L, 49L, 50L, 51L, 52L, 53L, 54L, 55L, 56L, 57L, 58L, 59L, 60L, 61L,
            62L, 63L, 64L, 65L, 66L, 67L, 68L, 69L, 70L, 71L, 72L, 73L, 74L, 75L, 76L, 77L, 78L, 79L, 80L, 81L, 82L, 83L, 84L, 85L,
            86L, 87L, 88L, 89L, 90L, 91L, 92L, 93L, 94L, 95L, 96L, 97L, 98L, 99L, 100L, 101L, 102L, 103L, 104L, 105L, 106L, 107L,
            108L, 109L, 110L, 111L, 112L, 113L,114L,115L};
~~~
代码里面添加对应的测试项id，并重新生成license文件进行授权

## 功能详解



### 1) 生成 License 文件
- 菜单选择：输入 `2`
- 交互式参数：
  - 被许可人名称：如 “为辰信安”
  - License 类型：`正式` / `临时` / `ENTERPRISE`
  - 硬件指纹：来自采集脚本或人工汇总
  - 过期日期：格式 `yyyy-MM-dd HH:mm:ss`，必须晚于当前时间
  - 最大用户数量：默认 10（回车跳过使用默认）
  - 授权测试项（可选）：逗号分隔的数字 ID（回车跳过使用默认全量）
  - 授权功能模块（可选）：逗号分隔路径（回车跳过使用默认全量；若包含 `/task`，会自动补 `/task/list`、`/task/create`）
  - 是否加密：输入 `Y` 表示加密（依据硬件指纹），其他值为明文
  - 私钥文件路径：默认 `license_private_key.txt`（直接回车使用默认）
- 生成文件：
  - `license_<硬件指纹>_<被许可人>_yyyyMMdd_HHmmss.lic`
- 文件内容：
  - JSON：`{"licenseInfo": { ... }, "signature": "..."}`
  - 若选择加密，则 JSON 会使用硬件指纹加密后写入 `.lic`
- 终端输出：保存路径、类型、被许可人、有效期等信息

---


## 常见问题（FAQ）

- Q: 运行时提示日期格式错误？
  - A: 请严格使用 `yyyy-MM-dd HH:mm:ss`，例如 `2025-12-31 23:59:59`。

- Q: 提示过期日期不能小于当前日期？
  - A: 选择未来时间，不能早于当前时间。

- Q: 私钥文件路径怎么填写？
  - A: 直接回车使用默认 `license_private_key.txt`，或输入绝对/相对路径。

- Q: 生成的 `.lic` 是明文还是密文？
  - A: 选择 `Y` 加密则为密文，否则为 JSON 明文。

- Q: 功能模块如何配置？
  - A: 用逗号分隔路径，例如 `/task,/result,/system`。包含 `/task` 会自动补 `/task/list` 和 `/task/create`。

- Q: 测试项 ID 如何配置？
  - A: 输入逗号分隔数字，例如 `1,2,5,8`。不输入则使用工具内置全量集合。
---




