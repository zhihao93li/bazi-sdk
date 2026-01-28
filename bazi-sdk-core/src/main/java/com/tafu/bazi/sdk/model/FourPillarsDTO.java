package com.tafu.bazi.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 四柱 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FourPillarsDTO {
  /** 年柱 */
  private PillarDTO year;

  /** 月柱 */
  private PillarDTO month;

  /** 日柱 */
  private PillarDTO day;

  /** 时柱 */
  private PillarDTO hour;
}
