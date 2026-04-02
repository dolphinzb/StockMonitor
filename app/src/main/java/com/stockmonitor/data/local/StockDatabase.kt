package com.stockmonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stockmonitor.data.local.entity.StockEntity

/**
 * StockMonitor 数据库
 * 包含所有本地数据表
 */
@Database(
    entities = [StockDataEntity::class, StockEntity::class, StockAlertEntity::class],
    version = 3,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    /**
     * 获取股票数据访问对象
     */
    abstract fun stockDataDao(): StockDataDao

    /**
     * 获取股票池数据访问对象
     */
    abstract fun stockDao(): StockDao

    /**
     * 获取告警状态数据访问对象
     */
    abstract fun stockAlertDao(): StockAlertDao

    companion object {
        const val DATABASE_NAME = "stock_monitor_db"
    }
}
