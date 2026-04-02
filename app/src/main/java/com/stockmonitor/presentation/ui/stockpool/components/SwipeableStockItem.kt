package com.stockmonitor.presentation.ui.stockpool.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.stockmonitor.domain.model.Stock
import kotlin.math.roundToInt

/**
 * 可滑动的股票列表项组件
 * 支持左滑显示删除按钮，点击按钮后才删除
 */
@Composable
fun SwipeableStockItem(
    stock: Stock,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectionChange: ((Boolean) -> Unit)? = null,
    onMonitoringToggle: ((Boolean) -> Unit)? = null
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var showDeleteButton by remember { mutableStateOf(false) }

    val deleteThreshold = 150f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        SwipeBackground(
            offsetX = offsetX,
            showDeleteButton = showDeleteButton,
            onDeleteClick = {
                onDelete()
            }
        )

        StockItemContent(
            stock = stock,
            onClick = {
                if (showDeleteButton) {
                    showDeleteButton = false
                    offsetX = 0f
                } else {
                    onClick()
                }
            },
            isEditMode = isEditMode,
            isSelected = isSelected,
            onSelectionChange = onSelectionChange,
            onMonitoringToggle = onMonitoringToggle,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < -deleteThreshold) {
                                showDeleteButton = true
                                offsetX = -deleteThreshold
                            } else {
                                offsetX = 0f
                                showDeleteButton = false
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                            showDeleteButton = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(-deleteThreshold * 1.5f, 0f)
                        }
                    )
                }
        )
    }
}

@Composable
private fun SwipeBackground(
    offsetX: Float,
    showDeleteButton: Boolean,
    onDeleteClick: () -> Unit
) {
    val backgroundAlpha = if (showDeleteButton) {
        1f
    } else {
        ((-offsetX / 150f).coerceIn(0f, 1f))
    }

    val color = MaterialTheme.colorScheme.error.copy(alpha = backgroundAlpha)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "删除",
            modifier = Modifier
                .size(24.dp)
                .then(
                    if (showDeleteButton) {
                        Modifier.clickable { onDeleteClick() }
                    } else {
                        Modifier
                    }
                ),
            tint = Color.White
        )
    }
}

@Composable
private fun StockItemContent(
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
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                if (isEditMode && onSelectionChange != null) {
                    onSelectionChange(!isSelected)
                } else {
                    onClick()
                }
            },
        shape = RoundedCornerShape(12.dp),
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
                .height(72.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
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
