package com.tafu.bazi.sdk;

import com.tafu.bazi.sdk.model.BaziRequest;
import com.tafu.bazi.sdk.model.BaziResponse;

/**
 * 八字计算器核心接口
 *
 * <p>提供八字排盘、日主分析、五行统计、十神计算、格局判断等完整功能
 *
 * @author Tafu Team
 * @version 1.0.0
 * @since 2026-01-27
 */
public interface BaziCalculator {

  /**
   * 核心方法: 计算完整八字信息
   *
   * @param request 八字计算请求参数
   * @return 完整的八字分析结果
   * @throws IllegalArgumentException 参数校验失败时抛出
   */
  BaziResponse calculate(BaziRequest request);

  /**
   * 辅助方法: 查询指定年份的闰月
   *
   * @param lunarYear 农历年份 (1901-2100)
   * @return 闰月月份 (0 表示无闰月, 1-12 表示闰几月)
   * @throws IllegalArgumentException 年份超出支持范围时抛出
   */
  int getLeapMonth(int lunarYear);
}
