## Why

The app currently shows a placeholder "Hello Android" screen. Riders need a dark, high-contrast dashboard that displays real-time bike telemetry (battery, power, ride settings, session stats, and warnings) at a glance while riding. The data layer (story 1.2) and DI infrastructure (story 2.3) are complete — the UI layer is the next logical step.

## What Changes

- **Theme overhaul**: Replace default Material purple/pink palette with a dark dashboard colour scheme (near-black background, Stark gold accent, semantic battery/warning/power colours). Remove dynamic colours and light theme. Extend typography for dashboard metrics.
- **MVI contract**: Introduce `DashboardUiState` (flat, all nullable telemetry fields using UI-specific enums), `DashboardUiIntent` (LoadDashboard, RetryLoad), and `DashboardUiEffect` (empty placeholder). UI-layer types: `DashboardChargingState`, `DashboardPowerMap`, `DashboardWarningSeverity` enums and `DashboardWarningInfo` data class — no domain types in the UI layer.
- **ViewModel**: `DashboardViewModel` (`@HiltViewModel`) that calls `GetBikeInfoUseCase`, flattens `BikeInfo` domain model into `DashboardUiState` (including domain-to-UI enum/model mapping), and handles loading/error/retry states.
- **Composables**: Landscape dashboard layout with `BatteryPanel`, `PowerPanel`, `SessionPanel`, `RideSettingsBar`, and `WarningBanner`. All null values display "--" placeholder. Full `@Preview` coverage.
- **Wiring**: `MainActivity` connects `hiltViewModel()` + `collectAsStateWithLifecycle()` + `LaunchedEffect`.
- **Dependencies**: Add `hilt-navigation-compose` and `lifecycle-runtime-compose`.
- **Strings**: Extract all user-facing strings to `strings.xml` (UK English, `global_` prefix for common ones).
- **Cleanup**: Delete `ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt`.

## Capabilities

### New Capabilities
- `dashboard-ui`: Dashboard screen UI — theme, MVI architecture (state/intent/effect/ViewModel), composable components (battery, power, session, ride settings, warning banner), MainActivity wiring, and all associated tests.

### Modified Capabilities
- `bike-telemetry-data`: Add `hilt-navigation-compose` and `lifecycle-runtime-compose` dependencies to the project's dependency set.

## Impact

- **Code**: New files in `ui/state/`, `ui/intent/`, `ui/effect/`, `ui/viewmodel/`, `ui/screens/`, `ui/screens/components/`. Modified: `Color.kt`, `Theme.kt`, `Type.kt`, `MainActivity.kt`, `strings.xml`. Deleted: `ExampleUnitTest.kt`, `ExampleInstrumentedTest.kt`.
- **Dependencies**: `hilt-navigation-compose` and `lifecycle-runtime-compose` added to `libs.versions.toml` and `build.gradle.kts`.
- **Tests**: New `DashboardViewModelTest` (unit) and `DashboardScreenTest` (Compose UI).
- **No breaking changes**: No existing APIs modified, no existing tests affected (beyond deleting placeholders).
