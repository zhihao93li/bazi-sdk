package com.tafu.bazi.sdk.model;

import java.util.*;

/**
 * 八字常量定义类
 * 包含天干地支、五行关系、藏干权重等静态数据
 * 
 * @author Tafu Team
 * @version 1.0.0
 */
public class BaziDef {

    // ========== 天干地支基础信息 ==========
    
    /** 天干数组 */
    public static final String[] TIAN_GAN = {
        "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
    };
    
    /** 地支数组 */
    public static final String[] DI_ZHI = {
        "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
    };
    
    /** 天干五行映射 */
    public static final Map<String, String> TIAN_GAN_ELEMENT = new HashMap<>() {{
        put("甲", "wood"); put("乙", "wood");
        put("丙", "fire"); put("丁", "fire");
        put("戊", "earth"); put("己", "earth");
        put("庚", "metal"); put("辛", "metal");
        put("壬", "water"); put("癸", "water");
    }};
    
    /** 天干阴阳映射 */
    public static final Map<String, String> TIAN_GAN_YIN_YANG = new HashMap<>() {{
        put("甲", "yang"); put("乙", "yin");
        put("丙", "yang"); put("丁", "yin");
        put("戊", "yang"); put("己", "yin");
        put("庚", "yang"); put("辛", "yin");
        put("壬", "yang"); put("癸", "yin");
    }};
    
    /** 地支五行映射 */
    public static final Map<String, String> DI_ZHI_ELEMENT = new HashMap<>() {{
        put("子", "water"); put("丑", "earth");
        put("寅", "wood"); put("卯", "wood");
        put("辰", "earth"); put("巳", "fire");
        put("午", "fire"); put("未", "earth");
        put("申", "metal"); put("酉", "metal");
        put("戌", "earth"); put("亥", "water");
    }};
    
    /** 地支阴阳映射 */
    public static final Map<String, String> DI_ZHI_YIN_YANG = new HashMap<>() {{
        put("子", "yang"); put("丑", "yin");
        put("寅", "yang"); put("卯", "yin");
        put("辰", "yang"); put("巳", "yin");
        put("午", "yang"); put("未", "yin");
        put("申", "yang"); put("酉", "yin");
        put("戌", "yang"); put("亥", "yin");
    }};

    // ========== 地支藏干映射 ==========
    
    /** 地支藏干映射 (完整版,包含本气、中气、余气) */
    public static final Map<String, List<String>> DI_ZHI_HIDDEN_STEMS = new HashMap<>() {{
        put("子", List.of("癸"));
        put("丑", List.of("己", "癸", "辛"));
        put("寅", List.of("甲", "丙", "戊"));
        put("卯", List.of("乙"));
        put("辰", List.of("戊", "乙", "癸"));
        put("巳", List.of("丙", "庚", "戊"));
        put("午", List.of("丁", "己"));
        put("未", List.of("己", "丁", "乙"));
        put("申", List.of("庚", "壬", "戊"));
        put("酉", List.of("辛"));
        put("戌", List.of("戊", "辛", "丁"));
        put("亥", List.of("壬", "甲"));
    }};
    
    /** 地支藏干权重映射 (用于日主强弱计算) */
    public static final Map<String, Map<String, Double>> DI_ZHI_HIDDEN_STEMS_WEIGHT = new HashMap<>() {{
        put("子", Map.of("癸", 10.0));
        put("丑", Map.of("己", 6.0, "癸", 2.0, "辛", 2.0));
        put("寅", Map.of("甲", 6.0, "丙", 2.0, "戊", 2.0));
        put("卯", Map.of("乙", 10.0));
        put("辰", Map.of("戊", 6.0, "乙", 2.0, "癸", 2.0));
        put("巳", Map.of("丙", 6.0, "庚", 2.0, "戊", 2.0));
        put("午", Map.of("丁", 7.0, "己", 3.0));
        put("未", Map.of("己", 6.0, "丁", 2.0, "乙", 2.0));
        put("申", Map.of("庚", 6.0, "壬", 2.0, "戊", 2.0));
        put("酉", Map.of("辛", 10.0));
        put("戌", Map.of("戊", 6.0, "辛", 2.0, "丁", 2.0));
        put("亥", Map.of("壬", 7.0, "甲", 3.0));
    }};

