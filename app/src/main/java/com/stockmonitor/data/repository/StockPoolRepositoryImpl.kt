package com.stockmonitor.data.repository

import com.stockmonitor.data.local.StockDao
import com.stockmonitor.data.local.entity.StockEntity
import com.stockmonitor.domain.model.Stock
import com.stockmonitor.domain.repository.StockPoolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 股票池仓库实现类
 * 实现 StockPoolRepository 接口，负责股票池数据的持久化操作
 */
@Singleton
class StockPoolRepositoryImpl @Inject constructor(
    private val stockDao: StockDao
) : StockPoolRepository {

    override fun getAllStocks(): Flow<List<Stock>> {
        return stockDao.getAllStocks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchStocks(keyword: String): Flow<List<Stock>> {
        return stockDao.searchStocks(keyword).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getStockById(id: Long): Stock? {
        return stockDao.getStockById(id)?.toDomain()
    }

    override suspend fun addStock(stock: Stock): Long {
        return stockDao.addStock(StockEntity.fromDomain(stock))
    }

    override suspend fun updateStock(stock: Stock) {
        stockDao.updateStock(StockEntity.fromDomain(stock))
    }

    override suspend fun deleteStock(stock: Stock) {
        stockDao.deleteStock(StockEntity.fromDomain(stock))
    }

    override suspend fun deleteStocksByIds(ids: List<Long>) {
        stockDao.deleteStocksByIds(ids)
    }

    override suspend fun updateThreshold(id: Long, sellThreshold: Double?, buyThreshold: Double?) {
        stockDao.updateThreshold(id, sellThreshold, buyThreshold, System.currentTimeMillis())
    }

    override suspend fun isStockCodeExists(code: String): Boolean {
        return stockDao.isStockCodeExists(code)
    }

    override suspend fun updateSortOrder(stockId: Long, newSortOrder: Int) {
        stockDao.updateSortOrder(stockId, newSortOrder, System.currentTimeMillis())
    }

    override suspend fun reorderStocks(stocks: List<Stock>) {
        val entities = stocks.map { StockEntity.fromDomain(it) }
        stockDao.insertAll(entities)
    }
}
