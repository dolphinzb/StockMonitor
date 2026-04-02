package com.stockmonitor.domain.model

/**
 * 用户股票配置实体
 * 包含关注的股票列表、刷新间隔偏好、告警阈值
 *
 * @param stockCodes 关注的股票代码列表
 * @param refreshIntervalMinutes 刷新间隔（分钟）
 * @param priceAlertThreshold 价格波动告警阈值（百分比）
 */
data class StockConfig(
    val stockCodes: List<String>,
    val refreshIntervalMinutes: Int = 3,
    val priceAlertThreshold: Double = 5.0
)