    // ========== 十神关系映射 ==========
    
    /** 
     * 十神关系映射表
     * 格式: Map<日主天干, Map<其他天干, 十神名称>>
     */
    public static final Map<String, Map<String, String>> TEN_GODS_MAP = new HashMap<>() {{
        put("甲", Map.of(
            "甲", "比肩", "乙", "劫财", "丙", "食神", "丁", "伤官", "戊", "偏财",
            "己", "正财", "庚", "七杀", "辛", "正官", "壬", "偏印", "癸", "正印"
        ));
        put("乙", Map.of(
            "甲", "劫财", "乙", "比肩", "丙", "伤官", "丁", "食神", "戊", "正财",
            "己", "偏财", "庚", "正官", "辛", "七杀", "壬", "正印", "癸", "偏印"
        ));
        put("丙", Map.of(
            "甲", "偏印", "乙", "正印", "丙", "比肩", "丁", "劫财", "戊", "食神",
            "己", "伤官", "庚", "偏财", "辛", "正财", "壬", "七杀", "癸", "正官"
        ));
        put("丁", Map.of(
            "甲", "正印", "乙", "偏印", "丙", "劫财", "丁", "比肩", "戊", "伤官",
            "己", "食神", "庚", "正财", "辛", "偏财", "壬", "正官", "癸", "七杀"
        ));
        put("戊", Map.of(
            "甲", "七杀", "乙", "正官", "丙", "偏印", "丁", "正印", "戊", "比肩",
            "己", "劫财", "庚", "食神", "辛", "伤官", "壬", "偏财", "癸", "正财"
        ));
        put("己", Map.of(
            "甲", "正官", "乙", "七杀", "丙", "正印", "丁", "偏印", "戊", "劫财",
            "己", "比肩", "庚", "伤官", "辛", "食神", "壬", "正财", "癸", "偏财"
        ));
        put("庚", Map.of(
            "甲", "偏财", "乙", "正财", "丙", "七杀", "丁", "正官", "戊", "偏印",
            "己", "正印", "庚", "比肩", "辛", "劫财", "壬", "食神", "癸", "伤官"
        ));
        put("辛", Map.of(
            "甲", "正财", "乙", "偏财", "丙", "正官", "丁", "七杀", "戊", "正印",
            "己", "偏印", "庚", "劫财", "辛", "比肩", "壬", "伤官", "癸", "食神"
        ));
        put("壬", Map.of(
            "甲", "食神", "乙", "伤官", "丙", "偏财", "丁", "正财", "戊", "七杀",
            "己", "正官", "庚", "偏印", "辛", "正印", "壬", "比肩", "癸", "劫财"
        ));
        put("癸", Map.of(
            "甲", "伤官", "乙", "食神", "丙", "正财", "丁", "偏财", "戊", "正官",
            "己", "七杀", "庚", "正印", "辛", "偏印", "壬", "劫财", "癸", "比肩"
        ));
    }};

    // ========== 五行关系映射 ==========
    
    /** 五行相生关系 */
    public static final Map<String, String> ELEMENT_GENERATE = Map.of(
        "wood", "fire",   // 木生火
        "fire", "earth",  // 火生土
        "earth", "metal", // 土生金
        "metal", "water", // 金生水
        "water", "wood"   // 水生木
    );
    
    /** 五行相克关系 */
    public static final Map<String, String> ELEMENT_CONQUER = Map.of(
        "wood", "earth",  // 木克土
        "fire", "metal",  // 火克金
        "earth", "water", // 土克水
        "metal", "wood",  // 金克木
        "water", "fire"   // 水克火
    );
    
    /** 五行中文名称映射 */
    public static final Map<String, String> ELEMENT_CHINESE = Map.of(
        "wood", "木",
        "fire", "火",
        "earth", "土",
        "metal", "金",
        "water", "水"
    );

    // ========== 五行状态权重映射 (用于日主强弱计算) ==========
    
