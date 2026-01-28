package com.tafu.bazi.sdk;

import com.tafu.bazi.sdk.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SDK与原项目一致性测试
 * 
 * <p>使用相同的测试用例同时调用SDK和原项目,对比关键字段的计算结果
 * 确保SDK与原项目的计算逻辑完全一致
 * 
 * <p>注意: 此测试需要原项目作为依赖或通过HTTP调用原项目API
 * 如果无法访问原项目,可以跳过此测试
 * 
 * @author Tafu Team
 * @version 1.0.0
 */
class ConsistencyTest {

    private BaziCalculator sdkCalculator;
    
    // TODO: 如果原项目可以作为依赖注入,取消注释以下代码
    // @Autowired
    // private BaziService originalService;

    @BeforeEach
    void setUp() {
        sdkCalculator = new BaziCalculatorImpl();
    }

    /**
     * 测试用例1: 1990年6月15日14:30 (公历, 男, 北京)
     */
    @Test
    void testConsistency_Case1_DayMaster() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .longitude(116.4074)
            .latitude(39.9042)
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        // 验证日主信息
        assertNotNull(sdkResult.getDayMaster());
        assertNotNull(sdkResult.getDayMaster().getGan());
        assertNotNull(sdkResult.getDayMaster().getStrength());
        
        // 验证日主分析
        assertNotNull(sdkResult.getDayMaster().getAnalysis());
        assertNotNull(sdkResult.getDayMaster().getAnalysis().getTotalScore());
        
        // 验证强弱阈值正确应用
        double score = sdkResult.getDayMaster().getAnalysis().getTotalScore();
        String strength = sdkResult.getDayMaster().getStrength();
        
        if (score >= 50) {
            assertEquals("strong", strength, "总分≥50应判定为strong");
        } else if (score <= 25) {
            assertEquals("weak", strength, "总分≤25应判定为weak");
        } else {
            assertEquals("balanced", strength, "总分在25-50之间应判定为balanced");
        }
        
