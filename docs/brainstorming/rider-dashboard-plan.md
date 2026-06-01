# Stark Future Rider Dashboard — Implementation Plan

## Context

Build a landscape-only motorcycle rider dashboard that reads a static JSON snapshot from assets and displays real-time telemetry (speed, battery, power, ride settings, warnings). This is a technical assessment for Stark Future — the code will be read as a PR. Requirements: MVI + Clean Architecture, manual DI, Kotlinx Serialization, dark-only theme, loading/error/empty state handling, unit tests + Compose UI tests.

## Design Decisions (Agreed)

- **Layout A (Central Speed Focus)**: speed dominates centre, battery left, power right, settings bar bottom, warning banner top
- **Dark-only theme**: high-contrast for riding, Stark gold accent (`#C8A84E`)
- **Manual DI**: no Hilt — MainActivity wires the graph
- **Kotlinx Serialization**: compile-time safe, Kotlin-native
- **Real-time only**: no session stats (duration, distance)
- **`current_speed_kmh`**: added to `motor` object in mock JSON

---

## Phase 1: Project Infrastructure

### Gradle (`gradle/libs.versions.toml`)
Add versions and libraries:
- `kotlinx-serialization-json` (1.8.1)
- `androidx-lifecycle-viewmodel-compose` (2.10.0)
- `mockk` (1.14.4) — testImplementation
- `kotlinx-coroutines-test` (1.10.2) — testImplementation
- Plugin: `kotlin-serialization` using existing `kotlin` version ref

### Build files
- `build.gradle.kts` (project): add `kotlin-serialization` plugin `apply false`
- `app/build.gradle.kts`: apply plugin + add 4 dependencies

### Asset
- Create `app/src/main/assets/bike_info_snapshot.json` — copy from `docs/mocks/` with `current_speed_kmh: 47.3` added to `motor` object

### Manifest
- Add `android:screenOrientation="sensorLandscape"` to the activity

---

## Phase 2: Data Layer

**`data/model/BikeInfoSnapshotDto.kt`** — all DTOs in one file, `@Serializable`, `@SerialName` for snake_case fields:
- `BikeInfoSnapshotDto` (root) → `BikeDto`, `BatteryDto`, `MotorDto` (with new `currentSpeedKmh`), `RideSettingsDto`, `SessionDto`, `DiagnosticsDto`, `WarningDto`

**`domain/repository/BikeInfoRepository.kt`** — interface:
```kotlin
interface BikeInfoRepository {
    suspend fun getBikeInfoSnapshot(): Result<BikeInfoSnapshotDto>
}
```

**`data/repository/LocalBikeInfoRepository.kt`** — reads from `AssetManager`, parses with `Json { ignoreUnknownKeys = true }`, wraps in `runCatching`, uses `Dispatchers.IO`

---

## Phase 3: Domain Layer

**`domain/model/BikeInfo.kt`** — clean data classes + enums:
- `BikeInfo`, `BatteryInfo`, `MotorInfo`, `RideSettingsInfo`, `WarningInfo`
- Enums: `ChargingState`, `PowerMap`, `WarningSeverity` — each with `UNKNOWN` fallback

**`domain/mapper/BikeInfoMapper.kt`** — `BikeInfoSnapshotDto.toDomain()` extension

**`domain/usecase/GetBikeInfoUseCase.kt`** — calls repository, maps result

---

## Phase 4: Theme

