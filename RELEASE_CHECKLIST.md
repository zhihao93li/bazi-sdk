# 发布检查清单 ✅

> 在发布新版本前,请确认以下所有事项已完成!

---

## 首次发布准备

### 1. GitHub 仓库配置

- [ ] 在 GitHub 创建仓库 `bazi-sdk`
- [ ] 设置仓库可见性 (公开/私有)
- [ ] 添加仓库描述: "八字计算 SDK - 提供完整的八字排盘、分析功能"
- [ ] 添加 Topics: `java`, `maven`, `bazi`, `sdk`, `chinese-astrology`

### 2. POM 文件配置

- [ ] 替换 `pom.xml` 中的 `YOUR_GITHUB_USERNAME` 为实际用户名
- [ ] 替换 `your-email@example.com` 为实际邮箱
- [ ] 确认 `<version>` 版本号正确
- [ ] 确认 `<url>` 指向正确的 GitHub 仓库
- [ ] 确认 `<scm>` 配置正确

### 3. GitHub Token

- [ ] 生成 Personal Access Token
  - 访问: https://github.com/settings/tokens/new
  - Scopes: `write:packages`, `read:packages`
- [ ] 配置 `~/.m2/settings.xml` (本地发布时需要)
- [ ] GitHub Actions 会自动使用 `GITHUB_TOKEN` (无需额外配置)

### 4. 代码提交

- [ ] 所有代码已提交到 Git
- [ ] 无未跟踪的文件 (除 `.gitignore` 中列出的)
- [ ] 代码已推送到 GitHub `main` 分支

---

## 每次发布检查

### 1. 版本管理

- [ ] 更新 `pom.xml` 中的 `<version>` (如 `1.0.1`)
- [ ] 遵循语义化版本规范:
  - MAJOR: 不兼容的 API 变更
  - MINOR: 向后兼容的功能新增
  - PATCH: 向后兼容的问题修复

### 2. 代码质量

- [ ] 所有代码编译通过: `mvn clean compile`
- [ ] 所有测试通过: `mvn test`
- [ ] 无明显的代码质量问题
- [ ] JavaDoc 注释完整 (至少公共 API 有注释)

### 3. 文档更新

- [ ] 更新 `README.md` (如有新功能)
- [ ] 更新 `CHANGELOG.md` (记录本次更新内容)
- [ ] 更新示例代码 (如 API 有变化)
- [ ] 确认所有文档中的版本号已更新

### 4. Git 操作

- [ ] 提交所有更改:
  ```bash
  git add .
  git commit -m "chore: bump version to x.x.x"
  ```

- [ ] 推送到 GitHub:
  ```bash
  git push origin main
  ```

- [ ] 创建 Git Tag:
  ```bash
  git tag vx.x.x
  git push origin vx.x.x
  ```

### 5. 发布方式选择

#### 选项 A: GitHub Actions 自动发布 (推荐)

- [ ] 在 GitHub 创建 Release:
  - 访问: `https://github.com/YOUR_USERNAME/bazi-sdk/releases/new`
  - Tag version: `vx.x.x`
  - Release title: `vx.x.x - 简短描述`
  - Description: 详细更新说明
  - 点击 **Publish release**

- [ ] 等待 GitHub Actions 完成:
  - 访问: `https://github.com/YOUR_USERNAME/bazi-sdk/actions`
  - 确认 **Publish to GitHub Packages** 工作流成功

#### 选项 B: 本地手动发布

- [ ] 确认 `~/.m2/settings.xml` 已配置 GitHub 认证
- [ ] 执行发布命令:
  ```bash
  mvn clean deploy
  ```
- [ ] 确认发布成功 (无错误信息)

### 6. 发布验证

- [ ] 访问 GitHub Packages 页面:
  ```
  https://github.com/YOUR_USERNAME/bazi-sdk/packages
  ```

- [ ] 确认新版本已出现:
  - `bazi-sdk-core` - 版本 `x.x.x`
  - `bazi-sdk-spring-boot-starter` - 版本 `x.x.x`

- [ ] 检查包详情:
  - 版本号正确
  - 包含源码 jar (`-sources.jar`)
  - 包含文档 jar (`-javadoc.jar`)

### 7. 功能测试

- [ ] 在测试项目中使用新版本:
  ```xml
  <dependency>
      <groupId>com.tafu</groupId>
      <artifactId>bazi-sdk-spring-boot-starter</artifactId>
      <version>x.x.x</version>
  </dependency>
  ```

- [ ] 执行 `mvn clean install` 确认依赖下载成功
- [ ] 运行基本功能测试,确认 SDK 工作正常

### 8. 文档同步

- [ ] 在 GitHub Release 中添加详细说明
- [ ] 更新项目 Wiki (如有)
- [ ] 通知团队成员新版本发布 (如适用)

---

## 常见问题排查

### 发布失败

**401 Unauthorized**
- 检查 GitHub Token 是否过期
- 检查 Token 权限是否包含 `write:packages`
- 检查 `~/.m2/settings.xml` 中的配置

**409 Conflict**
- 版本号已存在,无法覆盖
- 更新版本号后重新发布

**Compilation Error**
- 运行 `mvn clean compile` 检查编译错误
- 修复错误后重新发布

### 依赖下载失败

**404 Not Found**
- 检查仓库 URL 是否正确
- 检查版本号是否存在
- 检查 GitHub 用户名拼写

**401 Unauthorized**
- 用户需要配置 GitHub Token 认证
- 检查 `~/.m2/settings.xml` 配置

---

## 发布后操作

- [ ] 通知相关项目更新依赖版本
- [ ] 在社交媒体/博客宣布新版本 (如适用)
- [ ] 关闭相关的 Issues 和 Pull Requests
- [ ] 规划下一个版本的功能

---

## 快速命令

```bash
# 完整发布流程 (自动发布)
git add .
git commit -m "chore: bump version to 1.0.1"
git push origin main
git tag v1.0.1
git push origin v1.0.1
# 然后在 GitHub 创建 Release

# 本地发布
mvn versions:set -DnewVersion=1.0.1
mvn versions:commit
git add pom.xml */pom.xml
git commit -m "chore: bump version to 1.0.1"
git push
mvn clean deploy

# 验证
curl -H "Authorization: token YOUR_TOKEN" \
  https://maven.pkg.github.com/YOUR_USERNAME/bazi-sdk/com/tafu/bazi-sdk-core/1.0.1/
```

---

**祝发布顺利!** 🚀
