## 1. Prerequisites

- [ ] 1.1 Add `kotlinx-datetime` dependency: add `kotlinxDatetime = "0.6.2"` version and `kotlinx-datetime = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlinxDatetime" }` library entry to `gradle/libs.versions.toml`; add `implementation(libs.kotlinx.datetime)` to `app/build.gradle.kts`
- [ ] 1.2 Update domain model `BikeInfo.kt`: change `timestamp: String` to `timestamp: Instant` with `import kotlinx.datetime.Instant`
- [ ] 1.3 Update domain model `DiagnosticsInfo.kt`: add `faultCodes: List<String>` field before `warnings`

## 2. Timestamp Parsing (BDD)

- [ ] 2.1 Write test: GIVEN a fully populated DTO with timestamp "2025-05-19T10:32:45Z" WHEN toDomain is called THEN timestamp is `Instant.parse("2025-05-19T10:32:45Z")` — update existing assertion in `BikeInfoMapperTest`
- [ ] 2.2 Implement: parse timestamp to `Instant` in `BikeInfoMapper.toDomain()` using `Instant.parse(timestamp)`

## 3. Fault Codes Mapping (BDD)

- [ ] 3.1 Write test: GIVEN a DTO with faultCodes ["F001"] WHEN toDomain is called THEN diagnostics.faultCodes contains ["F001"] — add assertion to existing fully-populated test in `BikeInfoMapperTest`
- [ ] 3.2 Write test: GIVEN a DTO with empty faultCodes WHEN toDomain is called THEN diagnostics.faultCodes is empty — add assertion to existing empty-warnings test and add dedicated empty-faultCodes test in `BikeInfoMapperTest`
- [ ] 3.3 Implement: map `faultCodes = diagnostics.faultCodes` in `BikeInfoMapper.toDomain()` DiagnosticsInfo constructor

## 4. Repository Test Updates (BDD)

- [ ] 4.1 Write test: GIVEN a valid JSON asset WHEN getBikeInfoSnapshot is called THEN the returned BikeInfo.timestamp is an Instant value — update assertion in `LocalBikeInfoRepositoryTest`

## 5. Documentation Updates

- [ ] 5.1 Update `openspec/specs/bike-telemetry-data/spec.md`: update Domain Models requirement to reflect `timestamp: Instant` and `DiagnosticsInfo.faultCodes`; update Mapper requirement to include timestamp parsing and faultCodes mapping; remove the note about intentionally excluding faultCodes
- [ ] 5.2 Update `docs/userstories/1.1-data-infrastructure-DONE.md`: update Domain Models and Mapper sections to reflect `timestamp: Instant` and `faultCodes` mapping

## 6. Verification

- [ ] 6.1 Run `./gradlew clean assembleDebug` to verify project compiles
- [ ] 6.2 Run `./gradlew test` to verify all unit tests pass
- [ ] 6.3 Run `./gradlew check` to verify code quality
