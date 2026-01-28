package com.tafu.bazi.sdk;

import com.nlf.calendar.EightChar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import com.nlf.calendar.eightchar.DaYun;
import com.nlf.calendar.eightchar.LiuNian;
import com.nlf.calendar.eightchar.Yun;
import com.tafu.bazi.sdk.model.*;
import com.tafu.bazi.sdk.utils.LunarUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 八字计算器实现类
 * 
 * <p>核心功能:
 * <ul>
 *   <li>真太阳时转换</li>
 *   <li>四柱计算 (年月日时)</li>
 *   <li>日主强弱分析</li>
 *   <li>五行统计</li>
 *   <li>十神计算</li>
 *   <li>格局判断</li>
 *   <li>大运流年计算</li>
 *   <li>神煞计算</li>
 * </ul>
 * 
 * @author Tafu Team
 * @version 1.0.0
 */
@Slf4j
public class BaziCalculatorImpl implements BaziCalculator {

    @Override
    public BaziResponse calculate(BaziRequest request) {
        // 参数校验
        validateRequest(request);
        
        Solar solar;
        
        // 1. 处理输入日期 & 真太阳时
        if ("lunar".equals(request.getCalendarType())) {
            // 农历输入
            Lunar lunar = LunarUtils.createLunar(
                request.getYear(),
                request.getMonth(),
                request.getDay(),
                request.getHour(),
                request.getMinute(),
                Boolean.TRUE.equals(request.getIsLeapMonth())
            );
            Solar tempSolar = lunar.getSolar();
            
            // 如果提供了经度,进行真太阳时修正
            if (request.getLongitude() != null) {
                solar = LunarUtils.getTrueSolarTime(
                    tempSolar.getYear(),
                    tempSolar.getMonth(),
                    tempSolar.getDay(),
                    request.getHour(),
                    request.getMinute(),
                    request.getLongitude()
                );
            } else {
                solar = LunarUtils.createSolar(
                    tempSolar.getYear(),
                    tempSolar.getMonth(),
                    tempSolar.getDay(),
                    request.getHour(),
                    request.getMinute()
                );
            }
        } else {
            // 公历输入
            if (request.getLongitude() != null) {
                solar = LunarUtils.getTrueSolarTime(
                    request.getYear(),
                    request.getMonth(),
                    request.getDay(),
                    request.getHour(),
                    request.getMinute(),
                    request.getLongitude()
                );
            } else {
                solar = LunarUtils.createSolar(
                    request.getYear(),
                    request.getMonth(),
                    request.getDay(),
                    request.getHour(),
                    request.getMinute()
                );
            }
        }
        
        // 2. 获取农历和八字对象
        Lunar lunar = solar.getLunar();
        EightChar eightChar = lunar.getEightChar();
        eightChar.setSect(1); // 晚子时日柱算明天
        
        String dayMasterGan = eightChar.getDayGan();
        
        // 3. 构建四柱
        FourPillarsDTO fourPillars = buildFourPillars(eightChar, dayMasterGan);
        
        // 4. 核心分析
        DayMasterDTO dayMaster = calculateDayMaster(fourPillars, dayMasterGan);
        FiveElementsDTO fiveElements = calculateFiveElements(fourPillars, dayMaster);
        TenGodsDTO tenGods = calculateTenGods(fourPillars, dayMasterGan);
        PatternDTO pattern = calculatePattern(fourPillars, dayMaster, fiveElements);
        List<String> dayMasterCharacteristics = getDayMasterCharacteristics(dayMasterGan);
        
        // 5. 大运流年
        Yun yunObj = eightChar.getYun("male".equals(request.getGender()) ? 1 : 0);
        YunInfoDTO yunInfo = calculateYun(yunObj, lunar);
        
        // 6. 神煞
        ShenShaDTO shenSha = calculateShenSha(lunar);
        
        // 7. 其他信息
        String shengXiao = LunarUtils.getShengXiao(eightChar.getYearZhi());
        String taiYuan = eightChar.getTaiYuan();
        String mingGong = eightChar.getMingGong();
        String shenGong = eightChar.getShenGong();
        String xunKong = eightChar.getDayXunKong();
        
        // 8. 构建真太阳时信息
        TrueSolarTimeDTO trueSolarTime = null;
        if (request.getLongitude() != null) {
            trueSolarTime = TrueSolarTimeDTO.builder()
                .year(solar.getYear())
                .month(solar.getMonth())
                .day(solar.getDay())
                .hour(solar.getHour())
                .minute(solar.getMinute())
                .build();
        }
        
        // 9. 构建返回结果
        return BaziResponse.builder()
            .gender(request.getGender())
            .solarDate(solar.toYmdHms())
            .lunarDate(lunar.toString())
            .trueSolarTime(trueSolarTime)
            .fourPillars(fourPillars)
            .dayMaster(dayMaster)
            .fiveElements(fiveElements)
            .tenGods(tenGods)
            .pattern(pattern)
            .yun(yunInfo)
            .shenSha(shenSha)
            .shengXiao(shengXiao)
            .taiYuan(taiYuan)
            .mingGong(mingGong)
            .shenGong(shenGong)
            .xunKong(xunKong)
            .dayMasterCharacteristics(dayMasterCharacteristics)
            .build();
    }

