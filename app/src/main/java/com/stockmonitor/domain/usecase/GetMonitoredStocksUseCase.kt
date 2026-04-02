package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.model.MonitoredStock
import com.stockmonitor.domain.repository.StockMonitorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取监控股票用例
 */
class GetMonitoredStocksUseCase @Inject constructor(
    private val repository: StockMonitorRepository
) {
    operator fun invoke(): Flow<List<MonitoredStock>> {
        return repository.getMonitoredStocks()
    }
}
