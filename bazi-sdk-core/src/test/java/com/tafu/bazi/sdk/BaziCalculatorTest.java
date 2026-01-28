package com.tafu.bazi.sdk;

import com.tafu.bazi.sdk.model.BaziRequest;
import com.tafu.bazi.sdk.model.BaziResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaziCalculator 测试类
 * 
 * @author Tafu Team
 * @version 1.0.0
 */
class BaziCalculatorTest {

    private BaziCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new BaziCalculatorImpl();
    }

    @Test
    void testCalculate_Solar_WithLongitude() {
        // 公历日期 + 真太阳时修正
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .longitude(116.4074)  // 北京经度
            .latitude(39.9042)    // 北京纬度
            .build();

        BaziResponse response = calculator.calculate(request);

        assertNotNull(response);
        assertEquals("male", response.getGender());
        assertNotNull(response.getSolarDate());
        assertNotNull(response.getLunarDate());
        assertNotNull(response.getTrueSolarTime());
        assertNotNull(response.getFourPillars());
        assertNotNull(response.getDayMaster());
        assertNotNull(response.getFiveElements());
        assertNotNull(response.getTenGods());
        assertNotNull(response.getPattern());
        assertNotNull(response.getYun());
        assertNotNull(response.getShenSha());
        
        System.out.println("公历日期: " + response.getSolarDate());
        System.out.println("农历日期: " + response.getLunarDate());
        System.out.println("日主: " + response.getDayMaster().getGan());
        System.out.println("日主强弱: " + response.getDayMaster().getStrength());
        System.out.println("格局: " + response.getPattern().getName());
    }

    @Test
    void testCalculate_Solar_WithoutLongitude() {
        // 公历日期 (不进行真太阳时修正)
        BaziRequest request = BaziRequest.builder()
            .year(1985)
            .month(3)
            .day(20)
            .hour(8)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse response = calculator.calculate(request);

        assertNotNull(response);
        assertNull(response.getTrueSolarTime()); // 未提供经度,不进行修正
        assertNotNull(response.getFourPillars());
    }

    @Test
    void testCalculate_Lunar_WithLeapMonth() {
        // 农历日期 (闰五月)
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(5)
            .day(23)
            .hour(14)
            .minute(30)
            .calendarType("lunar")
            .gender("female")
            .isLeapMonth(true)
            .longitude(121.4737)  // 上海经度
            .build();

        BaziResponse response = calculator.calculate(request);

        assertNotNull(response);
        assertEquals("female", response.getGender());
        assertNotNull(response.getTrueSolarTime());
    }

    @Test
    void testCalculate_Lunar_Normal() {
        // 农历日期 (正常月份)
        BaziRequest request = BaziRequest.builder()
            .year(2000)
            .month(1)
            .day(1)
            .hour(12)
            .minute(0)
            .calendarType("lunar")
            .gender("male")
            .build();

        BaziResponse response = calculator.calculate(request);

        assertNotNull(response);
        assertNotNull(response.getFourPillars());
        assertNotNull(response.getDayMaster());
    }

    @Test
    void testGetLeapMonth() {
        // 1990 年闰五月
        int leapMonth = calculator.getLeapMonth(1990);
        assertEquals(5, leapMonth);

        // 2023 年闰二月
        leapMonth = calculator.getLeapMonth(2023);
        assertEquals(2, leapMonth);

        // 2024 年无闰月
        leapMonth = calculator.getLeapMonth(2024);
        assertEquals(0, leapMonth);
    }

    @Test
    void testValidation_NullRequest() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(null);
        });
    }

    @Test
    void testValidation_InvalidYear() {
        BaziRequest request = BaziRequest.builder()
            .year(1800)  // 超出范围
            .month(1)
            .day(1)
            .hour(12)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .build();

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(request);
        });
    }

    @Test
    void testValidation_InvalidCalendarType() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(1)
            .day(1)
            .hour(12)
            .minute(0)
            .calendarType("invalid")  // 无效值
            .gender("male")
            .build();

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(request);
        });
    }

    @Test
    void testValidation_InvalidGender() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(1)
            .day(1)
            .hour(12)
            .minute(0)
            .calendarType("solar")
            .gender("unknown")  // 无效值
            .build();

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(request);
        });
    }

    @Test
    void testValidation_InvalidLongitude() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(1)
            .day(1)
            .hour(12)
            .minute(0)
            .calendarType("solar")
            .gender("male")
            .longitude(200.0)  // 超出范围
            .build();

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(request);
        });
    }

    @Test
    void testFourPillarsContent() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse response = calculator.calculate(request);

        // 验证四柱结构
        assertNotNull(response.getFourPillars().getYear());
        assertNotNull(response.getFourPillars().getMonth());
        assertNotNull(response.getFourPillars().getDay());
        assertNotNull(response.getFourPillars().getHour());

        // 验证年柱内容
        assertNotNull(response.getFourPillars().getYear().getHeavenlyStem());
        assertNotNull(response.getFourPillars().getYear().getEarthlyBranch());
        assertNotNull(response.getFourPillars().getYear().getNaYin());
        assertNotNull(response.getFourPillars().getYear().getHiddenStems());
        assertNotNull(response.getFourPillars().getYear().getXunKong());
        assertNotNull(response.getFourPillars().getYear().getTenGod());

        // 验证藏干不为空
        assertFalse(response.getFourPillars().getYear().getHiddenStems().isEmpty());
    }

    @Test
    void testDayMasterAnalysis() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse response = calculator.calculate(request);

        // 验证日主分析
        assertNotNull(response.getDayMaster().getGan());
        assertNotNull(response.getDayMaster().getStrength());
        assertTrue(
            response.getDayMaster().getStrength().equals("weak") ||
            response.getDayMaster().getStrength().equals("balanced") ||
            response.getDayMaster().getStrength().equals("strong")
        );

        // 验证日主分析详情
        assertNotNull(response.getDayMaster().getAnalysis());
        assertNotNull(response.getDayMaster().getAnalysis().getDeLingDesc());
        assertNotNull(response.getDayMaster().getAnalysis().getDeDiDesc());
        assertNotNull(response.getDayMaster().getAnalysis().getTianGanHelpDesc());
        
        double totalScore = response.getDayMaster().getAnalysis().getTotalScore();
        assertTrue(totalScore >= -50 && totalScore <= 100); // 合理范围
    }

    @Test
    void testFiveElementsAnalysis() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse response = calculator.calculate(request);

        // 验证五行分析
        assertNotNull(response.getFiveElements().getDistribution());
        assertNotNull(response.getFiveElements().getCounts());
        assertNotNull(response.getFiveElements().getStrongest());
        assertNotNull(response.getFiveElements().getWeakest());
        assertNotNull(response.getFiveElements().getFavorable());
        assertNotNull(response.getFiveElements().getUnfavorable());
        assertNotNull(response.getFiveElements().getElementStates());
        assertNotNull(response.getFiveElements().getMonthElement());

        // 验证五行分布包含所有五行
        assertEquals(5, response.getFiveElements().getDistribution().size());
        assertEquals(5, response.getFiveElements().getCounts().size());
    }

    @Test
    void testYunCalculation() {
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse response = calculator.calculate(request);

        // 验证大运信息
        assertNotNull(response.getYun());
        assertTrue(response.getYun().getStartAge() >= 0);
        assertNotNull(response.getYun().getDaYunList());
        assertFalse(response.getYun().getDaYunList().isEmpty());

        // 验证第一步大运
        var firstDaYun = response.getYun().getDaYunList().get(0);
        assertNotNull(firstDaYun.getGanZhi());
        assertNotNull(firstDaYun.getGan());
        assertNotNull(firstDaYun.getZhi());
        assertTrue(firstDaYun.getStartAge() >= 0);
        assertTrue(firstDaYun.getEndAge() > firstDaYun.getStartAge());
        assertNotNull(firstDaYun.getLiuNian());
        assertFalse(firstDaYun.getLiuNian().isEmpty());
    }
}
