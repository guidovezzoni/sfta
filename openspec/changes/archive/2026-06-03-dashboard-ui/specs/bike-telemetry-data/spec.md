## MODIFIED Requirements

### Requirement: Project compiles with serialization and testing dependencies
The project SHALL include `kotlinx-serialization-json`, `lifecycle-viewmodel-compose`, `mockk`, `kotlinx-coroutines-test`, `hilt-navigation-compose`, and `lifecycle-runtime-compose` as dependencies, and the `kotlin-serialization` Gradle plugin SHALL be applied.

#### Scenario: Project compiles with new dependencies
- **WHEN** `./gradlew assembleDebug` is executed
- **THEN** the project compiles without errors

#### Scenario: hiltViewModel() is available in Compose
- **WHEN** a composable calls `hiltViewModel<DashboardViewModel>()`
- **THEN** it resolves the ViewModel via Hilt without compilation errors

#### Scenario: collectAsStateWithLifecycle() is available in Compose
- **WHEN** a composable calls `stateFlow.collectAsStateWithLifecycle()`
- **THEN** it compiles without errors and collects state in a lifecycle-aware manner
