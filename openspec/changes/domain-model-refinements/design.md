## Context

The domain model currently represents `BikeInfo.timestamp` as a raw `String` and silently drops `DiagnosticsDto.faultCodes` during mapping. Story 2.1 (Clean Architecture fix) has been completed, so the mapper lives in `data/mapper/` and the repository returns `Result<BikeInfo>`.

The JSON asset contains an ISO-8601 timestamp (`"2025-05-19T10:32:45Z"`) and a `fault_codes` array that is already parsed into `DiagnosticsDto.faultCodes` but not mapped to the domain layer.

## Goals / Non-Goals

**Goals:**
- Parse the ISO-8601 timestamp string into a type-safe `kotlinx.datetime.Instant` in the domain model
- Expose `faultCodes` in the domain model so downstream consumers have access to all diagnostic data
- Keep all existing tests green with updated assertions
- Update specs and documentation to reflect the new domain model

**Non-Goals:**
- Formatting the timestamp for display (that belongs to story 1.2's ViewModel)
- Displaying fault codes in the UI (future story)
- Changing the JSON asset format or DTO layer
- Adding new test files (only updating existing ones)

## Decisions

### D1: Use `kotlinx-datetime` for `Instant`

**Decision**: Use `kotlinx.datetime.Instant` rather than `java.time.Instant`.

**Rationale**: `kotlinx-datetime` is the Kotlin-native datetime library, consistent with the project's existing use of `kotlinx-serialization`. It is multiplatform-compatible and lightweight (~100 KB). The project targets `minSdk = 24`, so `java.time` would also work, but `kotlinx-datetime` is the idiomatic Kotlin choice and integrates better with coroutines and serialization.

**Alternative considered**: `java.time.Instant` — available on API 26+ (project minSdk is 24, so would need desugaring). More verbose and less idiomatic in a Kotlin-first project.

### D2: Let `Instant.parse()` throw on invalid input

**Decision**: Use `Instant.parse(timestamp)` directly without try-catch in the mapper. Invalid timestamps will throw `IllegalArgumentException`, caught by the repository's existing `runCatching`.

**Rationale**: The JSON comes from a bundled asset, so malformed timestamps indicate a development error, not a runtime condition. The existing error propagation path (`runCatching` → `Result.failure`) handles this correctly.

### D3: Place `faultCodes` before `warnings` in `DiagnosticsInfo`

**Decision**: Add the `faultCodes` field before `warnings` in the `DiagnosticsInfo` data class constructor to match the JSON field ordering and the DTO field ordering.

**Rationale**: Consistency with `DiagnosticsDto` field order reduces cognitive overhead when reading mapper code.

## Risks / Trade-offs

- **[Risk] `kotlinx-datetime` version compatibility** → Mitigated: version `0.6.2` is stable and compatible with Kotlin `2.2.x` used in this project.
- **[Risk] Breaking downstream consumers of `BikeInfo.timestamp`** → Mitigated: no UI consumers exist yet (story 1.2 is not started). The only consumers are tests, which will be updated in this story.
- **[Trade-off] Adding a new dependency for a single `Instant.parse()` call** → Accepted: `kotlinx-datetime` will also be needed by story 1.2 for timestamp formatting, so the dependency is not premature.
