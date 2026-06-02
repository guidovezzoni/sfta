## Context

The app follows Clean Architecture with `data`, `domain`, and `ui` layers. Story 1.1 established the data infrastructure, but introduced a dependency rule violation: the domain-layer `BikeInfoRepository` interface imports and returns `Result<BikeInfoSnapshotDto>` (a data-layer type), and the mapper lives in `domain/mapper/` despite converting data-layer types.

The domain layer should depend only on its own types. Data-to-domain conversion belongs in the data layer, behind the repository boundary.

## Goals / Non-Goals

**Goals:**
- Remove all `com.guidovezzoni.sfta.data.*` imports from the domain layer
- Relocate `BikeInfoMapper` to the data layer where it logically belongs
- Keep the `GetBikeInfoUseCase` as a thin wrapper for future orchestration
- Update tests, specs, and documentation to reflect the corrected architecture

**Non-Goals:**
- Changing any mapping logic or domain model structure
- Introducing caching, new data sources, or DI frameworks
- Modifying the use case's public API (`suspend operator fun invoke(): Result<BikeInfo>`)

## Decisions

### 1. Mapper moves to `data/mapper/` (not deleted and recreated)

Use `git mv` to relocate `BikeInfoMapper.kt` and `BikeInfoMapperTest.kt` from `domain/mapper/` to `data/mapper/`. This preserves git history. The only code change is the `package` declaration.

**Alternative considered**: Delete and recreate the files. Rejected because it loses git blame history for no benefit.

### 2. Mapping happens inside `runCatching` in the repository

The existing `runCatching` block in `LocalBikeInfoRepository` already wraps JSON parsing. Adding `.toDomain()` inside the same block means mapping errors (e.g., unexpected null) are also caught and returned as `Result.failure`. This is the right behaviour — callers should not need to handle mapping errors separately from parsing errors.

**Alternative considered**: Map outside `runCatching`. Rejected because it would let mapping exceptions propagate uncaught, violating the `Result`-based error handling pattern.

### 3. Use case becomes a pass-through (not removed)

`GetBikeInfoUseCase` will simply return `repository.getBikeInfoSnapshot()` directly. The class is kept as a thin wrapper to maintain the use case pattern — future stories may add orchestration logic (e.g., caching, combining data sources).

**Alternative considered**: Remove the use case entirely and have the ViewModel call the repository directly. Rejected because it breaks the layered architecture pattern and makes future orchestration harder to add.

## Risks / Trade-offs

**[Risk] Mapper test package change breaks imports** → Mitigation: `git mv` handles the file move; only the `package` declaration needs a manual update. The test content is unchanged.

**[Trade-off] Repository now does two things (parse + map)** → Acceptable because both operations are part of "provide domain data from a raw source", which is the repository's responsibility. The `runCatching` block cleanly wraps both.

**[Trade-off] Pass-through use case adds a layer with no logic** → Acceptable for architectural consistency. The indirection cost is negligible (one function call) and the use case is the natural extension point for future business logic.
