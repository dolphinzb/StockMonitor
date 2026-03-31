package com.stockmonitor.domain.model

/**
 * 缓存数据实体
 * 存储最近一次同步的股票数据，支持离线查看
 *
 * @param stocks 缓存的股票数据列表
 * @param cachedAt 缓存时间戳
 */
data class CacheData(
    val stocks: List<StockData>,
    val cachedAt: Long
)
