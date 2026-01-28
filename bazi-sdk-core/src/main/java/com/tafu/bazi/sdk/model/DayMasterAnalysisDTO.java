package com.tafu.bazi.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日主分析 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayMasterAnalysisDTO {
  /** 得令分数 (-20 ~ 40) */
  private double deLing;

  /** 得令描述 */
  private String deLingDesc;

  /** 得地分数 (0 ~ 30) */
  private double deDi;

  /** 得地描述 */
  private String deDiDesc;

  /** 天干帮扶 (-20 ~ 20) */
  private double tianGanHelp;

  /** 帮扶描述 */
  private String tianGanHelpDesc;

  /** 总分 (0 ~ 100) */
  private double totalScore;
}
