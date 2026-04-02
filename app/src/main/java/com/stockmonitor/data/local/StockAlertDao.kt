package com.stockmonitor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 股票告警状态数据访问对象 (DAO)
 */
@Dao
interface StockAlertDao {
    @Query("SELECT * FROM stock_alert_state WHERE code = :code")
    suspend fun getAlertState(code: String): StockAlertEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: StockAlertEntity)

    @Query("DELETE FROM stock_alert_state WHERE code = :code")
    suspend fun deleteByCode(code: String)
}
