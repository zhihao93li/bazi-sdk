package com.tafu.bazi.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日主 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayMasterDTO {
  /** 日主天干 (如 "甲") */
  private String gan;

  /** 强弱: "weak" | "balanced" | "strong" */
  private String strength;

  /** 详细分析 */
  private DayMasterAnalysisDTO analysis;
}
