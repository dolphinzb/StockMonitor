package com.stockmonitor.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stockmonitor.data.local.StockDao
import com.stockmonitor.data.remote.StockApiService
import com.stockmonitor.domain.repository.SettingsRepository
import com.stockmonitor.domain.repository.StockMonitorRepository
import com.stockmonitor.domain.usecase.CheckThresholdsUseCase
import com.stockmonitor.domain.usecase.SendNotificationUseCase
import com.stockmonitor.util.RefreshEventBus
import com.stockmonitor.util.RefreshStateManager
import com.stockmonitor.util.TradingTimeChecker
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class StockPriceWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG = "StockPriceWorker"

    private val stockDao: StockDao by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).stockDao()
    }

    private val stockMonitorRepository: StockMonitorRepository by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).stockMonitorRepository()
    }

    private val settingsRepository: SettingsRepository by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).settingsRepository()
    }

    private val stockApiService: StockApiService by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).stockApiService()
    }

    private val checkThresholdsUseCase: CheckThresholdsUseCase by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).checkThresholdsUseCase()
    }

    private val sendNotificationUseCase: SendNotificationUseCase by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).sendNotificationUseCase()
    }

    private val refreshStateManager: RefreshStateManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).refreshStateManager()
    }

    private val refreshEventBus: RefreshEventBus by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            StockPriceWorkerEntryPoint::class.java
        ).refreshEventBus()
    }

    companion object {
        const val TAG = "StockPriceWorker"
        const val WORK_NAME = "stock_price_worker"
        const val REPEAT_INTERVAL_MINUTES = 3L
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "定时任务开始执行")

        val settings = settingsRepository.getSettingsWithDefaults()

        if (!TradingTimeChecker.isTradingTime(
                settings.morningStartTime,
                settings.morningEndTime,
                settings.afternoonStartTime,
                settings.afternoonEndTime
            )
        ) {
            Log.d(TAG, "非交易时间，任务跳过")
            return Result.success()
        }

        return try {
            val monitoredStocks = stockMonitorRepository.getMonitoredStocks().first()

            if (monitoredStocks.isEmpty()) {
                Log.d(TAG, "没有监控股票，任务跳过")
                return Result.success()
            }

            val codes = monitoredStocks.map { it.code }
            val refreshResult = stockMonitorRepository.refreshStockPrices(codes)

            if (refreshResult.isSuccess) {
                val refreshData = refreshResult.getOrNull() ?: return Result.success()
                val stockDataMap = refreshData.stockDataList.associateBy { it.code }

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
            Log.e(TAG, "定时任务执行异常", e)
            refreshStateManager.saveRefreshState(false)
            Result.retry()
        }
    }

    private suspend fun checkAndNotify(
        monitoredStock: com.stockmonitor.domain.model.MonitoredStock,
        stockData: com.stockmonitor.domain.model.StockData
    ) {
        val thresholdResult = checkThresholdsUseCase(stockData, monitoredStock)
        if (thresholdResult.isExceeded) {
            sendNotificationUseCase(
                monitoredStock = monitoredStock,
                alertType = thresholdResult.alertType ?: com.stockmonitor.domain.model.AlertType.SELL_THRESHOLD,
                currentPrice = stockData.currentPrice
            )
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface StockPriceWorkerEntryPoint {
    fun stockDao(): StockDao
    fun stockMonitorRepository(): StockMonitorRepository
    fun settingsRepository(): SettingsRepository
    fun stockApiService(): StockApiService
    fun checkThresholdsUseCase(): CheckThresholdsUseCase
    fun sendNotificationUseCase(): SendNotificationUseCase
    fun refreshStateManager(): RefreshStateManager
    fun refreshEventBus(): RefreshEventBus
}
