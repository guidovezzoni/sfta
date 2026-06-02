## Context

The project is a fresh Android Studio template (AGP 9.2.1, Kotlin 2.2.10, Compose BOM 2026.02.01) with no custom code beyond the default `MainActivity` and theme files. The package is `com.guidovezzoni.sfta`. This change builds the entire data pipeline from JSON asset to domain model, establishing patterns that subsequent stories will follow.

A static JSON file (`bike_info_snapshot.json`) represents a single telemetry snapshot from a Stark VARG electric motorbike. The file is bundled as an Android asset and read at runtime.

## Goals / Non-Goals

**Goals:**

- Establish data and domain layer package structure following Clean Architecture
- Parse the bundled JSON asset into typed DTOs using kotlinx-serialization
- Map DTOs to domain models with safe enum handling (UNKNOWN fallback)
- Provide a use case that UI layers can call to get a `Result<BikeInfo>`
- Lock the app to landscape orientation for the dashboard layout
- Achieve 80%+ test coverage on repository, mapper, and use case

**Non-Goals:**

- No UI changes (story 1.2)
- No dependency injection framework (manual constructor injection for now)
- No caching layer (asset is ~1 KB, single read is sufficient)
- No network calls or remote data sources
- No loading of the `image_url` field (deferred to UI story with HTTPS-only constraint)

## Decisions

### 1. kotlinx-serialization over Gson/Moshi

**Choice**: `kotlinx-serialization-json 1.8.1`

**Rationale**: First-party Kotlin library with compile-time code generation, no reflection. `@Serializable` and `@SerialName` provide explicit snake_case mapping. Forward-compatible via `ignoreUnknownKeys = true`.

**Alternatives considered**: Gson (reflection-based, no compile-time safety), Moshi (good but adds a third-party dependency when kotlinx-serialization is already part of the Kotlin ecosystem).

### 2. One DTO class per file

**Choice**: 8 separate files in `data/model/`.

**Rationale**: Project Android guidelines mandate "one class per file". Improves navigability and keeps diffs focused.

**Alternatives considered**: Single file with all DTOs (simpler initially but violates guidelines and becomes unwieldy).

### 3. Repository returns `Result<BikeInfoSnapshotDto>`

**Choice**: `Result` type wrapping DTO, not domain model.

**Rationale**: Keeps the repository focused on data access. The DTO-to-domain mapping responsibility stays in the domain layer (mapper + use case). This separation means the repository can be tested independently of mapping logic.

### 4. `Dispatchers.IO` via `withContext` in repository

**Choice**: Repository internally switches to IO dispatcher for file reading.

**Rationale**: The `AssetManager.open()` call performs file I/O and must not block the main thread. Encapsulating the dispatcher switch inside the repository keeps callers simple. The dispatcher can be injected for testability (using `UnconfinedTestDispatcher` in tests).

### 5. Enum fallback to UNKNOWN

**Choice**: Case-insensitive `entries.find` with `UNKNOWN` as default.

**Rationale**: The JSON comes from firmware which may introduce new values. Crashing on unknown enum strings is unacceptable for a dashboard app. `UNKNOWN` allows the UI to handle gracefully (e.g., display a generic label).

### 6. Domain model mirrors DTO nesting, drops unused fields

**Choice**: Domain models keep the same nested structure as DTOs. `BikeInfo` contains `bike: BikeDetails` and `diagnostics: DiagnosticsInfo` as nested objects. `DiagnosticsInfo` contains only `warnings` — `faultCodes` is excluded.

**Rationale**: Keeping the domain model structure parallel to the DTO makes future expansion straightforward — new DTO fields are added to the corresponding nested domain model without changing the root. Flattening makes adding fields harder because it changes the shape. Unused fields (like `faultCodes`) are simply dropped from the domain counterpart.

## Risks / Trade-offs

- **[Risk] JSON schema drift**: The bundled asset is static, but if the schema changes in future stories, DTOs need updating → Mitigated by `ignoreUnknownKeys = true` for additive changes; breaking changes require a story.
- **[Risk] No dispatcher injection in repository constructor**: If we hardcode `Dispatchers.IO`, tests need `UnconfinedTestDispatcher` workarounds → Mitigated by accepting a `CoroutineDispatcher` parameter with default `Dispatchers.IO`.
- **[Trade-off] No caching**: Each call re-reads the asset file → Acceptable for a ~1 KB file; caching can be added later if needed.
- **[Trade-off] Manual DI**: No Hilt means wiring is manual in `MainActivity` → Acceptable at this project size; the guidelines say to introduce Hilt when the project grows.
