package com.stockmonitor.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 刷新状态管理器
 * 用于持久化存储最后刷新时间和刷新结果
 */
@Singleton
class RefreshStateManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "stock_monitor_refresh"
        private const val KEY_LAST_REFRESH_TIME = "last_refresh_time"
        private const val KEY_LAST_REFRESH_SUCCESS = "last_refresh_success"
    }

    /**
     * 保存刷新状态
     */
    fun saveRefreshState(success: Boolean) {
        prefs.edit()
            .putLong(KEY_LAST_REFRESH_TIME, System.currentTimeMillis())
            .putBoolean(KEY_LAST_REFRESH_SUCCESS, success)
            .apply()
    }

    /**
     * 获取最后刷新时间
     */
    fun getLastRefreshTime(): Long {
        return prefs.getLong(KEY_LAST_REFRESH_TIME, 0L)
    }

    /**
     * 获取最后刷新是否成功
     */
    fun getLastRefreshSuccess(): Boolean? {
        return if (prefs.contains(KEY_LAST_REFRESH_SUCCESS)) {
            prefs.getBoolean(KEY_LAST_REFRESH_SUCCESS, false)
        } else {
            null
        }
    }
}
