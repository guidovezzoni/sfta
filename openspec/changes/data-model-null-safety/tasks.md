## 1. Prerequisites: Remove `currentSpeedKmh`

- [ ] 1.1 Remove `currentSpeedKmh` field from `MotorDto.kt` (remove `@SerialName("current_speed_kmh") val currentSpeedKmh: Double`)
- [ ] 1.2 Remove `currentSpeedKmh` field from `MotorInfo.kt` (remove `val currentSpeedKmh: Double`)

## 2. Prerequisites: Make all DTO fields nullable

- [ ] 2.1 Make all fields in `BikeInfoSnapshotDto.kt` nullable with `= null` defaults
- [ ] 2.2 Make all fields in `BikeDto.kt` nullable with `= null` defaults
- [ ] 2.3 Make all fields in `BatteryDto.kt` nullable with `= null` defaults
- [ ] 2.4 Make all remaining fields in `MotorDto.kt` nullable with `= null` defaults
- [ ] 2.5 Make all fields in `RideSettingsDto.kt` nullable with `= null` defaults
- [ ] 2.6 Make all fields in `SessionDto.kt` nullable with `= null` defaults
- [ ] 2.7 Make all fields in `DiagnosticsDto.kt` nullable with `= null` defaults
- [ ] 2.8 Make all fields in `WarningDto.kt` nullable with `= null` defaults

## 3. Prerequisites: Make all domain model fields nullable

- [ ] 3.1 Make all fields in `BikeInfo.kt` nullable
- [ ] 3.2 Make all fields in `BikeDetails.kt` nullable
- [ ] 3.3 Make all fields in `BatteryInfo.kt` nullable
- [ ] 3.4 Make all fields in `MotorInfo.kt` nullable
- [ ] 3.5 Make all fields in `RideSettingsInfo.kt` nullable
- [ ] 3.6 Make all fields in `SessionInfo.kt` nullable
- [ ] 3.7 Make all fields in `DiagnosticsInfo.kt` nullable
- [ ] 3.8 Make all fields in `WarningInfo.kt` nullable

## 4. Mapper Null Handling (BDD)

- [ ] 4.1 Write test: GIVEN a fully populated DTO without currentSpeedKmh WHEN toDomain is called THEN all fields are correctly mapped — update existing `fullyPopulatedDto` fixture and full-mapping test assertions in `BikeInfoMapperTest`
- [ ] 4.2 Write test: GIVEN a DTO with null sub-objects WHEN toDomain is called THEN corresponding domain fields are null — new test in `BikeInfoMapperTest`
- [ ] 4.3 Write test: GIVEN a DTO with all-null leaf fields WHEN toDomain is called THEN all domain fields are null — new test in `BikeInfoMapperTest`
- [ ] 4.4 Write test: GIVEN a DTO with null enum strings WHEN toDomain is called THEN domain enum fields are null not UNKNOWN — new test in `BikeInfoMapperTest`
- [ ] 4.5 Write test: GIVEN a DTO with null timestamp WHEN toDomain is called THEN domain Instant is null — new test in `BikeInfoMapperTest`
- [ ] 4.6 Write test: GIVEN a DTO with null lists WHEN toDomain is called THEN domain list fields are null — new test in `BikeInfoMapperTest`
- [ ] 4.7 Implement: rewrite `BikeInfoMapper.toDomain()` — remove `currentSpeedKmh` mapping, add `?.let {}` chains for sub-objects, propagate null primitives directly, add null-check before enum lookup (`?.let { str -> entries.find { ... } ?: UNKNOWN }`), add null-check before `Instant.parse()`, propagate null lists

## 5. Update Existing Test Fixtures

- [ ] 5.1 Update `LocalBikeInfoRepositoryTest`: remove `"current_speed_kmh": 47.3` from JSON fixture, remove `currentSpeedKmh` assertion, verify all 3 tests pass
- [ ] 5.2 Update `GetBikeInfoUseCaseTest`: remove `currentSpeedKmh` from `sampleBikeInfo` `MotorInfo()` constructor, verify both tests pass

## 6. Verification

- [ ] 6.1 Run `./gradlew clean test` — all unit tests pass
- [ ] 6.2 Run `./gradlew check` — code quality checks pass