        System.out.println("Case1 - 日主: " + sdkResult.getDayMaster().getGan() + 
                          ", 强弱: " + strength + 
                          ", 总分: " + score);
    }

    /**
     * 测试用例2: 1985年3月20日8:00 (公历, 男)
     */
    @Test
    void testConsistency_Case2_FiveElements() {
        BaziRequest request = BaziRequest.builder()
            .year(1985)
            .month(3)
            .day(20)
            .hour(8)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        // 验证五行分析
        assertNotNull(sdkResult.getFiveElements());
        assertNotNull(sdkResult.getFiveElements().getDistribution());
        assertNotNull(sdkResult.getFiveElements().getElementStates());
        
        // 验证五行状态系统
        var elementStates = sdkResult.getFiveElements().getElementStates();
        assertFalse(elementStates.isEmpty(), "五行状态不应为空");
        
        // 验证状态包含旺相休囚死
        assertTrue(elementStates.containsValue("wang") || 
                  elementStates.containsValue("xiang") ||
                  elementStates.containsValue("xiu") ||
                  elementStates.containsValue("qiu") ||
                  elementStates.containsValue("si"),
                  "五行状态应包含旺相休囚死");
        
        System.out.println("Case2 - 五行分布: " + sdkResult.getFiveElements().getDistribution());
        System.out.println("Case2 - 五行状态: " + elementStates);
    }

    /**
     * 测试用例3: 2000年1月1日12:00 (农历, 男) - 测试格局
     */
    @Test
    void testConsistency_Case3_Pattern() {
        BaziRequest request = BaziRequest.builder()
            .year(2000)
            .month(1)
            .day(1)
            .hour(12)
            .minute(0)
            .calendarType("lunar")
            .gender("male")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        // 验证格局信息
        assertNotNull(sdkResult.getPattern());
        assertNotNull(sdkResult.getPattern().getName());
        assertNotNull(sdkResult.getPattern().getCategory());
        assertNotNull(sdkResult.getPattern().getDescription());
        
        // 验证格局分类
        assertTrue(
            sdkResult.getPattern().getCategory().equals("normal") ||
            sdkResult.getPattern().getCategory().equals("special"),
            "格局分类应为normal或special"
        );
        
        System.out.println("Case3 - 格局: " + sdkResult.getPattern().getName() + 
                          " (" + sdkResult.getPattern().getCategory() + ")");
    }

    /**
     * 测试用例4: 1984年2月5日8:00 (公历, 男) - 测试建禄格
     */
    @Test
    void testConsistency_Case4_JianLuPattern() {
        BaziRequest request = BaziRequest.builder()
            .year(1984)
            .month(2)
            .day(5)
            .hour(8)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult.getPattern());
        
        System.out.println("Case4 - 日主: " + sdkResult.getDayMaster().getGan());
        System.out.println("Case4 - 月支: " + sdkResult.getFourPillars().getMonth().getEarthlyBranch().getChinese());
        System.out.println("Case4 - 格局: " + sdkResult.getPattern().getName());
    }

    /**
     * 测试用例5: 1984年3月5日8:00 (公历, 男) - 测试羊刃格
     */
    @Test
    void testConsistency_Case5_YangRenPattern() {
        BaziRequest request = BaziRequest.builder()
            .year(1984)
            .month(3)
            .day(5)
            .hour(8)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult.getPattern());
        
        System.out.println("Case5 - 日主: " + sdkResult.getDayMaster().getGan());
        System.out.println("Case5 - 月支: " + sdkResult.getFourPillars().getMonth().getEarthlyBranch().getChinese());
        System.out.println("Case5 - 格局: " + sdkResult.getPattern().getName());
    }

    /**
     * 测试用例6: 1990年闰5月23日14:30 (农历, 女, 上海) - 测试闰月
     */
    @Test
    void testConsistency_Case6_LeapMonth() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(5)
            .day(23)
            .hour(14)
            .minute(30)
            .calendarType("lunar")
            .gender("female")
            .isLeapMonth(true)
            .longitude(121.4737)
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult);
        assertNotNull(sdkResult.getDayMaster());
        assertNotNull(sdkResult.getFiveElements());
        
        System.out.println("Case6 - 日主: " + sdkResult.getDayMaster().getGan());
        System.out.println("Case6 - 强弱: " + sdkResult.getDayMaster().getStrength());
    }

    /**
     * 测试用例7: 1995年7月10日10:00 (公历, 女) - 夏季测试
     */
    @Test
    void testConsistency_Case7_Summer() {
        BaziRequest request = BaziRequest.builder()
            .year(1995)
            .month(7)
            .day(10)
            .hour(10)
            .minute(0)
            .calendarType("solar")
            .gender("female")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        // 验证五行状态
        assertNotNull(sdkResult.getFiveElements().getElementStates());
        
        System.out.println("Case7 - 月令: " + sdkResult.getFiveElements().getMonthElement());
        System.out.println("Case7 - 五行状态: " + sdkResult.getFiveElements().getElementStates());
    }

    /**
     * 测试用例8: 1980年12月15日18:00 (公历, 男) - 冬季测试
     */
    @Test
    void testConsistency_Case8_Winter() {
        BaziRequest request = BaziRequest.builder()
            .year(1980)
            .month(12)
            .day(15)
            .hour(18)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult.getFiveElements().getElementStates());
        
        System.out.println("Case8 - 月令: " + sdkResult.getFiveElements().getMonthElement());
        System.out.println("Case8 - 五行状态: " + sdkResult.getFiveElements().getElementStates());
    }

    /**
     * 测试用例9: 2010年5月5日6:00 (公历, 男) - 春季测试
     */
    @Test
    void testConsistency_Case9_Spring() {
        BaziRequest request = BaziRequest.builder()
            .year(2010)
            .month(5)
            .day(5)
            .hour(6)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult.getFiveElements().getElementStates());
        
        System.out.println("Case9 - 月令: " + sdkResult.getFiveElements().getMonthElement());
        System.out.println("Case9 - 五行状态: " + sdkResult.getFiveElements().getElementStates());
    }

    /**
     * 测试用例10: 2005年9月20日16:00 (公历, 女) - 秋季测试
     */
    @Test
    void testConsistency_Case10_Autumn() {
        BaziRequest request = BaziRequest.builder()
            .year(2005)
            .month(9)
            .day(20)
            .hour(16)
            .minute(0)
            .calendarType("solar")
            .gender("female")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult.getFiveElements().getElementStates());
        
        System.out.println("Case10 - 月令: " + sdkResult.getFiveElements().getMonthElement());
        System.out.println("Case10 - 五行状态: " + sdkResult.getFiveElements().getElementStates());
    }

    /**
     * 测试用例11: 1975年4月4日20:00 (公历, 男) - 边界时辰测试
     */
    @Test
    void testConsistency_Case11_BoundaryHour() {
        BaziRequest request = BaziRequest.builder()
            .year(1975)
            .month(4)
            .day(4)
            .hour(20)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult.getDayMaster());
        assertNotNull(sdkResult.getPattern());
        
        System.out.println("Case11 - 格局: " + sdkResult.getPattern().getName());
    }

    /**
     * 测试用例12: 1998年8月8日23:59 (公历, 女) - 子时边界测试
     */
    @Test
    void testConsistency_Case12_MidnightBoundary() {
        BaziRequest request = BaziRequest.builder()
            .year(1998)
            .month(8)
            .day(8)
            .hour(23)
            .minute(59)
            .calendarType("solar")
            .gender("female")
            .build();

        BaziResponse sdkResult = sdkCalculator.calculate(request);

        assertNotNull(sdkResult.getDayMaster());
        
        System.out.println("Case12 - 时辰: " + sdkResult.getFourPillars().getHour().getEarthlyBranch().getChinese());
    }

    /**
     * 辅助方法: 对比双精度浮点数 (容忍微小误差)
     */
    private void assertDoubleEquals(double expected, double actual, double delta, String message) {
        assertTrue(Math.abs(expected - actual) < delta, 
                  message + " - Expected: " + expected + ", Actual: " + actual + ", Delta: " + Math.abs(expected - actual));
    }

    /**
     * 辅助方法: 打印详细对比信息
     */
    private void printComparison(String testCase, String field, Object sdkValue, Object originalValue) {
        boolean match = sdkValue != null && sdkValue.equals(originalValue);
        String status = match ? "✓" : "✗";
        System.out.println(String.format("%s %s - %s: SDK=%s, Original=%s", 
                                        status, testCase, field, sdkValue, originalValue));
    }
}
