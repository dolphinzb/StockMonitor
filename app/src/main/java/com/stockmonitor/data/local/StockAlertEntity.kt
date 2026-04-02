package com.stockmonitor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stockmonitor.domain.model.AlertState
import com.stockmonitor.domain.model.AlertType

/**
 * 股票告警状态实体 - Room 数据库表
 * 用于持久化存储股票的告警状态
 *
 * @param code 股票代码 (主键)
 * @param alertType 告警类型 NONE/SELL_THRESHOLD/BUY_THRESHOLD
 * @param lastAlertTime 上次发送通知的时间戳
 */
@Entity(tableName = "stock_alert_state")
data class StockAlertEntity(
    @PrimaryKey
    val code: String,
    val alertType: String = AlertType.NONE.name,
    val lastAlertTime: Long = 0L
) {
    fun toDomainModel(): AlertState {
        return AlertState(
            alertType = AlertType.valueOf(alertType),
            lastAlertTime = lastAlertTime
        )
    }

    companion object {
        fun fromDomainModel(code: String, alertState: AlertState): StockAlertEntity {
            return StockAlertEntity(
                code = code,
                alertType = alertState.alertType.name,
                lastAlertTime = alertState.lastAlertTime
            )
        }
    }
}
