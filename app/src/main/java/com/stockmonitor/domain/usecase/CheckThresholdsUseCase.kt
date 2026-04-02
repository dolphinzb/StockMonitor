package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.model.AlertState
import com.stockmonitor.domain.model.AlertType
import com.stockmonitor.domain.model.MonitoredStock
import com.stockmonitor.domain.model.StockData
import javax.inject.Inject

/**
 * 检查阈值用例
 * 比较拉取到的股票价格是否高于卖出阈值或低于买入阈值
 */
class CheckThresholdsUseCase @Inject constructor() {
    /**
     * 检查股票价格是否超出阈值
     *
     * @param stockData 最新股票价格数据
     * @param monitoredStock 监控股票信息（包含阈值设置）
     * @return 检查结果，包含是否超出阈值和对应的告警类型
     */
    operator fun invoke(stockData: StockData, monitoredStock: MonitoredStock): ThresholdCheckResult {
        val currentPrice = stockData.currentPrice
        val sellThreshold = monitoredStock.sellThreshold
        val buyThreshold = monitoredStock.buyThreshold

        return when {
            sellThreshold != null && currentPrice > sellThreshold -> {
                ThresholdCheckResult(
                    isExceeded = true,
                    alertType = AlertType.SELL_THRESHOLD,
                    thresholdValue = sellThreshold,
                    currentPrice = currentPrice
                )
            }
            buyThreshold != null && currentPrice < buyThreshold -> {
                ThresholdCheckResult(
                    isExceeded = true,
                    alertType = AlertType.BUY_THRESHOLD,
                    thresholdValue = buyThreshold,
                    currentPrice = currentPrice
                )
            }
            else -> {
                ThresholdCheckResult(
                    isExceeded = false,
                    alertType = AlertType.NONE,
                    thresholdValue = null,
                    currentPrice = currentPrice
                )
            }
        }
    }
}

/**
 * 阈值检查结果
 */
data class ThresholdCheckResult(
    val isExceeded: Boolean,
    val alertType: AlertType,
    val thresholdValue: Double?,
    val currentPrice: Double
)
