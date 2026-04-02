package com.stockmonitor.domain.model

/**
 * 监控股票领域实体
 * 聚合了股票基本信息、阈值设置和最新价格数据
 *
 * @param stock 股票基础信息
 * @param stockData 最新价格数据（可能为null如果从未获取过）
 * @param lastAlertState 上次告警状态（用于防轰炸）
 */
data class MonitoredStock(
    val stock: Stock,
    val stockData: StockData?,
    val lastAlertState: AlertState
) {
    val code: String get() = stock.code
    val name: String get() = stock.name
    val sellThreshold: Double? get() = stock.sellThreshold
    val buyThreshold: Double? get() = stock.buyThreshold
    val currentPrice: Double? get() = stockData?.currentPrice
    val lastUpdateTime: Long? get() = stockData?.updateTime
    val hasThreshold: Boolean get() = sellThreshold != null || buyThreshold != null
}
