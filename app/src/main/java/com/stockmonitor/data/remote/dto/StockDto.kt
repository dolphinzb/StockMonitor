package com.stockmonitor.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.stockmonitor.data.local.StockDataEntity

/**
 * 股票 API 响应数据
 */
data class StockResponse(
    @SerializedName("data")
    val data: List<StockDto>
)

/**
 * 股票数据传输对象
 */
data class StockDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("change")
    val change: Double,
    @SerializedName("percent")
    val percent: Double,
    @SerializedName("time")
    val time: Long
) {
    /**
     * 转换为本地实体
     */
    fun toEntity(): StockDataEntity {
        return StockDataEntity(
            code = code,
            name = name,
            currentPrice = price,
            changeAmount = change,
            changePercent = percent,
            updateTime = time
        )
    }
}
