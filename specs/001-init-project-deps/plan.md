# Implementation Plan: 初始化项目结构与依赖

**Branch**: `001-init-project-deps` | **Date**: 2026-03-31 | **Spec**: [spec.md](file:///c:/AndroidProjects/stock_monitor/specs/001-init-project-deps/spec.md)
**Input**: Feature specification from `/specs/001-init-project-deps/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

初始化股票监控Android应用项目结构，配置所有核心依赖库（Retrofit、OkHttp、Room、Hilt、Jetpack Compose），建立Clean Architecture + MVVM架构的基础框架，配置单元测试和UI测试环境，确保项目可成功编译并运行。

## Technical Context

**Language/Version**: Kotlin 1.9+
**Primary Dependencies**: Retrofit 2.9.0, OkHttp 4.12.0, Room 2.6.1, Hilt 2.50, Jetpack Compose BOM 2024.02.00
**Storage**: Room Database (SQLite abstraction)
**Testing**: JUnit 5, Mockito 5.8.0, Espresso 3.5.1
**Target Platform**: Android (API 30-35)
**Project Type**: Mobile-app (Android)
**Performance Goals**: 编译时间 < 5分钟，APK大小 < 50MB，内存占用 < 100MB
**Constraints**: 数据刷新间隔3分钟，UI响应 < 500ms
**Scale/Scope**: 单用户本地应用，支持离线缓存

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| 宪法条款 | 检查项 | 状态 |
|----------|--------|------|
| I. 数据及时性 (DATA_TIMELINESS) | 默认刷新间隔3分钟 | ✅ PASS |
| II. 电量与性能优化 (PERFORMANCE_OPTIMIZED) | 内存占用 < 100MB | ✅ PASS |
| III. 离线可用性 (OFFLINE_CAPABLE) | Room本地缓存配置 | ✅ PASS |
| IV. 简洁UI/UX (SIMPLE_UI) | Jetpack Compose声明式UI | ✅ PASS |
| V. 测试覆盖率 (TEST_DRIVEN) | JUnit + Mockito + Espresso | ✅ PASS |
| 技术约束 - Kotlin 1.9+ | 强制要求 | ✅ PASS |
| 技术约束 - MVVM + Clean Architecture | 强制要求 | ✅ PASS |
| 技术约束 - Hilt | 强制要求 | ✅ PASS |
| 技术约束 - Coroutines + Flow | 强制要求 | ✅ PASS |
| 技术约束 - Retrofit + OkHttp | 强制要求 | ✅ PASS |
| 技术约束 - Room Database | 强制要求 | ✅ PASS |
| 技术约束 - Jetpack Compose | 强制要求 | ✅ PASS |
| 技术约束 - Min SDK 30 | 强制要求 | ✅ PASS |
| 技术约束 - Target SDK 35 | 强制要求 | ✅ PASS |
| 权限要求 - INTERNET | 强制要求 | ✅ PASS |
| 权限要求 - 禁止后台自启 | 强制要求 | ✅ PASS |

## Project Structure

### Documentation (this feature)

```text
specs/001-init-project-deps/
├── plan.md              # This file
├── research.md          # Phase 0 output (dependency research)
├── data-model.md        # Phase 1 output (entity definitions)
├── quickstart.md        # Phase 1 output (build/run guide)
└── tasks.md             # Phase 2 output (/speckit.tasks)

contracts/
└── (N/A - 内部项目无外部接口)
```

### Source Code (repository root)

```text
stock_monitor/                    # 项目根目录
├── app/                          # 主应用模块
│   ├── src/main/
│   │   ├── java/com/stockmonitor/
│   │   │   ├── data/             # 数据层 (Clean Architecture)
│   │   │   │   ├── local/        # Room数据库、本地数据源
│   │   │   │   ├── remote/       # Retrofit API、网络数据源
│   │   │   │   └── repository/   # Repository实现
│   │   │   ├── domain/           # 领域层 (Clean Architecture)
│   │   │   │   ├── model/        # 领域实体
│   │   │   │   ├── repository/   # Repository接口
│   │   │   │   └── usecase/     # 用例
│   │   │   ├── presentation/    # 展示层 (MVVM)
│   │   │   │   ├── ui/           # Compose UI
│   │   │   │   ├── viewmodel/    # ViewModel
│   │   │   │   └── state/        # UI状态
│   │   │   ├── di/               # Hilt依赖注入模块
│   │   │   │   └── AppModule.kt
│   │   │   └── StockMonitorApp.kt # Application类
│   │   └── res/                 # 资源文件
│   └── build.gradle.kts
├── gradle/                       # 仅用于IDE识别（可选）
├── build.gradle.kts              # 根构建配置
├── settings.gradle.kts           # 项目设置
└── gradle.properties            # Gradle属性

tests/
├── unit/                        # 单元测试
│   └── (对应源码结构)
└── androidTest/                 # UI测试 (Espresso)
    └── (对应源码结构)
```

**Structure Decision**: 标准Android项目结构，采用Clean Architecture分三层（data/domain/presentation），使用Hilt进行依赖注入。app模块为唯一应用模块，tests目录分离单元测试和Instrumented测试。

## Complexity Tracking

> 无复杂度违规。完全遵循宪法技术约束，未引入额外复杂性。

---

# Phase 0: Research

## research.md - 依赖库版本研究

### 依赖库版本选择

| 库 | 版本 | 选择理由 |
|----|------|----------|
| Kotlin | 1.9.22 | 宪法要求1.9+，稳定版本 |
| Compose BOM | 2024.02.00 | 与Kotlin 1.9.x兼容的最新稳定版 |
| Hilt | 2.50 | 支持Kotlin 1.9的最新版 |
| Room | 2.6.1 | 支持Kotlin 1.9的最新版 |
| Retrofit | 2.9.0 | 稳定版本，与OkHttp 4.x兼容 |
| OkHttp | 4.12.0 | 最新稳定版，支持HTTP/2 |
| Coroutines | 1.7.3 | 与Kotlin 1.9.x配套稳定版 |
| JUnit | 5.10.1 | 最新稳定版本 |
| Mockito | 5.8.0 | 支持Kotlin 5.0前最新稳定版 |
| Espresso | 3.5.1 | Compose测试支持版本 |

### Gradle版本

| 组件 | 版本 | 选择理由 |
|------|------|----------|
| Gradle | 8.14.4 | 用户本地版本，支持AGP 8.2+，Kotlin 1.9 |
| Android Gradle Plugin | 8.2.2 | 支持SDK 35，兼容Gradle 8.4 |
| KSP | 1.9.22-1.0.17 | 与Kotlin 1.9.22配套 |

### 替代方案评估

**Compose vs XML ViewSystem**: 选择Compose
- 宪法要求Jetpack Compose
- 声明式UI更适合现代化Android开发
- 更好的预览和测试支持

**Kotlin DSL vs Groovy**: 选择Kotlin DSL
- 现代Android标准
- 类型安全，IDE支持更好
- 宪法未指定，保持现代实践

---

# Phase 1: Design

## data-model.md - 数据模型定义

### 核心实体

```kotlin
/**
 * 股票数据实体
 * 包含股票代码、名称、当前价格、涨跌额、涨跌幅、更新时间
 */
data class StockData(
    val code: String,          // 股票代码，如 "600000" (上证) 或 "000001" (深证)
    val name: String,          // 股票名称
    val currentPrice: Double, // 当前价格
    val changeAmount: Double, // 涨跌额
    val changePercent: Double, // 涨跌幅百分比
    val updateTime: Long      // 更新时间戳 (毫秒)
)

/**
 * 用户股票配置
 * 包含关注的股票列表、刷新间隔偏好、告警阈值
 */
data class StockConfig(
    val stockCodes: List<String>,    // 关注的股票代码列表
    val refreshIntervalMinutes: Int, // 刷新间隔（分钟）
    val priceAlertThreshold: Double  // 价格波动告警阈值（百分比）
)

/**
 * 缓存数据实体
 * 存储最近一次同步的股票数据，支持离线查看
 */
data class CacheData(
    val stocks: List<StockData>, // 缓存的股票数据列表
    val cachedAt: Long          // 缓存时间戳
)
```

### Room数据库表

```kotlin
@Entity(tableName = "stock_data")
data class StockDataEntity(
    @PrimaryKey val code: String,
    val name: String,
    val currentPrice: Double,
    val changeAmount: Double,
    val changePercent: Double,
    val updateTime: Long
)

@Entity(tableName = "stock_config")
data class StockConfigEntity(
    @PrimaryKey val id: Int = 1, // 单例配置
    val stockCodes: String,     // JSON序列化
    val refreshIntervalMinutes: Int,
    val priceAlertThreshold: Double
)
```

## quickstart.md - 快速开始指南

### 环境要求

- JDK 17+
- Trae IDE (Android开发插件)
- Android SDK API 30-35
- Gradle 8.14.4

### 构建步骤

```bash
# 1. 克隆项目
cd StockMonitor

# 2. 同步依赖
gradle dependencies

# 3. 编译Debug APK
gradle assembleDebug

# 4. 运行lint检查
gradle lint

# 5. 运行单元测试
gradle test

# 6. 运行UI测试 (需设备或模拟器)
gradle connectedAndroidTest
```

### 项目架构概览

- **data层**: 处理网络请求(Retrofit)和本地存储(Room)
- **domain层**: 定义业务模型和用例
- **presentation层**: 实现MVVM模式，使用Compose构建UI

### 关键文件位置

| 文件 | 路径 |
|------|------|
| Application类 | app/src/main/java/com/stockmonitor/StockMonitorApp.kt |
| Hilt模块 | app/src/main/java/com/stockmonitor/di/AppModule.kt |
| 主页UI | app/src/main/java/com/stockmonitor/presentation/ui/MainScreen.kt |
| ViewModel | app/src/main/java/com/stockmonitor/presentation/viewmodel/StockViewModel.kt |
| 数据库 | app/src/main/java/com/stockmonitor/data/local/StockDatabase.kt |
| API服务 | app/src/main/java/com/stockmonitor/data/remote/StockApiService.kt |
