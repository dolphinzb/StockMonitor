package com.stockmonitor.presentation.ui.stockpool

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockmonitor.presentation.ui.stockpool.components.AddStockDialog
import com.stockmonitor.presentation.ui.stockpool.components.EditStockDialog
import com.stockmonitor.presentation.ui.stockpool.components.StockItem
import com.stockmonitor.domain.model.Stock
import com.stockmonitor.presentation.ui.stockpool.components.SwipeableStockItem

/**
 * 股票池主界面
 * 显示股票列表，支持添加股票、搜索、编辑和删除
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockPoolScreen(
    viewModel: StockPoolViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(StockPoolEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            uiState.isEditMode -> "选择删除"
                            uiState.isReorderMode -> "调整顺序"
                            else -> "股票池"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = when {
                        uiState.isEditMode -> MaterialTheme.colorScheme.errorContainer
                        uiState.isReorderMode -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    titleContentColor = when {
                        uiState.isEditMode -> MaterialTheme.colorScheme.onErrorContainer
                        uiState.isReorderMode -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                ),
                actions = {
                    when {
                        uiState.isEditMode -> {
                            IconButton(
                                onClick = { viewModel.onEvent(StockPoolEvent.DeleteSelectedStocks) },
                                enabled = uiState.selectedStockIds.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "删除",
                                    tint = if (uiState.selectedStockIds.isNotEmpty()) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.38f)
                                    }
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(StockPoolEvent.ToggleEditMode) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "取消"
                                )
                            }
                        }
                        uiState.isReorderMode -> {
                            IconButton(onClick = { viewModel.onEvent(StockPoolEvent.ToggleReorderMode) }) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "完成"
                                )
                            }
                        }
                        else -> {
                            IconButton(onClick = { viewModel.onEvent(StockPoolEvent.ToggleReorderMode) }) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "排序"
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(StockPoolEvent.ToggleEditMode) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "编辑"
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isEditMode && !uiState.isReorderMode) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(StockPoolEvent.ShowAddDialog) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加股票"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!uiState.isReorderMode) {
                OutlinedTextField(
                    value = uiState.searchKeyword,
                    onValueChange = { viewModel.onEvent(StockPoolEvent.Search(it)) },
                    placeholder = { Text("搜索股票代码或名称") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.stocks.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.stocks,
                            key = { _, stock -> stock.id }
                        ) { index, stock ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SwipeableStockItem(
                                    stock = stock,
                                    onClick = {
                                        if (uiState.isReorderMode) {
                                        } else {
                                            viewModel.onEvent(StockPoolEvent.ShowEditDialog(stock))
                                        }
                                    },
                                    onDelete = {
                                        viewModel.onEvent(StockPoolEvent.DeleteStock(stock.id))
                                    },
                                    isEditMode = uiState.isEditMode,
                                    isSelected = stock.id in uiState.selectedStockIds,
                                    onSelectionChange = { viewModel.onEvent(StockPoolEvent.ToggleStockSelection(stock.id)) },
                                    onMonitoringToggle = { viewModel.onEvent(StockPoolEvent.ToggleMonitoring(stock.id)) },
                                    modifier = Modifier.weight(1f)
                                )

                                if (uiState.isReorderMode) {
                                    Column {
                                        IconButton(
                                            onClick = {
                                                if (index > 0) {
                                                    val newList = uiState.stocks.toMutableList()
                                                    val temp = newList[index]
                                                    newList[index] = newList[index - 1]
                                                    newList[index - 1] = temp
                                                    viewModel.onEvent(StockPoolEvent.ReorderStocks(newList))
                                                }
                                            },
                                            enabled = index > 0
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ExpandLess,
                                                contentDescription = "上移"
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                if (index < uiState.stocks.size - 1) {
                                                    val newList = uiState.stocks.toMutableList()
                                                    val temp = newList[index]
                                                    newList[index] = newList[index + 1]
                                                    newList[index + 1] = temp
                                                    viewModel.onEvent(StockPoolEvent.ReorderStocks(newList))
                                                }
                                            },
                                            enabled = index < uiState.stocks.size - 1
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ExpandMore,
                                                contentDescription = "下移"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isAddDialogVisible) {
            AddStockDialog(
                onDismiss = { viewModel.onEvent(StockPoolEvent.HideAddDialog) },
                onConfirm = { code, name ->
                    viewModel.onEvent(StockPoolEvent.AddStock(code, name))
                }
            )
        }

        if (uiState.isEditDialogVisible && uiState.editingStock != null) {
            EditStockDialog(
                stock = uiState.editingStock!!,
                onDismiss = { viewModel.onEvent(StockPoolEvent.HideEditDialog) },
                onConfirm = { name, sellThreshold, buyThreshold ->
                    viewModel.onEvent(
                        StockPoolEvent.UpdateStock(
                            id = uiState.editingStock!!.id,
                            name = name,
                            sellThreshold = sellThreshold,
                            buyThreshold = buyThreshold
                        )
                    )
                }
            )
        }
    }
}

/**
 * 空状态组件
 */
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "暂无关注的股票",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "请点击右下角添加",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
