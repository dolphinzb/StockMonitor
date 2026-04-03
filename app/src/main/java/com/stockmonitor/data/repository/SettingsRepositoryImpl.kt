package com.stockmonitor.data.repository

import com.stockmonitor.data.local.SettingsDao
import com.stockmonitor.data.local.entity.SettingsEntity
import com.stockmonitor.domain.model.SettingsConfig
import com.stockmonitor.domain.repository.SettingsRepository
import com.stockmonitor.domain.repository.SettingsSaveResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设置仓储实现
 * 处理设置的持久化和验证逻辑
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override suspend fun getSettings(): SettingsConfig? {
        return settingsDao.getSettings()?.toDomainModel()
    }

    override suspend fun getSettingsWithDefaults(): SettingsConfig {
        val existing = getSettings()
        if (existing != null) {
            return existing
        }

        val defaultEntity = SettingsEntity.createDefault()
        settingsDao.insertOrUpdate(defaultEntity)
        return SettingsConfig.DEFAULT
    }

    override suspend fun saveSettings(settings: SettingsConfig): SettingsSaveResult {
        val validationError = validateSettings(settings)
        if (validationError != null) {
            return SettingsSaveResult.Error(validationError)
        }

        val currentSettings = getSettings()
        val intervalChanged = currentSettings?.refreshIntervalMinutes != settings.refreshIntervalMinutes

        settingsDao.insertOrUpdate(SettingsEntity.fromDomainModel(settings))

        return if (intervalChanged) {
            SettingsSaveResult.SuccessRequiresReschedule
        } else {
            SettingsSaveResult.SuccessNoReschedule
        }
    }

    /**
     * 验证设置的有效性
     * @return 错误信息，如果验证通过则返回null
     */
    private fun validateSettings(settings: SettingsConfig): String? {
        if (!validateTimeRange(
                settings.morningStartTime,
                settings.morningEndTime,
                "上午"
            )
        ) {
            return "上午时间段格式或逻辑不正确"
        }

        if (!validateTimeRange(
                settings.afternoonStartTime,
                settings.afternoonEndTime,
                "下午"
            )
        ) {
            return "下午时间段格式或逻辑不正确"
        }

        if (!validateNoOverlap(settings)) {
            return "上午和下午时间段不能重叠"
        }

        if (!validateRefreshInterval(settings.refreshIntervalMinutes)) {
            return "监控间隔必须在1-30分钟之间"
        }

        return null
    }

    /**
     * 验证单个时间段
     * @param startTime 开始时间 (HH:mm)
     * @param endTime 结束时间 (HH:mm)
     * @param periodName 时段名称（用于错误提示）
     * @return 验证是否通过
     */
    private fun validateTimeRange(startTime: String, endTime: String, periodName: String): Boolean {
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
            return false
        }

        val startMinutes = timeToMinutes(startTime)
        val endMinutes = timeToMinutes(endTime)

        return endMinutes > startMinutes
    }

    /**
     * 验证时间段不重叠
     * 上午结束时间必须早于下午开始时间
     */
    private fun validateNoOverlap(settings: SettingsConfig): Boolean {
        val morningEndMinutes = timeToMinutes(settings.morningEndTime)
        val afternoonStartMinutes = timeToMinutes(settings.afternoonStartTime)

        return morningEndMinutes < afternoonStartMinutes
    }

    /**
     * 验证时间格式 (HH:mm)
     */
    private fun isValidTimeFormat(time: String): Boolean {
        if (time.length != 5 || time[2] != ':') {
            return false
        }

        val parts = time.split(":")
        if (parts.size != 2) {
            return false
        }

        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false

        return hour in 0..23 && minute in 0..59
    }

    /**
     * 将HH:mm转换为分钟数
     */
    private fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        return hour * 60 + minute
    }

    /**
     * 验证监控间隔
     * @param interval 间隔分钟数
     * @return 验证是否通过
     */
    private fun validateRefreshInterval(interval: Int): Boolean {
        return interval in 1..30
    }
}
