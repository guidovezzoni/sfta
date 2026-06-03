# Spec: Bike Telemetry Data

## Purpose

Defines the data infrastructure for loading and exposing bike telemetry data from a bundled JSON snapshot. Covers the full stack from raw JSON asset through DTOs, repository, domain models, mapper, and use case.

## Requirements

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
`LocalBikeInfoRepository` SHALL implement `BikeInfoRepository` and read `bike_info_snapshot.json` via `AssetManager`. It SHALL use `Json { ignoreUnknownKeys = true }` for forward compatibility, wrap the operation in `runCatching`, execute on `Dispatchers.IO`, and map the parsed DTO to the domain model using `BikeInfoSnapshotDto.toDomain()` before returning. The repository SHALL return `Result<BikeInfo>`.

#### Scenario: Repository returns domain model on success
- **WHEN** `getBikeInfoSnapshot()` is called with a valid asset file available
- **THEN** it returns `Result.success` containing a `BikeInfo` domain model with all fields correctly mapped

#### Scenario: Repository handles missing file gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the asset file does not exist
- **THEN** it returns `Result.failure` with an appropriate exception

#### Scenario: Repository handles malformed JSON gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the asset file contains invalid JSON
- **THEN** it returns `Result.failure` with a serialisation exception

#### Scenario: Repository handles mapping errors gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the DTO-to-domain mapping fails
- **THEN** it returns `Result.failure` with the mapping exception

### Requirement: Domain models represent clean business data
The domain layer SHALL provide `BikeInfo`, `BikeDetails`, `BatteryInfo`, `MotorInfo`, `RideSettingsInfo`, `SessionInfo`, `DiagnosticsInfo`, and `WarningInfo` data classes, plus `ChargingState`, `PowerMap`, and `WarningSeverity` enums. The domain model structure SHALL mirror DTO nesting — `BikeInfo` contains nested `bike: BikeDetails` and `diagnostics: DiagnosticsInfo` objects. `BikeInfo.timestamp` SHALL be of type `kotlinx.datetime.Instant`. `DiagnosticsInfo` SHALL contain `faultCodes: List<String>` and `warnings: List<WarningInfo>`. Each enum SHALL include an `UNKNOWN` fallback value. Each class and enum SHALL be in its own file under `domain/model/`.

#### Scenario: Domain models have all required fields
- **WHEN** a `BikeInfo` instance is constructed
- **THEN** it contains `bike` (BikeDetails), `timestamp` (Instant), `battery`, `motor`, `rideSettings`, `session`, and `diagnostics` (DiagnosticsInfo) fields with correct types

#### Scenario: DiagnosticsInfo contains faultCodes and warnings
- **WHEN** a `DiagnosticsInfo` instance is constructed
- **THEN** it contains `faultCodes: List<String>` and `warnings: List<WarningInfo>` fields

### Requirement: Mapper converts DTOs to domain models with safe enum handling
`BikeInfoMapper` SHALL reside in the data layer (`data/mapper/`) and provide a `BikeInfoSnapshotDto.toDomain()` extension function that maps every DTO field to its domain counterpart, preserving the nested structure. `BikeDto` SHALL map to `BikeDetails`. The mapper SHALL parse `BikeInfoSnapshotDto.timestamp` from an ISO-8601 string to `kotlinx.datetime.Instant` using `Instant.parse()`. `DiagnosticsDto` SHALL map to `DiagnosticsInfo` including both `faultCodes` (direct pass-through) and `warnings` (with enum conversion). Enum conversion SHALL use case-insensitive lookup with `UNKNOWN` fallback.

#### Scenario: Mapper converts a fully populated DTO completely
- **WHEN** `toDomain()` is called on a fully populated `BikeInfoSnapshotDto`
- **THEN** all fields in the resulting `BikeInfo` are correctly mapped, `timestamp` is an `Instant`, and `diagnostics.faultCodes` contains the DTO's fault codes

#### Scenario: Timestamp is parsed to Instant
- **WHEN** `toDomain()` is called on a DTO with timestamp `"2025-05-19T10:32:45Z"`
- **THEN** `BikeInfo.timestamp` is an `Instant` representing `2025-05-19T10:32:45Z`

#### Scenario: Fault codes are mapped to domain model
- **WHEN** `toDomain()` is called on a DTO with `faultCodes` containing `["F001", "F002"]`
- **THEN** `DiagnosticsInfo.faultCodes` contains `["F001", "F002"]`

#### Scenario: Empty fault codes list is preserved
- **WHEN** `toDomain()` is called on a DTO with an empty `faultCodes` list
- **THEN** `DiagnosticsInfo.faultCodes` is an empty list

#### Scenario: Malformed timestamp causes mapping failure
- **WHEN** `toDomain()` is called on a DTO with a malformed timestamp string (e.g. `"not-a-date"`)
- **THEN** `Instant.parse()` throws an `IllegalArgumentException`

#### Scenario: Mapper handles unknown enum values with UNKNOWN fallback
- **WHEN** `toDomain()` is called on a DTO with unrecognised enum string values
- **THEN** the corresponding enum fields fall back to `UNKNOWN`

#### Scenario: Mapper handles empty warnings list
- **WHEN** `toDomain()` is called on a DTO with an empty warnings list
- **THEN** the `BikeInfo.diagnostics.warnings` list is empty

### Requirement: Use case delegates to repository
`GetBikeInfoUseCase` SHALL call the repository's `getBikeInfoSnapshot()` and return the result directly. The repository already returns `Result<BikeInfo>`, so the use case SHALL NOT perform any mapping. On failure, it SHALL propagate the `Result.failure` unchanged.

#### Scenario: Use case returns domain model on success
- **WHEN** the repository returns a successful `Result<BikeInfo>`
- **THEN** `GetBikeInfoUseCase` returns the same `Result.success` unchanged

#### Scenario: Use case propagates failure from repository
- **WHEN** the repository returns a failure `Result`
- **THEN** `GetBikeInfoUseCase` returns the same `Result.failure`

### Requirement: App is locked to landscape orientation
The `<activity>` element in `AndroidManifest.xml` SHALL have `android:screenOrientation="sensorLandscape"` to lock the app to landscape orientation (allowing both left and right landscape).

#### Scenario: App displays in landscape only
- **WHEN** the app is launched on a device
- **THEN** it displays only in landscape orientation
