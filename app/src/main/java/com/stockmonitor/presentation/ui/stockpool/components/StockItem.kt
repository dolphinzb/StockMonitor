package com.stockmonitor.presentation.ui.stockpool.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stockmonitor.domain.model.Stock

/**
 * 股票列表项组件
 * 显示股票代码、名称和阈值图标，支持编辑模式多选和监控开关
 */
@Composable
fun StockItem(
    stock: Stock,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectionChange: ((Boolean) -> Unit)? = null,
    onMonitoringToggle: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (isEditMode && onSelectionChange != null) {
                    onSelectionChange(!isSelected)
                } else {
                    onClick()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stock.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stock.code,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (stock.hasThreshold) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    stock.sellThreshold?.let {
                        Text(
                            text = "卖出: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    stock.buyThreshold?.let {
                        Text(
                            text = "买入: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (onMonitoringToggle != null) {
                Switch(
                    checked = stock.isMonitoring,
                    onCheckedChange = onMonitoringToggle,
                    enabled = stock.hasThreshold
                )
            }
        }
    }
}
