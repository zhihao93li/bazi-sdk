# 故障排除指南

> 解决 GitHub 推送和发布过程中的常见问题

---

## 🚨 问题 1: 推送失败 - 401 Unauthorized

### 错误信息
```
remote: Support for password authentication was removed on August 13, 2021.
remote: Please use a personal access token instead.
fatal: Authentication failed for 'https://github.com/zhihao93li/bazi-sdk.git/'
```

### 原因
GitHub 从 2021 年 8 月起不再支持密码认证,必须使用 Personal Access Token (PAT)。

### 解决方案

#### 方法 1: 使用自动化脚本 (推荐)

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

# 添加执行权限
chmod +x push-to-github.sh

# 运行脚本
./push-to-github.sh
```

脚本会引导您:
1. 生成 GitHub Token
2. 输入 Token
3. 自动配置并推送

#### 方法 2: 手动配置

**步骤 1: 生成 Personal Access Token**

1. 访问: https://github.com/settings/tokens/new
2. 配置:
   - **Note**: `Bazi SDK`
   - **Expiration**: 选择 `No expiration` 或 `1 year`
   - **Scopes**: 勾选以下权限:
     - ✅ `repo` (完整仓库访问)
     - ✅ `write:packages` (发布包)
     - ✅ `read:packages` (读取包)
3. 点击 **Generate token**
4. **立即复制 Token** (只显示一次!)

**步骤 2: 更新 Git Remote URL**

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

# 方式 A: 使用 Token 嵌入 URL (简单但不够安全)
git remote set-url origin https://YOUR_TOKEN@github.com/zhihao93li/bazi-sdk.git

# 方式 B: 使用 Git Credential Helper (推荐)
git remote set-url origin https://github.com/zhihao93li/bazi-sdk.git
git config credential.helper store

# 下次推送时会提示输入用户名和密码:
# Username: zhihao93li
# Password: YOUR_TOKEN (粘贴 Token)
```

**步骤 3: 推送代码**

```bash
git push -u origin main
```

---

## 🚨 问题 2: 推送失败 - 403 Forbidden

### 错误信息
```
remote: Permission to zhihao93li/bazi-sdk.git denied to zhihao93li.
fatal: unable to access 'https://github.com/zhihao93li/bazi-sdk.git/': The requested URL returned error: 403
```

### 原因
1. Token 权限不足
2. Token 已过期
3. 仓库不存在或无访问权限

### 解决方案

**1. 检查仓库是否存在**

访问: https://github.com/zhihao93li/bazi-sdk

- 如果显示 404: 需要先创建仓库
- 如果显示 403: 需要检查账号登录状态

**2. 重新生成 Token (确保权限正确)**

访问: https://github.com/settings/tokens

- 检查现有 Token 是否包含 `repo` 权限
- 如果没有,删除旧 Token,重新生成

**3. 验证 Token 有效性**

```bash
# 测试 Token 是否有效
curl -H "Authorization: token YOUR_TOKEN" https://api.github.com/user

# 应该返回您的用户信息
```

**4. 清除缓存的凭据**

```bash
# macOS
git credential-osxkeychain erase
host=github.com
protocol=https

# 然后重新推送
git push -u origin main
```

---

## 🚨 问题 3: 仓库不存在 - Repository not found

### 错误信息
```
remote: Repository not found.
fatal: repository 'https://github.com/zhihao93li/bazi-sdk.git/' not found
```

### 解决方案

**在 GitHub 创建仓库**

1. 访问: https://github.com/new
2. 配置:
   - **Repository name**: `bazi-sdk`
   - **Description**: `八字计算 SDK - 提供完整的八字排盘、分析功能`
   - **Visibility**: 选择 `Public` 或 `Private`
   - **不要** 勾选 "Initialize this repository with a README"
3. 点击 **Create repository**

然后推送代码:

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

git remote set-url origin https://github.com/zhihao93li/bazi-sdk.git
git push -u origin main
```

---

## 🚨 问题 4: Maven 发布失败 - 401 Unauthorized

### 错误信息
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-deploy-plugin:3.0.0:deploy
Return code is: 401, ReasonPhrase: Unauthorized.
```

### 原因
`~/.m2/settings.xml` 中没有配置 GitHub 认证信息。

### 解决方案

**配置 Maven Settings**

创建或编辑 `~/.m2/settings.xml`:

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

