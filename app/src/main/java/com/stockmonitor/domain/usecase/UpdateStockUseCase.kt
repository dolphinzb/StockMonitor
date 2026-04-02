package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.model.Stock
import com.stockmonitor.domain.repository.StockPoolRepository
import javax.inject.Inject

/**
 * 更新股票用例
 * 负责更新股票的基本信息（名称等）
 */
class UpdateStockUseCase @Inject constructor(
    private val repository: StockPoolRepository
) {
    /**
     * 执行更新股票操作
     * @param stock 股票实体（包含要更新的信息）
     * @return Result
     */
    suspend operator fun invoke(stock: Stock): Result<Unit> {
        val trimmedName = stock.name.trim()

        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("股票名称不能为空"))
        }

        val existingStock = repository.getStockById(stock.id)
            ?: return Result.failure(IllegalArgumentException("股票不存在"))

        val updatedStock = existingStock.copy(
            name = trimmedName,
            updatedAt = System.currentTimeMillis()
        )

        return try {
            repository.updateStock(updatedStock)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
