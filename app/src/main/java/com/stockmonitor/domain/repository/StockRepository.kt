package com.stockmonitor.domain.repository

import com.stockmonitor.domain.model.StockData
import kotlinx.coroutines.flow.Flow

/**
 * 股票数据仓库接口
 * 定义股票数据获取的抽象方法
 */
interface StockRepository {
    /**
     * 获取股票数据列表
     * @param codes 股票代码列表
     * @return 股票数据流
     */
    fun getStockData(codes: List<String>): Flow<List<StockData>>

    /**
     * 刷新股票数据
     * @param codes 股票代码列表
     * @return 是否刷新成功
     */
    suspend fun refreshStockData(codes: List<String>): Result<Unit>

    /**
     * 获取缓存的股票数据
     * @param codes 股票代码列表
     * @return 缓存的股票数据
     */
    suspend fun getCachedStockData(codes: List<String>): List<StockData>
}
