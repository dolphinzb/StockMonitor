package com.stockmonitor.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stockmonitor.presentation.ui.stockmonitor.StockMonitorScreen
import com.stockmonitor.presentation.ui.stockpool.StockPoolScreen
import com.stockmonitor.util.TradingTimeChecker

/**
 * 导航项定义
 */
sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object StockMonitor : NavItem(
        route = "stock_monitor",
        title = "监控",
        icon = Icons.Default.Home
    )

    data object StockPool : NavItem(
        route = "stock_pool",
        title = "股票池",
        icon = Icons.Default.List
    )
}

/**
 * 底部导航图
 */
@Composable
fun BottomNavGraph() {
    val navController = rememberNavController()
    val navItems = listOf(NavItem.StockMonitor, NavItem.StockPool)
    val isTradingTime = remember { TradingTimeChecker.isTradingTime() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavItem.StockMonitor.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavItem.StockMonitor.route) {
                StockMonitorScreen(isTradingTime = isTradingTime)
            }
            composable(NavItem.StockPool.route) {
                StockPoolScreen()
            }
        }
    }
}
