## 1. Prerequisites

- [ ] 1.1 Add `hilt-navigation-compose` and `lifecycle-runtime-compose` to `libs.versions.toml` and `build.gradle.kts`
- [ ] 1.2 Delete `ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt`
- [ ] 1.3 Add all dashboard strings to `strings.xml` (UK English, `global_` prefix for common strings)

## 2. Theme

- [ ] 2.1 Replace default colours in `Color.kt` with dashboard palette (DashboardBackground, DashboardSurface, StarkGold, BatteryGreen/Amber/Red, WarningAmber/Red, PowerBlue, TextPrimary, TextSecondary)
- [ ] 2.2 Extend `Type.kt` typography with `displayLarge` (48sp Bold), `titleLarge` (20sp SemiBold), `titleMedium` (16sp Medium), `labelMedium` (12sp Normal)
- [ ] 2.3 Update `Theme.kt` to dark-only `darkColorScheme`, remove `darkTheme`/`dynamicColor` params, remove `LightColorScheme` and dynamic colour logic

## 3. MVI Contract

- [ ] 3.1 Create `DashboardChargingState.kt` in `ui/state/` — UI enum mirroring domain `ChargingState` values (`DISCHARGING`, `UNKNOWN`)
- [ ] 3.2 Create `DashboardPowerMap.kt` in `ui/state/` — UI enum mirroring domain `PowerMap` values (`ENDURO`, `UNKNOWN`)
- [ ] 3.3 Create `DashboardWarningSeverity.kt` in `ui/state/` — UI enum mirroring domain `WarningSeverity` values (`WARNING`, `UNKNOWN`)
- [ ] 3.4 Create `DashboardWarningInfo.kt` in `ui/state/` — UI data class with `code: String?`, `message: String?`, `severity: DashboardWarningSeverity?`
- [ ] 3.5 Create `DashboardUiState.kt` in `ui/state/` — flat data class using UI-layer types (`DashboardChargingState`, `DashboardPowerMap`, `DashboardWarningInfo`), no domain imports
- [ ] 3.6 Create `DashboardUiIntent.kt` in `ui/intent/` — sealed class with `LoadDashboard` and `RetryLoad`
- [ ] 3.7 Create `DashboardUiEffect.kt` in `ui/effect/` — empty sealed class placeholder

## 4. DashboardViewModel (BDD)

- [ ] 4.1 Write test: `GIVEN use case returns success WHEN LoadDashboard intent received THEN emits loading then populated state` in `DashboardViewModelTest`
- [ ] 4.2 Write test: `GIVEN use case returns failure WHEN LoadDashboard intent received THEN emits error state` in `DashboardViewModelTest`
- [ ] 4.3 Write test: `GIVEN ViewModel is in error state WHEN RetryLoad intent received THEN clears error and reloads` in `DashboardViewModelTest`
- [ ] 4.4 Write test: `GIVEN use case returns BikeInfo with warnings WHEN LoadDashboard intent received THEN state contains warnings` in `DashboardViewModelTest`
- [ ] 4.5 Write test: `GIVEN use case returns BikeInfo with null battery WHEN LoadDashboard intent received THEN battery fields are null in state` in `DashboardViewModelTest`
- [ ] 4.6 Implement `DashboardViewModel` — `@HiltViewModel`, inject `GetBikeInfoUseCase`, process intents, flatten `BikeInfo` to `DashboardUiState`, map domain enums/models to UI types (`ChargingState` → `DashboardChargingState`, `PowerMap` → `DashboardPowerMap`, `WarningInfo` → `DashboardWarningInfo`)

## 5. Dashboard Composables (BDD)

- [ ] 5.1 Write test: `GIVEN loading state WHEN screen renders THEN spinner is displayed` in `DashboardScreenTest`
- [ ] 5.2 Write test: `GIVEN error state WHEN screen renders THEN error message and retry button visible` in `DashboardScreenTest`
- [ ] 5.3 Write test: `GIVEN error state WHEN retry tapped THEN RetryLoad intent emitted` in `DashboardScreenTest`
- [ ] 5.4 Write test: `GIVEN populated state WHEN screen renders THEN battery and power panels show values` in `DashboardScreenTest`
- [ ] 5.5 Write test: `GIVEN populated state WHEN screen renders THEN session info shows formatted values` in `DashboardScreenTest`
- [ ] 5.6 Write test: `GIVEN populated state WHEN screen renders THEN settings bar shows all values` in `DashboardScreenTest`
- [ ] 5.7 Write test: `GIVEN state with null fields WHEN screen renders THEN placeholders shown` in `DashboardScreenTest`
- [ ] 5.8 Write test: `GIVEN state with warnings WHEN screen renders THEN warning banner visible` in `DashboardScreenTest`
- [ ] 5.9 Write test: `GIVEN state with no warnings WHEN screen renders THEN no warning banner` in `DashboardScreenTest`
- [ ] 5.10 Implement `BatteryPanel` composable — SOC% focal metric with colour logic, range, temperature, charging state, null placeholder "--"
- [ ] 5.11 Implement `PowerPanel` composable — HP focal metric with PowerBlue accent, motor temperature, null placeholder "--"
- [ ] 5.12 Implement `SessionPanel` composable — horizontal row with duration (Xh Xm), distance (km), max speed (km/h), null placeholder "--"
- [ ] 5.13 Implement `RideSettingsBar` composable — horizontal row with power map, max power, braking, regen, null placeholder "--"
- [ ] 5.14 Implement `WarningBanner` composable — full-width banner, severity-based colour, conditional visibility
- [ ] 5.15 Implement `DashboardScreen` composable — loading/error/content states, integrates all panels
- [ ] 5.16 Add `@Preview` functions for all composables — content, loading, error, with-warnings, low-battery, null-fields; every UiState field non-default in at least one preview

## 6. Wiring

- [ ] 6.1 Update `MainActivity` — remove `Greeting`, wire `hiltViewModel()` + `collectAsStateWithLifecycle()` + `LaunchedEffect(Unit)` for `LoadDashboard`, pass state and `onIntent` to `DashboardScreen`

## 7. Verification

- [ ] 7.1 Run `./gradlew clean build` — project compiles without errors
- [ ] 7.2 Run `./gradlew test` — all unit tests pass
- [ ] 7.3 Run `./gradlew connectedDebugAndroidTest` — all Compose UI tests pass (requires device)
- [ ] 7.4 On-device verification — app launches and displays dashboard with telemetry from bundled JSON
