package com.stockmonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * StockMonitor 数据库
 * 包含所有本地数据表
 */
@Database(
    entities = [StockDataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    /**
     * 获取股票数据访问对象
     */
    abstract fun stockDataDao(): StockDataDao

    companion object {
        const val DATABASE_NAME = "stock_monitor_db"
    }
}
