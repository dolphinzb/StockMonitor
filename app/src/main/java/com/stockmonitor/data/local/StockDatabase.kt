package com.stockmonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stockmonitor.data.local.entity.SettingsEntity
import com.stockmonitor.data.local.entity.StockEntity
import com.stockmonitor.data.local.StockAlertEntity
import com.stockmonitor.data.local.StockDataEntity

/**
 * StockMonitor 数据库
 * 包含所有本地数据表
 */
@Database(
    entities = [StockDataEntity::class, StockEntity::class, StockAlertEntity::class, SettingsEntity::class],
    version = 4,
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

    /**
     * 获取设置数据访问对象
     */
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val DATABASE_NAME = "stock_monitor_db"
    }
}
