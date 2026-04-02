package com.stockmonitor.data.repository

import com.stockmonitor.data.local.StockDataDao
import com.stockmonitor.data.local.StockDataEntity
import com.stockmonitor.data.remote.StockApiService
import com.stockmonitor.data.remote.dto.StockDataParser
import com.stockmonitor.domain.model.StockData
import com.stockmonitor.domain.repository.StockRepository
import com.stockmonitor.util.FileLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 股票数据仓库实现
 * 实现 StockRepository 接口，负责整合本地数据库和网络 API 数据源
 */
@Singleton
class StockRepositoryImpl @Inject constructor(
    private val stockDataDao: StockDataDao,
    private val stockApiService: StockApiService,
    private val fileLogger: FileLogger
) : StockRepository {

    override fun getStockData(codes: List<String>): Flow<List<StockData>> {
        return stockDataDao.getStockDataByCodes(codes).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshStockData(codes: List<String>): Result<Unit> {
        return try {
            val formattedCodes = codes.map { code ->
                when {
                    code.startsWith("sh") || code.startsWith("sz") -> code
                    code.startsWith("6") -> "sh$code"
                    else -> "sz$code"
                }
            }.joinToString(",")

            val response = stockApiService.getStockData("list=$formattedCodes")
            val parseResult = StockDataParser.parseSinaResponse(response, codes)

            val entities = parseResult.stockDataList.map { dto ->
                StockDataEntity(
                    code = dto.code,
                    name = dto.name,
                    currentPrice = dto.price,
                    changeAmount = dto.change,
                    changePercent = dto.percent,
                    updateTime = System.currentTimeMillis()
                )
            }
            stockDataDao.insertAll(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            fileLogger.logApiError(
                apiName = "refreshStockData",
                errorMessage = e.message ?: "Unknown error",
                throwable = e
            )
            Result.failure(e)
        }
    }

    override suspend fun getCachedStockData(codes: List<String>): List<StockData> {
        return stockDataDao.getStockDataByCodes(codes).map { entities ->
            entities.map { it.toDomainModel() }
        }.let { flow ->
            val result = mutableListOf<StockData>()
            flow.collect { result.addAll(it) }
            result
        }
    }
}
