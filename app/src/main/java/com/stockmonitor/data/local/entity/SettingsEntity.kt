package com.stockmonitor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stockmonitor.domain.model.SettingsConfig
import com.stockmonitor.domain.model.StockApiSource

/**
 * 设置实体
 * 用于Room数据库持久化存储用户设置
 *
 * @property id 主键，固定为1
 * @property morningStartTime 上午交易开始时间 (HH:mm)
 * @property morningEndTime 上午交易结束时间 (HH:mm)
 * @property afternoonStartTime 下午交易开始时间 (HH:mm)
 * @property afternoonEndTime 下午交易结束时间 (HH:mm)
 * @property refreshIntervalMinutes 监控刷新间隔 (分钟)
 * @property apiSource API来源 (SINA/TENCENT/TONGHUASHUN)
 */
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Long = 1,
    val morningStartTime: String,
    val morningEndTime: String,
    val afternoonStartTime: String,
    val afternoonEndTime: String,
    val refreshIntervalMinutes: Int,
    val apiSource: String
) {
    /**
     * 转换为领域模型SettingsConfig
     */
    fun toDomainModel(): SettingsConfig {
        return SettingsConfig(
            morningStartTime = morningStartTime,
            morningEndTime = morningEndTime,
            afternoonStartTime = afternoonStartTime,
            afternoonEndTime = afternoonEndTime,
            refreshIntervalMinutes = refreshIntervalMinutes,
            apiSource = StockApiSource.valueOf(apiSource)
        )
    }

    companion object {
        /**
         * 从领域模型创建设置实体
         */
        fun fromDomainModel(config: SettingsConfig): SettingsEntity {
            return SettingsEntity(
                id = 1,
                morningStartTime = config.morningStartTime,
                morningEndTime = config.morningEndTime,
                afternoonStartTime = config.afternoonStartTime,
                afternoonEndTime = config.afternoonEndTime,
                refreshIntervalMinutes = config.refreshIntervalMinutes,
                apiSource = config.apiSource.name
            )
        }

        /**
         * 创建默认设置实体
         */
        fun createDefault(): SettingsEntity {
            return fromDomainModel(SettingsConfig.DEFAULT)
        }
    }
}