    @Override
    public int getLeapMonth(int lunarYear) {
        return LunarUtils.getLeapMonth(lunarYear);
    }

    // ==================== 私有方法 ====================

    /**
     * 参数校验
     */
    private void validateRequest(BaziRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getYear() == null || request.getYear() < 1901 || request.getYear() > 2100) {
            throw new IllegalArgumentException("Year must be between 1901 and 2100");
        }
        if (request.getMonth() == null || Math.abs(request.getMonth()) < 1 || Math.abs(request.getMonth()) > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12 (or -1 to -12 for leap month)");
        }
        if (request.getDay() == null || request.getDay() < 1 || request.getDay() > 31) {
            throw new IllegalArgumentException("Day must be between 1 and 31");
        }
        if (request.getHour() == null || request.getHour() < 0 || request.getHour() > 23) {
            throw new IllegalArgumentException("Hour must be between 0 and 23");
        }
        if (request.getMinute() == null || request.getMinute() < 0 || request.getMinute() > 59) {
            throw new IllegalArgumentException("Minute must be between 0 and 59");
        }
        if (request.getCalendarType() == null || 
            (!request.getCalendarType().equals("solar") && !request.getCalendarType().equals("lunar"))) {
            throw new IllegalArgumentException("CalendarType must be 'solar' or 'lunar'");
        }
        if (request.getGender() == null || 
            (!request.getGender().equals("male") && !request.getGender().equals("female"))) {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'");
        }
        if (request.getLongitude() != null && 
            (request.getLongitude() < -180.0 || request.getLongitude() > 180.0)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        if (request.getLatitude() != null && 
            (request.getLatitude() < -90.0 || request.getLatitude() > 90.0)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
    }

    /**
     * 构建四柱
     */
    private FourPillarsDTO buildFourPillars(EightChar eightChar, String dayMasterGan) {
        return FourPillarsDTO.builder()
            .year(buildPillar(eightChar.getYearGan(), eightChar.getYearZhi(), 
                eightChar.getYearNaYin(), dayMasterGan))
            .month(buildPillar(eightChar.getMonthGan(), eightChar.getMonthZhi(), 
                eightChar.getMonthNaYin(), dayMasterGan))
            .day(buildPillar(eightChar.getDayGan(), eightChar.getDayZhi(), 
                eightChar.getDayNaYin(), dayMasterGan))
            .hour(buildPillar(eightChar.getTimeGan(), eightChar.getTimeZhi(), 
                eightChar.getTimeNaYin(), dayMasterGan))
            .build();
    }

    /**
     * 构建单柱
     */
    private PillarDTO buildPillar(String gan, String zhi, String naYin, String dayMasterGan) {
        // 构建天干
        HeavenlyStemDTO heavenlyStem = HeavenlyStemDTO.builder()
            .chinese(gan)
            .element(BaziDef.TIAN_GAN_ELEMENT.get(gan))
            .yinYang(BaziDef.TIAN_GAN_YIN_YANG.get(gan))
            .build();
        
        // 构建地支
        EarthlyBranchDTO earthlyBranch = EarthlyBranchDTO.builder()
            .chinese(zhi)
            .element(BaziDef.DI_ZHI_ELEMENT.get(zhi))
            .build();
        
        // 构建藏干列表
        List<String> hiddenStemsStr = LunarUtils.getHiddenStems(zhi);
        List<HiddenStemDTO> hiddenStems = hiddenStemsStr.stream()
            .map(stem -> HiddenStemDTO.builder()
                .chinese(stem)
                .element(BaziDef.TIAN_GAN_ELEMENT.get(stem))
                .yinYang(BaziDef.TIAN_GAN_YIN_YANG.get(stem))
                .build())
            .collect(Collectors.toList());
        
        // 计算空亡
        String xunKong = LunarUtils.calculateXunKong(gan + zhi);
        
        // 计算十神
        String tenGod = getTenGod(dayMasterGan, gan);
        
        return PillarDTO.builder()
            .heavenlyStem(heavenlyStem)
            .earthlyBranch(earthlyBranch)
            .naYin(naYin)
            .hiddenStems(hiddenStems)
            .xunKong(xunKong)
            .tenGod(tenGod)
            .build();
    }

    /**
     * 计算十神
     * 
     * @param dayMasterGan 日主天干
     * @param targetGan 目标天干
     * @return 十神名称
     */
    /**
     * 获取十神 (旧版本 - 使用静态表查询)
     * @deprecated 建议使用基于StemInfo的动态计算版本
     */
    @Deprecated
    private String getTenGod(String dayMasterGan, String targetGan) {
        Map<String, String> tenGodMap = BaziDef.TEN_GODS_MAP.get(dayMasterGan);
        return tenGodMap != null ? tenGodMap.get(targetGan) : null;
    }
    
    /**
     * 获取十神 (新版本 - 动态计算)
     * 基于五行和阴阳关系动态计算十神
     */
    private String getTenGod(BaziDef.StemInfo dayStem, BaziDef.StemInfo otherStem) {
        if (dayStem.getChinese().equals(otherStem.getChinese())) return "比肩";
        
        BaziDef.FiveElement dayEl = dayStem.getElement();
        BaziDef.YinYang dayYY = dayStem.getYinYang();
        BaziDef.FiveElement otherEl = otherStem.getElement();
        BaziDef.YinYang otherYY = otherStem.getYinYang();
        
        boolean sameYinYang = dayYY == otherYY;
        
        if (dayEl == otherEl) return sameYinYang ? "比肩" : "劫财";
        if (generatesElement(dayEl, otherEl)) return sameYinYang ? "食神" : "伤官";
        if (restrictsElement(dayEl, otherEl)) return sameYinYang ? "偏财" : "正财";
        if (restrictsElement(otherEl, dayEl)) return sameYinYang ? "七杀" : "正官";
        if (generatesElement(otherEl, dayEl)) return sameYinYang ? "偏印" : "正印";
        
        return null;
    }

    /**
     * 计算日主强弱
     */
    private DayMasterDTO calculateDayMaster(FourPillarsDTO fourPillars, String dayMasterGan) {
        String dayGan = fourPillars.getDay().getHeavenlyStem().getChinese();
        String monthZhi = fourPillars.getMonth().getEarthlyBranch().getChinese();
        
        String dayElement = BaziDef.TIAN_GAN_ELEMENT.get(dayGan);
        String monthElement = BaziDef.DI_ZHI_ELEMENT.get(monthZhi);
        
        // 1. 得令 (月令支持)
        double deLing = 0;
        String deLingDesc = "";
        
        if (dayElement.equals(monthElement)) {
            deLing = 40;
            deLingDesc = "日主当令";
        } else if (monthElement.equals(BaziDef.ELEMENT_GENERATE.get(dayElement))) {
            deLing = 30;
            deLingDesc = "月令生扶";
        } else if (dayElement.equals(BaziDef.ELEMENT_GENERATE.get(monthElement))) {
            deLing = -10;
            deLingDesc = "月令泄气";
        } else if (dayElement.equals(BaziDef.ELEMENT_CONQUER.get(monthElement))) {
            deLing = -20;
            deLingDesc = "月令克制";
        } else {
            deLing = -5;
            deLingDesc = "日主耗气";
        }
        
        // 2. 得地 (地支藏干中有根)
        double deDi = 0;
        List<String> roots = new ArrayList<>();
        PillarDTO[] pillars = {fourPillars.getYear(), fourPillars.getMonth(), 
                               fourPillars.getDay(), fourPillars.getHour()};
        String[] pillarNames = {"年支", "月支", "日支", "时支"};
        
        for (int i = 0; i < pillars.length; i++) {
            String zhi = pillars[i].getEarthlyBranch().getChinese();
            List<HiddenStemDTO> hiddenStems = pillars[i].getHiddenStems();
            
            for (int j = 0; j < hiddenStems.size(); j++) {
                HiddenStemDTO stem = hiddenStems.get(j);
                double weight = LunarUtils.getHiddenStemWeight(zhi, stem.getChinese());
                
                if (dayElement.equals(stem.getElement())) {
                    deDi += weight * 15;  // 修改: 从1.5改为15
                    roots.add(pillarNames[i] + "藏" + stem.getChinese());
                } else if (stem.getElement().equals(BaziDef.ELEMENT_GENERATE.get(dayElement))) {
                    deDi += weight * 10;  // 修改: 从1.0改为10
                    roots.add(pillarNames[i] + "藏" + stem.getChinese() + "(印)");
                }
            }
        }
        deDi = Math.min(deDi, 30); // 最多30分
        
        String deDiDesc = roots.isEmpty() ? "无根" : String.join("、", roots);
        
        // 3. 天干帮扶
        double tianGanHelp = 0;
        List<String> helpers = new ArrayList<>();
        PillarDTO[] stemPillars = {fourPillars.getYear(), fourPillars.getMonth(), fourPillars.getHour()};
        String[] stemNames = {"年干", "月干", "时干"};
        
        for (int i = 0; i < stemPillars.length; i++) {
            String stemChar = stemPillars[i].getHeavenlyStem().getChinese();
            String stemElement = stemPillars[i].getHeavenlyStem().getElement();
            
            if (dayElement.equals(stemElement)) {
                tianGanHelp += 8;
                helpers.add(stemNames[i] + stemChar + "比劫");
            } else if (stemElement.equals(BaziDef.ELEMENT_GENERATE.get(dayElement))) {
                tianGanHelp += 6;
                helpers.add(stemNames[i] + stemChar + "印星");
            } else if (dayElement.equals(BaziDef.ELEMENT_CONQUER.get(stemElement))) {
                tianGanHelp -= 5;
                helpers.add(stemNames[i] + stemChar + "官杀");
            } else if (stemElement.equals(BaziDef.ELEMENT_GENERATE.get(dayElement))) {
                tianGanHelp -= 3;
                helpers.add(stemNames[i] + stemChar + "食伤");
            }
        }
        tianGanHelp = Math.max(Math.min(tianGanHelp, 20), -20); // 限制在 -20 ~ 20
        
        String tianGanHelpDesc = helpers.isEmpty() ? "无帮扶" : String.join("、", helpers);
        
        // 4. 计算总分
        double totalScore = deLing + deDi + tianGanHelp;
        
        // 5. 判断强弱 (修改阈值: 从60/40改为50/25)
        String strength;
        if (totalScore >= 50) {
            strength = "strong";
        } else if (totalScore <= 25) {
            strength = "weak";
        } else {
            strength = "balanced";
        }
        
        // 6. 构建分析对象
        DayMasterAnalysisDTO analysis = DayMasterAnalysisDTO.builder()
            .deLing(deLing)
            .deLingDesc(deLingDesc)
            .deDi(deDi)
            .deDiDesc(deDiDesc)
            .tianGanHelp(tianGanHelp)
            .tianGanHelpDesc(tianGanHelpDesc)
            .totalScore(totalScore)
            .build();
        
        return DayMasterDTO.builder()
            .gan(dayGan)
            .strength(strength)
            .analysis(analysis)
            .build();
    }

    /**
     * 计算五行分布
     */
    private FiveElementsDTO calculateFiveElements(FourPillarsDTO fourPillars, DayMasterDTO dayMaster) {
        Map<String, Double> distribution = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        
        // 初始化
        for (String element : List.of("wood", "fire", "earth", "metal", "water")) {
            distribution.put(element, 0.0);
            counts.put(element, 0);
        }
        
        // 获取月令五行和五行状态
        String monthZhi = fourPillars.getMonth().getEarthlyBranch().getChinese();
        BaziDef.FiveElement monthElement = BaziDef.MONTH_BRANCH_ELEMENT.get(monthZhi);
        Map<String, String> elementStates = getElementStates(monthElement);
        
        // 统计天干 (权重 1.0 * stateWeight)
        PillarDTO[] pillars = {fourPillars.getYear(), fourPillars.getMonth(), 
                               fourPillars.getDay(), fourPillars.getHour()};
        for (PillarDTO pillar : pillars) {
            String gan = pillar.getHeavenlyStem().getChinese();
            BaziDef.StemInfo info = BaziDef.STEMS_INFO.get(gan);
            if (info != null) {
                BaziDef.FiveElement e = info.getElement();
                String state = elementStates.getOrDefault(e.getCode(), "si");
                double stateWeight = BaziDef.STATE_WEIGHTS.getOrDefault(state, 0.5);
                distribution.merge(e.getCode(), 1.0 * stateWeight, Double::sum);
                counts.merge(e.getCode(), 1, Integer::sum);
            }
        }
        
        // 统计藏干 (权重 weight * stateWeight)
        for (PillarDTO pillar : pillars) {
            String zhi = pillar.getEarthlyBranch().getChinese();
            List<HiddenStemDTO> hiddenStems = pillar.getHiddenStems();
            List<Double> weights = BaziDef.HIDDEN_STEM_WEIGHTS.getOrDefault(zhi, Collections.emptyList());
            
            for (int i = 0; i < hiddenStems.size(); i++) {
                HiddenStemDTO stem = hiddenStems.get(i);
                BaziDef.StemInfo info = BaziDef.STEMS_INFO.get(stem.getChinese());
                if (info == null) continue;
                
                double weight = (i < weights.size()) ? weights.get(i) : 0.2;
                String state = elementStates.getOrDefault(info.getElement().getCode(), "si");
                double stateWeight = BaziDef.STATE_WEIGHTS.getOrDefault(state, 0.5);
                
                distribution.merge(info.getElement().getCode(), weight * stateWeight, Double::sum);
                
                // 只统计本气 (第一个)
                if (i == 0) {
                    counts.merge(info.getElement().getCode(), 1, Integer::sum);
                }
            }
        }
        
        // 找最旺和最弱
        String strongest = distribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        String weakest = distribution.entrySet().stream()
            .filter(e -> e.getValue() > 0)
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        // 计算喜用和忌讳五行
        List<String> favorable = new ArrayList<>();
        List<String> unfavorable = new ArrayList<>();
        
        BaziDef.StemInfo dayInfo = BaziDef.STEMS_INFO.get(dayMaster.getGan());
        String dayElement = dayInfo.getElement().getCode();
        if ("weak".equals(dayMaster.getStrength())) {
            // 日主弱,喜生扶
            favorable.add(dayElement); // 比劫
            BaziDef.FiveElement generatedBy = BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(dayInfo.getElement());
            if (generatedBy != null) {
                favorable.add(generatedBy.getCode()); // 印星
            }
            BaziDef.FiveElement restricted = BaziDef.FIVE_ELEMENTS_RESTRICTION.get(dayInfo.getElement());
            if (restricted != null) {
                unfavorable.add(restricted.getCode()); // 财星
            }
        } else if ("strong".equals(dayMaster.getStrength())) {
            // 日主强,喜克泄
            unfavorable.add(dayElement); // 比劫
            BaziDef.FiveElement generatedBy = BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(dayInfo.getElement());
            if (generatedBy != null) {
                unfavorable.add(generatedBy.getCode()); // 印星
            }
            BaziDef.FiveElement restricted = BaziDef.FIVE_ELEMENTS_RESTRICTION.get(dayInfo.getElement());
            if (restricted != null) {
                favorable.add(restricted.getCode()); // 财星
            }
        }
        
        return FiveElementsDTO.builder()
            .distribution(distribution)
            .counts(counts)
            .strongest(strongest)
            .weakest(weakest)
            .favorable(favorable)
            .unfavorable(unfavorable)
            .elementStates(elementStates)
            .monthElement(monthElement != null ? monthElement.getCode() : null)
            .build();
    }
    
    /**
     * 计算五行状态 (旺相休囚死)
     */
    private Map<String, String> getElementStates(BaziDef.FiveElement monthElement) {
        Map<String, String> states = new HashMap<>();
        
        if (monthElement == null) return states;
        
        // Wang: 月令本气
        states.put(monthElement.getCode(), "wang");
        
        // Xiang: 月令生的
        BaziDef.FiveElement generated = BaziDef.FIVE_ELEMENTS_GENERATION.get(monthElement);
        if (generated != null) states.put(generated.getCode(), "xiang");
        
        // Xiu: 生月令的
        BaziDef.FiveElement generator = BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(monthElement);
        if (generator != null) states.put(generator.getCode(), "xiu");
        
        // Qiu: 克月令的
        for (Map.Entry<BaziDef.FiveElement, BaziDef.FiveElement> entry : BaziDef.FIVE_ELEMENTS_RESTRICTION.entrySet()) {
            if (entry.getValue() == monthElement) {
                states.put(entry.getKey().getCode(), "qiu");
                break;
            }
        }
        
        // Si: 月令克的
        BaziDef.FiveElement restricted = BaziDef.FIVE_ELEMENTS_RESTRICTION.get(monthElement);
        if (restricted != null) states.put(restricted.getCode(), "si");
        
        return states;
    }

    /**
     * 计算十神
     */
    private TenGodsDTO calculateTenGods(FourPillarsDTO fourPillars, String dayMasterGan) {
        Map<String, TenGodInfoDTO> gods = new HashMap<>();
        
        PillarDTO[] pillars = {fourPillars.getYear(), fourPillars.getMonth(), 
                               fourPillars.getDay(), fourPillars.getHour()};
        String[] positions = {"年干", "月干", "日干", "时干"};
        
        for (int i = 0; i < pillars.length; i++) {
            String gan = pillars[i].getHeavenlyStem().getChinese();
            String tenGod = getTenGod(dayMasterGan, gan);
            
            if (tenGod != null) {
                TenGodInfoDTO info = gods.getOrDefault(tenGod, TenGodInfoDTO.builder()
                    .name(tenGod)
                    .count(0)
                    .positions(new ArrayList<>())
                    .build());
                
                info.setCount(info.getCount() + 1);
                info.getPositions().add(positions[i]);
                gods.put(tenGod, info);
            }
        }
        
        return TenGodsDTO.builder()
            .gods(gods)
            .build();
    }

    /**
     * 计算格局
     */
    private PatternDTO calculatePattern(FourPillarsDTO fourPillars, DayMasterDTO dayMaster, 
                                        FiveElementsDTO fiveElements) {
        String dayStemName = dayMaster.getGan();
        BaziDef.StemInfo dayStemInfo = BaziDef.STEMS_INFO.get(dayStemName);
        BaziDef.FiveElement dayElement = dayStemInfo.getElement();
        
        String monthBranch = fourPillars.getMonth().getEarthlyBranch().getChinese();
        List<HiddenStemDTO> monthHiddenStems = fourPillars.getMonth().getHiddenStems();
        List<String> tianGan = List.of(
            fourPillars.getYear().getHeavenlyStem().getChinese(),
            fourPillars.getMonth().getHeavenlyStem().getChinese(),
            fourPillars.getHour().getHeavenlyStem().getChinese()
        );
        
        // Lu and Ren maps
        Map<String, String> luMap = Map.of(
            "甲", "寅", "乙", "卯", "丙", "巳", "丁", "午", 
            "戊", "巳", "己", "午", "庚", "申", "辛", "酉", 
            "壬", "亥", "癸", "子"
        );
        Map<String, String> renMap = Map.of(
            "甲", "卯", "乙", "寅", "丙", "午", "丁", "巳", 
            "戊", "午", "己", "巳", "庚", "酉", "辛", "申", 
            "壬", "子", "癸", "亥"
        );
        
        // 1. 建禄格
        if (monthBranch.equals(luMap.get(dayStemName))) {
            return PatternDTO.builder()
                .name("建禄格")
                .category("normal")
                .description("月支为日主之禄，主身旺有根，宜见财官食伤")
                .monthStem(monthHiddenStems.isEmpty() ? null : monthHiddenStems.get(0).getChinese())
                .isTransparent(false)
                .build();
        }
        
        // 2. 羊刃格
        if (monthBranch.equals(renMap.get(dayStemName))) {
            return PatternDTO.builder()
                .name("羊刃格")
                .category("normal")
                .description("月支为日主之刃，主身强刚烈，宜见官杀制刃")
                .monthStem(monthHiddenStems.isEmpty() ? null : monthHiddenStems.get(0).getChinese())
                .isTransparent(false)
                .build();
        }
        
        // 3. 特殊格局
        double score = dayMaster.getAnalysis() != null ? dayMaster.getAnalysis().getTotalScore() : 50.0;
        
        // 从格 (Score < 20)
        if (score < 20) {
            Map<String, Double> dist = fiveElements.getDistribution();
            // Find strongest non-day-element
            String strongestElStr = dayElement.getCode();
            double strongestVal = 0;
            
            for (BaziDef.FiveElement el : BaziDef.FiveElement.values()) {
                if (el != dayElement && dist.getOrDefault(el.getCode(), 0.0) > strongestVal) {
                    strongestVal = dist.get(el.getCode());
                    strongestElStr = el.getCode();
                }
            }
            
            BaziDef.FiveElement strongestEl = BaziDef.FiveElement.fromCode(strongestElStr);
            if (strongestEl != null) {
                if (restrictsElement(dayElement, strongestEl)) {
                    return PatternDTO.builder()
                        .name("从财格")
                        .category("special")
                        .description("日主极弱而财星极旺，弃命从财，宜顺从财势")
                        .build();
                }
                if (restrictsElement(strongestEl, dayElement)) {
                    return PatternDTO.builder()
                        .name("从官格")
                        .category("special")
                        .description("日主极弱而官杀极旺，弃命从官，宜顺从官势")
                        .build();
                }
                if (generatesElement(dayElement, strongestEl)) {
                    return PatternDTO.builder()
                        .name("从儿格")
                        .category("special")
                        .description("日主极弱而食伤极旺，弃命从儿，宜顺从食伤之势")
                        .build();
                }
            }
        }
        
        // 专旺格 (Score > 75)
        if (score > 75) {
            Map<BaziDef.FiveElement, String> nameMap = Map.of(
                BaziDef.FiveElement.WOOD, "曲直格",
                BaziDef.FiveElement.FIRE, "炎上格",
                BaziDef.FiveElement.EARTH, "稼穑格",
                BaziDef.FiveElement.METAL, "从革格",
                BaziDef.FiveElement.WATER, "润下格"
            );
            Map<BaziDef.FiveElement, String> descMap = Map.of(
                BaziDef.FiveElement.WOOD, "木气专旺成局，主仁慈正直，宜水木运",
                BaziDef.FiveElement.FIRE, "火气炎上成局，主热情礼仪，宜木火运",
                BaziDef.FiveElement.EARTH, "土气稼穑成局，主忠厚信实，宜火土运",
                BaziDef.FiveElement.METAL, "金气从革成局，主刚毅果决，宜土金运",
                BaziDef.FiveElement.WATER, "水气润下成局，主聪慧灵活，宜金水运"
            );
            
            return PatternDTO.builder()
                .name(nameMap.get(dayElement))
                .category("special")
                .description(descMap.get(dayElement))
                .build();
        }
        
        // 4. 正格
        if (!monthHiddenStems.isEmpty()) {
            for (HiddenStemDTO hiddenStemDTO : monthHiddenStems) {
                String hiddenStem = hiddenStemDTO.getChinese();
                BaziDef.StemInfo hiddenStemInfo = BaziDef.STEMS_INFO.get(hiddenStem);
                String tenGod = getTenGod(dayStemInfo, hiddenStemInfo);
                
                if ("比肩".equals(tenGod) || "劫财".equals(tenGod)) continue;
                
                boolean isTransparent = tianGan.contains(hiddenStem);
                
                Map<String, Map.Entry<String, String>> patternMap = Map.of(
                    "正官", Map.entry("正官格", "月令透正官，主贵气端正，宜见财印相生"),
                    "七杀", Map.entry("七杀格", "月令透七杀，主威严果决，宜见食伤制杀或印化杀"),
                    "正财", Map.entry("正财格", "月令透正财，主务实勤俭，宜见官杀护财"),
                    "偏财", Map.entry("偏财格", "月令透偏财，主豪爽大方，宜见官杀护财"),
                    "正印", Map.entry("正印格", "月令透正印，主聪慧仁厚，宜见官杀生印"),
                    "偏印", Map.entry("偏印格", "月令透偏印，主机敏多思，宜见财星制印"),
                    "食神", Map.entry("食神格", "月令透食神，主温和福厚，宜见财星泄秀"),
                    "伤官", Map.entry("伤官格", "月令透伤官，主聪明傲气，宜见财星或印星")
                );
                
                if (tenGod != null && patternMap.containsKey(tenGod)) {
                    Map.Entry<String, String> info = patternMap.get(tenGod);
                    return PatternDTO.builder()
                        .name(info.getKey())
                        .category("normal")
                        .description(info.getValue())
                        .monthStem(hiddenStem)
                        .monthStemTenGod(tenGod)
                        .isTransparent(isTransparent)
                        .build();
                }
            }
        }
        
        // 5. 杂格
        return PatternDTO.builder()
            .name("杂格")
            .category("normal")
            .description("月令无明显成格条件，需综合分析八字整体格局")
            .build();
    }
    
    /**
     * 判断五行相生关系
     */
    private boolean generatesElement(BaziDef.FiveElement from, BaziDef.FiveElement to) {
        return BaziDef.FIVE_ELEMENTS_GENERATION.get(from) == to;
    }
    
    /**
     * 判断五行相克关系
     */
    private boolean restrictsElement(BaziDef.FiveElement from, BaziDef.FiveElement to) {
        return BaziDef.FIVE_ELEMENTS_RESTRICTION.get(from) == to;
    }

    /**
     * 计算大运流年
     */
    private YunInfoDTO calculateYun(Yun yunObj, Lunar lunar) {
        // Yun 对象没有直接的 getStartAge() 方法
        // 需要从第一个大运中获取
        DaYun[] daYunArray = yunObj.getDaYun();
        int startAge = daYunArray.length > 0 ? daYunArray[0].getStartAge() : 0;
        boolean forward = true; // lunar-java 1.7.7 默认顺行
        
        List<DaYunDTO> daYunList = new ArrayList<>();
        
        int birthYear = lunar.getYear();
        
        // lunar-java 支持的年份范围
        final int MIN_YEAR = 1901;
        final int MAX_YEAR = 2100;
        
        for (int i = 0; i < Math.min(10, daYunArray.length); i++) {
            DaYun daYun = daYunArray[i];
            int daYunStartAge = daYun.getStartAge();
            int daYunEndAge = daYun.getEndAge();
            int daYunStartYear = daYun.getStartYear();
            int daYunEndYear = daYun.getEndYear();
            
            // 从 getGanZhi() 中提取干支
            String ganZhi = daYun.getGanZhi();
            String gan = ganZhi != null && ganZhi.length() >= 1 ? ganZhi.substring(0, 1) : "";
            String zhi = ganZhi != null && ganZhi.length() >= 2 ? ganZhi.substring(1, 2) : "";
            
            // 计算流年: 为该大运内的每一年生成流年数据
            List<LiuNianDTO> liuNianList = new ArrayList<>();
            
            for (int year = daYunStartYear; year <= daYunEndYear; year++) {
                // 跳过超出支持范围的年份
                if (year < MIN_YEAR || year > MAX_YEAR) {
                    continue;
                }
                
                int age = daYunStartAge + (year - daYunStartYear);
                
                // 使用 lunar-java 计算流年干支
                // 使用7月1日(年中)来确保一定过了立春,获取该年正确的干支
                try {
                    Solar yearSolar = Solar.fromYmd(year, 7, 1);
                    Lunar yearLunar = yearSolar.getLunar();
                    String yearGanZhi = yearLunar.getYearInGanZhiExact(); // 精确年干支
                    String yearGan = yearGanZhi != null && yearGanZhi.length() >= 1 ? yearGanZhi.substring(0, 1) : "";
                    String yearZhi = yearGanZhi != null && yearGanZhi.length() >= 2 ? yearGanZhi.substring(1, 2) : "";
                    
                    liuNianList.add(LiuNianDTO.builder()
                        .year(year)
                        .age(age)
                        .ganZhi(yearGanZhi)
                        .gan(yearGan)
                        .zhi(yearZhi)
                        .build());
                } catch (Exception e) {
                    log.warn("计算流年失败 year={}: {}", year, e.getMessage());
                }
            }
            
            daYunList.add(DaYunDTO.builder()
                .index(i)
                .startAge(daYunStartAge)
                .endAge(daYunEndAge)
                .ganZhi(ganZhi)
                .gan(gan)
                .zhi(zhi)
                .startYear(daYunStartYear)
                .endYear(daYunEndYear)
                .liuNian(liuNianList)
                .build());
        }
        
        return YunInfoDTO.builder()
            .startAge(startAge)
            .forward(forward)
            .daYunList(daYunList)
            .build();
    }

    /**
     * 计算神煞
     * 使用反射调用 lunar-java 的神煞 API (版本兼容性更好)
     */
    private ShenShaDTO calculateShenSha(Lunar lunar) {
        // 使用反射安全调用神煞方法
        List<String> yearShenSha = getShenShaByReflection(lunar, "getYearShenSha");
        List<String> monthShenSha = getShenShaByReflection(lunar, "getMonthShenSha");
        List<String> dayShenSha = getShenShaByReflection(lunar, "getDayShenSha");
        List<String> hourShenSha = getShenShaByReflection(lunar, "getTimeShenSha");
        
        return ShenShaDTO.builder()
            .year(yearShenSha)
            .month(monthShenSha)
            .day(dayShenSha)
            .hour(hourShenSha)
            .build();
    }
    
    /**
     * 通过反射调用神煞方法 (兼容不同版本的 lunar-java)
     * 
     * @param lunar Lunar 对象
     * @param methodName 方法名
     * @return 神煞名称列表
     */
    private List<String> getShenShaByReflection(Lunar lunar, String methodName) {
        List<String> result = new ArrayList<>();
        try {
            Object shenShaResult = lunar.getClass().getMethod(methodName).invoke(lunar);
            if (shenShaResult instanceof java.util.List) {
                java.util.List<?> shenShaList = (java.util.List<?>) shenShaResult;
                for (Object obj : shenShaList) {
                    try {
                        // 尝试调用 getName() 方法
                        String name = (String) obj.getClass().getMethod("getName").invoke(obj);
                        if (name != null && !name.isEmpty()) {
                            result.add(name);
                        }
                    } catch (Exception e) {
                        // 如果失败，直接使用 toString()
                        result.add(obj.toString());
                    }
                }
            }
        } catch (Exception e) {
            // 忽略错误，返回空列表
            log.debug("获取神煞失败 [{}]: {}", methodName, e.getMessage());
        }
        return result;
    }

    /**
     * 获取日主特征描述
     */
    private List<String> getDayMasterCharacteristics(String dayGan) {
        // 日主特征描述表 (简化版)
        Map<String, List<String>> characteristics = new HashMap<>() {{
            put("甲", List.of("仁慈正直", "进取心强", "有领导才能", "性格直率"));
            put("乙", List.of("温和体贴", "灵活变通", "艺术才华", "优柔寡断"));
            put("丙", List.of("热情开朗", "积极向上", "富有激情", "易冲动"));
            put("丁", List.of("细腻敏感", "思维敏捷", "重视精神", "情绪波动"));
            put("戊", List.of("稳重踏实", "包容性强", "诚信可靠", "固执保守"));
            put("己", List.of("细心谨慎", "善于理财", "内敛含蓄", "多虑"));
            put("庚", List.of("刚毅果断", "执行力强", "讲究原则", "不够圆滑"));
            put("辛", List.of("细致精巧", "品味高雅", "自尊心强", "敏感脆弱"));
            put("壬", List.of("智慧聪明", "应变能力强", "善于交际", "缺乏恒心"));
            put("癸", List.of("柔和内敛", "直觉敏锐", "富有同情心", "容易悲观"));
        }};
        
        return characteristics.getOrDefault(dayGan, List.of("日主特征待完善"));
    }
}
