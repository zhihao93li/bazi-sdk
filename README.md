# Bazi SDK

八字计算 SDK,提供完整的八字排盘、日主分析、五行统计、十神计算、格局判断等功能。

## 项目结构

```
bazi-sdk/
├── bazi-sdk-core/                      # 核心计算模块 (纯 Java)
│   ├── src/main/java/com/tafu/bazi/sdk/
│   │   ├── BaziCalculator.java         # 核心接口
│   │   ├── BaziCalculatorImpl.java     # 核心实现
│   │   ├── model/                      # 数据模型 (DTO)
│   │   │   ├── BaziRequest.java
│   │   │   ├── BaziResponse.java
│   │   │   ├── BaziDef.java            # 常量定义
│   │   │   └── ... (15+ DTO classes)
│   │   └── utils/                      # 工具类
│   │       └── LunarUtils.java
│   └── pom.xml
│
├── bazi-sdk-spring-boot-starter/       # Spring Boot 集成模块
│   ├── src/main/java/com/tafu/bazi/sdk/spring/
│   │   └── autoconfigure/
│   │       ├── BaziAutoConfiguration.java
│   │       └── BaziProperties.java
│   ├── src/main/resources/META-INF/spring/
│   │   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│   └── pom.xml
│
├── pom.xml                             # 父 POM
└── README.md
```

## 快速开始

### 📦 发布方式

本 SDK 支持两种发布方式:

#### 方式 1: GitHub Packages (推荐)

通过 GitHub Packages 分发,支持远程依赖管理:

```bash
# 快速开始: 查看 5 分钟快速指南
cat QUICK_START.md

# 详细文档: 查看完整发布指南
cat GITHUB_PACKAGES_GUIDE.md
```

**快速发布步骤**:
1. 替换 `pom.xml` 中的 `YOUR_GITHUB_USERNAME`
2. 生成 GitHub Token (需要 `write:packages` 权限)
3. 推送代码到 GitHub
4. 创建 GitHub Release,自动发布! 🎉

**使用 SDK** (在其他项目):
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

> ⚠️ **注意**: 使用时需要在 `~/.m2/settings.xml` 中配置 GitHub Token 认证

---

#### 方式 2: 本地 Maven 仓库

适合本地开发和测试:

```bash
cd bazi-sdk
mvn clean install
```

**使用 SDK**:
```xml
<!-- 纯 Java 项目 -->
<dependency>
    <groupId>com.tafu</groupId>
    <artifactId>bazi-sdk-core</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Spring Boot 项目 -->
<dependency>
    <groupId>com.tafu</groupId>
    <artifactId>bazi-sdk-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 使用示例

### 纯 Java 使用

```java
import com.tafu.bazi.sdk.BaziCalculator;
import com.tafu.bazi.sdk.BaziCalculatorImpl;
import com.tafu.bazi.sdk.model.BaziRequest;
import com.tafu.bazi.sdk.model.BaziResponse;

public class Example {
    public static void main(String[] args) {
        // 创建计算器
        BaziCalculator calculator = new BaziCalculatorImpl();
        
        // 构建请求
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")      // 公历
            .gender("male")             // 男性
            .longitude(116.4074)        // 北京经度 (可选)
            .latitude(39.9042)          // 北京纬度 (可选)
            .build();
        
        // 执行计算
        BaziResponse response = calculator.calculate(request);
        
        // 使用结果
        System.out.println("公历日期: " + response.getSolarDate());
        System.out.println("农历日期: " + response.getLunarDate());
        System.out.println("日主: " + response.getDayMaster().getGan());
        System.out.println("日主强弱: " + response.getDayMaster().getStrength());
        System.out.println("格局: " + response.getPattern().getName());
    }
}
```

### Spring Boot 使用

```java
import com.tafu.bazi.sdk.BaziCalculator;
import com.tafu.bazi.sdk.model.BaziRequest;
import com.tafu.bazi.sdk.model.BaziResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaziService {
    
    @Autowired
    private BaziCalculator baziCalculator;
    
    public BaziResponse calculate(BaziRequest request) {
        return baziCalculator.calculate(request);
    }
}
```

### 农历日期示例

```java
// 农历日期 (闰五月)
BaziRequest request = BaziRequest.builder()
    .year(1990)
    .month(5)
    .day(23)
    .hour(14)
    .minute(30)
    .calendarType("lunar")      // 农历
    .gender("female")           // 女性
    .isLeapMonth(true)          // 闰月
    .longitude(121.4737)        // 上海经度
    .build();

