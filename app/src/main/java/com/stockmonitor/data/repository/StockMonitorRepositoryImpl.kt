package com.stockmonitor.data.repository

import com.stockmonitor.data.local.StockAlertDao
import com.stockmonitor.data.local.StockAlertEntity
import com.stockmonitor.data.local.StockDao
import com.stockmonitor.data.local.StockDataDao
import com.stockmonitor.data.local.StockDataEntity
import com.stockmonitor.data.remote.StockApiService
import com.stockmonitor.data.remote.dto.ParseResult
import com.stockmonitor.data.remote.dto.StockDataParser
import com.stockmonitor.domain.model.AlertState
import com.stockmonitor.domain.model.MonitoredStock
import com.stockmonitor.domain.model.RefreshResult
import com.stockmonitor.domain.model.StockData
import com.stockmonitor.domain.repository.StockMonitorRepository
import com.stockmonitor.util.FileLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 股票监控仓库实现
 */
@Singleton
class StockMonitorRepositoryImpl @Inject constructor(
    private val stockDao: StockDao,
    private val stockDataDao: StockDataDao,
    private val stockAlertDao: StockAlertDao,
    private val stockApiService: StockApiService,
    private val fileLogger: FileLogger
) : StockMonitorRepository {

    override fun getMonitoredStocks(): Flow<List<MonitoredStock>> {
        return combine(
            stockDao.getMonitoredStocks(),
            stockDataDao.getAllStockData()
        ) { stocks, stockDataList ->
            val stockDataMap = stockDataList.associateBy { normalizeCode(it.code) }
            stocks.map { stockEntity ->
                val normalizedCode = normalizeCode(stockEntity.code)
                val stockData = stockDataMap[normalizedCode]?.toDomainModel()
                val alertState = getAlertStateSync(stockEntity.code)
                MonitoredStock(
                    stock = stockEntity.toDomain(),
                    stockData = stockData,
                    lastAlertState = alertState
                )
            }
        }
    }

    private fun normalizeCode(code: String): String {
        return when {
            code.startsWith("sh") -> code.removePrefix("sh")
            code.startsWith("sz") -> code.removePrefix("sz")
            else -> code
        }
    }

    private suspend fun getAlertStateSync(code: String): AlertState {
        return stockAlertDao.getAlertState(code)?.toDomainModel() ?: AlertState()
    }

    override suspend fun getStockData(code: String): Result<StockData> {
        return try {
            val formattedCode = if (code.startsWith("sh") || code.startsWith("sz")) {
                code
            } else if (code.startsWith("6")) {
                "sh$code"
            } else {
                "sz$code"
            }

            val response = stockApiService.getStockData("list=$formattedCode")
            val parseResult = StockDataParser.parseSinaResponse(response, listOf(code))

            val dto = parseResult.stockDataList.firstOrNull()
                ?: return Result.failure(Exception("股票 $code 未找到或数据无效"))

            val stockData = StockData(
                code = dto.code,
                name = dto.name,
                currentPrice = dto.price,
                changeAmount = dto.change,
                changePercent = dto.percent,
                updateTime = System.currentTimeMillis()
            )
            cacheStockData(stockData)
            Result.success(stockData)
        } catch (e: Exception) {
            fileLogger.logApiError(
                apiName = "getStockData",
                code = code,
                errorMessage = e.message ?: "Unknown error",
                throwable = e
            )
            Result.failure(e)
        }
    }

    override suspend fun refreshStockPrices(codes: List<String>): Result<RefreshResult> {
        return try {
            val formattedCodes = codes.map { code ->
                when {
                    code.startsWith("sh") || code.startsWith("sz") -> code
                    code.startsWith("6") -> "sh$code"
                    else -> "sz$code"
                }
            }.joinToString(",")

            val response = stockApiService.getStockDataList("list=$formattedCodes")
            val parseResult = StockDataParser.parseSinaResponse(response, codes)

            if (parseResult.stockDataList.isNotEmpty()) {
                stockDataDao.insertAll(parseResult.stockDataList.map { dto ->
                    StockDataEntity(
                        code = dto.code,
                        name = dto.name,
                        currentPrice = dto.price,
                        changeAmount = dto.change,
                        changePercent = dto.percent,
                        updateTime = System.currentTimeMillis()
                    )
                })
            }

            val result = RefreshResult(
                successCount = parseResult.successCount,
                failedCodes = parseResult.failedCodes,
                totalRequested = codes.size,
                stockDataList = parseResult.stockDataList.map { dto ->
                    StockData(
                        code = dto.code,
                        name = dto.name,
                        currentPrice = dto.price,
                        changeAmount = dto.change,
                        changePercent = dto.percent,
                        updateTime = System.currentTimeMillis()
                    )
                }
            )
            Result.success(result)
        } catch (e: Exception) {
            fileLogger.logApiError(
                apiName = "refreshStockPrices",
                errorMessage = e.message ?: "Unknown error",
                throwable = e
            )
            Result.failure(e)
        }
    }

    override suspend fun updateAlertState(code: String, alertState: AlertState) {
        stockAlertDao.insertOrUpdate(
            StockAlertEntity(
                code = code,
                alertType = alertState.alertType.name,
                lastAlertTime = alertState.lastAlertTime
            )
        )
    }

    override suspend fun cacheStockData(stockData: StockData) {
        stockDataDao.insertAll(
            listOf(
                StockDataEntity(
                    code = stockData.code,
                    name = stockData.name,
                    currentPrice = stockData.currentPrice,
                    changeAmount = stockData.changeAmount,
                    changePercent = stockData.changePercent,
                    updateTime = stockData.updateTime
                )
            )
        )
    }

    override suspend fun getAlertState(code: String): AlertState {
        return getAlertStateSync(code)
    }
}
