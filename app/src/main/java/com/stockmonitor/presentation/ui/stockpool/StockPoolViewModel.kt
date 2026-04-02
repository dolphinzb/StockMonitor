package com.stockmonitor.presentation.ui.stockpool

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockmonitor.data.local.StockDao
import com.stockmonitor.domain.model.Stock
import com.stockmonitor.domain.usecase.AddStockUseCase
import com.stockmonitor.domain.usecase.DeleteStocksUseCase
import com.stockmonitor.domain.usecase.GetStockListUseCase
import com.stockmonitor.domain.usecase.ReorderStocksUseCase
import com.stockmonitor.domain.usecase.UpdateStockUseCase
import com.stockmonitor.domain.usecase.UpdateThresholdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 股票池 ViewModel
 * 管理股票池界面的 UI 状态和处理用户事件
 */
@HiltViewModel
class StockPoolViewModel @Inject constructor(
    private val getStockListUseCase: GetStockListUseCase,
    private val addStockUseCase: AddStockUseCase,
    private val updateStockUseCase: UpdateStockUseCase,
    private val updateThresholdUseCase: UpdateThresholdUseCase,
    private val deleteStocksUseCase: DeleteStocksUseCase,
    private val reorderStocksUseCase: ReorderStocksUseCase,
    private val stockDao: StockDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockPoolUiState())
    val uiState: StateFlow<StockPoolUiState> = _uiState.asStateFlow()

    init {
        loadStocks()
    }

    /**
     * 处理股票池事件
     */
    fun onEvent(event: StockPoolEvent) {
        when (event) {
            is StockPoolEvent.AddStock -> addStock(event.code, event.name)
            is StockPoolEvent.Search -> search(event.keyword)
            is StockPoolEvent.ShowAddDialog -> showAddDialog()
            is StockPoolEvent.HideAddDialog -> hideAddDialog()
            is StockPoolEvent.ShowEditDialog -> showEditDialog(event.stock)
            is StockPoolEvent.HideEditDialog -> hideEditDialog()
            is StockPoolEvent.UpdateStock -> updateStock(event.id, event.name, event.sellThreshold, event.buyThreshold)
            is StockPoolEvent.ToggleEditMode -> toggleEditMode()
            is StockPoolEvent.ToggleStockSelection -> toggleStockSelection(event.stockId)
            is StockPoolEvent.DeleteSelectedStocks -> deleteSelectedStocks()
            is StockPoolEvent.DeleteStock -> deleteStock(event.id)
            is StockPoolEvent.ToggleReorderMode -> toggleReorderMode()
            is StockPoolEvent.ToggleMonitoring -> toggleMonitoring(event.stockId)
            is StockPoolEvent.ReorderStocks -> reorderStocks(event.stocks)
            is StockPoolEvent.ClearError -> clearError()
        }
    }

    /**
     * 加载股票列表
     */
    private fun loadStocks() {
        _uiState.update { it.copy(isLoading = true) }

        getStockListUseCase()
            .onEach { stocks ->
                _uiState.update {
                    it.copy(
                        stocks = stocks,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载失败"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 添加股票
     */
    private fun addStock(code: String, name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isAddDialogVisible = false) }

            addStockUseCase(code, name)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "添加失败"
                        )
                    }
                }
        }
    }

    /**
     * 搜索股票
     */
    private fun search(keyword: String) {
        _uiState.update { it.copy(searchKeyword = keyword, isLoading = true) }

        getStockListUseCase.search(keyword)
            .onEach { stocks ->
                _uiState.update {
                    it.copy(
                        stocks = stocks,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "搜索失败"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 显示添加对话框
     */
    private fun showAddDialog() {
        _uiState.update { it.copy(isAddDialogVisible = true) }
    }

    /**
     * 隐藏添加对话框
     */
    private fun hideAddDialog() {
        _uiState.update { it.copy(isAddDialogVisible = false) }
    }

    /**
     * 显示编辑对话框
     */
    private fun showEditDialog(stock: com.stockmonitor.domain.model.Stock) {
        _uiState.update { it.copy(isEditDialogVisible = true, editingStock = stock) }
    }

    /**
     * 隐藏编辑对话框
     */
    private fun hideEditDialog() {
        _uiState.update { it.copy(isEditDialogVisible = false, editingStock = null) }
    }

    /**
     * 更新股票信息
     */
    private fun updateStock(id: Long, name: String, sellThreshold: Double?, buyThreshold: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isEditDialogVisible = false) }

            val stock = _uiState.value.editingStock?.copy(name = name)
                ?: _uiState.value.stocks.find { it.id == id }

            if (stock != null) {
                updateStockUseCase(stock)
                    .onSuccess {
                        if (sellThreshold != null || buyThreshold != null) {
                            updateThresholdUseCase(id, sellThreshold, buyThreshold)
                        }
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "更新失败"
                            )
                        }
                    }
            }

            _uiState.update { it.copy(isLoading = false, editingStock = null) }
        }
    }

    /**
     * 切换编辑模式
     */
    private fun toggleEditMode() {
        _uiState.update {
            it.copy(
                isEditMode = !it.isEditMode,
                selectedStockIds = emptySet()
            )
        }
    }

    /**
     * 切换股票选择
     */
    private fun toggleStockSelection(stockId: Long) {
        _uiState.update { state ->
            val newSelectedIds = if (stockId in state.selectedStockIds) {
                state.selectedStockIds - stockId
            } else {
                state.selectedStockIds + stockId
            }
            state.copy(selectedStockIds = newSelectedIds)
        }
    }

    /**
     * 删除选中的股票
     */
    private fun deleteSelectedStocks() {
        viewModelScope.launch {
            val selectedIds = _uiState.value.selectedStockIds.toList()
            if (selectedIds.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "请选择要删除的股票") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, isEditMode = false) }

            deleteStocksUseCase.deleteMultiple(selectedIds)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, selectedStockIds = emptySet()) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "删除失败"
                        )
                    }
                }
        }
    }

    /**
     * 删除单个股票（滑动删除）
     */
    private fun deleteStock(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            deleteStocksUseCase.deleteMultiple(listOf(id))
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "删除失败"
                        )
                    }
                }
        }
    }

    /**
     * 切换排序模式
     */
    private fun toggleReorderMode() {
        _uiState.update { it.copy(isReorderMode = !it.isReorderMode) }
    }

    /**
     * 重新排序股票
     */
    private fun reorderStocks(stocks: List<com.stockmonitor.domain.model.Stock>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            reorderStocksUseCase(stocks)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "排序失败"
                        )
                    }
                }
        }
    }

    /**
     * 切换股票监控状态
     * 开启监控前检查阈值是否已设置
     */
    private fun toggleMonitoring(stockId: Long) {
        viewModelScope.launch {
            val stock = _uiState.value.stocks.find { it.id == stockId }
            if (stock == null) {
                _uiState.update { it.copy(errorMessage = "股票不存在") }
                return@launch
            }

            val newMonitoringState = !stock.isMonitoring

            // 开启监控时检查阈值是否已设置
            if (newMonitoringState && !stock.hasThreshold) {
                _uiState.update { it.copy(errorMessage = "请先设置阈值再开启监控") }
                return@launch
            }

            try {
                stockDao.updateMonitoringStatus(stockId, newMonitoringState, System.currentTimeMillis())
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "操作失败") }
            }
        }
    }

    /**
     * 清除错误消息
     */
    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
