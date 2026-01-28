# 快速开始指南 ⚡

> 5 分钟完成从发布到使用!

---

## 第一步: 准备工作 (2 分钟)

### 1. 替换 GitHub 用户名

在 `pom.xml` 中搜索并替换 `YOUR_GITHUB_USERNAME` 为你的实际 GitHub 用户名:

```bash
# macOS/Linux
sed -i '' 's/YOUR_GITHUB_USERNAME/your-actual-username/g' pom.xml

# 或手动编辑 pom.xml
```

### 2. 生成 GitHub Token

访问: https://github.com/settings/tokens/new

- Note: `Bazi SDK`
- Scopes: ✅ `write:packages` ✅ `read:packages`
- 点击 **Generate token**
- **复制并保存 Token** (只显示一次!)

---

## 第二步: 发布到 GitHub (2 分钟)

### 选项 A: 自动发布 (推荐)

```bash
# 1. 初始化 Git 仓库
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk
git init
git add .
git commit -m "Initial commit: Bazi SDK v1.0.0"

# 2. 推送到 GitHub
git branch -M main
git remote add origin https://github.com/zhihao93li/bazi-sdk.git
git push -u origin main

# 3. 创建 Release (在 GitHub 网页)
# 访问: https://github.com/bazi-sdk/bazi-sdk/releases/new
# - Tag: v1.0.0
# - Title: v1.0.0 - Initial Release
# - 点击 Publish release

# GitHub Actions 会自动发布! 🎉
```

### 选项 B: 本地发布

```bash
# 1. 配置 Maven Settings
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << 'EOF'
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
EOF

# 替换用户名和 Token!

# 2. 推送代码到 GitHub
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/bazi-sdk.git
git push -u origin main

# 3. 发布
mvn clean deploy
```

---

## 第三步: 使用 SDK (1 分钟)

### 1. 在其他项目配置认证

编辑 `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

### 2. 添加依赖

在目标项目的 `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/YOUR_USERNAME/bazi-sdk</url>
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

### 3. 开始使用!

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

## 验证发布成功

访问: `https://github.com/YOUR_USERNAME/bazi-sdk/packages`

应该看到:
- ✅ `bazi-sdk-core`
- ✅ `bazi-sdk-spring-boot-starter`

---

## 常见问题

**Q: 发布失败 401 错误?**
A: 检查 `~/.m2/settings.xml` 中的 Token 是否正确

**Q: 依赖下载 404?**
A: 确认 GitHub 用户名正确,且配置了认证

**Q: 如何更新版本?**
A: 创建新的 Git Tag 和 Release,GitHub Actions 会自动发布

---

## 下一步

📖 查看完整文档: [GITHUB_PACKAGES_GUIDE.md](./GITHUB_PACKAGES_GUIDE.md)

🚀 开始使用: [README.md](./README.md)

---

**祝您使用愉快!** 🎉
