package com.tafu.bazi.sdk.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大运 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaYunDTO {
  /** 大运序号 (0-9) */
  private int index;

  /** 起始年龄 */
  private int startAge;

  /** 结束年龄 */
  private int endAge;

  /** 干支 (如 "癸未") */
  private String ganZhi;

  /** 天干 */
  private String gan;

  /** 地支 */
  private String zhi;

  /** 起始年份 */
  private int startYear;

  /** 结束年份 */
  private int endYear;

  /** 流年列表 */
  private List<LiuNianDTO> liuNian;
}
