package com.tafu.bazi.sdk.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单柱 DTO (年/月/日/时柱)
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PillarDTO {
  /** 天干 */
  private HeavenlyStemDTO heavenlyStem;

  /** 地支 */
  private EarthlyBranchDTO earthlyBranch;

  /** 纳音 (如 "路旁土") */
  private String naYin;

  /** 藏干列表 */
  private List<HiddenStemDTO> hiddenStems;

  /** 空亡 (如 "辰巳") */
  private String xunKong;

  /** 十神 (相对日主) */
  private String tenGod;
}
