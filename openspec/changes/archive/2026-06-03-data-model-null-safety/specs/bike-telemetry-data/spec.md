## MODIFIED Requirements

### Requirement: Bike telemetry JSON asset is bundled and valid
The app SHALL bundle `bike_info_snapshot.json` in `app/src/main/assets/`. The JSON SHALL be a copy of `docs/mocks/bike_info_snapshot.json`. The motor object SHALL NOT contain a `current_speed_kmh` field.

#### Scenario: JSON asset is present and parseable
- **WHEN** the asset file is parsed with kotlinx-serialization
- **THEN** it deserialises into `BikeInfoSnapshotDto` without errors and the motor object contains only `powerHp` and `temperatureC` fields

### Requirement: DTOs represent the JSON schema with serialization annotations
The data layer SHALL provide 8 DTO classes (`BikeInfoSnapshotDto`, `BikeDto`, `BatteryDto`, `MotorDto`, `RideSettingsDto`, `SessionDto`, `DiagnosticsDto`, `WarningDto`) each annotated with `@Serializable` and using `@SerialName` for snake_case JSON key mapping. Each class SHALL be in its own file under `data/model/`. All DTO fields SHALL be nullable with `= null` defaults so that kotlinx-serialization treats them as optional — both missing keys and explicit `null` values in JSON SHALL produce `null` in the DTO. `MotorDto` SHALL NOT contain a `currentSpeedKmh` field.

#### Scenario: DTOs match JSON structure
- **WHEN** valid bike telemetry JSON is deserialised
- **THEN** all fields are correctly populated in the DTO hierarchy

#### Scenario: Missing JSON fields produce null in DTO
- **WHEN** a JSON object is missing one or more fields
- **THEN** the corresponding DTO fields are `null` and no exception is thrown

#### Scenario: Explicit null JSON values produce null in DTO
- **WHEN** a JSON field has an explicit `null` value
- **THEN** the corresponding DTO field is `null` and no exception is thrown

### Requirement: Domain models represent clean business data
The domain layer SHALL provide `BikeInfo`, `BikeDetails`, `BatteryInfo`, `MotorInfo`, `RideSettingsInfo`, `SessionInfo`, `DiagnosticsInfo`, and `WarningInfo` data classes, plus `ChargingState`, `PowerMap`, and `WarningSeverity` enums. The domain model structure SHALL mirror DTO nesting — `BikeInfo` contains nested `bike: BikeDetails?` and `diagnostics: DiagnosticsInfo?` objects. `BikeInfo.timestamp` SHALL be of type `kotlinx.datetime.Instant?`. `DiagnosticsInfo` SHALL contain `faultCodes: List<String>?` and `warnings: List<WarningInfo>?`. All domain model fields SHALL be nullable. `MotorInfo` SHALL NOT contain a `currentSpeedKmh` field. Each enum SHALL include an `UNKNOWN` fallback value. Enum field nullability semantics: `null` means no data received from sensor (field absent or explicitly null in JSON); `UNKNOWN` means data received but value not recognised. Each class and enum SHALL be in its own file under `domain/model/`.

#### Scenario: Domain models have all required fields with nullable types
- **WHEN** a `BikeInfo` instance is constructed
- **THEN** it contains `bike` (`BikeDetails?`), `timestamp` (`Instant?`), `battery` (`BatteryInfo?`), `motor` (`MotorInfo?`), `rideSettings` (`RideSettingsInfo?`), `session` (`SessionInfo?`), and `diagnostics` (`DiagnosticsInfo?`) fields

#### Scenario: DiagnosticsInfo contains nullable faultCodes and warnings
- **WHEN** a `DiagnosticsInfo` instance is constructed
- **THEN** it contains `faultCodes: List<String>?` and `warnings: List<WarningInfo>?` fields

#### Scenario: Null enum field is distinct from UNKNOWN
- **WHEN** an enum field is `null`
- **THEN** it means no data was received from the sensor
- **WHEN** an enum field is `UNKNOWN`
- **THEN** it means a non-null value was received but not recognised

### Requirement: Mapper converts DTOs to domain models with safe enum handling
`BikeInfoMapper` SHALL reside in the data layer (`data/mapper/`) and provide a `BikeInfoSnapshotDto.toDomain()` extension function that maps every DTO field to its domain counterpart, preserving the nested structure. Null sub-objects in the DTO SHALL map to `null` in the domain model using safe-call chains (`dto.battery?.let { mapBattery(it) }`). Null primitive fields SHALL map to `null` directly. Null enum strings SHALL map to `null` in the domain (NOT `UNKNOWN`); non-null unrecognised enum strings SHALL map to `UNKNOWN`. Null timestamp string SHALL map to `null` `Instant`. Null list fields SHALL map to `null` (NOT empty list). `BikeDto` SHALL map to `BikeDetails`. The mapper SHALL parse non-null `BikeInfoSnapshotDto.timestamp` from an ISO-8601 string to `kotlinx.datetime.Instant` using `Instant.parse()`. `MotorDto` mapping SHALL NOT include `currentSpeedKmh`.

#### Scenario: Mapper converts a fully populated DTO completely
- **WHEN** `toDomain()` is called on a fully populated `BikeInfoSnapshotDto`
- **THEN** all fields in the resulting `BikeInfo` are correctly mapped, `timestamp` is an `Instant`, and `diagnostics.faultCodes` contains the DTO's fault codes

#### Scenario: Null sub-objects map to null domain fields
- **WHEN** `toDomain()` is called on a DTO where `battery`, `session`, or other sub-objects are `null`
- **THEN** the corresponding domain fields are `null` and no exception is thrown

#### Scenario: Null leaf fields map to null domain fields
- **WHEN** `toDomain()` is called on a DTO where individual leaf fields are `null`
- **THEN** the corresponding domain fields are `null` and no exception is thrown

#### Scenario: Null enum string maps to null domain enum
- **WHEN** `toDomain()` is called on a DTO where an enum string field is `null` (e.g. `chargingState = null`)
- **THEN** the domain enum field is `null` (NOT `UNKNOWN`)

#### Scenario: Non-null unrecognised enum string maps to UNKNOWN
- **WHEN** `toDomain()` is called on a DTO with unrecognised enum string values
- **THEN** the corresponding enum fields fall back to `UNKNOWN`

#### Scenario: Null timestamp maps to null Instant
- **WHEN** `toDomain()` is called on a DTO where `timestamp` is `null`
- **THEN** `BikeInfo.timestamp` is `null` and no exception is thrown

#### Scenario: Null lists map to null domain lists
- **WHEN** `toDomain()` is called on a DTO where `faultCodes` or `warnings` are `null`
- **THEN** the corresponding domain list fields are `null` (NOT empty lists)

#### Scenario: Malformed timestamp causes mapping failure
- **WHEN** `toDomain()` is called on a DTO with a non-null malformed timestamp string (e.g. `"not-a-date"`)
- **THEN** `Instant.parse()` throws an `IllegalArgumentException`

#### Scenario: Mapper handles empty warnings list
- **WHEN** `toDomain()` is called on a DTO with an empty warnings list
- **THEN** the `BikeInfo.diagnostics.warnings` list is empty

## REMOVED Requirements

### Requirement: JSON asset contains current_speed_kmh in motor object
**Reason**: The `current_speed_kmh` field has been removed from the JSON snapshot. The motor object now only contains `power_hp` and `temperature_c`.
**Migration**: Remove `currentSpeedKmh` field from `MotorDto`, `MotorInfo`, and `BikeInfoMapper`. Remove from all test fixtures.
