package com.stockmonitor.presentation.ui.stockmonitor

import com.stockmonitor.domain.model.MonitoredStock

/**
 * 股票监控页面状态
 */
data class StockMonitorState(
    val monitoredStocks: List<MonitoredStock> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val lastUpdateTime: Long = 0L,
    val lastRefreshSuccess: Boolean? = null
)

/**
 * 股票监控页面事件
 */
sealed class StockMonitorEvent {
    data object Refresh : StockMonitorEvent()
    data object RefreshNow : StockMonitorEvent()
    data class RemoveMonitoring(val stockId: Long) : StockMonitorEvent()
}
