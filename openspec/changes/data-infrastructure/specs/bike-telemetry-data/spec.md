## ADDED Requirements

### Requirement: Project compiles with serialization and testing dependencies
The project SHALL include `kotlinx-serialization-json`, `lifecycle-viewmodel-compose`, `mockk`, and `kotlinx-coroutines-test` as dependencies, and the `kotlin-serialization` Gradle plugin SHALL be applied.

#### Scenario: Project compiles with new dependencies
- **WHEN** `./gradlew assembleDebug` is executed
- **THEN** the project compiles without errors

### Requirement: Bike telemetry JSON asset is bundled and valid
The app SHALL bundle `bike_info_snapshot.json` in `app/src/main/assets/`. The JSON SHALL be a copy of `docs/mocks/bike_info_snapshot.json` with `"current_speed_kmh": 47.3` added to the `motor` object.

#### Scenario: JSON asset is present and parseable
- **WHEN** the asset file is parsed with kotlinx-serialization
- **THEN** it deserialises into `BikeInfoSnapshotDto` without errors and the motor object contains `currentSpeedKmh = 47.3`

### Requirement: DTOs represent the JSON schema with serialization annotations
The data layer SHALL provide 8 DTO classes (`BikeInfoSnapshotDto`, `BikeDto`, `BatteryDto`, `MotorDto`, `RideSettingsDto`, `SessionDto`, `DiagnosticsDto`, `WarningDto`) each annotated with `@Serializable` and using `@SerialName` for snake_case JSON key mapping. Each class SHALL be in its own file under `data/model/`.

#### Scenario: DTOs match JSON structure
- **WHEN** valid bike telemetry JSON is deserialised
- **THEN** all fields are correctly populated in the DTO hierarchy

### Requirement: Repository reads and parses JSON from assets
`LocalBikeInfoRepository` SHALL implement `BikeInfoRepository` and read `bike_info_snapshot.json` via `AssetManager`. It SHALL use `Json { ignoreUnknownKeys = true }` for forward compatibility, wrap the operation in `runCatching`, and execute on `Dispatchers.IO`.

#### Scenario: Repository reads valid JSON successfully
- **WHEN** `getBikeInfoSnapshot()` is called with a valid asset file available
- **THEN** it returns `Result.success` containing the parsed `BikeInfoSnapshotDto`

#### Scenario: Repository handles missing file gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the asset file does not exist
- **THEN** it returns `Result.failure` with an appropriate exception

#### Scenario: Repository handles malformed JSON gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the asset file contains invalid JSON
- **THEN** it returns `Result.failure` with a serialisation exception

### Requirement: Domain models represent clean business data
The domain layer SHALL provide `BikeInfo`, `BatteryInfo`, `MotorInfo`, `RideSettingsInfo`, `SessionInfo`, and `WarningInfo` data classes, plus `ChargingState`, `PowerMap`, and `WarningSeverity` enums. Each enum SHALL include an `UNKNOWN` fallback value. Each class and enum SHALL be in its own file under `domain/model/`.

#### Scenario: Domain models have all required fields
- **WHEN** a `BikeInfo` instance is constructed
- **THEN** it contains `model`, `variant`, `firmwareVersion`, `imageUrl`, `timestamp`, `battery`, `motor`, `rideSettings`, `session`, and `warnings` fields with correct types

### Requirement: Mapper converts DTOs to domain models with safe enum handling
`BikeInfoMapper` SHALL provide a `BikeInfoSnapshotDto.toDomain()` extension function that maps every DTO field to its domain counterpart. Enum conversion SHALL use case-insensitive lookup with `UNKNOWN` fallback. The `diagnostics` object SHALL be flattened to `warnings` only (excluding `faultCodes`).

#### Scenario: Mapper converts a fully populated DTO completely
- **WHEN** `toDomain()` is called on a fully populated `BikeInfoSnapshotDto`
- **THEN** all fields in the resulting `BikeInfo` are correctly mapped

#### Scenario: Mapper handles unknown enum values with UNKNOWN fallback
- **WHEN** `toDomain()` is called on a DTO with unrecognised enum string values
- **THEN** the corresponding enum fields fall back to `UNKNOWN`

#### Scenario: Mapper handles empty warnings list
- **WHEN** `toDomain()` is called on a DTO with an empty warnings list
- **THEN** the `BikeInfo.warnings` list is empty

### Requirement: Use case chains repository and mapper
`GetBikeInfoUseCase` SHALL call the repository's `getBikeInfoSnapshot()` and map the successful result using `toDomain()`. On failure, it SHALL propagate the `Result.failure` unchanged.

#### Scenario: Use case returns mapped domain model on success
- **WHEN** the repository returns a successful `Result`
- **THEN** `GetBikeInfoUseCase` returns `Result.success` containing the mapped `BikeInfo`

#### Scenario: Use case propagates failure from repository
- **WHEN** the repository returns a failure `Result`
- **THEN** `GetBikeInfoUseCase` returns the same `Result.failure`

### Requirement: App is locked to landscape orientation
The `<activity>` element in `AndroidManifest.xml` SHALL have `android:screenOrientation="sensorLandscape"` to lock the app to landscape orientation (allowing both left and right landscape).

#### Scenario: App displays in landscape only
- **WHEN** the app is launched on a device
- **THEN** it displays only in landscape orientation
