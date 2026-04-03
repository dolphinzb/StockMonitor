package com.stockmonitor.presentation.ui.settings

import com.stockmonitor.domain.model.SettingsConfig

/**
 * 设置页面状态
 *
 * @property isLoading 是否正在加载
 * @property settings 已保存的设置配置
 * @property editedSettings 编辑中的设置配置
 * @property validationErrors 验证错误信息
 * @property saveSuccess 保存是否成功
 */
data class SettingsState(
    val isLoading: Boolean = true,
    val settings: SettingsConfig = SettingsConfig.DEFAULT,
    val editedSettings: SettingsConfig = SettingsConfig.DEFAULT,
    val validationErrors: Map<String, String> = emptyMap(),
    val saveSuccess: Boolean? = null
) {
    /**
     * 判断是否有未保存的更改
     */
    val hasUnsavedChanges: Boolean
        get() = settings != editedSettings

    /**
     * 判断编辑中的配置是否有效
     */
    val isValid: Boolean
        get() = validationErrors.isEmpty()
}
