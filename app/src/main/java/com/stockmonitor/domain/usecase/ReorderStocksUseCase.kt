package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.model.Stock
import com.stockmonitor.domain.repository.StockPoolRepository
import javax.inject.Inject

/**
 * 调整股票顺序用例
 * 负责更新股票的排序权重
 */
class ReorderStocksUseCase @Inject constructor(
    private val repository: StockPoolRepository
) {
    /**
     * 移动股票到新位置
     * @param stocks 重新排序后的股票列表
     * @return Result
     */
    suspend operator fun invoke(stocks: List<Stock>): Result<Unit> {
        if (stocks.isEmpty()) {
            return Result.success(Unit)
        }

        return try {
            val updatedStocks = stocks.mapIndexed { index, stock ->
                stock.copy(
                    sortOrder = index,
                    updatedAt = System.currentTimeMillis()
                )
            }
            repository.reorderStocks(updatedStocks)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
