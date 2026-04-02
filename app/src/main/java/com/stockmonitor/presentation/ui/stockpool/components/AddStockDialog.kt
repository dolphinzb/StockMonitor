package com.stockmonitor.presentation.ui.stockpool.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * 添加股票对话框组件
 * 用于输入股票代码和名称
 */
@Composable
fun AddStockDialog(
    onDismiss: () -> Unit,
    onConfirm: (code: String, name: String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "添加股票",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it.filter { char -> char.isDigit() }.take(6)
                        codeError = null
                    },
                    label = { Text("股票代码") },
                    placeholder = { Text("请输入6位股票代码") },
                    singleLine = true,
                    isError = codeError != null,
                    supportingText = codeError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it.take(20)
                        nameError = null
                    },
                    label = { Text("股票名称") },
                    placeholder = { Text("请输入股票名称") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    var hasError = false

                    if (code.isBlank()) {
                        codeError = "股票代码不能为空"
                        hasError = true
                    } else if (code.length != 6) {
                        codeError = "股票代码必须是6位数字"
                        hasError = true
                    }

                    if (name.isBlank()) {
                        nameError = "股票名称不能为空"
                        hasError = true
                    }

                    if (!hasError) {
                        onConfirm(code, name)
                    }
                }
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
