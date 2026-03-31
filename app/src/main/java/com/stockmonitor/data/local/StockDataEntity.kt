package com.stockmonitor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stockmonitor.domain.model.StockData

/**
 * 股票数据实体 - Room 数据库表
 * 用于持久化存储股票数据
 *
 * @param code 股票代码 (主键)
 * @param name 股票名称
 * @param currentPrice 当前价格
 * @param changeAmount 涨跌额
 * @param changePercent 涨跌幅百分比
 * @param updateTime 更新时间戳
 */
@Entity(tableName = "stock_data")
data class StockDataEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val currentPrice: Double,
    val changeAmount: Double,
    val changePercent: Double,
    val updateTime: Long
) {
    /**
     * 将 Entity 转换为领域模型
     */
    fun toDomainModel(): StockData {
        return StockData(
            code = code,
            name = name,
            currentPrice = currentPrice,
            changeAmount = changeAmount,
            changePercent = changePercent,
            updateTime = updateTime
        )
    }

    companion object {
        /**
         * 从领域模型创建 Entity
         */
        fun fromDomainModel(stockData: StockData): StockDataEntity {
            return StockDataEntity(
                code = stockData.code,
                name = stockData.name,
                currentPrice = stockData.currentPrice,
                changeAmount = stockData.changeAmount,
                changePercent = stockData.changePercent,
                updateTime = stockData.updateTime
            )
        }
    }
}
