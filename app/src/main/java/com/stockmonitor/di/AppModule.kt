package com.stockmonitor.di

import android.content.Context
import androidx.room.Room
import com.stockmonitor.data.local.StockDatabase
import com.stockmonitor.data.local.StockDataDao
import com.stockmonitor.data.remote.StockApiService
import com.stockmonitor.data.repository.StockRepositoryImpl
import com.stockmonitor.domain.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt 依赖注入模块
 * 提供应用程序所需的各种依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.stockmonitor.com/"

    /**
     * 提供 Room 数据库实例
     */
    @Provides
    @Singleton
    fun provideStockDatabase(
        @ApplicationContext context: Context
    ): StockDatabase {
        return Room.databaseBuilder(
            context,
            StockDatabase::class.java,
            StockDatabase.DATABASE_NAME
        ).build()
    }

    /**
     * 提供股票数据访问对象
     */
    @Provides
    @Singleton
    fun provideStockDataDao(database: StockDatabase): StockDataDao {
        return database.stockDataDao()
    }

    /**
     * 提供 OkHttpClient 实例
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 提供 Retrofit 实例
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 提供股票 API 服务
     */
    @Provides
    @Singleton
    fun provideStockApiService(retrofit: Retrofit): StockApiService {
        return retrofit.create(StockApiService::class.java)
    }

    /**
     * 提供股票仓库
     */
    @Provides
    @Singleton
    fun provideStockRepository(
        stockDataDao: StockDataDao,
        stockApiService: StockApiService
    ): StockRepository {
        return StockRepositoryImpl(stockDataDao, stockApiService)
    }
}
