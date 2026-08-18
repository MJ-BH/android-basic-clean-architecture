# Android Basic Clean Architecture

> **Enterprise Android Kotlin Architecture with Jetpack Compose, Koin DI & Ktor Auth Interceptors**  
> *Architectural guidelines defined in `AGENTS.md`.*

This repository provides a production-ready starter template for building modern Android applications using **Kotlin**, **Jetpack Compose (Material Design 3)**, **Koin Dependency Injection**, **Ktor Network Client**, and **Clean Architecture**.

---

## 🏛️ Architectural Foundation

Our Android engineering philosophy strictly enforces Clean Architecture and Jetpack Compose best practices:

1. **Strict Layer Separation (`domain`, `data`, `ui`):**
   * `domain`: Pure Kotlin entities, Result container interfaces, and repository abstractions. Zero Android SDK dependencies.
   * `data`: Ktor Remote Data Source, DTOs, Mappers (`FileItemMapper`), and Repository implementations (`ExplorerRepositoryImpl`).
   * `ui`: Jetpack Compose screens, ViewModels (`ExplorerViewModel`), and sealed state representations (`UiState<T>`).
2. **UI Isolation:** Compose views never make direct network or database calls. State flows exclusively via `StateFlow<UiState<T>>`.
3. **Koin Dependency Injection (`di/AppModule.kt`):** Decoupled singleton definitions and `viewModel { ... }` scoped instances.
4. **Result Container Error Handling (`Result<T, E>`):** Explicit `Result.Success` and `Result.Failure` handling for predictable error propagation without runtime crashes.

---

## 📁 Repository Layout

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

## 🚀 Key Features

* **Jetpack Compose (Material Design 3):** Fully declarative UI built with standard Compose components.
* **Koin Dependency Injection:** Lightweight, idiomatic Kotlin DI framework.
* **Sealed `UiState<T>` State Machine:** Immutable reactive state management using `StateFlow` (`Loading`, `Success`, `Error`, `Empty`).
* **Ktor Client Network Layer:** Modern Kotlin-multiplatform compatible HTTP client.
* **Fake Data Provider (`FakeExplorerApi`):** Pre-configured mock data source with latency simulation for offline testing.

---

# 📖 How to Add a New Feature to the Android App

This step-by-step guide outlines how to implement a new feature following our **Kotlin Clean Architecture** standard.

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
└─────────────────────────────────────────────────────────────┘
```

---

### Step 1: Define DTO & API Layer (`data/dto/NewFeatureDto.kt`)

```kotlin
package com.android.basiccleanarchitecture.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewFeatureDto(
    val id: String,
    val title: String
)
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
// Domain Interface
package com.android.basiccleanarchitecture.domain.repository
import com.android.basiccleanarchitecture.core.result.Result
import com.android.basiccleanarchitecture.domain.model.NewFeatureEntity

interface NewFeatureRepository {
    suspend fun getFeatureData(): Result<List<NewFeatureEntity>, Throwable>
}

// Data Implementation
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
2. **Sealed UI States:** Every screen state must handle `Loading`, `Success`, `Error`, and `Empty`.
3. **Verification Command:**
   ```bash
   ./gradlew test assembleDebug
   ```
