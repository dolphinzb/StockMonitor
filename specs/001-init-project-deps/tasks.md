# 任务列表：初始化项目结构与依赖

**输入**: 来自 `/specs/001-init-project-deps/` 的设计文档
**前置条件**: plan.md (必需), spec.md (用户故事必需)

## 格式: `[ID] [P?] [Story] 描述`

- **[P]**: 可并行执行（不同文件，无依赖）
- **[Story]**: 所属用户故事 (如 US1, US2, US3)
- 描述中需包含具体文件路径

## 路径约定

- 项目根目录: `.` (即 `c:\AndroidProjects\StockMonitor`)
- Android 应用模块: `./app/`
- 测试目录: `./tests/unit/`, `./tests/androidTest/`

## 阶段 1: 项目初始化

**目的**: 创建带有 Gradle wrapper 和基本配置的 Android 项目骨架

- [x] T001 [P] 在当前目录创建根项目结构和 build.gradle.kts
- [x] T002 [P] 创建 settings.gradle.kts，包含项目名称和模块配置
- [x] T003 [P] 创建 gradle.properties，包含 AndroidX 配置和 Kotlin daemon 参数
- [x] T004 [P] 创建 app 模块目录结构 (src/main/java/com/stockmonitor/, src/main/res/)
- [x] T005 [P] 在项目根目录初始化 Gradle wrapper (gradle wrapper)

---

## 阶段 2: 核心配置

**目的**: 配置构建脚本中的所有依赖，创建基础应用程序基础设施

**⚠️ 关键**: 此阶段完成前，任何用户故事都不能开始

- [x] T006 创建根 build.gradle.kts，包含插件声明 (Kotlin, Android, Hilt, KSP)
- [x] T007 创建 app/build.gradle.kts，包含所有依赖 (Retrofit 2.9.0, OkHttp 4.12.0, Room 2.6.1, Hilt 2.50, Compose BOM 2024.02.00, Coroutines 1.7.3)
- [x] T008 创建 gradle-wrapper.properties，包含 Gradle 8.14.4 配置
- [x] T009 [P] 创建 AndroidManifest.xml，包含 INTERNET 权限和 API 30-35 SDK 目标
- [x] T010 [P] 创建 StockMonitorApp.kt Application 类，带 @HiltAndroidApp 注解
- [x] T011 [P] 创建 MainActivity.kt，包含 Jetpack Compose 内容
- [x] T012 配置 proguard-rules.pro 用于发布版构建优化
- [x] T013 在 app/build.gradle.kts 中配置测试依赖 (JUnit 5.10.1, Mockito 5.8.0, Espresso 3.5.1)

**检查点**: 基础配置完成 - 项目应能成功编译

---

## 阶段 3: 用户故事 1 - 开发者初始化项目环境 (优先级: P1) 🎯 MVP

**目标**: 开发者可以克隆代码仓库后，运行 gradle assembleDebug 命令，成功编译出APK安装包

**独立测试**: 运行 `gradle assembleDebug` 成功生成 APK 且无错误

### 用户故事 1 的测试 (构建验证)

- [ ] T014 [P] [US1] 在 tests/unit/build/gradlew_test.sh 验证 gradle wrapper 是否正常工作
- [ ] T015 [P] [US1] 在 tests/unit/build/compile_test.sh 验证项目能否用 assembleDebug 编译

### 用户故事 1 的实现

- [x] T016 [US1] 在 app/src/main/java/com/stockmonitor/StockMonitorApp.kt 创建基础 StockMonitorApp Application 类
- [x] T017 [US1] 在 app/src/main/java/com/stockmonitor/presentation/ui/MainActivity.kt 创建带 Compose 主题的 MainActivity
- [x] T018 [US1] 在 app/src/main/java/com/stockmonitor/presentation/ui/theme/ 创建基础 Compose 主题
- [x] T019 [US1] 在 app/src/main/res/ 添加 Android 资源文件 (colors.xml, strings.xml, themes.xml)

**检查点**: 用户故事 1 完成 - APK 构建成功

---

## 阶段 4: 用户故事 2 - 项目依赖管理 (优先级: P1)

**目标**: 开发者可以运行 gradle dependencies 查看依赖树，确认关键依赖已正确引入

**独立测试**: 运行 `gradle dependencies` 输出包含所有核心依赖

### 用户故事 2 的实现

- [x] T020 [P] [US2] 在 app/src/main/java/com/stockmonitor/di/AppModule.kt 创建 Hilt AppModule 用于依赖注入
- [x] T021 [P] [US2] 在 app/src/main/java/com/stockmonitor/data/local/ 创建 Room 数据库类 (StockDatabase, StockDataEntity)
- [x] T022 [P] [US2] 在 app/src/main/java/com/stockmonitor/data/remote/StockApiService.kt 创建 Retrofit API 接口
- [x] T023 [P] [US2] 在 app/src/main/java/com/stockmonitor/data/repository/ 创建 Repository 实现
- [x] T024 [US2] 在 app/src/main/java/com/stockmonitor/domain/model/ 创建领域模型类 (StockData, StockConfig, CacheData)
- [x] T025 [US2] 在 app/src/main/java/com/stockmonitor/domain/repository/ 创建领域仓库接口
- [x] T026 [US2] 在 app/src/main/java/com/stockmonitor/domain/usecase/ 创建用例类

**检查点**: 用户故事 2 完成 - 所有核心依赖已配置并使用

---

## 阶段 5: 用户故事 3 - 代码规范检查 (优先级: P2)

**目标**: 开发者可以运行 gradle lint 执行代码检查，查看规范报告

**独立测试**: 运行 `gradle lint` 生成 build/reports/lint-results.html

### 用户故事 3 的实现

