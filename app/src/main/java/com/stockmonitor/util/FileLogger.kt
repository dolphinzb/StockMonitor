package com.stockmonitor.util

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 文件日志工具类
 * 用于将错误和异常信息记录到本地文件
 */
@Singleton
class FileLogger @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val LOG_DIR = "logs"
        private const val ERROR_LOG_FILE = "error.log"
        private const val MAX_LOG_SIZE = 5 * 1024 * 1024L
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private fun getLogDir(): File {
        val dir = File(context.filesDir, LOG_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun getLogFile(): File {
        return File(getLogDir(), ERROR_LOG_FILE)
    }

    /**
     * 记录错误日志
     */
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        try {
            val logFile = getLogFile()
            checkAndRotateLogFile()

            PrintWriter(FileWriter(logFile, true)).use { writer ->
                val timestamp = dateFormat.format(Date())
                writer.println("========================================")
                writer.println("时间: $timestamp")
                writer.println("标签: $tag")
                writer.println("消息: $message")
                throwable?.let {
                    writer.println("异常类型: ${it.javaClass.name}")
                    writer.println("异常信息: ${it.message}")
                    writer.println("堆栈跟踪:")
                    it.printStackTrace(writer)
                }
                writer.println()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 记录 API 错误
     */
    fun logApiError(apiName: String, code: String? = null, errorMessage: String, response: String? = null, throwable: Throwable? = null) {
        try {
            val logFile = getLogFile()
            checkAndRotateLogFile()

            PrintWriter(FileWriter(logFile, true)).use { writer ->
                val timestamp = dateFormat.format(Date())
                writer.println("========================================")
                writer.println("时间: $timestamp")
                writer.println("类型: API 错误")
                writer.println("API: $apiName")
                code?.let { writer.println("股票代码: $it") }
                writer.println("错误信息: $errorMessage")
                response?.let {
                    writer.println("响应内容: ${it.take(500)}")
                }
                throwable?.let {
                    writer.println("异常类型: ${it.javaClass.name}")
                    writer.println("异常信息: ${it.message}")
                    writer.println("堆栈跟踪:")
                    it.printStackTrace(writer)
                }
                writer.println()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 记录普通信息
     */
    fun logInfo(tag: String, message: String) {
        try {
            val logFile = getLogFile()
            checkAndRotateLogFile()

            PrintWriter(FileWriter(logFile, true)).use { writer ->
                val timestamp = dateFormat.format(Date())
                writer.println("[$timestamp] [$tag] $message")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkAndRotateLogFile() {
        val logFile = getLogFile()
        if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
            val backupFile = File(getLogDir(), "error_${System.currentTimeMillis()}.log")
            logFile.copyTo(backupFile, overwrite = true)
            logFile.delete()
        }
    }

    /**
     * 获取日志文件路径
     */
    fun getLogFilePath(): String {
        return getLogFile().absolutePath
    }

    /**
     * 获取所有日志文件
     */
    fun getAllLogFiles(): List<File> {
        val dir = getLogDir()
        return dir.listFiles()?.filter { it.isFile && it.extension == "log" }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
}
