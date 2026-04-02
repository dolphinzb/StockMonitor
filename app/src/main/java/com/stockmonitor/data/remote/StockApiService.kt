package com.stockmonitor.data.remote

import retrofit2.http.GET
import retrofit2.http.Url

/**
 * 股票 API 服务接口
 * 新浪财经 API: https://hq.sinajs.cn/list=sh600519,sz000001
 * 返回格式: var hq_str_sh600519="name,price,change,percent,open,yesterdayClose,high,low,volume,amount,time,date";
 */
interface StockApiService {
    @GET
    suspend fun getStockData(
        @Url codes: String
    ): String

    @GET
    suspend fun getStockDataList(
        @Url codes: String
    ): String
}
