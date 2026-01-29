package com.tafu.bazi.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 藏干 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiddenStemDTO {
  /** 中文名 (如 "甲") */
  private String chinese;

  /** 五行 (wood/fire/earth/metal/water) */
  private String element;

  /** 阴阳 (yang/yin) */
  private String yinYang;

  /** 十神 (比肩、劫财、食神、伤官、偏财、正财、七杀、正官、偏印、正印) */
  private String tenGod;
}
