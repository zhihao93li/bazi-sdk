package com.tafu.bazi.sdk.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 八字计算请求参数
 *
 * <p>包含出生时间、历法类型、性别等必要信息,以及可选的经纬度用于真太阳时修正
 *
 * @author Tafu Team
 * @version 1.0.0
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaziRequest {

  // ========== 时间参数 (必填) ==========

  /**
   * 出生年份
   *
   * <p>范围: 1901-2100 (受 lunar-java 库支持范围限制)
   */
  @NotNull(message = "年份不能为空")
  @Min(value = 1901, message = "年份不能早于1901年")
  @Max(value = 2100, message = "年份不能晚于2100年")
  private Integer year;

  /**
   * 出生月份
   *
   * <p>范围: 1-12
   */
  @NotNull(message = "月份不能为空")
  @Min(value = 1, message = "月份必须在1-12之间")
  @Max(value = 12, message = "月份必须在1-12之间")
  private Integer month;

  /**
   * 出生日期
   *
   * <p>范围: 1-31
   */
  @NotNull(message = "日期不能为空")
  @Min(value = 1, message = "日期必须在1-31之间")
  @Max(value = 31, message = "日期必须在1-31之间")
  private Integer day;

  /**
   * 出生小时
   *
   * <p>范围: 0-23
   */
  @NotNull(message = "小时不能为空")
  @Min(value = 0, message = "小时必须在0-23之间")
  @Max(value = 23, message = "小时必须在0-23之间")
  private Integer hour;

  /**
   * 出生分钟
   *
   * <p>范围: 0-59
   */
  @NotNull(message = "分钟不能为空")
  @Min(value = 0, message = "分钟必须在0-59之间")
  @Max(value = 59, message = "分钟必须在0-59之间")
  private Integer minute;

  // ========== 类型参数 (必填) ==========

  /**
   * 历法类型
   *
   * <p>可选值: "solar" (公历) | "lunar" (农历)
   */
  @NotBlank(message = "历法类型不能为空")
  @Pattern(regexp = "^(solar|lunar)$", message = "历法类型必须为 solar 或 lunar")
  private String calendarType;

  /**
   * 性别
   *
   * <p>可选值: "male" (男) | "female" (女)
   *
   * <p>用于计算大运顺逆
   */
  @NotBlank(message = "性别不能为空")
  @Pattern(regexp = "^(male|female)$", message = "性别必须为 male 或 female")
  private String gender;

  // ========== 可选参数 ==========

  /**
   * 是否闰月 (仅农历有效)
   *
   * <p>默认: false
   */
  private Boolean isLeapMonth;

  /**
   * 出生地经度 (用于真太阳时计算)
   *
   * <p>范围: -180.0 ~ 180.0 (东经为正,西经为负)
   *
   * <p>如果不提供,则不进行真太阳时修正
   *
   * <p>示例: 北京 116.4074, 上海 121.4737, 广州 113.2644
   */
  private Double longitude;

  /**
   * 出生地纬度 (可选,用于未来扩展)
   *
   * <p>范围: -90.0 ~ 90.0 (北纬为正,南纬为负)
   */
  private Double latitude;
}