**`ui/theme/Color.kt`** — replace with dashboard palette:
- `DashboardBackground` (#0D0D0D), `DashboardSurface` (#1A1A1A), `StarkGold` (#C8A84E)
- Semantic: `SpeedWhite`, `BatteryGreen`/`Amber`/`Red`, `WarningAmber`/`Red`, `PowerBlue`

**`ui/theme/Theme.kt`** — dark-only `darkColorScheme`, no dynamic colours, remove `darkTheme`/`dynamicColor` params

**`ui/theme/Type.kt`** — add `displayLarge` (96sp bold for speed), `titleLarge` (20sp for panel headers), `labelMedium` (12sp for labels)

---

## Phase 5: MVI Contract + ViewModel

**`ui/state/DashboardUiState.kt`** — flat data class with all fields + defaults:
- `isLoading`, `errorMessage`, `currentSpeedKmh`, battery fields, motor fields, ride settings fields, `warnings`, `bikeName`

**`ui/intent/DashboardUiIntent.kt`** — `LoadDashboard`, `RetryLoad`

**`ui/effect/DashboardUiEffect.kt`** — empty sealed class (structural placeholder)

**`ui/viewmodel/DashboardViewModel.kt`** — processes intents, calls use case, flattens domain model to UiState

**`ui/viewmodel/DashboardViewModelFactory.kt`** — `ViewModelProvider.Factory` for manual DI

---

## Phase 6: Composables

**`ui/screens/DashboardScreen.kt`** — main screen composable:
- `when` block: `isLoading` → spinner, `errorMessage != null` → error + retry, else → dashboard content
- Dashboard content: `Column` with warning banner (conditional), `Row` (battery | speed | power), settings bar

**`ui/screens/components/`** — sub-composables:
- `BatteryPanel.kt` — SOC%, range, temp, charging state; colour changes by level
- `SpeedDisplay.kt` — speed in `displayLarge` white, km/h label, bike name
- `PowerPanel.kt` — HP, motor temp
- `RideSettingsBar.kt` — horizontal row: power map, max power, regen, engine braking
- `WarningBanner.kt` — full-width, amber/red by severity

Each gets `@Preview` functions. `DashboardScreen` gets previews for: content, loading, error, with-warning, low-battery — ensuring every UiState field appears non-default in at least one preview.

---

## Phase 7: Wiring

**`MainActivity.kt`** — create repository → use case → factory → viewModel, collect state with `collectAsStateWithLifecycle`, dispatch `LoadDashboard` via `LaunchedEffect`

**`strings.xml`** — all user-facing strings extracted, UK English, `global_` prefix for common ones

Delete `ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt`.

---

## Phase 8: Testing

### Unit Tests (`app/src/test/`)
- **`LocalBikeInfoRepositoryTest`** — valid JSON → success, missing file → failure, malformed JSON → failure (MockK `AssetManager`)
- **`BikeInfoMapperTest`** — complete mapping, unknown enum fallbacks, empty warnings
- **`GetBikeInfoUseCaseTest`** — success → mapped model, failure → propagated
- **`DashboardViewModelTest`** — LoadDashboard → loading then content, failure → error state, RetryLoad → reloads, warnings populated (MockK use case, `runTest`, `UnconfinedTestDispatcher`)

### Compose UI Tests (`app/src/androidTest/`)
- **`DashboardScreenTest`** — loading shows spinner, error shows message + retry button, retry click emits intent, content shows speed/battery/power/settings, warnings visible when present, hidden when empty

---

## Implementation Sequence

1. Gradle + asset + manifest (compilable checkpoint)
2. Data layer — DTOs + repository
3. Domain layer — models + mapper + use case
4. Theme — colours, dark scheme, typography
5. MVI contract — UiState, UiIntent, UiEffect
6. ViewModel + factory
7. Composables — screen + components + previews
8. MainActivity wiring + strings
9. Unit tests
10. Compose UI tests
11. Final verification + README

---

## File Manifest

### Files to CREATE (23)
| # | Path | Purpose |
|---|---|---|
| 1 | `app/src/main/assets/bike_info_snapshot.json` | Mock JSON with `current_speed_kmh` |
| 2 | `app/src/main/java/.../data/model/BikeInfoSnapshotDto.kt` | Serializable DTOs |
| 3 | `app/src/main/java/.../data/repository/LocalBikeInfoRepository.kt` | JSON asset reader |
| 4 | `app/src/main/java/.../domain/repository/BikeInfoRepository.kt` | Repository interface |
| 5 | `app/src/main/java/.../domain/model/BikeInfo.kt` | Domain models + enums |
| 6 | `app/src/main/java/.../domain/mapper/BikeInfoMapper.kt` | DTO → domain mapping |
| 7 | `app/src/main/java/.../domain/usecase/GetBikeInfoUseCase.kt` | Use case |
| 8 | `app/src/main/java/.../ui/state/DashboardUiState.kt` | MVI state |
| 9 | `app/src/main/java/.../ui/intent/DashboardUiIntent.kt` | MVI intents |
| 10 | `app/src/main/java/.../ui/effect/DashboardUiEffect.kt` | MVI effects |
| 11 | `app/src/main/java/.../ui/viewmodel/DashboardViewModel.kt` | ViewModel |
| 12 | `app/src/main/java/.../ui/viewmodel/DashboardViewModelFactory.kt` | Manual DI factory |
| 13 | `app/src/main/java/.../ui/screens/DashboardScreen.kt` | Main screen composable |
| 14 | `app/src/main/java/.../ui/screens/components/BatteryPanel.kt` | Battery sub-composable |
| 15 | `app/src/main/java/.../ui/screens/components/SpeedDisplay.kt` | Speed sub-composable |
| 16 | `app/src/main/java/.../ui/screens/components/PowerPanel.kt` | Power sub-composable |
| 17 | `app/src/main/java/.../ui/screens/components/RideSettingsBar.kt` | Settings sub-composable |
| 18 | `app/src/main/java/.../ui/screens/components/WarningBanner.kt` | Warning sub-composable |
| 19 | `app/src/test/.../data/repository/LocalBikeInfoRepositoryTest.kt` | Repository unit test |
| 20 | `app/src/test/.../domain/mapper/BikeInfoMapperTest.kt` | Mapper unit test |
| 21 | `app/src/test/.../domain/usecase/GetBikeInfoUseCaseTest.kt` | Use case unit test |
| 22 | `app/src/test/.../ui/viewmodel/DashboardViewModelTest.kt` | ViewModel unit test |
| 23 | `app/src/androidTest/.../ui/screens/DashboardScreenTest.kt` | Compose UI test |

### Files to MODIFY (9)
| # | Path | Change |
|---|---|---|
| 1 | `gradle/libs.versions.toml` | Add serialization, viewmodel-compose, mockk, coroutines-test |
| 2 | `build.gradle.kts` (project) | Add serialization plugin `apply false` |
| 3 | `app/build.gradle.kts` | Add serialization plugin + dependencies |
| 4 | `app/src/main/AndroidManifest.xml` | Add `screenOrientation="sensorLandscape"` |
| 5 | `app/src/main/java/.../MainActivity.kt` | Wire DI + DashboardScreen |
| 6 | `app/src/main/java/.../ui/theme/Color.kt` | Replace with dashboard palette |
| 7 | `app/src/main/java/.../ui/theme/Theme.kt` | Dark-only, no dynamic colours |
| 8 | `app/src/main/java/.../ui/theme/Type.kt` | Add display/title styles |
| 9 | `app/src/main/res/values/strings.xml` | Add all dashboard strings |

### Files to DELETE (2)
| # | Path |
|---|---|
| 1 | `app/src/test/.../ExampleUnitTest.kt` |
| 2 | `app/src/androidTest/.../ExampleInstrumentedTest.kt` |

---

## Verification

1. `./gradlew clean` → `./gradlew assembleDebug` — compiles
2. `./gradlew check` — lint + compile checks pass
3. `./gradlew test` — all unit tests pass
4. `adb devices` → `./gradlew connectedDebugAndroidTest` — UI tests pass (if device available)
5. `./gradlew installDebug` + `adb shell am start -n com.guidovezzoni.sfta/.ui.MainActivity` — dashboard displays in landscape with all stats visible
6. Visual check: loading spinner appears briefly, then dashboard content renders with correct data from mock JSON
