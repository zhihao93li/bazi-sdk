# GitHub Packages 发布和使用指南

本文档详细说明如何将 Bazi SDK 发布到 GitHub Packages,以及其他项目如何使用。

---

## 📦 一、前置准备

### 1.1 创建 GitHub 仓库

1. 在 GitHub 上创建新仓库: `bazi-sdk`
2. 将本地代码推送到仓库:

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk
git init
git add .
git commit -m "Initial commit: Bazi SDK v1.0.0"
git branch -M main
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk.git
git push -u origin main
```

### 1.2 更新 POM 配置

**重要**: 在发布前,请将 `pom.xml` 中的以下占位符替换为实际值:

```xml
<!-- 替换为你的 GitHub 用户名 -->
<url>https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk</url>
<scm>
    <connection>scm:git:git://github.com/YOUR_GITHUB_USERNAME/bazi-sdk.git</connection>
    <developerConnection>scm:git:ssh://github.com:YOUR_GITHUB_USERNAME/bazi-sdk.git</developerConnection>
    <url>https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk/tree/main</url>
</scm>
<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/YOUR_GITHUB_USERNAME/bazi-sdk</url>
    </repository>
</distributionManagement>

<!-- 替换为你的邮箱 -->
<developers>
    <developer>
        <name>Tafu Team</name>
        <email>your-email@example.com</email>
    </developer>
</developers>
```

### 1.3 生成 GitHub Personal Access Token

1. 访问: https://github.com/settings/tokens
2. 点击 **Generate new token (classic)**
3. 配置 Token:
   - **Note**: `Bazi SDK Maven Publishing`
   - **Expiration**: 选择过期时间 (建议 No expiration 或 1 year)
   - **Scopes**: 勾选以下权限:
     - ✅ `write:packages` (上传包)
     - ✅ `read:packages` (读取包)
     - ✅ `delete:packages` (删除包,可选)
4. 点击 **Generate token**
5. **重要**: 立即复制 Token,保存到安全的地方 (只显示一次)

---

## 🚀 二、发布到 GitHub Packages

### 方式 1: 使用 GitHub Actions 自动发布 (推荐)

#### 1) 创建 GitHub Release

```bash
# 创建并推送 Tag
git tag v1.0.0
git push origin v1.0.0

# 或在 GitHub 网页上创建 Release:
# https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk/releases/new
# - Tag version: v1.0.0
# - Release title: v1.0.0
# - Description: Initial release of Bazi SDK
```

#### 2) GitHub Actions 自动触发

创建 Release 后,GitHub Actions 会自动执行发布流程:
- 构建项目
- 运行测试
- 发布到 GitHub Packages
- 上传构建产物

查看发布进度: `https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk/actions`

#### 3) 手动触发发布 (可选)

在 GitHub 网页上:
1. 进入 **Actions** 标签页
2. 选择 **Publish to GitHub Packages** 工作流
3. 点击 **Run workflow**
4. 输入版本号 (如 `1.0.1`)
5. 点击 **Run workflow**

---

### 方式 2: 本地手动发布

#### 1) 配置 Maven Settings

编辑 `~/.m2/settings.xml` (如果不存在则创建):

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**替换说明**:
- `YOUR_GITHUB_USERNAME`: 你的 GitHub 用户名
- `YOUR_GITHUB_TOKEN`: 刚才生成的 Personal Access Token

#### 2) 执行发布命令

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

# 清理、编译、测试、发布
mvn clean deploy

# 如果跳过测试
mvn clean deploy -DskipTests
```

#### 3) 验证发布成功

访问: `https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk/packages`

应该能看到:
- `bazi-sdk-core`
- `bazi-sdk-spring-boot-starter`

---

## 📖 三、在其他项目中使用

### 3.1 配置 Maven Settings

使用 SDK 的项目也需要配置 GitHub 认证。

编辑 `~/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**说明**:
- 使用 SDK 的用户需要有 `read:packages` 权限的 Token
- 如果仓库是公开的,理论上只需要 `read:packages` 权限
- 如果仓库是私有的,需要仓库所有者授权

### 3.2 在项目中添加依赖

在目标项目的 `pom.xml` 中:

```xml
<project>
    <!-- 添加 GitHub Packages 仓库 -->
    <repositories>
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/YOUR_GITHUB_USERNAME/bazi-sdk</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

    <dependencies>
        <!-- Spring Boot 项目使用 Starter -->
        <dependency>
            <groupId>com.tafu</groupId>
            <artifactId>bazi-sdk-spring-boot-starter</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- 或纯 Java 项目使用 Core -->
        <dependency>
            <groupId>com.tafu</groupId>
            <artifactId>bazi-sdk-core</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

### 3.3 使用示例

