package com.stockmonitor.domain.repository

import com.stockmonitor.domain.model.SettingsConfig

/**
 * 设置仓储接口
 * 定义设置数据的访问操作
 */
interface SettingsRepository {

    /**
     * 获取设置
     * @return 设置配置，如果不存在则返回null
     */
    suspend fun getSettings(): SettingsConfig?

    /**
     * 获取设置，如果不存在则返回默认值
     * @return 设置配置（已保存的值或默认值）
     */
    suspend fun getSettingsWithDefaults(): SettingsConfig

    /**
     * 保存设置
     * @param settings 要保存的设置配置
     * @return 保存结果，指示是否需要重新调度定时任务
     */
    suspend fun saveSettings(settings: SettingsConfig): SettingsSaveResult
}
