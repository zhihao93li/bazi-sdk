package com.tafu.bazi.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地支 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarthlyBranchDTO {
  /** 中文名 (如 "子") */
  private String chinese;

  /** 本气五行 (wood/fire/earth/metal/water) */
  private String element;
}
