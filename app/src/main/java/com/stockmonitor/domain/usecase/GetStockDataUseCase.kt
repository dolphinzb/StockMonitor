package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.model.StockData
import com.stockmonitor.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取股票数据用例
 * 封装获取股票数据的业务逻辑
 */
class GetStockDataUseCase @Inject constructor(
    private val repository: StockRepository
) {
    /**
     * 执行获取股票数据
     * @param codes 股票代码列表
     * @return 股票数据流
     */
    operator fun invoke(codes: List<String>): Flow<List<StockData>> {
        return repository.getStockData(codes)
    }
}
