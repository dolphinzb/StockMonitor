package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.repository.StockRepository
import javax.inject.Inject

/**
 * 刷新股票数据用例
 * 封装刷新股票数据的业务逻辑
 */
class RefreshStockDataUseCase @Inject constructor(
    private val repository: StockRepository
) {
    /**
     * 执行刷新股票数据
     * @param codes 股票代码列表
     * @return 刷新结果
     */
    suspend operator fun invoke(codes: List<String>): Result<Unit> {
        return repository.refreshStockData(codes)
    }
}
