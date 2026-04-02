package com.stockmonitor.presentation.ui.stockmonitor

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.stockmonitor.data.local.StockDao
import com.stockmonitor.domain.repository.StockMonitorRepository
import com.stockmonitor.domain.usecase.GetMonitoredStocksUseCase
import com.stockmonitor.service.StockPriceWorker
import com.stockmonitor.util.RefreshEventBus
import com.stockmonitor.util.RefreshStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 股票监控 ViewModel
 */
@HiltViewModel
class StockMonitorViewModel @Inject constructor(
    private val getMonitoredStocksUseCase: GetMonitoredStocksUseCase,
    private val stockDao: StockDao,
    private val stockMonitorRepository: StockMonitorRepository,
    private val refreshStateManager: RefreshStateManager,
    private val refreshEventBus: RefreshEventBus,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "StockMonitorVM"
    }

    private val _state = MutableStateFlow(StockMonitorState())
    val state: StateFlow<StockMonitorState> = _state.asStateFlow()

    init {
        loadRefreshState()
        loadMonitoredStocks()
        scheduleStockPriceWorker()
        observeRefreshEvent()
    }

    private fun observeRefreshEvent() {
        viewModelScope.launch {
            refreshEventBus.refreshEvent.collect {
                loadRefreshState()
                loadMonitoredStocks()
            }
        }
    }

    private fun loadRefreshState() {
        _state.update {
            it.copy(
                lastUpdateTime = refreshStateManager.getLastRefreshTime(),
                lastRefreshSuccess = refreshStateManager.getLastRefreshSuccess()
            )
        }
    }

    fun onEvent(event: StockMonitorEvent) {
        when (event) {
            is StockMonitorEvent.Refresh -> loadMonitoredStocks()
            is StockMonitorEvent.RefreshNow -> refreshNow()
            is StockMonitorEvent.RemoveMonitoring -> removeMonitoring(event.stockId)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun loadMonitoredStocks() {
        getMonitoredStocksUseCase()
            .onEach { stocks ->
                _state.update { it.copy(monitoredStocks = stocks, isLoading = false, error = null) }
            }
            .catch { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshNow() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try {
                val monitoredStocks = getMonitoredStocksUseCase().first()
                val codes = monitoredStocks.map { it.stock.code }
                if (codes.isEmpty()) {
                    Toast.makeText(context, "没有监控中的股票", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val result = stockMonitorRepository.refreshStockPrices(codes)
                result.fold(
                    onSuccess = { refreshResult ->
                        val now = System.currentTimeMillis()
                        refreshStateManager.saveRefreshState(true)
                        _state.update { it.copy(lastUpdateTime = now, lastRefreshSuccess = true) }
                        val message = if (refreshResult.failedCodes.isNotEmpty()) {
                            "成功: ${refreshResult.successCount}只，失败: ${refreshResult.failedCodes.joinToString()}"
                        } else {
                            "成功获取 ${refreshResult.successCount} 只股票价格"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        loadMonitoredStocks()
                    },
                    onFailure = { e ->
                        refreshStateManager.saveRefreshState(false)
                        _state.update { it.copy(lastRefreshSuccess = false) }
                        Toast.makeText(context, "刷新失败: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            } catch (e: Exception) {
                refreshStateManager.saveRefreshState(false)
                Toast.makeText(context, "刷新异常: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun removeMonitoring(stockId: Long) {
        viewModelScope.launch {
            try {
                stockDao.updateMonitoringStatus(stockId, false, System.currentTimeMillis())
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun scheduleStockPriceWorker() {
        Log.d(TAG, "开始调度定时任务")
        val workRequest = PeriodicWorkRequestBuilder<StockPriceWorker>(
            StockPriceWorker.REPEAT_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            StockPriceWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        Log.d(TAG, "定时任务调度完成")
    }
}
