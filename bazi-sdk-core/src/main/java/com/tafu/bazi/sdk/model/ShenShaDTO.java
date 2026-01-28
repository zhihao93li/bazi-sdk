package com.tafu.bazi.sdk.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 神煞信息 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShenShaDTO {
  /** 年柱神煞 */
  private List<String> year;

  /** 月柱神煞 */
  private List<String> month;

  /** 日柱神煞 */
  private List<String> day;

  /** 时柱神煞 */
  private List<String> hour;
}
