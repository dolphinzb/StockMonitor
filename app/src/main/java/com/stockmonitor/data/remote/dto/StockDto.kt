package com.stockmonitor.data.remote.dto

import com.stockmonitor.data.local.StockDataEntity

/**
 * 股票数据解析结果，包含成功和失败信息
 */
data class ParseResult(
    val successCount: Int,
    val failedCodes: List<String>,
    val stockDataList: List<StockData>
)

/**
 * 新浪财经股票 API 响应数据
 * API: https://hq.sinajs.cn/list=sh600519,sz000001
 * 响应格式: var hq_str_sh600519="name,price,change,percent,open,yesterdayClose,high,low,volume,amount,time,date,...(共32个字段)";
 */
data class StockResponse(
    val rawResponse: String
)

/**
 * 单只股票数据解析结果
 */
data class StockData(
    val code: String,
    val name: String,
    val price: Double,
    val change: Double,
    val percent: Double,
    val open: Double,
    val yesterdayClose: Double,
    val high: Double,
    val low: Double,
    val volume: Long,
    val amount: Double,
    val time: String,
    val date: String
) {
    fun toEntity(): StockDataEntity {
        return StockDataEntity(
            code = code,
            name = name,
            currentPrice = price,
            changeAmount = change,
            changePercent = percent,
            updateTime = System.currentTimeMillis()
        )
    }
}

/**
 * 股票数据解析工具类
 * 新浪 API 返回格式: var hq_str_sh600519="贵州茅台,1850.00,25.50,1.40,1820.00,1824.50,1860.00,1840.00,500000,9250000000,10:30:00,2024-01-15,...,...;
 *
 * 字段索引:
 * 0: 股票名称
 * 1: 今日开盘价
 * 2: 昨日收盘价
 * 3: 当前价格
 * 4: 今日最高价
 * 5: 今日最低价
 * 8: 成交量（手）
 * 9: 成交额（元）
 * 30: 时间 (HH:MM:SS)
 * 31: 日期 (YYYY-MM-DD)
 * 32: 涨跌幅 (%)
 */
object StockDataParser {
    fun parse(code: String, response: String): StockData? {
        return try {
            val dataStr = response.substringAfter("=\"").substringBefore("\";")
            val parts = dataStr.split(",")
            if (parts.size < 32) return null

            val price = parts[3].toDoubleOrNull() ?: 0.0
            val yesterdayClose = parts[2].toDoubleOrNull() ?: 0.0
            val change = price - yesterdayClose
            val percent = parts[32].toDoubleOrNull() ?: 0.0

            StockData(
                code = code,
                name = parts[0],
                price = price,
                change = change,
                percent = percent,
                open = parts[1].toDoubleOrNull() ?: 0.0,
                yesterdayClose = yesterdayClose,
                high = parts[4].toDoubleOrNull() ?: 0.0,
                low = parts[5].toDoubleOrNull() ?: 0.0,
                volume = parts[8].toLongOrNull() ?: 0L,
                amount = parts[9].toDoubleOrNull() ?: 0.0,
                time = parts[31],
                date = parts[30]
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 解析新浪 API 的批量响应
     * 响应格式可能是多个 var hq_str_shxxxxxx="..."; 连接在一起
     */
    fun parseSinaResponse(response: String, requestedCodes: List<String>): ParseResult {
        val results = mutableListOf<StockData>()
        val failedCodes = mutableListOf<String>()
        val requestedCodeSet = requestedCodes.map {
            if (it.startsWith("sh") || it.startsWith("sz")) it else {
                if (it.startsWith("6")) "sh$it" else "sz$it"
            }
        }.toSet()

        val varPattern = Regex("""hq_str_(sh|sz)(\w+)="([^"]+)";?""")

        varPattern.findAll(response).forEach { match ->
            val prefix = match.groupValues[1]
            val codeNumber = match.groupValues[2]
            val dataContent = match.groupValues[3]
            val fullCode = "$prefix$codeNumber"

            val parts = dataContent.split(",")
            if (parts.size >= 32) {
                try {
                    val price = parts[3].toDoubleOrNull() ?: 0.0
                    if (price <= 0) {
                        failedCodes.add(fullCode)
                        return@forEach
                    }
                    val yesterdayClose = parts[2].toDoubleOrNull() ?: 0.0
                    val change = price - yesterdayClose
                    val percent = parts[32].toDoubleOrNull() ?: 0.0

                    results.add(StockData(
                        code = fullCode,
                        name = parts[0],
                        price = price,
                        change = change,
                        percent = percent,
                        open = parts[1].toDoubleOrNull() ?: 0.0,
                        yesterdayClose = yesterdayClose,
                        high = parts[4].toDoubleOrNull() ?: 0.0,
                        low = parts[5].toDoubleOrNull() ?: 0.0,
                        volume = parts[8].toLongOrNull() ?: 0L,
                        amount = parts[9].toDoubleOrNull() ?: 0.0,
                        time = parts[31],
                        date = parts[30]
                    ))
                } catch (e: Exception) {
                    failedCodes.add(fullCode)
                }
            } else {
                failedCodes.add(fullCode)
            }
        }

        return ParseResult(
            successCount = results.size,
            failedCodes = failedCodes,
            stockDataList = results
        )
    }

    fun parseMultiple(responses: Map<String, String>): List<StockData> {
        return responses.mapNotNull { (code, response) ->
            parse(code, response)
        }
    }
}
