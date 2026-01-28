# 逐步发布指南 - 解决 401/403 错误

> **您的 GitHub 用户名**: `zhihao93li`  
> **仓库地址**: https://github.com/zhihao93li/bazi-sdk

---

## 🎯 问题诊断

您遇到 **401/403 错误** 是因为:
1. ❌ GitHub 从 2021 年起不再支持密码认证
2. ✅ 必须使用 **Personal Access Token (PAT)**

---

## 📋 完整解决方案 (3 步搞定!)

### 第 1 步: 生成 GitHub Token (3 分钟)

#### 1.1 打开 Token 生成页面

访问: **https://github.com/settings/tokens/new**

#### 1.2 配置 Token

填写以下信息:

| 字段 | 值 |
|------|---|
| **Note** | `Bazi SDK Publishing` |
| **Expiration** | 选择 `No expiration` 或 `1 year` |
| **Scopes** | ✅ 勾选 `repo` (完整仓库权限)<br>✅ 勾选 `write:packages` (发布包)<br>✅ 勾选 `read:packages` (读取包) |

#### 1.3 生成并复制 Token

1. 点击页面底部的 **Generate token** 按钮
2. **立即复制生成的 Token** (形如: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`)
3. ⚠️ **重要**: Token 只显示一次,请保存到安全的地方!

**示例 Token** (您的会不一样):
```
ghp_1A2b3C4d5E6f7G8h9I0jK1lM2nO3pQ4rS5tU
```

---

### 第 2 步: 创建 GitHub 仓库 (1 分钟)

#### 2.1 访问创建页面

打开: **https://github.com/new**

#### 2.2 配置仓库

| 字段 | 值 |
|------|---|
| **Repository name** | `bazi-sdk` |
| **Description** | `八字计算 SDK - 提供完整的八字排盘、分析功能` |
| **Visibility** | 选择 `Public` (推荐) 或 `Private` |
| **Initialize** | ❌ **不要** 勾选任何初始化选项 |

#### 2.3 创建仓库

点击 **Create repository** 按钮

---

### 第 3 步: 推送代码到 GitHub (2 分钟)

现在有两种方式:

---

#### 方式 A: 使用自动化脚本 (推荐) ⭐

打开终端,执行:

```bash
# 进入项目目录
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

# 添加执行权限
chmod +x push-to-github.sh create-release.sh

# 运行推送脚本
./push-to-github.sh
```

**脚本会提示您**:
1. 是否已生成 Token → 输入 `y`
2. 请输入 Token → 粘贴您的 Token

**脚本会自动**:
- ✅ 更新 Git remote URL
- ✅ 提交更改
- ✅ 推送到 GitHub

---

#### 方式 B: 手动推送 (如果脚本不工作)

**步骤 1: 更新 Git Remote URL (使用 Token)**

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

# 将 YOUR_TOKEN 替换为您的实际 Token
git remote set-url origin https://YOUR_TOKEN@github.com/zhihao93li/bazi-sdk.git
```

**示例** (假设您的 Token 是 `ghp_1A2b3C4d5E6f7G8h9I0jK1lM2nO3pQ4rS5tU`):
```bash
git remote set-url origin https://ghp_1A2b3C4d5E6f7G8h9I0jK1lM2nO3pQ4rS5tU@github.com/zhihao93li/bazi-sdk.git
```

**步骤 2: 提交更改**

```bash
git add .
git commit -m "chore: update GitHub username to zhihao93li"
```

**步骤 3: 推送到 GitHub**

```bash
git push -u origin main
```

**如果成功**, 您会看到:
```
Enumerating objects: XX, done.
Counting objects: 100% (XX/XX), done.
...
To https://github.com/zhihao93li/bazi-sdk.git
 * [new branch]      main -> main
Branch 'main' set up to track remote branch 'main' from 'origin'.
```

✅ **推送成功!**

---

## 🏷️ 第 4 步: 创建 Release (触发自动发布)

### 4.1 方式 A: 使用脚本

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk
./create-release.sh
```

脚本会:
- ✅ 创建 Tag `v1.0.0`
- ✅ 推送 Tag 到 GitHub
- ✅ 提供 Release 创建链接

### 4.2 方式 B: 手动创建

**步骤 1: 创建并推送 Tag**

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

git tag v1.0.0
git push origin v1.0.0
```

**步骤 2: 在 GitHub 创建 Release**

访问: **https://github.com/zhihao93li/bazi-sdk/releases/new**

填写信息:
- **Choose a tag**: 选择 `v1.0.0`
- **Release title**: `v1.0.0 - Initial Release`
- **Description**: 复制粘贴以下内容:

```markdown
## 🎉 首次发布

完整的八字计算 SDK,提供以下功能:

### ✨ 核心功能
- ✅ 公历/农历输入 (支持闰月)
- ✅ 真太阳时修正
- ✅ 四柱排盘 (天干地支、纳音、藏干、空亡)
- ✅ 日主强弱分析
- ✅ 五行统计分析
- ✅ 十神计算
- ✅ 格局判断
- ✅ 大运流年 (10 步大运)
- ✅ 神煞计算

### 📦 使用方法

在项目中添加依赖:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/zhihao93li/bazi-sdk</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.tafu</groupId>
        <artifactId>bazi-sdk-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

详见: [README.md](https://github.com/zhihao93li/bazi-sdk)
```

**步骤 3: 发布**

点击 **Publish release** 按钮

---

## 🤖 第 5 步: 等待 GitHub Actions 自动发布

### 5.1 查看发布进度

访问: **https://github.com/zhihao93li/bazi-sdk/actions**

您会看到一个 **Publish to GitHub Packages** 工作流正在运行。

### 5.2 等待完成 (约 2-3 分钟)

工作流会自动:
1. ✅ 检出代码
2. ✅ 设置 Java 21 环境
3. ✅ 构建项目 (`mvn clean package`)
4. ✅ 发布到 GitHub Packages (`mvn deploy`)

### 5.3 验证发布成功

**方法 1: 查看 Packages**

访问: **https://github.com/zhihao93li/bazi-sdk/packages**

应该看到:
- ✅ `bazi-sdk-core` - 版本 1.0.0
- ✅ `bazi-sdk-spring-boot-starter` - 版本 1.0.0

**方法 2: 查看 Actions 日志**

如果看到绿色的 ✅,说明发布成功!

---

## 🎉 完成! 现在可以使用 SDK 了

### 在其他项目中使用

#### 1. 配置 Maven Settings

编辑 `~/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
    <servers>
        <server>
            <id>github</id>
            <username>zhihao93li</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**注意**: 使用您的 GitHub Token!

#### 2. 在项目中添加依赖

在目标项目的 `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/zhihao93li/bazi-sdk</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.tafu</groupId>
        <artifactId>bazi-sdk-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

#### 3. 开始使用

```java
@Autowired
private BaziCalculator baziCalculator;

public void test() {
    BaziRequest request = BaziRequest.builder()
        .year(1990).month(6).day(15)
        .hour(14).minute(30)
        .calendarType("solar")
        .gender("male")
        .longitude(116.4074)
        .build();
    
    BaziResponse response = baziCalculator.calculate(request);
    System.out.println(response.getLunarDate());
}
```

---

## ❓ 常见问题

### Q1: 推送时还是提示 401 错误?

**检查**:
1. Token 是否正确复制 (没有多余空格)
2. Token 是否包含 `repo` 权限
3. Token 是否已过期

**解决**: 重新生成 Token,确保权限正确。

### Q2: GitHub Actions 失败?

**查看日志**:
1. 访问 https://github.com/zhihao93li/bazi-sdk/actions
2. 点击失败的工作流
3. 查看错误详情

**常见原因**:
- 编译错误: 本地运行 `mvn clean compile` 检查
- 权限问题: 在仓库设置中启用 "Read and write permissions"

### Q3: 无法下载已发布的包?

**检查**:
1. 包是否已发布成功 (访问 Packages 页面)
2. `~/.m2/settings.xml` 是否配置正确
3. Token 是否有 `read:packages` 权限

---

## 📚 相关文档

- 📖 [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) - 详细的故障排除指南
- 📖 [GITHUB_PACKAGES_GUIDE.md](./GITHUB_PACKAGES_GUIDE.md) - GitHub Packages 完整指南
- 📖 [README.md](./README.md) - 项目主文档

---

## 🔧 快速命令参考

```bash
# 1. 生成 Token
open https://github.com/settings/tokens/new

# 2. 创建仓库
open https://github.com/new

# 3. 推送代码 (使用脚本)
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk
chmod +x push-to-github.sh
./push-to-github.sh

# 4. 创建 Release (使用脚本)
./create-release.sh

# 或手动推送
git remote set-url origin https://YOUR_TOKEN@github.com/zhihao93li/bazi-sdk.git
git push -u origin main
git tag v1.0.0
git push origin v1.0.0

# 5. 查看发布状态
open https://github.com/zhihao93li/bazi-sdk/actions
open https://github.com/zhihao93li/bazi-sdk/packages
```

---

**按照以上步骤操作,您就能成功发布 SDK!** 🚀

如有问题,请查看 [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) 获取帮助。
