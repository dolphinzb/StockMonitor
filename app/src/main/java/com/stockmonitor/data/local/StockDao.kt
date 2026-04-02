package com.stockmonitor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stockmonitor.data.local.entity.StockEntity
import kotlinx.coroutines.flow.Flow

/**
 * 股票数据访问对象 (DAO)
 * 提供股票池数据的 CRUD 操作接口
 */
@Dao
interface StockDao {

    /**
     * 获取所有股票，按 sortOrder 排序
     */
    @Query("SELECT * FROM stocks ORDER BY sortOrder ASC")
    fun getAllStocks(): Flow<List<StockEntity>>

    /**
     * 搜索股票（按代码或名称模糊搜索）
     */
    @Query("SELECT * FROM stocks WHERE code LIKE '%' || :keyword || '%' OR name LIKE '%' || :keyword || '%' ORDER BY sortOrder ASC")
    fun searchStocks(keyword: String): Flow<List<StockEntity>>

    /**
     * 根据 ID 获取单个股票
     */
    @Query("SELECT * FROM stocks WHERE id = :id")
    suspend fun getStockById(id: Long): StockEntity?

    /**
     * 添加股票
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStock(stock: StockEntity): Long

    /**
     * 更新股票
     */
    @Update
    suspend fun updateStock(stock: StockEntity)

    /**
     * 删除单支股票
     */
    @Delete
    suspend fun deleteStock(stock: StockEntity)

    /**
     * 根据 ID 列表批量删除股票
     */
    @Query("DELETE FROM stocks WHERE id IN (:ids)")
    suspend fun deleteStocksByIds(ids: List<Long>)

    /**
     * 更新股票阈值
     */
    @Query("UPDATE stocks SET sellThreshold = :sellThreshold, buyThreshold = :buyThreshold, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateThreshold(id: Long, sellThreshold: Double?, buyThreshold: Double?, updatedAt: Long)

    /**
     * 检查股票代码是否已存在
     */
    @Query("SELECT EXISTS(SELECT 1 FROM stocks WHERE code = :code)")
    suspend fun isStockCodeExists(code: String): Boolean

    /**
     * 获取股票数量
     */
    @Query("SELECT COUNT(*) FROM stocks")
    suspend fun getStockCount(): Int

    /**
     * 更新股票排序
     */
    @Query("UPDATE stocks SET sortOrder = :sortOrder, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int, updatedAt: Long)

    @Query("SELECT * FROM stocks WHERE isMonitoring = 1 ORDER BY sortOrder ASC")
    fun getMonitoredStocks(): Flow<List<StockEntity>>

    @Query("UPDATE stocks SET isMonitoring = :isMonitoring, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateMonitoringStatus(id: Long, isMonitoring: Boolean, updatedAt: Long)

    @Query("SELECT * FROM stocks WHERE isMonitoring = 1 ORDER BY sortOrder ASC")
    suspend fun getMonitoredStocksOnce(): List<StockEntity>

    /**
     * 批量更新排序
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stocks: List<StockEntity>)
}
