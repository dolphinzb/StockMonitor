package com.stockmonitor.domain.usecase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.stockmonitor.domain.model.AlertType
import com.stockmonitor.domain.model.MonitoredStock
import com.stockmonitor.presentation.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 发送通知用例
 */
class SendNotificationUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "stock_alert_channel"
        const val CHANNEL_NAME = "股票告警通知"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "股票价格超出阈值时发送告警通知"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    operator fun invoke(monitoredStock: MonitoredStock, alertType: AlertType, currentPrice: Double) {
        val stockName = monitoredStock.name
        val stockCode = monitoredStock.code
        val threshold = when (alertType) {
            AlertType.SELL_THRESHOLD -> monitoredStock.sellThreshold
            AlertType.BUY_THRESHOLD -> monitoredStock.buyThreshold
            else -> return
        }

        val thresholdDesc = when (alertType) {
            AlertType.SELL_THRESHOLD -> "高于卖出阈值"
            AlertType.BUY_THRESHOLD -> "低于买入阈值"
            else -> return
        }

        val message = "${stockName}（${stockCode}）当前价格${currentPrice}元，已${thresholdDesc}${threshold}元，请关注！"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("股票告警")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                stockCode.hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            // 通知权限未授予，静默处理
        }
    }
}
