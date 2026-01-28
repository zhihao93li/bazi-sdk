# Bazi SDK 安装指南

## 1. 构建和安装 SDK

在 `bazi-sdk` 目录下执行以下命令:

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk
mvn clean install
```

此命令会:
- 编译所有模块
- 运行测试用例
- 将 SDK 安装到本地 Maven 仓库 (`~/.m2/repository`)

## 2. 在其他项目中使用

### 2.1 纯 Java 项目

在项目的 `pom.xml` 中添加依赖:

```xml
<dependency>
    <groupId>com.tafu</groupId>
    <artifactId>bazi-sdk-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

使用示例:

```java
import com.tafu.bazi.sdk.BaziCalculator;
import com.tafu.bazi.sdk.BaziCalculatorImpl;
import com.tafu.bazi.sdk.model.BaziRequest;
import com.tafu.bazi.sdk.model.BaziResponse;

public class Main {
    public static void main(String[] args) {
        BaziCalculator calculator = new BaziCalculatorImpl();
        
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
        
        BaziResponse response = calculator.calculate(request);
        System.out.println("日主: " + response.getDayMaster().getGan());
    }
}
```

### 2.2 Spring Boot 项目

在项目的 `pom.xml` 中添加依赖:

```xml
<dependency>
    <groupId>com.tafu</groupId>
    <artifactId>bazi-sdk-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

使用示例:

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

### 2.3 在现有 tafu 项目中使用

在 `tafu/pom.xml` 中添加依赖:

```xml
<dependency>
    <groupId>com.tafu</groupId>
    <artifactId>bazi-sdk-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

然后可以重构现有的 `BaziServiceImpl` 使用 SDK:

```java
@Service
@RequiredArgsConstructor
public class BaziServiceImpl implements BaziService {
    
    private final BaziCalculator baziCalculator;  // 注入 SDK
    private final BaziMapper baziMapper;
    
    @Override
    public BaziResponse calculate(BaziCalculateRequest request) {
        // 转换请求格式
        com.tafu.bazi.sdk.model.BaziRequest sdkRequest = 
            com.tafu.bazi.sdk.model.BaziRequest.builder()
                .year(request.getYear())
                .month(request.getMonth())
                .day(request.getDay())
                .hour(request.getHour())
                .minute(request.getMinute())
                .calendarType(request.getCalendarType())
                .gender(request.getGender())
                .isLeapMonth(request.isLeapMonth())
                .longitude(LunarUtils.getLongitude(request.getLocation()))
                .build();
        
        // 调用 SDK
        com.tafu.bazi.sdk.model.BaziResponse sdkResponse = 
            baziCalculator.calculate(sdkRequest);
        
        // 转换响应格式 (如果需要)
        return baziMapper.mapToBaziResponse(sdkResponse);
    }
}
```

## 3. 验证安装

运行测试用例验证 SDK 功能:

```bash
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk
mvn test
```

## 4. 常见问题

### 4.1 编译错误: Java 版本不匹配

确保使用 Java 21:

```bash
java -version
# 应显示: openjdk version "21.x.x"
```

如果版本不对,设置 JAVA_HOME:

```bash
export JAVA_HOME=/path/to/jdk-21
```

### 4.2 找不到依赖

确保已安装到本地仓库:

```bash
ls ~/.m2/repository/com/tafu/bazi-sdk-core/1.0.0/
# 应包含: bazi-sdk-core-1.0.0.jar
```

### 4.3 Spring Boot 自动配置未生效

检查是否正确引入了 starter:

```bash
mvn dependency:tree | grep bazi-sdk
```

确保 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件存在。

## 5. 更新 SDK

修改代码后,重新安装:

```bash
mvn clean install
```

其他依赖此 SDK 的项目会自动使用新版本 (如果版本号未变)。

## 6. 发布到远程仓库 (可选)

如果需要发布到公司内部 Maven 仓库:

1. 配置 `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>company-releases</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
</servers>
```

2. 在 `bazi-sdk/pom.xml` 中添加:

```xml
<distributionManagement>
    <repository>
        <id>company-releases</id>
        <url>https://maven.company.com/releases</url>
    </repository>
</distributionManagement>
```

3. 执行发布:

```bash
mvn clean deploy
```

## 7. 目录结构说明

```
bazi-sdk/
├── bazi-sdk-core/                           # 核心模块
│   ├── src/main/java/com/tafu/bazi/sdk/
│   │   ├── BaziCalculator.java              # 主接口
│   │   ├── BaziCalculatorImpl.java          # 实现类
│   │   ├── model/                           # 数据模型
│   │   │   ├── BaziRequest.java             # 输入 DTO
│   │   │   ├── BaziResponse.java            # 输出 DTO
│   │   │   ├── BaziDef.java                 # 常量定义
│   │   │   ├── FourPillarsDTO.java          # 四柱
│   │   │   ├── PillarDTO.java               # 单柱
│   │   │   ├── HeavenlyStemDTO.java         # 天干
│   │   │   ├── EarthlyBranchDTO.java        # 地支
│   │   │   ├── HiddenStemDTO.java           # 藏干
│   │   │   ├── TrueSolarTimeDTO.java        # 真太阳时
│   │   │   ├── DayMasterDTO.java            # 日主
│   │   │   ├── DayMasterAnalysisDTO.java    # 日主分析
│   │   │   ├── FiveElementsDTO.java         # 五行
│   │   │   ├── TenGodsDTO.java              # 十神
│   │   │   ├── TenGodInfoDTO.java           # 十神详情
│   │   │   ├── PatternDTO.java              # 格局
│   │   │   ├── YunInfoDTO.java              # 大运
│   │   │   ├── DaYunDTO.java                # 大运详情
│   │   │   ├── LiuNianDTO.java              # 流年
│   │   │   └── ShenShaDTO.java              # 神煞
│   │   └── utils/
│   │       └── LunarUtils.java              # 农历工具
│   ├── src/test/java/com/tafu/bazi/sdk/
│   │   └── BaziCalculatorTest.java          # 测试类
│   └── pom.xml
│
├── bazi-sdk-spring-boot-starter/            # Spring Boot 集成
│   ├── src/main/java/com/tafu/bazi/sdk/spring/
│   │   └── autoconfigure/
│   │       ├── BaziAutoConfiguration.java   # 自动配置
│   │       └── BaziProperties.java          # 配置属性
│   ├── src/main/resources/META-INF/spring/
│   │   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│   └── pom.xml
│
├── pom.xml                                  # 父 POM
├── README.md                                # 使用文档
└── INSTALLATION.md                          # 本文档
```

## 8. 下一步

- ✅ SDK 基本功能已完成
- ✅ 单元测试已编写
- ⏳ 性能优化 (缓存机制)
- ⏳ 扩展农历库年份范围
- ⏳ 发布到公司 Maven 仓库
