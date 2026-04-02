package com.stockmonitor.domain.model

/**
 * 股票数据实体
 * 包含股票代码、名称、当前价格、涨跌额、涨跌幅、更新时间
 *
 * @param code 股票代码，如 "600000" (上证) 或 "000001" (深证)
 * @param name 股票名称
 * @param currentPrice 当前价格
 * @param changeAmount 涨跌额
 * @param changePercent 涨跌幅百分比
 * @param updateTime 更新时间戳 (毫秒)
 */
data class StockData(
    val code: String,
    val name: String,
    val currentPrice: Double,
    val changeAmount: Double,
    val changePercent: Double,
    val updateTime: Long
)

/**
 * 股票价格刷新结果
 *
 * @param successCount 成功获取价格的股票数量
 * @param failedCodes 失败股票代码列表
 * @param totalRequested 请求的股票总数
 * @param stockDataList 成功获取的股票数据列表
 */
data class RefreshResult(
    val successCount: Int,
    val failedCodes: List<String>,
    val totalRequested: Int,
    val stockDataList: List<StockData>
)
