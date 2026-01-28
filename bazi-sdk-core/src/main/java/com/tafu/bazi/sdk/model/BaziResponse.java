package com.tafu.bazi.sdk.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 八字计算响应结果
 *
 * <p>包含完整的八字排盘、日主分析、五行统计、十神计算、格局判断、大运流年等信息
 *
 * @author Tafu Team
 * @version 1.0.0
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaziResponse {

  // ========== 基本信息 ==========

  /** 性别: "male" | "female" */
  private String gender;

  /** 公历日期时间 (格式: "1990-06-15 14:30:00") */
  private String solarDate;

  /** 农历日期描述 (格式: "庚午年 壬午月 廿三 未时") */
  private String lunarDate;

  /** 真太阳时信息 (如果提供了 longitude) */
  private TrueSolarTimeDTO trueSolarTime;

  // ========== 核心八字数据 ==========

  /** 四柱 (年月日时) - 包含天干地支、纳音、藏干、空亡等完整信息 */
  private FourPillarsDTO fourPillars;

  // ========== 分析数据 ==========

  /** 日主强弱分析 */
  private DayMasterDTO dayMaster;

  /** 五行统计分析 */
  private FiveElementsDTO fiveElements;

  /** 十神分析 */
  private TenGodsDTO tenGods;

  /** 格局判断 */
  private PatternDTO pattern;

  /** 大运流年信息 */
  private YunInfoDTO yun;

  /** 神煞信息 */
  private ShenShaDTO shenSha;

  // ========== 其他信息 ==========

  /** 生肖 (如 "马") */
  private String shengXiao;

  /** 胎元 (如 "癸酉") */
  private String taiYuan;

  /** 命宫 (如 "甲戌") */
  private String mingGong;

  /** 身宫 (如 "丙子") */
  private String shenGong;

  /** 空亡 (如 "辰巳") */
  private String xunKong;

  /** 日主特征描述列表 */
  private List<String> dayMasterCharacteristics;
}
