## Why

The `BikeInfoRepository` interface in the domain layer imports and returns `Result<BikeInfoSnapshotDto>` — a data-layer DTO. This violates Clean Architecture's dependency rule: the domain layer must not depend on the data layer. The mapper that converts DTOs to domain models also lives in `domain/mapper/`, further coupling the domain to data-layer types. Fixing this now keeps the architecture clean before the UI layer (story 1.2) builds on top of it.

## What Changes

- **Repository interface** (`domain/repository/BikeInfoRepository.kt`): change return type from `Result<BikeInfoSnapshotDto>` to `Result<BikeInfo>`, removing the data-layer import.
- **Repository implementation** (`data/repository/LocalBikeInfoRepository.kt`): apply the DTO-to-domain mapping inside `runCatching` before returning, so the repository returns `Result<BikeInfo>`.
- **Mapper relocation**: `git mv` `BikeInfoMapper.kt` from `domain/mapper/` to `data/mapper/`, update the package declaration. The mapper converts data-layer types, so it belongs in the data layer.
- **Use case simplification** (`domain/usecase/GetBikeInfoUseCase.kt`): remove the `.map { it.toDomain() }` call — the repository already returns `BikeInfo`. The use case becomes a pass-through.
- **Test updates**: repository tests assert domain model fields; use case tests mock `Result<BikeInfo>` directly; mapper tests relocate with `git mv`.
- **Spec and documentation updates**: update the main spec and story 1.1 to reflect the corrected architecture.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `bike-telemetry-data`: The repository requirement changes from returning `Result<BikeInfoSnapshotDto>` to `Result<BikeInfo>`. The use case requirement changes from chaining the mapper to pass-through. The mapper requirement changes location from domain to data layer.

## Impact

- **Source files**: 3 modified (`BikeInfoRepository.kt`, `LocalBikeInfoRepository.kt`, `GetBikeInfoUseCase.kt`), 2 relocated via `git mv` (`BikeInfoMapper.kt`, `BikeInfoMapperTest.kt`), 1 empty directory removed (`domain/mapper/`).
- **Test files**: 3 modified (`LocalBikeInfoRepositoryTest.kt`, `GetBikeInfoUseCaseTest.kt`, `BikeInfoMapperTest.kt`).
- **Documentation**: `openspec/specs/bike-telemetry-data/spec.md` and `docs/userstories/1.1-data-infrastructure-DONE.md` updated.
- **APIs**: The `GetBikeInfoUseCase` public API (`suspend operator fun invoke(): Result<BikeInfo>`) is unchanged — no downstream impact on story 1.2's ViewModel.
- **Dependencies**: No new dependencies.
