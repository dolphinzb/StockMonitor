package com.stockmonitor.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 刷新事件总线
 * 用于 Worker 通知 UI 刷新
 */
@Singleton
class RefreshEventBus @Inject constructor() {
    private val _refreshEvent = MutableSharedFlow<Unit>()
    val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()

    suspend fun emitRefresh() {
        _refreshEvent.emit(Unit)
    }
}
