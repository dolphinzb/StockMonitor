package com.stockmonitor.domain.model

/**
 * 股票价格API来源枚举
 * 定义支持的股票价格数据提供方
 */
enum class StockApiSource(val displayName: String) {
    /** 新浪财经API */
    SINA("新浪财经"),

    /** 腾讯API */
    TENCENT("腾讯"),

    /** 同花顺API */
    TONGHUASHUN("同花顺")
}