**重要**:
- `<id>github</id>` 必须与 `pom.xml` 中的 `<distributionManagement>` 的 `<id>` 一致
- `<password>` 填写您的 GitHub Personal Access Token (不是密码!)

**验证配置**

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

# 测试发布
mvn clean deploy -DskipTests
```

---

## 🚨 问题 5: GitHub Actions 失败

### 错误信息
在 GitHub Actions 页面看到红色 ❌。

### 排查步骤

**1. 查看错误日志**

访问: https://github.com/zhihao93li/bazi-sdk/actions

- 点击失败的工作流
- 查看具体的错误信息

**2. 常见错误**

**编译失败**
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**解决**: 本地运行 `mvn clean compile` 检查错误,修复后重新推送。

**依赖下载失败**
```
[ERROR] Failed to collect dependencies
```

**解决**: 检查 `pom.xml` 中的依赖配置是否正确。

**发布权限问题**
```
[ERROR] status code: 403, Forbidden
```

**解决**: GitHub Actions 默认的 `GITHUB_TOKEN` 应该有足够权限。如果不行,在仓库设置中启用:
- Settings → Actions → General → Workflow permissions
- 选择 **Read and write permissions**

---

## 🚨 问题 6: 无法下载已发布的包

### 错误信息
```
Could not find artifact com.tafu:bazi-sdk-core:jar:1.0.0
```

### 原因
1. 包还没有发布成功
2. Maven 没有配置 GitHub Packages 仓库
3. 没有配置认证信息

### 解决方案

**1. 验证包是否已发布**

访问: https://github.com/zhihao93li/bazi-sdk/packages

确认看到:
- `bazi-sdk-core`
- `bazi-sdk-spring-boot-starter`

**2. 配置仓库和认证**

在使用 SDK 的项目中:

**pom.xml**:
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/zhihao93li/bazi-sdk</url>
    </repository>
</repositories>
```

**~/.m2/settings.xml**:
```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>zhihao93li</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**3. 测试下载**

```bash
mvn dependency:get -Dartifact=com.tafu:bazi-sdk-core:1.0.0
```

---

## 🚨 问题 7: Git 推送被拒绝 - rejected

### 错误信息
```
! [rejected]        main -> main (non-fast-forward)
error: failed to push some refs to 'https://github.com/zhihao93li/bazi-sdk.git'
```

### 原因
远程仓库有本地没有的提交(可能在 GitHub 网页上做了修改)。

### 解决方案

**方式 1: 合并远程更改 (推荐)**

```bash
git pull origin main --rebase
git push origin main
```

**方式 2: 强制推送 (谨慎使用!)**

```bash
# ⚠️  警告: 这会覆盖远程仓库的内容
git push origin main --force
```

---

## 📋 完整推送流程检查清单

按顺序执行以下步骤:

- [ ] 1. 生成 GitHub Personal Access Token (包含 `repo` 和 `write:packages` 权限)
- [ ] 2. 在 GitHub 创建仓库 `bazi-sdk`
- [ ] 3. 更新 `pom.xml` 中的用户名 (已完成 ✅)
- [ ] 4. 更新 Git remote URL (已完成 ✅)
- [ ] 5. 配置 Git 凭据 (使用 Token)
- [ ] 6. 提交更改: `git add . && git commit -m "chore: update config"`
- [ ] 7. 推送代码: `git push -u origin main`
- [ ] 8. 创建 Tag: `git tag v1.0.0 && git push origin v1.0.0`
- [ ] 9. 在 GitHub 创建 Release
- [ ] 10. 等待 GitHub Actions 完成发布

---

## 🔧 快速命令参考

```bash
# 1. 检查 Git 状态
git status
git remote -v

# 2. 更新 remote URL
git remote set-url origin https://github.com/zhihao93li/bazi-sdk.git

# 3. 配置凭据存储
git config credential.helper store

# 4. 推送代码
git push -u origin main

# 5. 创建和推送 Tag
git tag v1.0.0
git push origin v1.0.0

# 6. 本地测试发布
mvn clean deploy -DskipTests

# 7. 查看发布的包
open https://github.com/zhihao93li/bazi-sdk/packages
```

---

## 📞 获取帮助

如果以上方法都无法解决问题:

1. **查看详细错误日志**: 复制完整的错误信息
2. **检查 GitHub 状态**: https://www.githubstatus.com/
3. **搜索类似问题**: https://github.com/orgs/community/discussions
4. **提交 Issue**: https://github.com/zhihao93li/bazi-sdk/issues

---

**祝您顺利发布!** 🚀
