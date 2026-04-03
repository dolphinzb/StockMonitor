package com.stockmonitor.domain.repository

/**
 * 设置保存结果
 * 区分是否需要重新调度定时任务
 */
sealed class SettingsSaveResult {
    /**
     * 保存成功，但不需要重新调度定时任务
     * (其他设置项变更，如时间段、API来源)
     */
    data object SuccessNoReschedule : SettingsSaveResult()

    /**
     * 保存成功，需要重新调度定时任务
     * (监控间隔发生变更)
     */
    data object SuccessRequiresReschedule : SettingsSaveResult()

    /**
     * 保存失败
     * @property message 错误信息
     */
    data class Error(val message: String) : SettingsSaveResult()
}
