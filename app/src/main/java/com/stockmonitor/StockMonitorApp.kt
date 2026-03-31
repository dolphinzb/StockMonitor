package com.stockmonitor

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * StockMonitor 应用程序入口类
 * 使用 Hilt 进行依赖注入管理
 */
@HiltAndroidApp
class StockMonitorApp : Application()
