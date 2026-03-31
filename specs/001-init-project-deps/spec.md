# Feature Specification: 初始化项目结构与依赖

**Feature Branch**: `001-init-project-deps`
**Created**: 2026-03-31
**Status**: Draft
**Input**: User description: "创建项目，添加依赖"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - 开发者初始化项目环境 (Priority: P1)

作为Android开发者，我需要能够快速初始化股票监控应用的项目结构，以便开始功能开发。

**Why this priority**: 项目初始化是一切开发工作的基础，没有可运行的项目框架无法进行任何后续开发。

**Independent Test**: 开发者可以克隆代码仓库后，运行 `gradle assembleDebug` 命令，成功编译出APK安装包。

**Acceptance Scenarios**:

1. **Given** 空的Android项目目录，**When** 执行项目初始化脚本，**Then** 生成包含所有源代码和资源文件的完整项目结构
2. **Given** 完整的项目结构，**When** 执行 `gradle assembleDebug`，**Then** 成功编译生成APK且无错误
3. **Given** 编译成功的APK，**When** 安装到Android 11+设备，**Then** 应用可以正常启动并显示主界面

---

### User Story 2 - 项目依赖管理 (Priority: P1)

作为开发者，我需要项目包含所有必要的依赖库，以便实现股票数据获取、存储和展示功能。

**Why this priority**: 依赖库是功能实现的基础，缺少必要依赖将导致功能无法开发。

**Independent Test**: 开发者可以运行 `gradle dependencies` 查看依赖树，确认关键依赖已正确引入。

**Acceptance Scenarios**:

1. **Given** 项目根目录的 build.gradle.kts，**When** 检查依赖配置，**Then** 包含 Retrofit、OkHttp、Room、Hilt、Kotlin Coroutines、Jetpack Compose 等核心依赖
2. **Given** 应用模块的 build.gradle.kts，**When** 检查依赖配置，**Then** 所有依赖版本与宪法规定的技术栈一致
3. **Given** 依赖配置，**When** 执行 `gradle assembleRelease --dry-run`，**Then** 构建计划成功生成，无依赖冲突

---

### User Story 3 - 代码规范检查 (Priority: P2)

作为开发团队成员，我需要项目配置代码规范检查工具，确保代码符合项目质量要求。

**Why this priority**: 代码规范是保障代码质量和可维护性的基础，需要在项目初始化时一并配置。

**Independent Test**: 开发者可以运行 `gradle lint` 执行代码检查，查看规范报告。

**Acceptance Scenarios**:

1. **Given** 项目根目录配置，**When** 检查 lint 配置，**Then** 启用了中文注释检查和 Android Kotlin 编码规范检查
2. **Given** 项目代码存在规范问题，**When** 运行 `gradle lint`，**Then** 问题被正确报告到 build/reports/lint-results.html

---

### Edge Cases

- 如果 Gradle 版本不兼容，项目应能清晰提示需要的 Gradle 版本范围
- 如果某个依赖库无法下载（网络问题），构建应给出清晰的错误提示
- 如果本地缺少必要的 SDK 组件，build.gradle.kts 应能识别并提示安装

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: 项目必须使用 Kotlin 1.9+ 作为开发语言
- **FR-002**: 项目必须采用 MVVM + Clean Architecture 架构模式
- **FR-003**: 项目必须配置 Hilt 作为依赖注入框架
- **FR-004**: 项目必须使用 Kotlin Coroutines + Flow 处理异步操作
- **FR-005**: 项目必须使用 Retrofit + OkHttp 进行网络请求
- **FR-006**: 项目必须使用 Room Database 进行本地数据存储
- **FR-007**: 项目必须使用 Jetpack Compose 构建 UI
- **FR-008**: 项目最低支持 API 30 (Android 11)，目标 API 35 (Android 15)
- **FR-009**: 项目必须配置单元测试框架 (JUnit + Mockito)
- **FR-010**: 项目必须配置 Android UI 测试框架 (Espresso)

### Key Entities

- **StockData**: 股票数据实体，包含股票代码、名称、当前价格、涨跌额、涨跌幅、更新时间
- **StockConfig**: 用户自选的股票配置，包含关注的股票列表、刷新间隔偏好、告警阈值
- **CacheData**: 缓存数据实体，用于存储最近一次同步的股票数据，支持离线查看

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 开发者可以在 5 分钟内完成项目初始化并编译通过
- **SC-002**: `gradle assembleDebug` 成功执行且无错误输出
- **SC-003**: APK 可以成功安装到 Android 11+ 设备并正常启动
- **SC-004**: `gradle dependencies` 输出包含所有核心依赖（Retrofit, OkHttp, Room, Hilt, Compose）
- **SC-005**: `gradle lint` 可以成功执行并生成报告

## Assumptions

- 开发者已安装 Android Studio Jellyfish 或更高版本
- 开发者已配置 JAVA_HOME 环境变量，JDK 版本为 17 或更高
- 开发者已安装 Android SDK 并配置 ANDROID_HOME
- 开发者具有稳定网络连接以下载依赖库
- 项目代码将遵循宪法规定的代码规范（中文 Javadoc 注释）