- [x] T027 [P] [US3] 在当前目录配置 lint.xml，包含中文注释检查和 Android Kotlin 规则
- [x] T028 [P] [US3] 在 domain/model/ 中为所有公共类和方法添加 Javadoc 文档
- [x] T029 [US3] 创建符合项目约定的代码风格配置

**检查点**: 用户故事 3 完成 - lint 运行成功且配置正确

---

## 阶段 6: 收尾与跨领域事项

**目的**: 最终集成和验证

- [ ] T030 [P] 使用 assembleDebug 运行最终构建验证
- [ ] T031 [P] 验证 lint 配置是否正常工作
- [ ] T032 验证所有用户故事是否可独立测试
- [ ] T033 [P] 创建包含构建说明的 README.md（不主动创建 - 如未请求则跳过）

---

## 依赖关系与执行顺序

### 阶段依赖

- **项目初始化 (阶段 1)**: 无依赖 - 可立即开始
- **核心配置 (阶段 2)**: 依赖于项目初始化完成 - 阻塞所有用户故事
- **用户故事 (阶段 3-5)**: 均依赖于核心配置完成
- **收尾 (阶段 6)**: 依赖于所有用户故事完成

### 用户故事依赖

- **用户故事 1 (P1)**: 核心配置完成后即可开始 - 不依赖其他故事
- **用户故事 2 (P1)**: 核心配置完成后即可开始 - 不依赖其他故事
- **用户故事 3 (P2)**: 核心配置完成后即可开始 - 不依赖其他故事

### 各用户故事内部

- 应首先完成设置任务 (T001-T005)
- 必须在任何用户故事之前完成基础任务 (T006-T013)
- 核心配置完成后，用户故事任务可并行进行

### 并行执行机会

- 所有标记为 [P] 的设置任务可并行执行
- 所有标记为 [P] 的核心任务可并行执行
- 所有标记为 [P] 的用户故事任务可并行执行
- T016-T019 (US1) 在核心配置完成后可并行执行
- T020-T026 (US2) 在核心配置完成后可并行执行
- T027-T029 (US3) 在核心配置完成后可并行执行

---

## 并行执行示例：项目初始化阶段

```bash
# 项目初始化任务可并行执行
pwsh -Command "New-Item -ItemType Directory -Path './app/src/main/java/com/stockmonitor' -Force"
pwsh -Command "New-Item -ItemType Directory -Path './app/src/main/res' -Force"
pwsh -Command "New-Item -ItemType Directory -Path './gradle/wrapper' -Force"
```

---

## 实施策略

**MVP 范围**: 用户故事 1 - 可编译的基础项目
**增量交付**:
1. 阶段 1-2: 带基本配置的项目骨架 (T001-T013)
2. 阶段 3: 用户故事 1 - 可工作的 APK 构建 (T014-T019)
3. 阶段 4: 用户故事 2 - 所有依赖已集成 (T020-T026)
4. 阶段 5: 用户故事 3 - lint 配置 (T027-T029)
5. 阶段 6: 收尾和验证 (T030-T032)

---

## 文件路径参考

### 根目录
- `./build.gradle.kts`
- `./settings.gradle.kts`
- `./gradle.properties`
- `./gradle/wrapper/gradle-wrapper.jar`
- `./gradle/wrapper/gradle-wrapper.properties`
- `./gradlew`
- `./gradlew.bat`
- `./proguard-rules.pro`

### 应用模块
- `./app/build.gradle.kts`
- `./app/src/main/AndroidManifest.xml`
- `./app/src/main/java/com/stockmonitor/StockMonitorApp.kt`
- `./app/src/main/java/com/stockmonitor/MainActivity.kt`
- `./app/src/main/java/com/stockmonitor/di/AppModule.kt`
- `./app/src/main/java/com/stockmonitor/data/local/StockDatabase.kt`
- `./app/src/main/java/com/stockmonitor/data/local/StockDataEntity.kt`
- `./app/src/main/java/com/stockmonitor/data/remote/StockApiService.kt`
- `./app/src/main/java/com/stockmonitor/data/repository/StockRepositoryImpl.kt`
- `./app/src/main/java/com/stockmonitor/domain/model/StockData.kt`
- `./app/src/main/java/com/stockmonitor/domain/model/StockConfig.kt`
- `./app/src/main/java/com/stockmonitor/domain/model/CacheData.kt`
- `./app/src/main/java/com/stockmonitor/domain/repository/StockRepository.kt`
- `./app/src/main/java/com/stockmonitor/domain/usecase/`
- `./app/src/main/java/com/stockmonitor/presentation/ui/theme/`
- `./app/src/main/java/com/stockmonitor/presentation/viewmodel/`
- `./app/src/main/res/values/colors.xml`
- `./app/src/main/res/values/strings.xml`
- `./app/src/main/res/values/themes.xml`

### 测试
- `./tests/unit/build/gradlew_test.sh`
- `./tests/unit/build/compile_test.sh`
- `./tests/unit/com/stockmonitor/domain/model/`
- `./tests/unit/com/stockmonitor/data/repository/`
- `./tests/androidTest/com/stockmonitor/`

---

## 摘要

- **总任务数**: 33
- **已完成任务**: 29 (T001-T013, T016-T029)
- **待完成任务**: 4 (T014-T015, T030-T032)
- **用户故事 1 任务**: 6 (T014-T019) - 4已完成，2待完成
- **用户故事 2 任务**: 7 (T020-T026) - 全部完成
- **用户故事 3 任务**: 3 (T027-T029) - 全部完成
- **项目初始化和核心配置任务**: 13 (T001-T013) - 全部完成
- **收尾任务**: 3 (T030-T032) - 待完成
- **并行执行机会**: 18 个任务标记为 [P]
- **建议 MVP 范围**: 用户故事 1 (阶段 3) - 基础项目编译
