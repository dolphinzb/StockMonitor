package com.stockmonitor.util

import java.util.Calendar

/**
 * 交易时间判断工具
 * 判断当前时间是否在A股交易时间段内
 *
 * 交易时间（北京时间 UTC+8）：
 * - 周一至周五
 * - 上午盘：9:30 - 11:30
 * - 午休：11:30 - 13:00（不交易）
 * - 下午盘：13:00 - 15:00
 */
object TradingTimeChecker {
    /**
     * 判断当前时间是否为交易时间（使用默认时间段）
     *
     * @return true 表示当前在交易时间内，false 表示不在交易时间内
     */
    fun isTradingTime(): Boolean {
        return isTradingTime(
            morningStartTime = "09:30",
            morningEndTime = "11:30",
            afternoonStartTime = "13:00",
            afternoonEndTime = "15:00"
        )
    }

    /**
     * 判断当前时间是否为交易时间（使用自定义时间段）
     *
     * @param morningStartTime 上午开始时间 (HH:mm)
     * @param morningEndTime 上午结束时间 (HH:mm)
     * @param afternoonStartTime 下午开始时间 (HH:mm)
     * @param afternoonEndTime 下午结束时间 (HH:mm)
     * @return true 表示当前在交易时间内，false 表示不在交易时间内
     */
    fun isTradingTime(
        morningStartTime: String,
        morningEndTime: String,
        afternoonStartTime: String,
        afternoonEndTime: String
    ): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // 周六(7) 和 周日(1) 不交易
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false
        }

        // 转换为分钟计数
        val currentMinutes = hourOfDay * 60 + minute

        // 解析自定义时间段
        val morningStart = timeToMinutes(morningStartTime)
        val morningEnd = timeToMinutes(morningEndTime)
        val afternoonStart = timeToMinutes(afternoonStartTime)
        val afternoonEnd = timeToMinutes(afternoonEndTime)

        return (currentMinutes in morningStart until morningEnd) ||
                (currentMinutes in afternoonStart until afternoonEnd)
    }

    /**
     * 将HH:mm格式的时间转换为分钟数
     */
    private fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return hour * 60 + minute
    }

    /**
     * 获取下一个交易时段的开始时间（毫秒）
     * 用于计算下次拉取股票的延迟时间
     *
     * @return 下一个交易时段开始的毫秒时间戳
     */
    fun getNextTradingTimeStart(): Long {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val currentMinutes = hourOfDay * 60 + minute

        // 如果是周末，直接跳到周一
        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 根据当前时间设置目标时段
        when {
            // 已经过了下午盘（15:00以后），转到下一个工作日
            currentMinutes >= 15 * 60 -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                // 找到下一个工作日
                while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                calendar.set(Calendar.HOUR_OF_DAY, 9)
                calendar.set(Calendar.MINUTE, 30)
            }
            // 在午休时段（11:30-13:00），转到下午盘
            currentMinutes in (11 * 60 + 30) until (13 * 60) -> {
                calendar.set(Calendar.HOUR_OF_DAY, 13)
                calendar.set(Calendar.MINUTE, 0)
            }
            // 在上午盘之前（9:30之前），转到上午盘开始
            currentMinutes < (9 * 60 + 30) -> {
                calendar.set(Calendar.HOUR_OF_DAY, 9)
                calendar.set(Calendar.MINUTE, 30)
            }
            // 在上午盘中，维持当前时间
            else -> {
                // 已经在交易时段，不需要调整
            }
        }

        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }
}
