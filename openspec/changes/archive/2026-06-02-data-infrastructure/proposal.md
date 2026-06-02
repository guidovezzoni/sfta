## Why

The app needs to load, parse, and map a bike telemetry JSON snapshot before the UI layer (story 1.2) can display it. Without the data and domain layers, there is no data pipeline for the rider dashboard. This story establishes the foundational infrastructure so subsequent UI work can consume a clean domain model.

## What Changes

- Add Gradle dependencies: `kotlinx-serialization-json`, `lifecycle-viewmodel-compose`, `mockk`, `kotlinx-coroutines-test`, and the `kotlin-serialization` plugin
- Bundle `bike_info_snapshot.json` as an app asset (copied from `docs/mocks/` with `current_speed_kmh: 47.3` added to the `motor` object)
- Lock app orientation to `sensorLandscape` in AndroidManifest
- Create 8 DTO classes (`@Serializable` with `@SerialName` for snake_case JSON keys)
- Define `BikeInfoRepository` interface and `LocalBikeInfoRepository` implementation (reads from `AssetManager`, parses on `Dispatchers.IO`, returns `Result`)
- Create 6 domain model classes and 3 enums (`ChargingState`, `PowerMap`, `WarningSeverity`) each with `UNKNOWN` fallback
- Create `BikeInfoMapper` extension (`BikeInfoSnapshotDto.toDomain()`) with case-insensitive enum lookup
- Create `GetBikeInfoUseCase` that chains repository and mapper
- Add unit tests for repository, mapper, and use case

## Capabilities

### New Capabilities

- `bike-telemetry-data`: Loading, parsing, and mapping the bike telemetry JSON snapshot from a bundled asset into domain models via a repository, mapper, and use case

### Modified Capabilities

None.

## Impact

- **Gradle config**: `libs.versions.toml`, project `build.gradle.kts`, and `app/build.gradle.kts` all modified with new dependencies and plugin
- **AndroidManifest**: orientation lock added to the activity element
- **New packages**: `data/model/`, `data/repository/`, `domain/model/`, `domain/repository/`, `domain/mapper/`, `domain/usecase/` under `com.guidovezzoni.sfta`
- **Test packages**: mirror structure under `app/src/test/`
- **No network calls, no DI framework, no UI changes** in this story
