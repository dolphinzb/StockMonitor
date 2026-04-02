package com.stockmonitor.di

import android.content.Context
import androidx.room.Room
import com.stockmonitor.data.local.StockDao
import com.stockmonitor.data.local.StockDatabase
import com.stockmonitor.data.local.StockDataDao
import com.stockmonitor.data.local.StockAlertDao
import com.stockmonitor.data.remote.StockApiService
import com.stockmonitor.data.repository.StockRepositoryImpl
import com.stockmonitor.data.repository.StockPoolRepositoryImpl
import com.stockmonitor.service.StockPriceWorker
import com.stockmonitor.util.FileLogger
import com.stockmonitor.data.repository.StockMonitorRepositoryImpl
import com.stockmonitor.domain.repository.StockPoolRepository
import com.stockmonitor.domain.repository.StockRepository
import com.stockmonitor.domain.repository.StockMonitorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt 依赖注入模块
 * 提供应用程序所需的各种依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://hq.sinajs.cn/"

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
        )
            .fallbackToDestructiveMigration()
            .build()
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
     * 提供股票池数据访问对象
     */
    @Provides
    @Singleton
    fun provideStockDao(database: StockDatabase): StockDao {
        return database.stockDao()
    }

    /**
     * 提供告警状态数据访问对象
     */
    @Provides
    @Singleton
    fun provideStockAlertDao(database: StockDatabase): StockAlertDao {
        return database.stockAlertDao()
    }

    /**
     * 提供文件日志工具
     */
    @Provides
    @Singleton
    fun provideFileLogger(
        @ApplicationContext context: Context
    ): FileLogger {
        return FileLogger(context)
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
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Referer", "https://finance.sina.com.cn/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .build()
                chain.proceed(request)
            }
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
            .addConverterFactory(ScalarsConverterFactory.create())
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
        stockApiService: StockApiService,
        fileLogger: FileLogger
    ): StockRepository {
        return StockRepositoryImpl(stockDataDao, stockApiService, fileLogger)
    }

    /**
     * 提供股票池仓库
     */
    @Provides
    @Singleton
    fun provideStockPoolRepository(
        stockDao: StockDao
    ): StockPoolRepository {
        return StockPoolRepositoryImpl(stockDao)
    }

    /**
     * 提供股票监控仓库
     */
    @Provides
    @Singleton
    fun provideStockMonitorRepository(
        stockDao: StockDao,
        stockDataDao: StockDataDao,
        stockAlertDao: StockAlertDao,
        stockApiService: StockApiService,
        fileLogger: FileLogger
    ): StockMonitorRepository {
        return StockMonitorRepositoryImpl(stockDao, stockDataDao, stockAlertDao, stockApiService, fileLogger)
    }
}