```java
@SpringBootApplication
public class Application {
    
    @Autowired
    private BaziCalculator baziCalculator;
    
    public void example() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .longitude(116.4074)
            .build();
        
        BaziResponse response = baziCalculator.calculate(request);
        System.out.println("八字: " + response.getLunarDate());
    }
}
```

---

## 🔄 四、版本管理

### 4.1 发布新版本

#### 方式 1: 自动发布 (推荐)

```bash
# 1. 更新代码
git add .
git commit -m "feat: add new feature"
git push

# 2. 创建新 Tag
git tag v1.0.1
git push origin v1.0.1

# 3. 在 GitHub 创建 Release
# 访问: https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk/releases/new
# - Tag: v1.0.1
# - Title: v1.0.1
# - Description: 描述更新内容
```

#### 方式 2: 手动发布

```bash
# 1. 更新版本号
mvn versions:set -DnewVersion=1.0.1
mvn versions:commit

# 2. 提交并推送
git add .
git commit -m "chore: bump version to 1.0.1"
git push

# 3. 发布
mvn clean deploy
```

### 4.2 语义化版本

推荐遵循 [Semantic Versioning](https://semver.org/):

- **MAJOR (主版本)**: 不兼容的 API 变更 (如 `1.0.0` -> `2.0.0`)
- **MINOR (次版本)**: 向后兼容的功能新增 (如 `1.0.0` -> `1.1.0`)
- **PATCH (修订版本)**: 向后兼容的问题修复 (如 `1.0.0` -> `1.0.1`)

---

## 🛠️ 五、常见问题

### Q1: 发布失败 - 401 Unauthorized

**原因**: GitHub Token 无效或权限不足

**解决**:
1. 检查 `~/.m2/settings.xml` 中的 Token 是否正确
2. 确认 Token 有 `write:packages` 权限
3. Token 是否过期?重新生成新的 Token

---

### Q2: 依赖下载失败 - 404 Not Found

**原因**: 仓库地址不正确或没有配置认证

**解决**:
1. 确认 `<repository>` URL 中的 GitHub 用户名正确
2. 确认 `~/.m2/settings.xml` 中配置了认证信息
3. 确认 Token 有 `read:packages` 权限

---

### Q3: 如何删除已发布的版本?

**注意**: GitHub Packages 不建议删除已发布的版本!

如果必须删除:
1. 访问: `https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk/packages`
2. 选择要删除的包
3. 点击 **Package settings**
4. 在页面底部点击 **Delete this package**

---

### Q4: 如何设置为公开包?

默认情况下,GitHub Packages 的访问权限与仓库一致:
- 如果仓库是公开的,包也是公开的
- 如果仓库是私有的,包也是私有的

**使包公开**:
1. 访问包页面: `https://github.com/YOUR_GITHUB_USERNAME/bazi-sdk/packages`
2. 点击包名进入详情页
3. 点击 **Package settings**
4. 在 **Danger Zone** 区域,点击 **Change visibility**
5. 选择 **Public**

---

### Q5: 团队成员如何使用?

**私有仓库**:
- 团队成员需要有仓库的 `read` 权限
- 每个成员需要生成自己的 Personal Access Token
- 在各自的 `~/.m2/settings.xml` 中配置

**公开仓库**:
- 任何人都可以使用,但仍需配置 GitHub 认证
- 建议创建只有 `read:packages` 权限的 Token

---

## 📊 六、最佳实践

### 6.1 版本管理

- ✅ 使用 Git Tags 和 GitHub Releases 管理版本
- ✅ 遵循语义化版本规范
- ✅ 在 Release Notes 中详细说明更新内容
- ✅ 保持 `pom.xml` 版本与 Git Tag 一致

### 6.2 安全性

- ✅ 不要将 GitHub Token 提交到代码仓库
- ✅ 使用 GitHub Secrets 存储敏感信息
- ✅ 定期轮换 Personal Access Token
- ✅ 为不同用途创建不同权限的 Token

### 6.3 文档

- ✅ 在 `README.md` 中提供使用示例
- ✅ 使用 JavaDoc 注释公共 API
- ✅ 维护 CHANGELOG.md 记录版本历史
- ✅ 提供常见问题解答

### 6.4 CI/CD

- ✅ 使用 GitHub Actions 自动化构建和测试
- ✅ 每次 Push 都运行测试
- ✅ 只在创建 Release 时自动发布
- ✅ 保留构建产物方便排查问题

---

## 🔗 相关链接

- **GitHub Packages 官方文档**: https://docs.github.com/en/packages
- **Maven 发布指南**: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry
- **语义化版本规范**: https://semver.org/
- **Maven Settings 参考**: https://maven.apache.org/settings.html

---

## 🎉 完成!

现在你的 Bazi SDK 已经可以通过 GitHub Packages 分发使用了!

如有问题,请查看 GitHub Actions 日志或提交 Issue。
