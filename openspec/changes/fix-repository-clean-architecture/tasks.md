## 1. Prerequisites (file moves and package updates)

- [ ] 1.1 `git mv` `BikeInfoMapper.kt` from `domain/mapper/` to `data/mapper/` and update the package declaration to `com.guidovezzoni.sfta.data.mapper`
- [ ] 1.2 `git mv` `BikeInfoMapperTest.kt` from `domain/mapper/` to `data/mapper/` and update the package declaration to `com.guidovezzoni.sfta.data.mapper`
- [ ] 1.3 Remove the empty `domain/mapper/` directories (source and test) if no other files remain

## 2. Repository Interface (BDD)

- [ ] 2.1 Write test: update `LocalBikeInfoRepositoryTest` — GIVEN a valid JSON asset WHEN `getBikeInfoSnapshot` is called THEN it returns `Result.success` with a `BikeInfo` domain model (assert domain fields: `bikeInfo.bike.model`, `bikeInfo.battery.stateOfChargePercent`, `bikeInfo.motor.currentSpeedKmh`, `bikeInfo.rideSettings.powerMap` as `PowerMap.ENDURO`, `bikeInfo.session.durationSeconds`)
- [ ] 2.2 Implement: change `BikeInfoRepository` interface to return `Result<BikeInfo>` — replace `BikeInfoSnapshotDto` import with `BikeInfo` import
- [ ] 2.3 Implement: update `LocalBikeInfoRepository` to import `com.guidovezzoni.sfta.data.mapper.toDomain`, call `.toDomain()` on the parsed DTO inside `runCatching`, and return `Result<BikeInfo>`

## 3. Use Case Simplification (BDD)

- [ ] 3.1 Write test: update `GetBikeInfoUseCaseTest` — replace `sampleDto` (DTO construction) with `sampleBikeInfo` (domain model construction), mock repository returning `Result.success(sampleBikeInfo)`, GIVEN the repository returns a successful `Result<BikeInfo>` WHEN `GetBikeInfoUseCase` is invoked THEN it returns the same `Result` unchanged (assert identity: `assertEquals(sampleBikeInfo, result.getOrThrow())`)
- [ ] 3.2 Implement: simplify `GetBikeInfoUseCase` — remove `.map { it.toDomain() }`, remove the `toDomain` import, return `repository.getBikeInfoSnapshot()` directly

## 4. Spec and Documentation Updates

- [ ] 4.1 Update `openspec/specs/bike-telemetry-data/spec.md` — repository requirement returns `Result<BikeInfo>` and performs mapping; use case requirement reflects pass-through behaviour; mapper requirement notes `data/mapper/` location
- [ ] 4.2 Update `docs/userstories/1.1-data-infrastructure-DONE.md` — repository interface section reflects `Result<BikeInfo>` return type; mapper section notes `data/mapper/` location; use case section reflects pass-through behaviour

## 5. Verification

- [ ] 5.1 Run `./gradlew clean` followed by `./gradlew test` — all unit tests pass
- [ ] 5.2 Run `./gradlew assembleDebug` — project compiles without errors
- [ ] 5.3 Run `./gradlew check` — code quality checks pass
- [ ] 5.4 Verify `BikeInfoRepository.kt` contains zero imports from `com.guidovezzoni.sfta.data.*`
- [ ] 5.5 Verify `GetBikeInfoUseCase.kt` contains zero imports from `com.guidovezzoni.sfta.data.*`