    /**
     * 五行在不同月令下的状态权重
     * 旺(40) > 相(30) > 休(10) > 囚(0) > 死(-10)
     */
    public static final Map<String, Map<String, Integer>> ELEMENT_STATES_WEIGHT = new HashMap<>() {{
        // 寅卯月 (木旺)
        put("寅", Map.of("wood", 40, "fire", 30, "water", 10, "metal", 0, "earth", -10));
        put("卯", Map.of("wood", 40, "fire", 30, "water", 10, "metal", 0, "earth", -10));
        
        // 巳午月 (火旺)
        put("巳", Map.of("fire", 40, "earth", 30, "wood", 10, "water", 0, "metal", -10));
        put("午", Map.of("fire", 40, "earth", 30, "wood", 10, "water", 0, "metal", -10));
        
        // 申酉月 (金旺)
        put("申", Map.of("metal", 40, "water", 30, "earth", 10, "fire", 0, "wood", -10));
        put("酉", Map.of("metal", 40, "water", 30, "earth", 10, "fire", 0, "wood", -10));
        
        // 亥子月 (水旺)
        put("亥", Map.of("water", 40, "wood", 30, "metal", 10, "earth", 0, "fire", -10));
        put("子", Map.of("water", 40, "wood", 30, "metal", 10, "earth", 0, "fire", -10));
        
        // 辰戌丑未月 (土旺)
        put("辰", Map.of("earth", 40, "metal", 30, "fire", 10, "wood", 0, "water", -10));
        put("戌", Map.of("earth", 40, "metal", 30, "fire", 10, "wood", 0, "water", -10));
        put("丑", Map.of("earth", 40, "metal", 30, "fire", 10, "wood", 0, "water", -10));
        put("未", Map.of("earth", 40, "metal", 30, "fire", 10, "wood", 0, "water", -10));
    }};
    
    /**
     * 五行状态描述映射
     */
    public static final Map<Integer, String> ELEMENT_STATE_DESC = Map.of(
        40, "旺",
        30, "相",
        10, "休",
        0, "囚",
        -10, "死"
    );

    // ========== 格局常量 ==========
    
    /** 普通格局(以月令透干为主) */
    public static final String PATTERN_CATEGORY_NORMAL = "normal";
    
    /** 特殊格局(从格、化格等) */
    public static final String PATTERN_CATEGORY_SPECIAL = "special";

    // ========== 神煞常量 (预留扩展) ==========
    
    /** 桃花 */
    public static final Map<String, String> TAO_HUA = Map.of(
        "寅午戌", "卯",
        "申子辰", "酉",
        "巳酉丑", "午",
        "亥卯未", "子"
    );
    
    /** 驿马 */
    public static final Map<String, String> YI_MA = Map.of(
        "寅午戌", "申",
        "申子辰", "寅",
        "巳酉丑", "亥",
        "亥卯未", "巳"
    );

    // ========== 工具方法 ==========
    
    /**
     * 判断是否为阳干
     */
    public static boolean isYangGan(String gan) {
        return "yang".equals(TIAN_GAN_YIN_YANG.get(gan));
    }
    
    /**
     * 判断是否为阴干
     */
    public static boolean isYinGan(String gan) {
        return "yin".equals(TIAN_GAN_YIN_YANG.get(gan));
    }
    
    /**
     * 判断是否为阳支
     */
    public static boolean isYangZhi(String zhi) {
        return "yang".equals(DI_ZHI_YIN_YANG.get(zhi));
    }
    
    /**
     * 判断是否为阴支
     */
    public static boolean isYinZhi(String zhi) {
        return "yin".equals(DI_ZHI_YIN_YANG.get(zhi));
    }
    
    /**
     * 获取天干索引 (0-9)
     */
    public static int getTianGanIndex(String gan) {
        for (int i = 0; i < TIAN_GAN.length; i++) {
            if (TIAN_GAN[i].equals(gan)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 获取地支索引 (0-11)
     */
    public static int getDiZhiIndex(String zhi) {
        for (int i = 0; i < DI_ZHI.length; i++) {
            if (DI_ZHI[i].equals(zhi)) {
                return i;
            }
        }
        return -1;
    }
}
