package com.tafu.bazi.sdk.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 运程信息 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YunInfoDTO {
  /** 起运年龄 */
  private int startAge;

  /** 是否顺行 */
  private boolean forward;

  /** 十步大运列表 */
  private List<DaYunDTO> daYunList;
}
