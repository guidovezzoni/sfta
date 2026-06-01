## 1. Project Infrastructure (Prerequisites)

- [x] 1.1 Add dependency versions and library entries to `gradle/libs.versions.toml`: `kotlinxSerializationJson` (1.8.1), `lifecycleViewmodelCompose` (use `lifecycleRuntimeKtx` version ref), `mockk` (1.14.4), `kotlinxCoroutinesTest` (1.10.2), and `kotlin-serialization` plugin (use `kotlin` version ref)
- [x] 1.2 Add `kotlin-serialization` plugin with `apply false` to project-level `build.gradle.kts`
- [x] 1.3 Apply `kotlin-serialization` plugin and add 4 dependencies (`kotlinx.serialization.json`, `lifecycle.viewmodel.compose`, `mockk`, `kotlinx.coroutines.test`) to `app/build.gradle.kts`
- [x] 1.4 Create `app/src/main/assets/bike_info_snapshot.json` by copying from `docs/mocks/bike_info_snapshot.json` and adding `"current_speed_kmh": 47.3` to the `motor` object
- [x] 1.5 Add `android:screenOrientation="sensorLandscape"` to the `<activity>` element in `app/src/main/AndroidManifest.xml`
- [x] 1.6 Verify project compiles: `./gradlew assembleDebug`

## 2. DTO Classes (Prerequisites)

- [x] 2.1 Create `BikeInfoSnapshotDto.kt` in `data/model/` — `@Serializable` root DTO with fields: `bike`, `timestamp`, `battery`, `motor`, `rideSettings`, `session`, `diagnostics`
- [x] 2.2 Create `BikeDto.kt` in `data/model/` — fields: `model`, `variant`, `firmwareVersion`, `imageUrl`
- [x] 2.3 Create `BatteryDto.kt` in `data/model/` — fields: `stateOfChargePct`, `estimatedRangeKm`, `temperatureC`, `chargingState`
- [x] 2.4 Create `MotorDto.kt` in `data/model/` — fields: `powerHp`, `temperatureC`, `currentSpeedKmh`
- [x] 2.5 Create `RideSettingsDto.kt` in `data/model/` — fields: `powerMap`, `maxPowerHp`, `engineBrakingPct`, `regenPct`
- [x] 2.6 Create `SessionDto.kt` in `data/model/` — fields: `durationS`, `distanceKm`, `maxSpeedKmh`
- [x] 2.7 Create `DiagnosticsDto.kt` in `data/model/` — fields: `faultCodes`, `warnings`
- [x] 2.8 Create `WarningDto.kt` in `data/model/` — fields: `code`, `message`, `severity`

## 3. Domain Models and Enums (Prerequisites)

- [x] 3.1 Create `ChargingState.kt` enum in `domain/model/` — values: `CHARGING`, `DISCHARGING`, `FULL`, `UNKNOWN`
- [x] 3.2 Create `PowerMap.kt` enum in `domain/model/` — values: `ENDURO`, `MX`, `ECO`, `UNKNOWN`
- [x] 3.3 Create `WarningSeverity.kt` enum in `domain/model/` — values: `INFO`, `WARNING`, `CRITICAL`, `UNKNOWN`
- [x] 3.4 Create `BatteryInfo.kt` in `domain/model/` — fields: `stateOfChargePercent`, `estimatedRangeKm`, `temperatureCelsius`, `chargingState: ChargingState`
- [x] 3.5 Create `MotorInfo.kt` in `domain/model/` — fields: `powerHp`, `temperatureCelsius`, `currentSpeedKmh`
- [x] 3.6 Create `RideSettingsInfo.kt` in `domain/model/` — fields: `powerMap: PowerMap`, `maxPowerHp`, `engineBrakingPercent`, `regenPercent`
- [x] 3.7 Create `SessionInfo.kt` in `domain/model/` — fields: `durationSeconds`, `distanceKm`, `maxSpeedKmh`
- [x] 3.8 Create `WarningInfo.kt` in `domain/model/` — fields: `code`, `message`, `severity: WarningSeverity`
- [x] 3.9 Create `BikeInfo.kt` in `domain/model/` — fields: `model`, `variant`, `firmwareVersion`, `imageUrl`, `timestamp`, `battery`, `motor`, `rideSettings`, `session`, `warnings`

## 4. Repository Interface (Prerequisites)

- [x] 4.1 Create `BikeInfoRepository.kt` interface in `domain/repository/` — `suspend fun getBikeInfoSnapshot(): Result<BikeInfoSnapshotDto>`

## 5. Repository Implementation (BDD)

- [x] 5.1 Write test: GIVEN a valid JSON asset WHEN `getBikeInfoSnapshot()` is called THEN it returns `Result.success` with parsed DTO — in `LocalBikeInfoRepositoryTest`
- [x] 5.2 Write test: GIVEN the asset file does not exist WHEN `getBikeInfoSnapshot()` is called THEN it returns `Result.failure` — in `LocalBikeInfoRepositoryTest`
- [x] 5.3 Write test: GIVEN the asset file contains invalid JSON WHEN `getBikeInfoSnapshot()` is called THEN it returns `Result.failure` — in `LocalBikeInfoRepositoryTest`
- [x] 5.4 Implement `LocalBikeInfoRepository` in `data/repository/` — reads from `AssetManager`, parses with `Json { ignoreUnknownKeys = true }`, wraps in `runCatching`, runs on `Dispatchers.IO`

## 6. Mapper (BDD)

- [x] 6.1 Write test: GIVEN a fully populated `BikeInfoSnapshotDto` WHEN `toDomain()` is called THEN all fields are correctly mapped — in `BikeInfoMapperTest`
- [x] 6.2 Write test: GIVEN a DTO with unrecognised enum string values WHEN `toDomain()` is called THEN enums fall back to `UNKNOWN` — in `BikeInfoMapperTest`
- [x] 6.3 Write test: GIVEN a DTO with an empty warnings list WHEN `toDomain()` is called THEN `BikeInfo.warnings` is empty — in `BikeInfoMapperTest`
- [x] 6.4 Implement `BikeInfoMapper.kt` in `domain/mapper/` — `BikeInfoSnapshotDto.toDomain()` extension with case-insensitive enum lookup and UNKNOWN fallback

## 7. Use Case (BDD)

- [x] 7.1 Write test: GIVEN the repository returns a successful `Result` WHEN `GetBikeInfoUseCase` is invoked THEN it returns `Result.success` with mapped `BikeInfo` — in `GetBikeInfoUseCaseTest`
- [x] 7.2 Write test: GIVEN the repository returns a failure `Result` WHEN `GetBikeInfoUseCase` is invoked THEN it returns the same `Result.failure` — in `GetBikeInfoUseCaseTest`
- [x] 7.3 Implement `GetBikeInfoUseCase.kt` in `domain/usecase/` — calls repository, maps result with `toDomain()`

## 8. Verification

- [x] 8.1 Run `./gradlew test` — all unit tests pass
- [x] 8.2 Run `./gradlew check` — code quality checks pass
- [x] 8.3 Run `./gradlew assembleDebug` — full build succeeds
- [x] 8.4 On-device verification: app launches in landscape orientation only
