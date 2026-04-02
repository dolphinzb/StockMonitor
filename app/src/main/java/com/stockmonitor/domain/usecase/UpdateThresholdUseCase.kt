package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.repository.StockPoolRepository
import javax.inject.Inject

/**
 * 更新股票阈值用例
 * 负责验证和更新股票的卖出阈值、买入阈值
 * 规则：卖出阈值必须大于买入阈值
 */
class UpdateThresholdUseCase @Inject constructor(
    private val repository: StockPoolRepository
) {
    /**
     * 执行更新阈值操作
     * @param stockId 股票 ID
     * @param sellThreshold 卖出阈值（元）
     * @param buyThreshold 买入阈值（元）
     * @return Result
     */
    suspend operator fun invoke(
        stockId: Long,
        sellThreshold: Double?,
        buyThreshold: Double?
    ): Result<Unit> {
        if (sellThreshold != null && sellThreshold < 0) {
            return Result.failure(IllegalArgumentException("卖出阈值不能为负数"))
        }

        if (buyThreshold != null && buyThreshold < 0) {
            return Result.failure(IllegalArgumentException("买入阈值不能为负数"))
        }

        if (sellThreshold != null && buyThreshold != null && sellThreshold <= buyThreshold) {
            return Result.failure(IllegalArgumentException("卖出阈值必须大于买入阈值"))
        }

        repository.getStockById(stockId)
            ?: return Result.failure(IllegalArgumentException("股票不存在"))

        return try {
            repository.updateThreshold(stockId, sellThreshold, buyThreshold)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
