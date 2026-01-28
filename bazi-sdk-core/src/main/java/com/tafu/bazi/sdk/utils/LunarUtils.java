package com.tafu.bazi.sdk.utils;

import com.nlf.calendar.EightChar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import com.tafu.bazi.sdk.model.BaziDef;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 农历工具类
 * 提供农历相关的工具方法
 * 
 * @author Tafu Team
 * @version 1.0.0
 */
public class LunarUtils {

    /**
     * 根据公历日期创建 Solar 对象
     */
    public static Solar createSolar(int year, int month, int day, int hour, int minute) {
        return Solar.fromYmdHms(year, month, day, hour, minute, 0);
    }

    /**
     * 根据农历日期创建 Lunar 对象
     * 
     * @param year 农历年
     * @param month 农历月 (正数表示正常月份, 负数表示闰月)
     * @param day 农历日
     * @param hour 时
     * @param minute 分
     * @param isLeapMonth 是否闰月
     * @return Lunar 对象
     */
    public static Lunar createLunar(int year, int month, int day, int hour, int minute, boolean isLeapMonth) {
        // lunar-java 使用负数表示闰月
        int actualMonth = isLeapMonth ? -Math.abs(month) : month;
        return Lunar.fromYmdHms(year, actualMonth, day, hour, minute, 0);
    }

    /**
     * 计算真太阳时
     * 根据经度修正北京时间 (东经120度为基准)
     * 
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 时
     * @param minute 分
     * @param longitude 经度 (东经为正, 西经为负)
     * @return 修正后的 Solar 对象
     */
    public static Solar getTrueSolarTime(int year, int month, int day, int hour, int minute, double longitude) {
        // 计算时差: (经度 - 120) * 4 分钟
        double timeDiff = (longitude - 120.0) * 4.0;
        
        // 创建 Calendar 进行时间计算
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // 加上时差
        cal.add(Calendar.MINUTE, (int) Math.round(timeDiff));
        
        // 返回修正后的 Solar 对象
        return Solar.fromYmdHms(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            0
        );
    }

    /**
     * 从八字对象中提取干支字符串
     * 
     * @param eightChar 八字对象
     * @param pillarType 柱类型: "year" | "month" | "day" | "hour"
     * @return 干支字符串 (如 "甲子")
     */
    public static String extractGanZhi(EightChar eightChar, String pillarType) {
        return switch (pillarType) {
            case "year" -> eightChar.getYear();
            case "month" -> eightChar.getMonth();
            case "day" -> eightChar.getDay();
            case "hour" -> eightChar.getTime();
            default -> throw new IllegalArgumentException("Invalid pillar type: " + pillarType);
        };
    }

    /**
     * 从干支字符串中提取天干
     * 
     * @param ganZhi 干支字符串 (如 "甲子")
     * @return 天干 (如 "甲")
     */
    public static String extractGan(String ganZhi) {
        if (ganZhi == null || ganZhi.length() < 2) {
            throw new IllegalArgumentException("Invalid ganZhi: " + ganZhi);
        }
        return ganZhi.substring(0, 1);
    }

    /**
     * 从干支字符串中提取地支
     * 
     * @param ganZhi 干支字符串 (如 "甲子")
     * @return 地支 (如 "子")
     */
    public static String extractZhi(String ganZhi) {
        if (ganZhi == null || ganZhi.length() < 2) {
            throw new IllegalArgumentException("Invalid ganZhi: " + ganZhi);
        }
        return ganZhi.substring(1, 2);
    }

    /**
     * 获取指定年份的闰月
     * 
     * @param lunarYear 农历年份 (1900-2100)
     * @return 闰月月份 (0 表示无闰月, 1-12 表示闰几月)
     */
    public static int getLeapMonth(int lunarYear) {
        try {
            // 使用农历正月初一来获取该年的闰月信息
            Lunar lunar = Lunar.fromYmd(lunarYear, 1, 1);
            return lunar.getLeapMonth();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid lunar year: " + lunarYear, e);
        }
    }

    /**
     * 获取地支的藏干列表
     * 
     * @param zhi 地支
     * @return 藏干列表
     */
    public static List<String> getHiddenStems(String zhi) {
        List<String> stems = BaziDef.DI_ZHI_HIDDEN_STEMS.get(zhi);
        return stems != null ? new ArrayList<>(stems) : new ArrayList<>();
    }

    /**
     * 获取地支藏干的权重
     * 
     * @param zhi 地支
     * @param gan 天干
     * @return 权重值
     */
    public static double getHiddenStemWeight(String zhi, String gan) {
        var weightMap = BaziDef.DI_ZHI_HIDDEN_STEMS_WEIGHT.get(zhi);
        if (weightMap != null && weightMap.containsKey(gan)) {
            return weightMap.get(gan);
        }
        return 0.0;
    }

    /**
     * 计算空亡
     * 
     * @param ganZhi 日柱干支
     * @return 空亡地支 (如 "辰巳")
     */
    public static String calculateXunKong(String ganZhi) {
        if (ganZhi == null || ganZhi.length() < 2) {
            return "";
        }
        
        String gan = extractGan(ganZhi);
        String zhi = extractZhi(ganZhi);
        
        int ganIndex = BaziDef.getTianGanIndex(gan);
        int zhiIndex = BaziDef.getDiZhiIndex(zhi);
        
        if (ganIndex == -1 || zhiIndex == -1) {
            return "";
        }
        
        // 计算旬首 (天干索引 - 地支索引 + 12) % 12
        int xunShou = (ganIndex - zhiIndex + 12) % 12;
        
        // 空亡地支为旬首前两位
        int kong1 = (xunShou + 10) % 12;
        int kong2 = (xunShou + 11) % 12;
        
        return BaziDef.DI_ZHI[kong1] + BaziDef.DI_ZHI[kong2];
    }

