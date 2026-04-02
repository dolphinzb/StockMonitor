package com.stockmonitor.presentation.ui

import androidx.compose.runtime.Composable
import com.stockmonitor.presentation.navigation.BottomNavGraph

/**
 * StockMonitor 应用主界面
 * 包含底部导航，支持在股票监控和股票池之间切换
 */
@Composable
fun StockMonitorApp() {
    BottomNavGraph()
}
