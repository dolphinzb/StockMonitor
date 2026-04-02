package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.model.Stock
import com.stockmonitor.domain.repository.StockPoolRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取股票列表用例
 * 负责从数据库获取股票列表，支持搜索
 */
class GetStockListUseCase @Inject constructor(
    private val repository: StockPoolRepository
) {
    /**
     * 获取所有股票
     * @return 股票列表 Flow
     */
    operator fun invoke(): Flow<List<Stock>> {
        return repository.getAllStocks()
    }

    /**
     * 搜索股票
     * @param keyword 搜索关键词
     * @return 符合条件的股票列表 Flow
     */
    fun search(keyword: String): Flow<List<Stock>> {
        return if (keyword.isBlank()) {
            repository.getAllStocks()
        } else {
            repository.searchStocks(keyword.trim())
        }
    }
}
