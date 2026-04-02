package com.stockmonitor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 股票数据访问对象 (DAO)
 * 定义股票数据的数据库操作
 */
@Dao
interface StockDataDao {
    /**
     * 查询所有股票数据
     */
    @Query("SELECT * FROM stock_data")
    fun getAllStockData(): Flow<List<StockDataEntity>>

    /**
     * 根据股票代码查询股票数据
     */
    @Query("SELECT * FROM stock_data WHERE code IN (:codes)")
    fun getStockDataByCodes(codes: List<String>): Flow<List<StockDataEntity>>

    /**
     * 根据股票代码查询单条股票数据
     */
    @Query("SELECT * FROM stock_data WHERE code = :code")
    suspend fun getStockDataByCode(code: String): StockDataEntity?

    /**
     * 批量插入或更新股票数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stocks: List<StockDataEntity>)

    /**
     * 删除所有股票数据
     */
    @Query("DELETE FROM stock_data")
    suspend fun deleteAll()

    /**
     * 删除指定股票代码的数据
     */
    @Query("DELETE FROM stock_data WHERE code IN (:codes)")
    suspend fun deleteByCodes(codes: List<String>)
}
