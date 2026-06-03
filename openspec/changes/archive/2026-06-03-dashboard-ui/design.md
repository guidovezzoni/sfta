## Context

The app has a complete data pipeline (JSON asset → DTO → mapper → domain model → use case) and Hilt DI infrastructure. The UI layer currently shows a placeholder "Hello Android" composable. This change introduces the full dashboard UI using MVI architecture on Jetpack Compose with Material 3.

The app is locked to `sensorLandscape` orientation in the manifest. All domain model fields are nullable. The `GetBikeInfoUseCase` returns `Result<BikeInfo>`.

## Goals / Non-Goals

**Goals:**
- Dark-themed, high-contrast dashboard optimised for at-a-glance readability on a motorcycle mount
- MVI architecture with unidirectional data flow (Intent → ViewModel → State → Composable)
- Flat `DashboardUiState` that avoids unnecessary recompositions from nested object changes
- Full null safety — all telemetry fields nullable, UI shows "--" placeholder for missing values
- Complete test coverage: ViewModel unit tests and Compose UI tests for all states
- All strings extracted to `strings.xml` for i18n readiness

**Non-Goals:**
- Real-time data streaming or polling — single data load from bundled JSON
- Navigation — single-screen app, no navigation graph
- Animation or transitions
- Portrait layout optimisation (landscape-only)
- Custom fonts — system font stack only

## Decisions

### 1. Flat UiState over nested domain model mirroring

**Decision**: `DashboardUiState` uses flat nullable fields rather than nesting `BatteryUiState`, `MotorUiState`, etc.

**Rationale**: The composables each take individual primitive parameters (e.g. `batteryChargePercent: Int?`), not sub-state objects. A flat state makes the ViewModel mapping straightforward and avoids recompositions when unrelated nested objects are replaced. The domain model is already small enough (~15 telemetry fields) that flat structure stays readable.

**Alternative considered**: Nested sub-states. Rejected because it adds boilerplate without benefit — the composables don't consume state objects, they consume individual values.

### 2. UI-specific enums and models over domain types in UiState

**Decision**: `DashboardUiState` uses UI-layer types (`DashboardChargingState`, `DashboardPowerMap`, `DashboardWarningSeverity`, `DashboardWarningInfo`) instead of referencing domain types directly. The ViewModel maps domain enums/models to their UI counterparts.

**Rationale**: Clean layer separation — the UI layer should not depend on domain model types. This prevents domain refactoring from rippling into composables, keeps the UI contract self-contained, and makes UI tests independent of domain definitions. The mapping cost is minimal (a `when` expression per enum in the ViewModel).

**Alternative considered**: Reusing domain types directly. Rejected because it couples the UI layer to domain internals, violating Clean Architecture boundaries.

### 3. Theme function name preserved

**Decision**: Keep `StarkFutureTechnicalAssessmentTheme` as the theme function name, change only the internals.

**Rationale**: The function name is the project's identity in previews and `MainActivity`. Renaming it is a separate cosmetic concern. The story scope is to replace the colour scheme, not rebrand the theme function.

### 4. hiltViewModel() over manual ViewModel factory

**Decision**: Use `hiltViewModel()` from `hilt-navigation-compose` to obtain the ViewModel in `MainActivity`.

**Rationale**: The project already uses Hilt. `hiltViewModel()` is the standard Compose integration — it handles `ViewModelStoreOwner` scoping and Hilt injection automatically. The alternative (`viewModel()` with a manual factory) is more boilerplate with no benefit.

### 5. collectAsStateWithLifecycle() over collectAsState()

**Decision**: Use `collectAsStateWithLifecycle()` from `lifecycle-runtime-compose`.

**Rationale**: Lifecycle-aware collection stops when the UI is not visible, preventing wasted work. Standard recommendation for Android Compose apps. `collectAsState()` would keep collecting in the background.

### 6. Composable isolation for testing

**Decision**: `DashboardScreen` and component composables receive `uiState`/parameters directly — no ViewModel reference.

**Rationale**: Follows state hoisting. Compose UI tests can pass arbitrary `UiState` values and capture intents via lambda — no ViewModel mocking needed. The ViewModel is only referenced in `MainActivity`.

### 7. Battery colour thresholds as inline logic

**Decision**: SOC colour logic (green ≥ 50%, amber 20–49%, red < 20%) lives in `BatteryPanel` composable, not in the ViewModel.

**Rationale**: This is pure presentation logic — which colour to show for a given percentage. Keeping it in the composable means the ViewModel stays focused on data mapping and state transitions. The thresholds are simple enough that extracting them to constants is sufficient.

## Risks / Trade-offs

- **[Risk] Missing dependencies break build** → Mitigated by adding `hilt-navigation-compose` and `lifecycle-runtime-compose` as a prerequisite task before any code that imports them.
- **[Risk] Theme change affects existing previews** → Low risk. Only `GreetingPreview` exists and it's being deleted along with the `Greeting` composable.
- **[Trade-off] UI enum/model duplication adds mapping code** → Acceptable cost for clean layer boundaries. The mapping is a small `when` per enum in the ViewModel, and UI tests no longer depend on domain definitions.
- **[Trade-off] Flat UiState becomes unwieldy if many more fields added** → At ~17 fields it's manageable. If it grows past ~25 fields, consider grouping.
