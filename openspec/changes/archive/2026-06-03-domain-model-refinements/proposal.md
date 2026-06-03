## Why

`BikeInfo.timestamp` is a raw `String`, preventing locale-aware formatting and domain-level time reasoning. Additionally, `DiagnosticsDto.faultCodes` is parsed from JSON but silently dropped in the mapper — the domain model only exposes `warnings`, losing diagnostic data that is already available.

## What Changes

- Add `kotlinx-datetime` dependency (`0.6.2`) for `Instant` type support
- Change `BikeInfo.timestamp` from `String` to `kotlinx.datetime.Instant`
- Parse the ISO-8601 timestamp string in the mapper via `Instant.parse()`
- Add `faultCodes: List<String>` field to `DiagnosticsInfo`
- Map `DiagnosticsDto.faultCodes` through to the domain model
- Update existing unit tests (mapper and repository) for new types and fields
- Update the `bike-telemetry-data` spec to reflect the new domain model and mapper behaviour
- Update user story 1.1 documentation to reflect the changes

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `bike-telemetry-data`: Domain model requirement changes — `BikeInfo.timestamp` becomes `Instant` instead of `String`, `DiagnosticsInfo` gains a `faultCodes` field, and the mapper requirement adds timestamp parsing and faultCodes pass-through.

## Impact

- **Gradle**: new `kotlinx-datetime` version and library entry in `libs.versions.toml`; new `implementation` line in `app/build.gradle.kts`
- **Domain layer**: `BikeInfo.kt` and `DiagnosticsInfo.kt` field changes
- **Data layer**: `BikeInfoMapper.kt` mapping logic updated
- **Tests**: `BikeInfoMapperTest.kt` and `LocalBikeInfoRepositoryTest.kt` assertions updated
- **Specs/Docs**: `openspec/specs/bike-telemetry-data/spec.md` and `docs/userstories/1.1-data-infrastructure-DONE.md` updated
- **Downstream**: Story 1.2's ViewModel will receive `Instant` for timestamp formatting
