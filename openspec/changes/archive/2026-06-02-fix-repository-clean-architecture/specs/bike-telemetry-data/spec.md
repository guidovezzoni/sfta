## MODIFIED Requirements

### Requirement: Repository reads and parses JSON from assets
`LocalBikeInfoRepository` SHALL implement `BikeInfoRepository` and read `bike_info_snapshot.json` via `AssetManager`. It SHALL use `Json { ignoreUnknownKeys = true }` for forward compatibility, wrap the operation in `runCatching`, execute on `Dispatchers.IO`, and map the parsed DTO to the domain model using `BikeInfoSnapshotDto.toDomain()` before returning. The repository SHALL return `Result<BikeInfo>`.

#### Scenario: Repository returns domain model on success
- **WHEN** `getBikeInfoSnapshot()` is called with a valid asset file available
- **THEN** it returns `Result.success` containing a `BikeInfo` domain model with all fields correctly mapped

#### Scenario: Repository handles missing file gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the asset file does not exist
- **THEN** it returns `Result.failure` with an appropriate exception

#### Scenario: Repository handles malformed JSON gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the asset file contains invalid JSON
- **THEN** it returns `Result.failure` with a serialisation exception

#### Scenario: Repository handles mapping errors gracefully
- **WHEN** `getBikeInfoSnapshot()` is called and the DTO-to-domain mapping fails
- **THEN** it returns `Result.failure` with the mapping exception

### Requirement: Mapper converts DTOs to domain models with safe enum handling
`BikeInfoMapper` SHALL reside in the data layer (`data/mapper/`) and provide a `BikeInfoSnapshotDto.toDomain()` extension function that maps every DTO field to its domain counterpart, preserving the nested structure. `BikeDto` SHALL map to `BikeDetails`, and `DiagnosticsDto` SHALL map to `DiagnosticsInfo` (excluding `faultCodes`). Enum conversion SHALL use case-insensitive lookup with `UNKNOWN` fallback.

#### Scenario: Mapper converts a fully populated DTO completely
- **WHEN** `toDomain()` is called on a fully populated `BikeInfoSnapshotDto`
- **THEN** all fields in the resulting `BikeInfo` are correctly mapped

#### Scenario: Mapper handles unknown enum values with UNKNOWN fallback
- **WHEN** `toDomain()` is called on a DTO with unrecognised enum string values
- **THEN** the corresponding enum fields fall back to `UNKNOWN`

#### Scenario: Mapper handles empty warnings list
- **WHEN** `toDomain()` is called on a DTO with an empty warnings list
- **THEN** the `BikeInfo.diagnostics.warnings` list is empty

### Requirement: Use case delegates to repository
`GetBikeInfoUseCase` SHALL call the repository's `getBikeInfoSnapshot()` and return the result directly. The repository already returns `Result<BikeInfo>`, so the use case SHALL NOT perform any mapping. On failure, it SHALL propagate the `Result.failure` unchanged.

#### Scenario: Use case returns domain model on success
- **WHEN** the repository returns a successful `Result<BikeInfo>`
- **THEN** `GetBikeInfoUseCase` returns the same `Result.success` unchanged

#### Scenario: Use case propagates failure from repository
- **WHEN** the repository returns a failure `Result`
- **THEN** `GetBikeInfoUseCase` returns the same `Result.failure`
