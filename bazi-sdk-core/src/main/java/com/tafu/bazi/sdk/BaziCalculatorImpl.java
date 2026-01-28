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
    private String getTenGod(String dayMasterGan, String targetGan) {
        Map<String, String> tenGodMap = BaziDef.TEN_GODS_MAP.get(dayMasterGan);
        return tenGodMap != null ? tenGodMap.get(targetGan) : null;
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
                    deDi += weight * 1.5;
                    roots.add(pillarNames[i] + "藏" + stem.getChinese());
                } else if (stem.getElement().equals(BaziDef.ELEMENT_GENERATE.get(dayElement))) {
                    deDi += weight * 1.0;
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
        
        // 5. 判断强弱
        String strength;
        if (totalScore >= 60) {
            strength = "strong";
        } else if (totalScore >= 40) {
            strength = "balanced";
        } else {
            strength = "weak";
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
        
        // 统计天干
        PillarDTO[] pillars = {fourPillars.getYear(), fourPillars.getMonth(), 
                               fourPillars.getDay(), fourPillars.getHour()};
        for (PillarDTO pillar : pillars) {
            String ganElement = pillar.getHeavenlyStem().getElement();
            distribution.put(ganElement, distribution.get(ganElement) + 1.0);
            counts.put(ganElement, counts.get(ganElement) + 1);
            
            // 统计地支 (0.8权重)
            String zhiElement = pillar.getEarthlyBranch().getElement();
            distribution.put(zhiElement, distribution.get(zhiElement) + 0.8);
            
            // 统计藏干 (根据权重)
            String zhi = pillar.getEarthlyBranch().getChinese();
            for (HiddenStemDTO stem : pillar.getHiddenStems()) {
                double weight = LunarUtils.getHiddenStemWeight(zhi, stem.getChinese());
                distribution.put(stem.getElement(), distribution.get(stem.getElement()) + weight / 10.0);
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
        
        String dayElement = BaziDef.TIAN_GAN_ELEMENT.get(dayMaster.getGan());
        if ("weak".equals(dayMaster.getStrength())) {
            // 日主弱,喜生扶
            favorable.add(dayElement); // 比劫
            favorable.add(BaziDef.ELEMENT_GENERATE.get(dayElement)); // 印星
            unfavorable.add(BaziDef.ELEMENT_CONQUER.get(dayElement)); // 官杀
        } else if ("strong".equals(dayMaster.getStrength())) {
            // 日主强,喜克泄
            unfavorable.add(dayElement); // 比劫
            unfavorable.add(BaziDef.ELEMENT_GENERATE.get(dayElement)); // 印星
            favorable.add(BaziDef.ELEMENT_CONQUER.get(dayElement)); // 官杀
        }
        
        // 计算五行状态
        String monthZhi = fourPillars.getMonth().getEarthlyBranch().getChinese();
        Map<String, Integer> stateWeights = BaziDef.ELEMENT_STATES_WEIGHT.get(monthZhi);
        Map<String, String> elementStates = new HashMap<>();
        if (stateWeights != null) {
            for (Map.Entry<String, Integer> entry : stateWeights.entrySet()) {
                String state = BaziDef.ELEMENT_STATE_DESC.get(entry.getValue());
                elementStates.put(entry.getKey(), state);
            }
        }
        
        String monthElement = BaziDef.DI_ZHI_ELEMENT.get(monthZhi);
        
        return FiveElementsDTO.builder()
            .distribution(distribution)
            .counts(counts)
            .strongest(strongest)
            .weakest(weakest)
            .favorable(favorable)
            .unfavorable(unfavorable)
            .elementStates(elementStates)
            .monthElement(monthElement)
            .build();
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
        String monthGan = fourPillars.getMonth().getHeavenlyStem().getChinese();
        String monthZhi = fourPillars.getMonth().getEarthlyBranch().getChinese();
        String dayGan = dayMaster.getGan();
        
        // 获取月令藏干 (取本气)
        List<HiddenStemDTO> hiddenStems = fourPillars.getMonth().getHiddenStems();
        String monthStem = hiddenStems.isEmpty() ? null : hiddenStems.get(0).getChinese();
        
        // 计算月令本气的十神
        String monthStemTenGod = monthStem != null ? getTenGod(dayGan, monthStem) : null;
        
        // 判断是否透出
        boolean isTransparent = monthGan.equals(monthStem);
        
        // 格局名称和描述
        String name = monthStemTenGod != null ? monthStemTenGod + "格" : "未定格局";
        String category = "normal";
        String description = "以月令" + monthZhi + "本气" + monthStem + "(" + monthStemTenGod + ")为用";
        
        return PatternDTO.builder()
            .name(name)
            .category(category)
            .description(description)
            .monthStem(monthStem)
            .monthStemTenGod(monthStemTenGod)
            .isTransparent(isTransparent)
            .build();
    }

    /**
     * 计算大运流年
     */
    private YunInfoDTO calculateYun(Yun yunObj, Lunar lunar) {
        int startAge = yunObj.getStartAge();
        boolean forward = yunObj.isForward();
        
        List<DaYunDTO> daYunList = new ArrayList<>();
        DaYun[] daYunArray = yunObj.getDaYun();
        
        int birthYear = lunar.getYear();
        
        for (int i = 0; i < Math.min(10, daYunArray.length); i++) {
            DaYun daYun = daYunArray[i];
            int daYunStartAge = startAge + i * 10;
            int daYunEndAge = daYunStartAge + 9;
            int daYunStartYear = birthYear + daYunStartAge;
            int daYunEndYear = birthYear + daYunEndAge;
            
            String ganZhi = daYun.getGanZhi();
            String gan = LunarUtils.extractGan(ganZhi);
            String zhi = LunarUtils.extractZhi(ganZhi);
            
            // 计算流年
            List<LiuNianDTO> liuNianList = new ArrayList<>();
            LiuNian[] liuNianArray = daYun.getLiuNian();
            
            for (int j = 0; j < Math.min(10, liuNianArray.length); j++) {
                LiuNian liuNian = liuNianArray[j];
                int age = daYunStartAge + j;
                int year = daYunStartYear + j;
                
                String lnGanZhi = liuNian.getGanZhi();
                String lnGan = LunarUtils.extractGan(lnGanZhi);
                String lnZhi = LunarUtils.extractZhi(lnGanZhi);
                
                liuNianList.add(LiuNianDTO.builder()
                    .year(year)
                    .age(age)
                    .ganZhi(lnGanZhi)
                    .gan(lnGan)
                    .zhi(lnZhi)
                    .build());
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
     */
    private ShenShaDTO calculateShenSha(Lunar lunar) {
        // 使用 lunar-java 的神煞 API
        List<String> yearShenSha = new ArrayList<>(lunar.getYearShenSha());
        List<String> monthShenSha = new ArrayList<>(lunar.getMonthShenSha());
        List<String> dayShenSha = new ArrayList<>(lunar.getDayShenSha());
        List<String> hourShenSha = new ArrayList<>(lunar.getTimeShenSha());
        
        return ShenShaDTO.builder()
            .year(yearShenSha)
            .month(monthShenSha)
            .day(dayShenSha)
            .hour(hourShenSha)
            .build();
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
