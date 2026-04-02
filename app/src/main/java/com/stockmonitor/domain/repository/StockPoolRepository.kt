package com.stockmonitor.domain.repository

import com.stockmonitor.domain.model.Stock
import kotlinx.coroutines.flow.Flow

/**
 * 股票池仓库接口
 * 定义股票池数据操作的抽象方法
 */
interface StockPoolRepository {

    /**
     * 获取所有股票
     * @return 股票列表 Flow
     */
    fun getAllStocks(): Flow<List<Stock>>

    /**
     * 搜索股票
     * @param keyword 搜索关键词（代码或名称）
     * @return 符合条件的股票列表 Flow
     */
    fun searchStocks(keyword: String): Flow<List<Stock>>

    /**
     * 根据 ID 获取股票
     * @param id 股票 ID
     * @return 股票实体，如果不存在返回 null
     */
    suspend fun getStockById(id: Long): Stock?

    /**
     * 添加股票
     * @param stock 股票实体
     * @return 新增股票的 ID
     */
    suspend fun addStock(stock: Stock): Long

    /**
     * 更新股票
     * @param stock 股票实体
     */
    suspend fun updateStock(stock: Stock)

    /**
     * 删除股票
     * @param stock 股票实体
     */
    suspend fun deleteStock(stock: Stock)

    /**
     * 批量删除股票
     * @param ids 股票 ID 列表
     */
    suspend fun deleteStocksByIds(ids: List<Long>)

    /**
     * 更新股票阈值
     * @param id 股票 ID
     * @param sellThreshold 卖出阈值
     * @param buyThreshold 买入阈值
     */
    suspend fun updateThreshold(id: Long, sellThreshold: Double?, buyThreshold: Double?)

    /**
     * 检查股票代码是否已存在
     * @param code 股票代码
     * @return 是否存在
     */
    suspend fun isStockCodeExists(code: String): Boolean

    /**
     * 更新股票排序
     * @param stockId 股票 ID
     * @param newSortOrder 新的排序值
     */
    suspend fun updateSortOrder(stockId: Long, newSortOrder: Int)

    /**
     * 批量更新排序
     * @param stocks 股票列表（包含新的排序值）
     */
    suspend fun reorderStocks(stocks: List<Stock>)
}
