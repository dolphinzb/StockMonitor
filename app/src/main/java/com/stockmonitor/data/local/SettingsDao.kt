package com.stockmonitor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stockmonitor.data.local.entity.SettingsEntity

/**
 * 设置数据访问接口
 * 提供设置的CRUD操作
 */
@Dao
interface SettingsDao {

    /**
     * 获取设置
     * @return 设置实体，如果不存在则返回null
     */
    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): SettingsEntity?

    /**
     * 插入或更新设置
     * 使用REPLACE策略，当id冲突时替换整行
     *
     * @param settings 设置实体
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: SettingsEntity)

    /**
     * 检查设置是否存在
     * @return true表示存在，false表示不存在
     */
    @Query("SELECT EXISTS(SELECT 1 FROM settings WHERE id = 1)")
    suspend fun exists(): Boolean
}
