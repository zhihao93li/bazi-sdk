package com.tafu.bazi.sdk.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 十神分析 DTO
 *
 * @author Tafu Team
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenGodsDTO {
  /** 十神映射:十神名称 -> 详细信息 */
  private Map<String, TenGodInfoDTO> gods;
}
