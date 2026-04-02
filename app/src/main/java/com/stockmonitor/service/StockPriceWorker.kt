package com.stockmonitor.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stockmonitor.data.local.StockDao
import com.stockmonitor.data.remote.StockApiService
import com.stockmonitor.domain.model.AlertState
import com.stockmonitor.domain.model.AlertType
import com.stockmonitor.domain.model.MonitoredStock
import com.stockmonitor.domain.repository.StockMonitorRepository
import com.stockmonitor.domain.usecase.CheckThresholdsUseCase
import com.stockmonitor.domain.usecase.SendNotificationUseCase
import com.stockmonitor.util.RefreshEventBus
import com.stockmonitor.util.RefreshStateManager
import com.stockmonitor.util.TradingTimeChecker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * 股票价格定时拉取 Worker
 * 使用 WorkManager 实现周期性任务，在交易时间段内每3分钟拉取一次股票价格
 */
@HiltWorker
class StockPriceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val stockDao: StockDao,
    private val stockMonitorRepository: StockMonitorRepository,
    private val stockApiService: StockApiService,
    private val checkThresholdsUseCase: CheckThresholdsUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val refreshStateManager: RefreshStateManager,
    private val refreshEventBus: RefreshEventBus
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "StockPriceWorker"
        const val WORK_NAME = "stock_price_worker"
        const val REPEAT_INTERVAL_MINUTES = 3L
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "定时任务开始执行")
        // 检查是否为交易时间
        if (!TradingTimeChecker.isTradingTime()) {
            Log.d(TAG, "非交易时间，任务跳过")
            return Result.success()
        }

        return try {
            // 获取所有监控中的股票
            val monitoredStocks = stockMonitorRepository.getMonitoredStocks().first()

            if (monitoredStocks.isEmpty()) {
                Log.d(TAG, "没有监控股票，任务跳过")
                return Result.success()
            }

            // 批量获取股票价格
            val codes = monitoredStocks.map { it.code }
            val refreshResult = stockMonitorRepository.refreshStockPrices(codes)

            if (refreshResult.isSuccess) {
                val refreshData = refreshResult.getOrNull() ?: return Result.success()
                val stockDataMap = refreshData.stockDataList.associateBy { it.code }

                // 对每支股票检查阈值并发送通知
                for (monitoredStock in monitoredStocks) {
                    val stockData = stockDataMap[monitoredStock.code] ?: continue
                    checkAndNotify(monitoredStock, stockData)
                }

                refreshStateManager.saveRefreshState(true)
                refreshEventBus.emitRefresh()
                Log.d(TAG, "定时任务执行成功")
                Result.success()
            } else {
                refreshStateManager.saveRefreshState(false)
                Log.d(TAG, "定时任务执行失败，将重试")
                Result.retry()
            }
        } catch (e: Exception) {
            refreshStateManager.saveRefreshState(false)
            Log.e(TAG, "定时任务异常: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun checkAndNotify(monitoredStock: MonitoredStock, stockData: com.stockmonitor.domain.model.StockData) {
        val currentAlertState = monitoredStock.lastAlertState
        val checkResult = checkThresholdsUseCase(stockData, monitoredStock)

        if (checkResult.isExceeded) {
            // 价格超出阈值，检查是否需要发送通知
            if (currentAlertState.canSendNotification()) {
                // 发送通知
                sendNotificationUseCase(monitoredStock, checkResult.alertType, checkResult.currentPrice)

                // 更新告警状态
                stockMonitorRepository.updateAlertState(
                    monitoredStock.code,
                    AlertState(
                        alertType = checkResult.alertType,
                        lastAlertTime = System.currentTimeMillis()
                    )
                )
            }
        } else {
            // 价格恢复正常，清除告警状态
            if (currentAlertState.alertType != AlertType.NONE) {
                stockMonitorRepository.updateAlertState(
                    monitoredStock.code,
                    AlertState(alertType = AlertType.NONE, lastAlertTime = 0L)
                )
            }
        }
    }
}
