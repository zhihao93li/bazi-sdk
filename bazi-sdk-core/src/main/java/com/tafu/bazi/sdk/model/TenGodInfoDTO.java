package com.tafu.bazi.sdk.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 十神详情 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenGodInfoDTO {
  /** 十神名称 (如 "正财") */
  private String name;

  /** 出现次数 */
  private int count;

  /** 位置列表 (如 ["年干", "月干"]) */
  private List<String> positions;
}
