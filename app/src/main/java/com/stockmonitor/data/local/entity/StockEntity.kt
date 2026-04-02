package com.stockmonitor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stockmonitor.domain.model.Stock

/**
 * 股票实体 - Room 数据库表
 * 用于持久化存储用户关注的股票池
 *
 * @param id 唯一标识，新增时为0由数据库生成
 * @param code 股票代码，6位数字字符串
 * @param name 股票名称
 * @param sortOrder 排序权重，数值越小排越前
 * @param sellThreshold 卖出阈值（元），null表示未设置
 * @param buyThreshold 买入阈值（元），null表示未设置
 * @param createdAt 创建时间戳（毫秒）
 * @param updatedAt 更新时间戳（毫秒）
 */
@Entity(
    tableName = "stocks",
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["sortOrder"])
    ]
)
data class StockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "sortOrder")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "sellThreshold")
    val sellThreshold: Double? = null,

    @ColumnInfo(name = "buyThreshold")
    val buyThreshold: Double? = null,

    @ColumnInfo(name = "isMonitoring")
    val isMonitoring: Boolean = false,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
) {
    /**
     * 将 Entity 转换为领域模型
     */
    fun toDomain(): Stock {
        return Stock(
            id = id,
            code = code,
            name = name,
            sortOrder = sortOrder,
            sellThreshold = sellThreshold,
            buyThreshold = buyThreshold,
            isMonitoring = isMonitoring,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        /**
         * 从领域模型创建 Entity
         */
        fun fromDomain(stock: Stock): StockEntity {
            return StockEntity(
                id = stock.id,
                code = stock.code,
                name = stock.name,
                sortOrder = stock.sortOrder,
                sellThreshold = stock.sellThreshold,
                buyThreshold = stock.buyThreshold,
                isMonitoring = stock.isMonitoring,
                createdAt = stock.createdAt,
                updatedAt = stock.updatedAt
            )
        }
    }
}
