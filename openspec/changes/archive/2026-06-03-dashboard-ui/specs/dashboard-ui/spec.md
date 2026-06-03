## ADDED Requirements

### Requirement: Dashboard theme uses dark colour scheme with Stark branding
The app theme SHALL use a dark-only `darkColorScheme` with `DashboardBackground` (#0D0D0D) as background, `DashboardSurface` (#1A1A1A) as surface, `StarkGold` (#C8A84E) as primary, and `TextPrimary` (#FFFFFF) as onBackground and onSurface. The theme SHALL NOT support light mode or dynamic colours. The `darkTheme` and `dynamicColor` parameters SHALL be removed from the theme function. Semantic colours SHALL include `BatteryGreen` (#22C55E), `BatteryAmber` (#F59E0B), `BatteryRed` (#EF4444), `WarningAmber` (#F59E0B), `WarningRed` (#EF4444), `PowerBlue` (#3B82F6), and `TextSecondary` (#9CA3AF).

#### Scenario: App renders with dark background and gold accent
- **WHEN** the app is launched
- **THEN** the background is near-black (#0D0D0D) and accent elements use Stark gold (#C8A84E)

#### Scenario: No light theme or dynamic colours
- **WHEN** the theme composable is called
- **THEN** it always applies the dark colour scheme regardless of system theme or Android version

### Requirement: Dashboard typography defines metric display styles
The typography SHALL include `displayLarge` (48sp, Bold) for primary metrics, `titleLarge` (20sp, SemiBold) for panel headers, `titleMedium` (16sp, Medium) for secondary metrics, and `labelMedium` (12sp, Normal) for labels and units.

#### Scenario: Primary metrics use displayLarge style
- **WHEN** a primary metric (SOC%, HP) is rendered
- **THEN** it uses 48sp Bold typography

#### Scenario: Panel headers use titleLarge style
- **WHEN** a panel header (BATTERY, POWER, SESSION, SETTINGS) is rendered
- **THEN** it uses 20sp SemiBold typography

### Requirement: UI-layer enums and models for dashboard state
The UI layer SHALL define its own types instead of referencing domain types. Each type SHALL be in its own file under `ui/state/`:
- `DashboardChargingState`: enum with values matching domain `ChargingState` (`DISCHARGING`, `UNKNOWN`)
- `DashboardPowerMap`: enum with values matching domain `PowerMap` (`ENDURO`, `UNKNOWN`)
- `DashboardWarningSeverity`: enum with values matching domain `WarningSeverity` (`WARNING`, `UNKNOWN`)
- `DashboardWarningInfo`: data class with `code: String?`, `message: String?`, `severity: DashboardWarningSeverity?`

#### Scenario: UI types mirror domain enum values
- **WHEN** the UI enums are defined
- **THEN** they contain the same value set as their domain counterparts

#### Scenario: UI types have no imports from the domain layer
- **WHEN** the UI type files are reviewed
- **THEN** they contain no imports from `com.guidovezzoni.sfta.domain`

### Requirement: DashboardUiState is a flat data class with all nullable telemetry fields
`DashboardUiState` SHALL be a data class with `isLoading: Boolean` (default false), `errorMessage: String?` (default null), `bikeName: String?`, and nullable fields for battery (`batteryChargePercent: Int?`, `batteryRangeKm: Int?`, `batteryTemperatureCelsius: Double?`, `chargingState: DashboardChargingState?`), power (`motorPowerHp: Double?`, `motorTemperatureCelsius: Double?`), ride settings (`powerMap: DashboardPowerMap?`, `maxPowerHp: Int?`, `engineBrakingPercent: Int?`, `regenPercent: Int?`), session (`sessionDurationSeconds: Long?`, `sessionDistanceKm: Double?`, `sessionMaxSpeedKmh: Double?`), and warnings (`warnings: List<DashboardWarningInfo>?`). All referenced types SHALL be UI-layer types, not domain types.

#### Scenario: Default UiState represents initial idle state
- **WHEN** a `DashboardUiState` is constructed with default values
- **THEN** `isLoading` is false, `errorMessage` is null, and all telemetry fields are null

#### Scenario: UiState does not import domain types
- **WHEN** `DashboardUiState.kt` is reviewed
- **THEN** it contains no imports from `com.guidovezzoni.sfta.domain`

### Requirement: DashboardUiIntent defines LoadDashboard and RetryLoad intents
`DashboardUiIntent` SHALL be a sealed class with `LoadDashboard` (data object) and `RetryLoad` (data object) subclasses.

#### Scenario: LoadDashboard intent triggers initial data load
- **WHEN** the ViewModel receives a `LoadDashboard` intent
- **THEN** it initiates a data fetch from the use case

#### Scenario: RetryLoad intent re-fetches after error
- **WHEN** the ViewModel receives a `RetryLoad` intent
- **THEN** it clears the error state and re-fetches data

### Requirement: DashboardUiEffect is an empty structural placeholder
`DashboardUiEffect` SHALL be an empty sealed class. No one-shot effects are needed at this stage.

#### Scenario: No effects are emitted during normal operation
- **WHEN** the ViewModel processes any intent
- **THEN** no `DashboardUiEffect` is emitted

### Requirement: DashboardViewModel processes intents and maps domain model to flat UiState
`DashboardViewModel` SHALL be annotated with `@HiltViewModel` and inject `GetBikeInfoUseCase`. It SHALL expose `uiState: StateFlow<DashboardUiState>` and `uiEffect: SharedFlow<DashboardUiEffect>`. It SHALL provide `fun onIntent(intent: DashboardUiIntent)`. On `LoadDashboard` or `RetryLoad`, it SHALL emit `isLoading = true`, call the use case, and on success flatten `BikeInfo` fields into `DashboardUiState` with `isLoading = false`. On failure, it SHALL emit `errorMessage` with a user-friendly string. Mapping: `bikeName` from `bikeInfo.bike?.model` (appending variant if present), battery fields from `bikeInfo.battery`, motor fields from `bikeInfo.motor`, session fields from `bikeInfo.session`, ride settings from `bikeInfo.rideSettings`, warnings from `bikeInfo.diagnostics?.warnings`. The ViewModel SHALL map domain enums to UI enums (`ChargingState` → `DashboardChargingState`, `PowerMap` → `DashboardPowerMap`, `WarningSeverity` → `DashboardWarningSeverity`) and domain `WarningInfo` to `DashboardWarningInfo`.

#### Scenario: ViewModel emits loading then content on LoadDashboard
- **WHEN** the ViewModel receives `LoadDashboard` intent and the use case returns success
- **THEN** it first emits a state with `isLoading = true` and then emits a state with `isLoading = false` and all telemetry fields populated

#### Scenario: ViewModel emits error state on failure
- **WHEN** the ViewModel receives `LoadDashboard` intent and the use case returns failure
- **THEN** it emits a state with `errorMessage` set to a user-friendly message

#### Scenario: ViewModel clears error and reloads on RetryLoad
- **WHEN** the ViewModel is in an error state and receives `RetryLoad` intent
- **THEN** it clears the error, emits `isLoading = true`, and re-fetches data

#### Scenario: ViewModel populates warnings from diagnostics
- **WHEN** the use case returns a `BikeInfo` with active warnings
- **THEN** the emitted state contains the warning list from `diagnostics.warnings`

#### Scenario: ViewModel maps null domain fields to null UiState fields
- **WHEN** the use case returns a `BikeInfo` with null sub-objects (e.g. `battery = null`)
- **THEN** the corresponding UiState fields are null, `isLoading` is false, and `errorMessage` is null

### Requirement: DashboardScreen renders loading, error, and content states
`DashboardScreen` SHALL receive `uiState: DashboardUiState` and `onIntent: (DashboardUiIntent) -> Unit`. When `isLoading` is true, it SHALL show a centred `CircularProgressIndicator`. When `errorMessage` is non-null, it SHALL show the error message and a "Retry" button that emits `RetryLoad`. Otherwise, it SHALL render the dashboard content layout.

#### Scenario: Loading state shows spinner
- **WHEN** `DashboardScreen` receives a UiState with `isLoading = true`
- **THEN** a loading spinner is displayed

#### Scenario: Error state shows message and retry button
- **WHEN** `DashboardScreen` receives a UiState with an `errorMessage`
- **THEN** the error message is displayed and a retry button is visible

#### Scenario: Retry button emits RetryLoad intent
- **WHEN** the user taps the retry button in error state
- **THEN** a `RetryLoad` intent is emitted via `onIntent`

### Requirement: BatteryPanel displays SOC with colour-coded levels
`BatteryPanel` SHALL display `batteryChargePercent` as the focal metric using `displayLarge` typography. SOC colour SHALL be `BatteryGreen` when >= 50%, `BatteryAmber` when 20-49%, `BatteryRed` when < 20%, and `TextSecondary` when null. It SHALL also display range, temperature, and charging state (`DashboardChargingState?`) as secondary metrics. Null values SHALL display "--".

#### Scenario: Battery panel shows SOC, range, and temperature
- **WHEN** `BatteryPanel` receives populated values
- **THEN** it displays SOC percentage, range in km, and temperature in celsius

#### Scenario: SOC colour reflects battery level
- **WHEN** SOC is 73%
- **THEN** the SOC text colour is `BatteryGreen`
- **WHEN** SOC is 35%
- **THEN** the SOC text colour is `BatteryAmber`
- **WHEN** SOC is 15%
- **THEN** the SOC text colour is `BatteryRed`

#### Scenario: Null battery values show placeholder
- **WHEN** battery values are null
- **THEN** "--" is displayed and the panel layout remains stable

### Requirement: PowerPanel displays motor power and temperature
`PowerPanel` SHALL display `motorPowerHp` as the focal metric using `displayLarge` typography with `PowerBlue` accent colour. Motor temperature SHALL be displayed as a secondary metric. Null values SHALL display "--".

#### Scenario: Power panel shows HP and motor temperature
- **WHEN** `PowerPanel` receives populated values
- **THEN** it displays HP and motor temperature in celsius

#### Scenario: Null power values show placeholder
- **WHEN** power values are null
- **THEN** "--" is displayed and the panel layout remains stable

### Requirement: SessionPanel displays duration, distance, and max speed
`SessionPanel` SHALL display session metrics in a horizontal row: duration formatted as "Xh Xm", distance in km, and max speed in km/h. Null values SHALL display "--".

#### Scenario: Session panel shows formatted values
- **WHEN** `SessionPanel` receives `durationSeconds = 3742`, `distanceKm = 24.7`, `maxSpeedKmh = 94.1`
- **THEN** it displays "1h 2m", "24.7 km", and "94.1 km/h"

#### Scenario: Null session values show placeholder
- **WHEN** session values are null
- **THEN** "--" is displayed for each metric

### Requirement: RideSettingsBar displays all ride settings
`RideSettingsBar` SHALL display power map name, max power in HP, engine braking percentage, and regen percentage in a horizontal row. Null values SHALL display "--".

#### Scenario: Settings bar shows all values
- **WHEN** `RideSettingsBar` receives populated values
- **THEN** it displays power map, max power, braking percentage, and regen percentage

#### Scenario: Null settings values show placeholder
- **WHEN** settings values are null
- **THEN** "--" is displayed for each setting

### Requirement: WarningBanner shows when warnings are present
`WarningBanner` SHALL be a full-width banner displayed only when the warnings list (`List<DashboardWarningInfo>`) is non-null and non-empty. Background colour SHALL be based on highest `DashboardWarningSeverity`: `WarningRed` for UNKNOWN (future-proofing for critical), `WarningAmber` for WARNING. It SHALL display warning message text.

#### Scenario: Warning banner visible with warnings
- **WHEN** the UiState contains a non-empty warnings list
- **THEN** a warning banner is visible with the appropriate severity colour

#### Scenario: Warning banner hidden when no warnings
- **WHEN** the UiState has an empty or null warnings list
- **THEN** no warning banner is displayed

### Requirement: All composables have Preview coverage for every UiState field
Every composable SHALL have `@Preview` functions. `DashboardScreen` SHALL have previews for: content, loading, error, with-warnings, low-battery, and null-fields states. Every field of `DashboardUiState` SHALL appear in a non-default value in at least one preview.

#### Scenario: Preview coverage is complete
- **WHEN** all preview functions are compiled
- **THEN** every `DashboardUiState` field is exercised in at least one preview with a non-default value

### Requirement: MainActivity wires ViewModel to DashboardScreen
`MainActivity` SHALL obtain `DashboardViewModel` via `hiltViewModel()`, collect `uiState` with `collectAsStateWithLifecycle()`, dispatch `LoadDashboard` via `LaunchedEffect(Unit)`, and pass `uiState` and `onIntent` to `DashboardScreen`. The `Greeting` composable and its preview SHALL be removed.

#### Scenario: App launches and displays dashboard with telemetry
- **WHEN** the app is launched
- **THEN** the dashboard displays battery, power, session info, ride settings, and warnings from the bundled JSON

### Requirement: All user-facing strings are extracted to strings.xml
All user-facing strings SHALL be extracted to `strings.xml` in UK English. Common strings SHALL be prefixed with `global_`. Dashboard-specific strings SHALL be prefixed with `dashboard_`.

#### Scenario: No hardcoded strings in composables
- **WHEN** the dashboard composables are reviewed
- **THEN** all user-facing text comes from `stringResource()` calls

### Requirement: Example test files are deleted
`ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt` SHALL be deleted as they are replaced by real tests.

#### Scenario: Example tests no longer exist
- **WHEN** the test source sets are listed
- **THEN** `ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt` are absent
