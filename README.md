# Android Basic Clean Architecture Blueprint

> **Oolab-Inspired Android Kotlin Clean Architecture with Jetpack Compose, Koin, Ktor & Sealed UiState**

This repository provides an Android Clean Architecture starter kit written in Kotlin. It enforces sealed UI states, dependency injection via Koin, Ktor network clients, type-safe navigation, and unit testing protocols.

---

## 🏛️ Architecture Overview

```
android-basic-clean-architecture/
├── AGENTS.md                   # Strict coding standards & architecture rules
├── app/
│   └── src/main/java/com/trvlcode/blueprint/
│       ├── domain/             # Pure Kotlin entities, use cases & repository interfaces
│       ├── data/               # Ktor remote datasources, DTOs, mappers & repository implementations
│       └── ui/                 # Jetpack Compose screens, ViewModels (Koin DI), sealed UiStates
└── app/src/test/               # Coroutines & ViewModel unit tests
```

---

## 🚀 Key Architectural Features

* **Layer Separation:** `domain` (Pure Kotlin) ↔ `data` (Ktor Remote/Room Local) ↔ `ui` (Jetpack Compose).
* **Sealed Interface State Management:**
  ```kotlin
  sealed interface UiState<out T> {
      object Loading : UiState<Nothing>
      data class Success<T>(val data: T) : UiState<T>
      data class Error(val message: String, val cause: Throwable? = null) : UiState<Nothing>
      object Empty : UiState<Nothing>
  }
  ```
* **Koin Dependency Injection:** ViewModel injection using `koinViewModel()`.
* **Zero Crash Policy:** Graceful handling of HTTP 401, 404, 500, network timeouts, and offline states.
