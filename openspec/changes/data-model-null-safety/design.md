## Context

The Stark VARG rider dashboard loads bike telemetry from a bundled JSON snapshot. The current data layer declares all fields as non-nullable, which means any missing or null field in the JSON causes a `MissingFieldException` crash. The `current_speed_kmh` field has already been removed from the JSON snapshot but not from the code, creating a runtime crash. When real telemetry is introduced, any sensor could be offline, producing null values.

The data flow is: JSON → DTO (kotlinx-serialization) → Mapper → Domain model → (future) UI. Currently all three layers (DTO, mapper, domain) assume complete data.

## Goals / Non-Goals

**Goals:**

- Eliminate the `currentSpeedKmh` crash by removing the field from DTO, domain, and mapper
- Make all DTO fields nullable with `= null` defaults so kotlinx-serialization treats them as optional
- Make all domain model fields nullable so the UI layer can decide how to render missing data
- Establish clear null vs UNKNOWN enum semantics: `null` = no data from sensor; `UNKNOWN` = non-null unrecognised value
- Maintain full backward compatibility — fully populated JSON still parses correctly

**Non-Goals:**

- UI rendering of null values (deferred to the MVI/UI story)
- Default/fallback values in the domain layer — nulls are surfaced as-is
- Changes to the JSON snapshot file itself
- Changes to enums (they already have `UNKNOWN` fallback)

## Decisions

### 1. Nullable fields with `= null` defaults in DTOs (not wrapper types or sealed classes)

kotlinx-serialization treats `val field: Type? = null` as optional — both missing keys and explicit `null` values in JSON produce `null` in the DTO. This is the idiomatic approach and requires no custom serializers or wrapper types.

**Alternative considered**: Custom `Optional<T>` sealed class to distinguish "missing" from "explicitly null". Rejected because the user story treats both cases identically (no data = null) and the added complexity is not justified.

### 2. Domain fields mirror DTO nullability (no defaults or fallbacks in the domain layer)

Domain models expose nullable fields directly. The UI layer is responsible for deciding how to render missing data (placeholders, hidden panels, etc.).

**Alternative considered**: Non-nullable domain fields with default/fallback values (e.g. `stateOfChargePercent: Int = 0`). Rejected because fake defaults are misleading — a battery at 0% charge is a critical state, not a "no data" signal.

### 3. Null enum string → null domain enum (not UNKNOWN)

The mapper distinguishes between `null` input (no data from sensor) and a non-null unrecognised string (data received but value not mapped). `null` → `null`; `"supercharging"` → `UNKNOWN`.

**Alternative considered**: Mapping null to `UNKNOWN`. Rejected because it conflates two semantically different cases, making it impossible for the UI to distinguish "no sensor data" from "unknown sensor value".

### 4. Null sub-objects → null domain objects (not empty/default instances)

When `battery`, `session`, etc. are null in the DTO, the corresponding domain field is `null`. The mapper uses `dto.battery?.let { mapBattery(it) }`.

**Alternative considered**: Constructing empty domain objects with all-null fields. Rejected because a null `BatteryInfo` clearly signals "no battery data" while an all-null `BatteryInfo` instance is ambiguous.

### 5. Null lists → null (not empty lists)

`faultCodes: List<String>? = null` and `warnings: List<WarningDto>? = null`. Null means "diagnostics data not available", empty list means "data available, no faults/warnings".

## Risks / Trade-offs

- **[Risk] Widespread nullability increases null-check burden on future UI code** → Mitigated by making this an explicit design decision: the UI layer is the right place to handle presentation of missing data, and nullable types force the UI to handle every case at compile time.
- **[Risk] Existing malformed timestamp test must still work** → The test constructs a non-null malformed string (`"not-a-date"`). With nullable timestamps, `null` maps to `null` Instant (no exception), but a non-null malformed string still throws `IllegalArgumentException` via `Instant.parse()`. No risk — the test remains valid.
- **[Trade-off] All fields nullable vs selective nullability** → Making everything nullable is more changes now but avoids repeated "make field X nullable" stories as new telemetry fields are added or removed.
