# Oodrive / Android Technical Architecture Rules

1. **Tech Stack**:
   - Dependency Injection: `Koin`
   - Network Client: `Ktor Client` with Auth feature
   - UI Framework: `Jetpack Compose` (Material Design 3)
   - Navigation: `Type-Safe Compose Navigation` (`kotlinx.serialization`)

2. **Clean Architecture**:
   - `domain`: Pure Kotlin entities and repository interfaces.
   - `data`: Ktor Remote Data Source, DTOs, Mappers, and Repository implementations.
   - `ui`: Jetpack Compose screens, ViewModels (injected via Koin `koinViewModel()`), and sealed state representations (`UiState`).

3. **Concurrency & Verification**:
   - Use `Dispatchers.IO` for network operations with explicit dispatcher injection for unit testing.
   - Verification command: `./gradlew test assembledDebug`.
