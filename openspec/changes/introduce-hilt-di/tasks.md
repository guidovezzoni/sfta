## 1. Prerequisites — Gradle Configuration

- [ ] 1.1 Add Hilt and KSP version entries to `gradle/libs.versions.toml`: `hilt = "2.56.2"`, `ksp = "2.2.10-1.0.33"`
- [ ] 1.2 Add Hilt library entries to `gradle/libs.versions.toml`: `hilt-android` (`com.google.dagger:hilt-android`) and `hilt-compiler` (`com.google.dagger:hilt-android-compiler`)
- [ ] 1.3 Add plugin entries to `gradle/libs.versions.toml`: `hilt-android-gradle-plugin` (`com.google.dagger.hilt.android`) and `ksp` (`com.google.devtools.ksp`)
- [ ] 1.4 Add Hilt and KSP plugins to project-level `build.gradle.kts` with `apply false`
- [ ] 1.5 Apply Hilt and KSP plugins in `app/build.gradle.kts` and add `implementation(libs.hilt.android)` and `ksp(libs.hilt.compiler)` dependencies

## 2. Prerequisites — Application Class and Manifest

- [ ] 2.1 Create `SftaApplication.kt` at `app/src/main/java/com/guidovezzoni/sfta/SftaApplication.kt` with `@HiltAndroidApp` annotation
- [ ] 2.2 Add `android:name=".SftaApplication"` to the `<application>` element in `AndroidManifest.xml`

## 3. DI Module (BDD)

- [ ] 3.1 Write test: GIVEN AppModule provides bindings WHEN BikeInfoRepository is requested THEN a LocalBikeInfoRepository instance is returned — in `AppModuleTest`
- [ ] 3.2 Implement: Create `AppModule.kt` at `app/src/main/java/com/guidovezzoni/sfta/di/AppModule.kt` — `@Module` + `@InstallIn(SingletonComponent::class)` providing `AssetManager` (from `@ApplicationContext`) and `BikeInfoRepository` (`@Singleton`, bound to `LocalBikeInfoRepository`)

## 4. Use Case Annotation (BDD)

- [ ] 4.1 Write test: GIVEN GetBikeInfoUseCase has @Inject constructor WHEN constructed manually in tests THEN existing tests still pass unchanged — verify in `GetBikeInfoUseCaseTest`
- [ ] 4.2 Implement: Add `@Inject constructor` to `GetBikeInfoUseCase` in `domain/usecase/GetBikeInfoUseCase.kt`

## 5. Activity Annotation

- [ ] 5.1 Add `@AndroidEntryPoint` annotation to `MainActivity` in `ui/MainActivity.kt`

## 6. Build Verification

- [ ] 6.1 Run `./gradlew assembleDebug` — project compiles with Hilt annotation processing
- [ ] 6.2 Run `./gradlew test` — all existing unit tests pass without modification
- [ ] 6.3 Run `./gradlew check` — code quality checks pass

## 7. Spec and Documentation Updates

- [ ] 7.1 Update `openspec/specs/bike-telemetry-data/spec.md` — add DI requirement section documenting that Repository and Use Case are provided via Hilt
- [ ] 7.2 Update `docs/userstories/1.1-data-infrastructure-DONE.md` — note that dependencies are now Hilt-injected

## 8. On-Device Verification

- [ ] 8.1 Install and launch the app on a connected device — verify no Hilt initialisation crashes and existing UI renders correctly
