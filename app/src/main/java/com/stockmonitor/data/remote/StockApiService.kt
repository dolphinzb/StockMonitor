package com.stockmonitor.data.remote

import com.stockmonitor.data.remote.dto.StockResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 股票 API 服务接口
 * 定义股票数据获取的网络请求
 */
interface StockApiService {
    /**
     * 获取股票数据
     * @param codes 股票代码列表，多个代码用逗号分隔
     * @return 股票响应数据
     */
    @GET("api/stock")
    suspend fun getStockData(
        @Query("codes") codes: String
    ): StockResponse
}
