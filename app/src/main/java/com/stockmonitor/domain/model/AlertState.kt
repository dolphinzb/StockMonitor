package com.stockmonitor.domain.model

/**
 * 告警状态枚举
 */
enum class AlertType {
    NONE,
    SELL_THRESHOLD,
    BUY_THRESHOLD
}

/**
 * 告警状态
 * 用于跟踪和管理通知防轰炸
 *
 * @param alertType 当前告警类型
 * @param lastAlertTime 上次发送通知的时间（毫秒），用于判断是否需要发送新通知
 */
data class AlertState(
    val alertType: AlertType = AlertType.NONE,
    val lastAlertTime: Long = 0L
) {
    /**
     * 判断是否可以发送新通知
     * 只有当alertType为NONE（已恢复）时才允许发送同一类型的新通知
     */
    fun canSendNotification(): Boolean = alertType == AlertType.NONE
}
