package com.stockmonitor.domain.repository

import com.stockmonitor.domain.model.AlertState
import com.stockmonitor.domain.model.MonitoredStock
import com.stockmonitor.domain.model.RefreshResult
import com.stockmonitor.domain.model.StockData
import kotlinx.coroutines.flow.Flow

/**
 * 股票监控仓库接口
 */
interface StockMonitorRepository {
    /**
     * 获取所有已开启监控的股票列表
     */
    fun getMonitoredStocks(): Flow<List<MonitoredStock>>

    /**
     * 获取指定股票的最新价格数据
     */
    suspend fun getStockData(code: String): Result<StockData>

    /**
     * 批量获取股票价格（用于定时拉取）
     * @return RefreshResult 包含成功数量、失败代码列表和股票数据
     */
    suspend fun refreshStockPrices(codes: List<String>): Result<RefreshResult>

    /**
     * 更新告警状态
     */
    suspend fun updateAlertState(code: String, alertState: AlertState)

    /**
     * 保存价格数据到本地缓存
     */
    suspend fun cacheStockData(stockData: StockData)

    /**
     * 获取指定股票代码的告警状态
     */
    suspend fun getAlertState(code: String): AlertState
}
