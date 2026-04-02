package com.stockmonitor.domain.model

/**
 * 股票领域实体
 * 股票池中的单个股票，支持 CRUD 操作、排序和阈值设定
 *
 * @param id 唯一标识，新增时为0由数据库生成
 * @param code 股票代码，6位数字字符串
 * @param name 股票名称
 * @param sortOrder 排序权重，数值越小排越前
 * @param sellThreshold 卖出阈值（元），null表示未设置，保留2位小数
 * @param buyThreshold 买入阈值（元），null表示未设置，保留2位小数
 * @param createdAt 创建时间戳（毫秒）
 * @param updatedAt 更新时间戳（毫秒）
 */
data class Stock(
    val id: Long = 0,
    val code: String,
    val name: String,
    val sortOrder: Int = 0,
    val sellThreshold: Double? = null,
    val buyThreshold: Double? = null,
    val isMonitoring: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 是否设置了阈值
     */
    val hasThreshold: Boolean
        get() = sellThreshold != null || buyThreshold != null

    /**
     * 验证阈值设置是否有效
     * 卖出阈值必须大于买入阈值（如果两者都设置了）
     */
    fun isThresholdValid(): Boolean {
        return sellThreshold == null || buyThreshold == null || sellThreshold > buyThreshold
    }
}
