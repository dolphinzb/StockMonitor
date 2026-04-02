package com.stockmonitor.presentation.ui.stockpool

import com.stockmonitor.domain.model.Stock

/**
 * 股票池界面 UI 状态
 *
 * @param stocks 股票列表
 * @param isLoading 是否正在加载
 * @param searchKeyword 搜索关键词
 * @param errorMessage 错误信息（如果存在）
 * @param isAddDialogVisible 添加对话框是否显示
 * @param isEditDialogVisible 编辑对话框是否显示
 * @param editingStock 当前编辑的股票
 * @param isEditMode 是否处于编辑模式（选择删除）
 * @param selectedStockIds 已选择的股票 ID 集合
 * @param isReorderMode 是否处于排序模式
 */
data class StockPoolUiState(
    val stocks: List<Stock> = emptyList(),
    val isLoading: Boolean = false,
    val searchKeyword: String = "",
    val errorMessage: String? = null,
    val isAddDialogVisible: Boolean = false,
    val isEditDialogVisible: Boolean = false,
    val editingStock: Stock? = null,
    val isEditMode: Boolean = false,
    val selectedStockIds: Set<Long> = emptySet(),
    val isReorderMode: Boolean = false
)

/**
 * 股票池界面事件
 * 定义用户可以触发的事件
 */
sealed class StockPoolEvent {
    data class AddStock(val code: String, val name: String) : StockPoolEvent()
    data class Search(val keyword: String) : StockPoolEvent()
    data object ShowAddDialog : StockPoolEvent()
    data object HideAddDialog : StockPoolEvent()
    data class ShowEditDialog(val stock: Stock) : StockPoolEvent()
    data object HideEditDialog : StockPoolEvent()
    data class UpdateStock(
        val id: Long,
        val name: String,
        val sellThreshold: Double?,
        val buyThreshold: Double?
    ) : StockPoolEvent()
    data object ToggleEditMode : StockPoolEvent()
    data class ToggleStockSelection(val stockId: Long) : StockPoolEvent()
    data object DeleteSelectedStocks : StockPoolEvent()
    data class DeleteStock(val id: Long) : StockPoolEvent()
    data object ToggleReorderMode : StockPoolEvent()
    data class ToggleMonitoring(val stockId: Long) : StockPoolEvent()
    data class ReorderStocks(val stocks: List<Stock>) : StockPoolEvent()
    data object ClearError : StockPoolEvent()
}
