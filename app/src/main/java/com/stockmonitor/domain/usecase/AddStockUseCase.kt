package com.stockmonitor.domain.usecase

import com.stockmonitor.domain.model.Stock
import com.stockmonitor.domain.repository.StockPoolRepository
import javax.inject.Inject

/**
 * 添加股票用例
 * 负责验证股票格式（6位数字）、检测重复，然后添加到数据库
 */
class AddStockUseCase @Inject constructor(
    private val repository: StockPoolRepository
) {
    /**
     * 执行添加股票操作
     * @param code 股票代码
     * @param name 股票名称
     * @return Result，包含成功后的股票 ID 或错误信息
     */
    suspend operator fun invoke(code: String, name: String): Result<Long> {
        val trimmedCode = code.trim()
        val trimmedName = name.trim()

        if (trimmedCode.isEmpty()) {
            return Result.failure(IllegalArgumentException("股票代码不能为空"))
        }

        if (!isValidStockCode(trimmedCode)) {
            return Result.failure(IllegalArgumentException("股票代码必须是6位数字"))
        }

        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("股票名称不能为空"))
        }

        if (repository.isStockCodeExists(trimmedCode)) {
            return Result.failure(IllegalArgumentException("该股票已在股票池中"))
        }

        val stock = Stock(
            code = trimmedCode,
            name = trimmedName,
            sortOrder = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val id = repository.addStock(stock)
        return Result.success(id)
    }

    /**
     * 验证股票代码格式
     * 必须为6位数字
     */
    private fun isValidStockCode(code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() }
    }
}