BaziResponse response = calculator.calculate(request);
```

### 查询闰月

```java
// 查询 1990 年的闰月
int leapMonth = calculator.getLeapMonth(1990);
// 返回: 5 (表示闰五月, 0 表示无闰月)
```

## API 文档

### BaziRequest (输入参数)

| 字段 | 类型 | 必填 | 说明 | 范围/示例 |
|------|------|------|------|-----------|
| year | Integer | ✅ | 出生年份 | 1901-2100 |
| month | Integer | ✅ | 出生月份 | 1-12 (农历负数表示闰月, 如 -5) |
| day | Integer | ✅ | 出生日期 | 1-31 |
| hour | Integer | ✅ | 出生小时 | 0-23 |
| minute | Integer | ✅ | 出生分钟 | 0-59 |
| calendarType | String | ✅ | 历法类型 | "solar" (公历) / "lunar" (农历) |
| gender | String | ✅ | 性别 | "male" (男) / "female" (女) |
| isLeapMonth | Boolean | ❌ | 是否闰月 (仅农历) | true / false |
| longitude | Double | ❌ | 出生地经度 (真太阳时) | -180.0 ~ 180.0 (东经为正) |
| latitude | Double | ❌ | 出生地纬度 (预留) | -90.0 ~ 90.0 (北纬为正) |

### BaziResponse (输出结果)

| 字段 | 类型 | 说明 |
|------|------|------|
| gender | String | 性别 |
| solarDate | String | 公历日期时间 (格式: "1990-06-15 14:30:00") |
| lunarDate | String | 农历日期描述 (格式: "庚午年 壬午月 廿三 未时") |
| trueSolarTime | TrueSolarTimeDTO | 真太阳时信息 (如提供经度) |
| fourPillars | FourPillarsDTO | 四柱 (年月日时) |
| dayMaster | DayMasterDTO | 日主强弱分析 |
| fiveElements | FiveElementsDTO | 五行统计分析 |
| tenGods | TenGodsDTO | 十神分析 |
| pattern | PatternDTO | 格局判断 |
| yun | YunInfoDTO | 大运流年信息 |
| shenSha | ShenShaDTO | 神煞信息 |
| shengXiao | String | 生肖 (如 "马") |
| taiYuan | String | 胎元 (如 "癸酉") |
| mingGong | String | 命宫 (如 "甲戌") |
| shenGong | String | 身宫 (如 "丙子") |
| xunKong | String | 空亡 (如 "辰巳") |
| dayMasterCharacteristics | List<String> | 日主特征描述列表 |

详细的嵌套 DTO 结构说明,请参考源码或在线文档。

## 核心功能

### 1. 真太阳时计算
根据经度自动修正北京时间,公式: `时差(分钟) = (经度 - 120) * 4`

示例:
- 北京 (116.4°): 时差 = -14.4 分钟
- 上海 (121.5°): 时差 = +6 分钟
- 乌鲁木齐 (87.6°): 时差 = -129.6 分钟

### 2. 四柱计算
- 天干地支完整信息 (五行、阴阳)
- 纳音 (如 "路旁土")
- 藏干 (本气、中气、余气)
- 空亡 (如 "辰巳")
- 十神 (相对日主)

### 3. 日主强弱分析
- **得令**: 月令对日主的支持程度 (-20 ~ 40 分)
- **得地**: 地支藏干中有根 (0 ~ 30 分)
- **天干帮扶**: 天干对日主的帮扶 (-20 ~ 20 分)
- **总分**: 综合评分,判断强弱 (weak / balanced / strong)

### 4. 五行统计
- 五行分布权重
- 五行个数统计
- 最旺/最弱五行
- 喜用/忌讳五行
- 五行状态 (旺/相/休/囚/死)

### 5. 十神计算
统计四柱中各十神的出现次数和位置:
- 比肩、劫财
- 食神、伤官
- 偏财、正财
- 七杀、正官
- 偏印、正印

### 6. 格局判断
以月令本气十神为主判断格局,如:
- 正财格
- 正印格
- 七杀格
- ...

### 7. 大运流年
- 起运年龄
- 顺行/逆行
- 10 步大运 (每步 10 年)
- 每步大运包含 10 个流年

### 8. 神煞计算
使用 lunar-java 库计算各柱神煞,如:
- 桃花、驿马
- 天乙贵人、文昌
- ...

## 技术栈

- **编程语言**: Java 21
- **构建工具**: Maven 3.6+
- **核心依赖**: lunar-java 1.7.7+ (农历库)
- **Spring Boot**: 3.x (可选,仅 Starter 模块)
- **工具库**: Lombok, Jackson

## 支持范围

- **年份范围**: 1901-2100 (受 lunar-java 库限制)
- **历法**: 公历、农历 (支持闰月)
- **真太阳时**: 支持全球任意经纬度

## 配置项 (Spring Boot)

```yaml
bazi:
  cache:
    enabled: false  # 是否启用缓存 (当前版本未实现)
```

## 注意事项

1. **年份范围**: 仅支持 1901-2100 年,超出范围会抛出 `IllegalArgumentException`
2. **真太阳时**: 如果不提供经度,则不进行真太阳时修正,直接使用输入时间
3. **农历闰月**: 使用 `isLeapMonth=true` 标记闰月,或传入负数月份 (如 -5 表示闰五月)
4. **线程安全**: `BaziCalculatorImpl` 是无状态的,可安全用于多线程环境

## 开发计划

- [x] 核心计算逻辑
- [x] Spring Boot 自动配置
- [x] Maven 本地仓库发布
- [x] GitHub Packages 自动发布
- [x] GitHub Actions CI/CD
- [ ] 单元测试 (已有基础测试)
- [ ] 缓存功能
- [ ] 性能优化
- [ ] 扩展农历库年份范围

## 相关文档

- 📖 [QUICK_START.md](./QUICK_START.md) - 5 分钟快速开始指南
- 📖 [GITHUB_PACKAGES_GUIDE.md](./GITHUB_PACKAGES_GUIDE.md) - GitHub Packages 完整发布指南
- 📖 [INSTALLATION.md](./INSTALLATION.md) - 本地安装详细说明

## License

MIT License

Copyright © 2026 Tafu Team. All rights reserved.
