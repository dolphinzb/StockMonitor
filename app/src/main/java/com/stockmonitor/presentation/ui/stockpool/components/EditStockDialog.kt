package com.stockmonitor.presentation.ui.stockpool.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stockmonitor.domain.model.Stock

/**
 * 编辑股票对话框组件
 * 用于修改股票名称和阈值
 */
@Composable
fun EditStockDialog(
    stock: Stock,
    onDismiss: () -> Unit,
    onConfirm: (name: String, sellThreshold: Double?, buyThreshold: Double?) -> Unit
) {
    var name by remember { mutableStateOf(stock.name) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var sellThresholdText by remember { mutableStateOf(stock.sellThreshold?.toString() ?: "") }
    var buyThresholdText by remember { mutableStateOf(stock.buyThreshold?.toString() ?: "") }
    var sellThresholdError by remember { mutableStateOf<String?>(null) }
    var buyThresholdError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "编辑股票",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "股票代码: ${stock.code}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it.take(20)
                        nameError = null
                    },
                    label = { Text("股票名称") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = sellThresholdText,
                    onValueChange = {
                        sellThresholdText = it.filter { c -> c.isDigit() || c == '.' }.take(10)
                        sellThresholdError = null
                    },
                    label = { Text("卖出阈值（元）") },
                    placeholder = { Text("选填") },
                    singleLine = true,
                    isError = sellThresholdError != null,
                    supportingText = sellThresholdError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = buyThresholdText,
                    onValueChange = {
                        buyThresholdText = it.filter { c -> c.isDigit() || c == '.' }.take(10)
                        buyThresholdError = null
                    },
                    label = { Text("买入阈值（元）") },
                    placeholder = { Text("选填") },
                    singleLine = true,
                    isError = buyThresholdError != null,
                    supportingText = buyThresholdError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    var hasError = false

                    if (name.isBlank()) {
                        nameError = "股票名称不能为空"
                        hasError = true
                    }

                    val sellThreshold = sellThresholdText.toDoubleOrNull()
                    val buyThreshold = buyThresholdText.toDoubleOrNull()

                    if (sellThresholdText.isNotEmpty() && sellThreshold == null) {
                        sellThresholdError = "请输入有效数字"
                        hasError = true
                    }

                    if (buyThresholdText.isNotEmpty() && buyThreshold == null) {
                        buyThresholdError = "请输入有效数字"
                        hasError = true
                    }

                    if (sellThreshold != null && buyThreshold != null && sellThreshold <= buyThreshold) {
                        sellThresholdError = "卖出阈值必须大于买入阈值"
                        hasError = true
                    }

                    if (!hasError) {
                        onConfirm(name, sellThreshold, buyThreshold)
                    }
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
