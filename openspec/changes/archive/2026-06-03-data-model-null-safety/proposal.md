## Why

The JSON snapshot no longer provides `current_speed_kmh` in the motor object, but `MotorDto` still declares it as a non-nullable required field — the app crashes at runtime with `MissingFieldException`. Beyond this immediate fix, when real data collection is implemented any JSON field could be `null` (sensor offline, partial telemetry). The data and domain layers must handle missing/null values gracefully so the UI can decide how to present them.

## What Changes

- **Remove `currentSpeedKmh`** from `MotorDto`, `MotorInfo`, `BikeInfoMapper`, and all test fixtures
- **Make all DTO fields nullable** with `= null` defaults across all 8 DTO classes, so `kotlinx-serialization` treats every field as optional
- **Make all domain model fields nullable** across all 8 domain model classes, so the UI layer can render missing data appropriately
- **Update the mapper** to propagate nulls correctly: null sub-objects → null, null enum strings → null (not `UNKNOWN`), null lists → null (not empty list), null timestamp → null `Instant`
- **Update all test fixtures** to remove `currentSpeedKmh` references and add 5 new null-handling tests to `BikeInfoMapperTest`

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `bike-telemetry-data`: All DTO fields become nullable with defaults; all domain model fields become nullable; `currentSpeedKmh` is removed from motor models; mapper propagates nulls with `?.let {}` chains; null enum semantics clarified (null = no data from sensor, UNKNOWN = unrecognised non-null value)

## Impact

- **Data layer** (8 DTO files): all fields become nullable with `= null` defaults; `MotorDto.currentSpeedKmh` removed
- **Domain layer** (8 model files): all fields become nullable; `MotorInfo.currentSpeedKmh` removed
- **Mapper** (`BikeInfoMapper.kt`): rewritten with safe-call chains for null propagation
- **Tests** (3 test files): fixtures updated, 5 new tests added to `BikeInfoMapperTest`
- **No API or dependency changes** — purely internal data model refactoring
- **No breaking changes to consumers** — there are no UI consumers yet (MVI layer is story 1.2's successor)
