# Technical Documentation — Android Basic Clean Architecture

> **Enterprise Android Kotlin Modular Architecture with Product Flavors, Jetpack Compose, Koin DI & Ktor Auth Interceptors**  
> *Architectural guidelines defined in `AGENTS.md`.*

This repository provides a production-ready starter template for building modern Android applications using **Kotlin**, **Jetpack Compose (Material Design 3)**, **Koin Dependency Injection**, **Ktor Network Client**, and **Modular Clean Architecture**.

---

## 🔗 Backend Microservices Integration

This Android mobile application connects directly with our **[microservices-blueprint-architecture](https://github.com/MJ-BH/microservices-blueprint-architecture)** backend ecosystem:
- **`auth-service`**: Handles login, registration, and Ktor 401 token refresh interceptors.
- **`explorer-service`**: Serves file & folder hierarchies (`FileItem`), parent-child navigation, and folder management.
- **`url-builder-service`**: Generates secure pre-signed upload/download URLs (AWS S3, Cloudflare R2, MinIO, Local NAS) when users upload documents from Jetpack Compose screens.
- **`email-service`**: Consumes background RabbitMQ messages to dispatch email alerts and file receipts.

---

## 🌐 White-Label, Multi-Brand & Product Flavor Strategy

Just like our Flutter architecture, this Android blueprint supports **Single Core Monorepo → Multi-Brand & Multi-Country Deployments**.

By isolating shared logic into `:core:domain`, `:core:data`, and `:core:ui`, you can build and maintain multiple distinct applications or client variants using Android **Gradle Product Flavors** (`app/build.gradle.kts`):

* 🏢 **Multi-Client / White-Label Deployments:** Client A (`clientA`) vs Client B (`clientB`) with unique `applicationId`, API base URLs, and feature flags.
* 🌍 **Multi-Country & Regional Variants:** Unique package namespaces (`com.example.us`, `com.example.fr`), local currency formats, and country-specific payment SDKs.
* 🎨 **Dynamic UI Themes & Branding:** Swapping Material Design 3 color palettes and logo resource bundles without modifying feature ViewModels or use cases.
* 🚩 **Feature Flag Governance:** Toggling modules on/off dynamically per client tier via `BuildConfig` fields or Remote Config.

---

## 🛠️ Product Flavors Configuration (`app/build.gradle.kts`)

```kotlin
android {
    ...
    flavorDimensions += "brand"

    productFlavors {
        create("clientA") {
            dimension = "brand"
            applicationId = "com.android.clienta"
            resValue("string", "app_name", "Alpha Brand (Client A)")
            buildConfigField("String", "BASE_URL", "\"https://api.alpha-brand.com/v1\"")
            buildConfigField("Boolean", "ENABLE_MOBILITY_MODULE", "true")
        }

        create("clientB") {
            dimension = "brand"
            applicationId = "com.android.clientb"
            resValue("string", "app_name", "Beta Brand (Client B)")
            buildConfigField("String", "BASE_URL", "\"https://api.beta-brand.fr/v1\"")
            buildConfigField("Boolean", "ENABLE_MOBILITY_MODULE", "false")
        }
    }
}
```

---

## 🚀 Build Commands for Product Flavors

```bash
# Build Debug APK for Client A Target
./gradlew assembleClientADebug

# Build Debug APK for Client B Target
./gradlew assembleClientBDebug

# Build Release APK for Client A Target
./gradlew assembleClientARelease

# Run Unit Tests across all Flavors
./gradlew test
```

---

## 🏛️ Modular Clean Architecture Structure

The project is organized into clean, decoupled Gradle modules adhering to Clean Architecture principles:

- **`:app`** — UI presentation layer (Screens, ViewModels, Navigation, Product Flavors, Android Manifest).
- **`:core:domain`** — Pure Kotlin domain layer containing core models (`FileItem`) and repository contracts. Zero Android SDK dependencies.
- **`:core:data`** — Data layer containing Ktor HTTP client implementation, repository concrete implementations, and Preference storage.
- **`:core:ui`** — Reusable Compose components (`FileItemRow`, dialogs) and the Material 3 design system (`AppTheme`).

```
android-basic-clean-architecture/
├── AGENTS.md                   # Non-negotiable Android development rules
├── README.md                   # Technical documentation & guide
└── app/src/main/java/com/android/basiccleanarchitecture/
    ├── core/
    │   └── result/             # Result<T, E> sealed interface & fold extension
    ├── data/
    │   ├── api/                # FakeExplorerApi (Ktor Network Client simulation)
    │   ├── dto/                # FileItemDto data transfer objects
    │   ├── mapper/             # FileItemMapper (DTO-to-Domain mapping)
    │   └── repository/         # ExplorerRepositoryImpl implementation
    ├── di/                     # AppModule (Koin DI module definitions)
    ├── domain/
    │   ├── model/              # FileItem & FileItemType pure Kotlin domain models
    │   └── repository/         # ExplorerRepository domain interface
    └── ui/
        ├── explorer/           # ExplorerViewModel & Compose UI screens
        └── state/              # UiState<T> sealed interface (Loading, Success, Error, Empty)
```

---

## 📦 Core Dependencies & Stack (`gradle/libs.versions.toml`)

The project relies on the following production dependencies:

- **Dependency Injection**:
  - `io.insert-koin:koin-android` (`4.0.0`)
  - `io.insert-koin:koin-androidx-compose` (`4.0.0`)
  - `io.insert-koin:koin-test` (`4.0.0`)
- **Networking**:
  - `io.ktor:ktor-client-core` (`3.0.1`)
  - `io.ktor:ktor-client-cio` (`3.0.1`)
  - `io.ktor:ktor-client-content-negotiation` (`3.0.1`)
  - `io.ktor:ktor-client-logging` (`3.0.1`)
  - `io.ktor:ktor-client-auth` (`3.0.1`)
- **JSON Serialization**:
  - `org.jetbrains.kotlinx:kotlinx-serialization-json` (`1.7.3`)
  - `io.ktor:ktor-serialization-kotlinx-json` (`3.0.1`)
- **UI Framework & Design System**:
  - `androidx.compose.material3:material3` (Compose BOM `2024.11.00`)
  - `androidx.compose.ui:ui`
  - `androidx.compose.ui:ui-tooling-preview`
  - `androidx.navigation:navigation-compose` (`2.8.4`)
  - `androidx.compose.material:material-icons-extended` (`1.7.5`)
- **Image Loading**:
  - `io.coil-kt:coil-compose` (`2.7.0`)
- **Coroutines & Async Operations**:
  - `org.jetbrains.kotlinx:kotlinx-coroutines-core` (`1.9.0`)
  - `org.jetbrains.kotlinx:kotlinx-coroutines-test` (`1.9.0`)

---

## 🤔 Architectural Rationale & Dependency Choices

- **Koin**: 
  Lightweight, pragmatic, Kotlin-native Dependency Injection framework. Unlike Dagger/Hilt, Koin does not rely on heavy code generation or annotation processing (kapt/ksp), resulting in significantly faster build times and a simpler codebase while offering out-of-the-box support for Compose ViewModels (`koinViewModel()`).

- **Ktor Client**:
  A modern, asynchronous Kotlin Multiplatform HTTP client that integrates natively with Kotlin Coroutines. It allows fine-grained control over network request logging (`LogLevel.HEADERS`) to avoid buffering binary payload bytes in RAM during large file uploads.

- **Jetpack Compose & Material 3**:
  The official modern standard for declarative UI on Android. It allows building responsive, reactive, and maintainable user interfaces with custom design themes (`AppTheme`), dynamic cards, dialogs, pull-to-refresh, and smooth transitions.

- **Coil**:
  Built specifically for Kotlin Coroutines and Compose, Coil provides lightweight, fast, and memory-efficient image loading and caching for remote media thumbnails.

- **Kotlinx Serialization**:
  The official Kotlin serialization compiler plugin. It is fast, reflection-free, and works seamlessly with Ktor for converting API responses to domain data models.

---

## ✨ User Experience & Platform Features

- **Search & Multi-Criteria Sorting**: Real-time file/folder search and multi-criteria sorting (Name A-Z/Z-A, Date Newest/Oldest).
- **Material 3 Pull-to-Refresh**: Integrated pull-to-refresh with summary counts for files and directories.
- **Soft Keyboard Handling**: Handled soft keyboard overlap using Compose `imePadding()` and vertical scrolling.
- **High-Resolution Branding**: Integrated custom high-resolution dark navy/cyan app icon across all density mipmap folders.
- **Compose Tooling Previews**: Added complete `@Preview` functions for all screens and UI components.

---

# 📖 How to Add a New Feature to the Android App

```
┌─────────────────────────────────────────────────────────────┐
│ 1. DTO & API Layer (data/dto/ & data/api/)                  │
│    FileItemDto & FakeExplorerApi returning Result<T, E>     │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│ 2. Mapper Layer (data/mapper/)                              │
│    FileItemMapper converting DTOs to Pure Domain Models      │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│ 3. Repository Layer (domain/repository/ & data/repository/) │
│    ExplorerRepository interface & ExplorerRepositoryImpl    │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│ 4. ViewModel Layer (ui/feature/)                            │
│    ExplorerViewModel managing StateFlow<UiState<T>>        │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│ 5. Jetpack Compose UI (ui/feature/)                         │
│    Composable screen reacting to UiState                    │
└──────────────────────────────┴──────────────────────────────┘
```

### Step 1: Define DTO & API Layer (`data/dto/NewFeatureDto.kt`)
```kotlin
package com.android.basiccleanarchitecture.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewFeatureDto(val id: String, val title: String)
```

### Step 2: DTO-to-Domain Mapper (`data/mapper/NewFeatureMapper.kt`)
```kotlin
package com.android.basiccleanarchitecture.data.mapper

import com.android.basiccleanarchitecture.data.dto.NewFeatureDto
import com.android.basiccleanarchitecture.domain.model.NewFeatureEntity

class NewFeatureMapper {
    fun mapToDomain(dto: NewFeatureDto): NewFeatureEntity {
        return NewFeatureEntity(id = dto.id, title = dto.title)
    }
}
```

### Step 3: Repository Interface & Implementation (`data/repository/`)
```kotlin
package com.android.basiccleanarchitecture.domain.repository
import com.android.basiccleanarchitecture.core.result.Result
import com.android.basiccleanarchitecture.domain.model.NewFeatureEntity

interface NewFeatureRepository {
    suspend fun getFeatureData(): Result<List<NewFeatureEntity>, Throwable>
}

package com.android.basiccleanarchitecture.data.repository
class NewFeatureRepositoryImpl(
    private val api: FakeExplorerApi,
    private val mapper: NewFeatureMapper
) : NewFeatureRepository {
    override suspend fun getFeatureData(): Result<List<NewFeatureEntity>, Throwable> {
        return withContext(Dispatchers.IO) {
            when (val result = api.fetchFeatureData()) {
                is Result.Success -> Result.Success(result.data.map { mapper.mapToDomain(it) })
                is Result.Failure -> Result.Failure(result.error)
            }
        }
    }
}
```

### Step 4: ViewModel Layer (`ui/feature/NewFeatureViewModel.kt`)
```kotlin
package com.android.basiccleanarchitecture.ui.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.basiccleanarchitecture.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewFeatureViewModel(
    private val repository: NewFeatureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<NewFeatureEntity>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<NewFeatureEntity>>> = _uiState

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getFeatureData().fold(
                onSuccess = { items ->
                    _uiState.value = if (items.isEmpty()) UiState.Empty else UiState.Success(items)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load data")
                }
            )
        }
    }
}
```

### Step 5: Register DI Module in Koin (`di/AppModule.kt`)
```kotlin
val appModule = module {
    single { NewFeatureMapper() }
    single<NewFeatureRepository> { NewFeatureRepositoryImpl(get(), get()) }
    viewModel { NewFeatureViewModel(get()) }
}
```

---

## ✅ Best Practices & Verification Commands

1. **Explicit Dispatchers:** Always use `Dispatchers.IO` for network operations with explicit dispatcher injection for unit testing.
2. **Product Flavors:** 100% of code inside `:core:domain`, `:core:data`, and `:core:ui` is shared across all product flavors (`clientA`, `clientB`).
3. **Sealed UI States:** Every screen state must handle `Loading`, `Success`, `Error`, and `Empty`.
4. **Verification Command:**
   ```bash
   ./gradlew test assembleClientADebug
   ```
