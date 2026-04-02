package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.repository.StockPoolRepository
import javax.inject.Inject

/**
 * 删除股票用例
 * 支持单删和批量删除
 */
class DeleteStocksUseCase @Inject constructor(
    private val repository: StockPoolRepository
) {
    /**
     * 删除单支股票
     * @param stockId 股票 ID
     */
    suspend operator fun invoke(stockId: Long): Result<Unit> {
        return try {
            repository.deleteStocksByIds(listOf(stockId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量删除股票
     * @param stockIds 股票 ID 列表
     */
    suspend fun deleteMultiple(stockIds: List<Long>): Result<Unit> {
        if (stockIds.isEmpty()) {
            return Result.failure(IllegalArgumentException("请选择要删除的股票"))
        }
        return try {
            repository.deleteStocksByIds(stockIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
