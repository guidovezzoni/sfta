## Why

Manual dependency injection works at the current project size but won't scale as new features are added. With stories 2.1 (repository clean architecture) and 2.2 (domain model refinements) complete, the data and domain layers are stable — this is the right time to introduce Hilt before building the Dashboard UI (story 1.2). Hilt will enable declarative dependency management, allowing story 1.2 to use `@HiltViewModel` with `@Inject constructor` instead of a manual `ViewModelFactory`.

## What Changes

- **Gradle configuration**: Add Hilt 2.56.2 and KSP 2.2.10-1.0.33 to the version catalog, project-level, and app-level build files.
- **Application class**: Create `SftaApplication` annotated with `@HiltAndroidApp` and register it in `AndroidManifest.xml`.
- **DI module**: Create `AppModule` providing `AssetManager` and `BikeInfoRepository` (as `LocalBikeInfoRepository`, singleton-scoped).
- **Use case annotation**: Add `@Inject constructor` to `GetBikeInfoUseCase` for Hilt auto-resolution.
- **Activity annotation**: Add `@AndroidEntryPoint` to `MainActivity` to prepare for ViewModel injection.
- **Documentation**: Update OpenSpec spec and user story 1.1 to reflect Hilt-provided dependencies.

## Capabilities

### New Capabilities

_(none — Hilt DI is an infrastructure concern, not a new user-facing capability)_

### Modified Capabilities

- `bike-telemetry-data`: Add requirement that the Repository and Use Case are provided via Hilt dependency injection, with `BikeInfoRepository` scoped as a singleton.

## Impact

- **Dependencies**: Adds `com.google.dagger:hilt-android:2.56.2` (runtime) and `com.google.dagger:hilt-android-compiler:2.56.2` (KSP annotation processor). Adds `com.google.devtools.ksp:2.2.10-1.0.33` plugin.
- **Domain layer**: `GetBikeInfoUseCase` gains a `javax.inject.Inject` annotation (JSR-330, not Hilt-specific). This is widely accepted in Android clean architecture.
- **Build time**: KSP annotation processing adds a small overhead to the build.
- **Existing tests**: Unaffected — all tests construct dependencies manually, independent of Hilt.
- **No breaking changes**: All modifications are additive and backward-compatible.