    /**
     * 获取纳音
     * 
     * @param ganZhi 干支字符串
     * @return 纳音名称
     */
    public static String getNaYin(String ganZhi) {
        if (ganZhi == null || ganZhi.length() < 2) {
            return "";
        }
        
        // 纳音对照表 (60甲子)
        String[] naYinTable = {
            "海中金", "炉中火", "大林木", "路旁土", "剑锋金", "山头火", "涧下水", "城头土", "白蜡金", "杨柳木",
            "泉中水", "屋上土", "霹雳火", "松柏木", "长流水", "沙中金", "山下火", "平地木", "壁上土", "金箔金",
            "覆灯火", "天河水", "大驿土", "钗钏金", "桑柘木", "大溪水", "沙中土", "天上火", "石榴木", "大海水",
            "海中金", "炉中火", "大林木", "路旁土", "剑锋金", "山头火", "涧下水", "城头土", "白蜡金", "杨柳木",
            "泉中水", "屋上土", "霹雳火", "松柏木", "长流水", "沙中金", "山下火", "平地木", "壁上土", "金箔金",
            "覆灯火", "天河水", "大驿土", "钗钏金", "桑柘木", "大溪水", "沙中土", "天上火", "石榴木", "大海水"
        };
        
        String gan = extractGan(ganZhi);
        String zhi = extractZhi(ganZhi);
        
        int ganIndex = BaziDef.getTianGanIndex(gan);
        int zhiIndex = BaziDef.getDiZhiIndex(zhi);
        
        if (ganIndex == -1 || zhiIndex == -1) {
            return "";
        }
        
        // 计算60甲子序号: 天干序号*6 + 地支序号/2
        int index = (ganIndex * 6 + zhiIndex / 2) % 60;
        
        return naYinTable[index];
    }

    /**
     * 获取生肖
     * 
     * @param yearZhi 年柱地支
     * @return 生肖名称
     */
    public static String getShengXiao(String yearZhi) {
        String[] shengXiao = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        int index = BaziDef.getDiZhiIndex(yearZhi);
        return index >= 0 ? shengXiao[index] : "";
    }

    /**
     * 计算胎元
     * 
     * @param monthGanZhi 月柱干支
     * @return 胎元干支
     */
    public static String calculateTaiYuan(String monthGanZhi) {
        if (monthGanZhi == null || monthGanZhi.length() < 2) {
            return "";
        }
        
        String gan = extractGan(monthGanZhi);
        String zhi = extractZhi(monthGanZhi);
        
        int ganIndex = BaziDef.getTianGanIndex(gan);
        int zhiIndex = BaziDef.getDiZhiIndex(zhi);
        
        if (ganIndex == -1 || zhiIndex == -1) {
            return "";
        }
        
        // 胎元 = 月干后一位 + 月支后三位
        int taiYuanGanIndex = (ganIndex + 1) % 10;
        int taiYuanZhiIndex = (zhiIndex + 3) % 12;
        
        return BaziDef.TIAN_GAN[taiYuanGanIndex] + BaziDef.DI_ZHI[taiYuanZhiIndex];
    }

    /**
     * 计算命宫
     * 
     * @param monthZhi 月支
     * @param hourZhi 时支
     * @return 命宫干支
     */
    public static String calculateMingGong(String monthZhi, String hourZhi) {
        int monthIndex = BaziDef.getDiZhiIndex(monthZhi);
        int hourIndex = BaziDef.getDiZhiIndex(hourZhi);
        
        if (monthIndex == -1 || hourIndex == -1) {
            return "";
        }
        
        // 命宫地支 = 卯位(2) - 月支 + 时支
        int mingGongZhiIndex = (2 - monthIndex + hourIndex + 12) % 12;
        
        // 命宫天干按六十甲子推算 (简化算法,从甲子开始)
        int mingGongGanIndex = mingGongZhiIndex % 10;
        
        return BaziDef.TIAN_GAN[mingGongGanIndex] + BaziDef.DI_ZHI[mingGongZhiIndex];
    }

    /**
     * 计算身宫
     * 
     * @param monthZhi 月支
     * @param hourZhi 时支
     * @return 身宫干支
     */
    public static String calculateShenGong(String monthZhi, String hourZhi) {
        int monthIndex = BaziDef.getDiZhiIndex(monthZhi);
        int hourIndex = BaziDef.getDiZhiIndex(hourZhi);
        
        if (monthIndex == -1 || hourIndex == -1) {
            return "";
        }
        
        // 身宫地支 = 月支 + 时支 - 卯位
        int shenGongZhiIndex = (monthIndex + hourIndex - 2 + 12) % 12;
        
        // 身宫天干按六十甲子推算
        int shenGongGanIndex = shenGongZhiIndex % 10;
        
        return BaziDef.TIAN_GAN[shenGongGanIndex] + BaziDef.DI_ZHI[shenGongZhiIndex];
    }
}
