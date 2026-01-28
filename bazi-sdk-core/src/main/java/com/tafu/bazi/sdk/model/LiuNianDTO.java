package com.tafu.bazi.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流年 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiuNianDTO {
  /** 流年年份 */
  private int year;

  /** 当时年龄 */
  private int age;

  /** 流年干支 (如 "甲子") */
  private String ganZhi;

  /** 天干 */
  private String gan;

  /** 地支 */
  private String zhi;
}
