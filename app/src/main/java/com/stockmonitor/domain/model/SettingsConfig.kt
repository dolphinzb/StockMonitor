package com.stockmonitor.domain.model

/**
 * 用户设置配置数据类
 * 包含交易时间段、监控间隔、API来源等配置
 *
 * @property morningStartTime 上午交易开始时间 (HH:mm格式)
 * @property morningEndTime 上午交易结束时间 (HH:mm格式)
 * @property afternoonStartTime 下午交易开始时间 (HH:mm格式)
 * @property afternoonEndTime 下午交易结束时间 (HH:mm格式)
 * @property refreshIntervalMinutes 监控刷新间隔 (分钟, 1-30)
 * @property apiSource 股票价格API来源
 */
data class SettingsConfig(
    val morningStartTime: String = "09:30",
    val morningEndTime: String = "11:30",
    val afternoonStartTime: String = "13:00",
    val afternoonEndTime: String = "15:00",
    val refreshIntervalMinutes: Int = 3,
    val apiSource: StockApiSource = StockApiSource.SINA
) {
    companion object {
        /** 默认配置 */
        val DEFAULT = SettingsConfig()
    }
}
