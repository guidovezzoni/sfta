## MODIFIED Requirements

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

#### Scenario: Mapper handles unknown enum values with UNKNOWN fallback
- **WHEN** `toDomain()` is called on a DTO with unrecognised enum string values
- **THEN** the corresponding enum fields fall back to `UNKNOWN`

#### Scenario: Mapper handles empty warnings list
- **WHEN** `toDomain()` is called on a DTO with an empty warnings list
- **THEN** the `BikeInfo.diagnostics.warnings` list is empty
