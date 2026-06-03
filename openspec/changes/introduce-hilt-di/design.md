## Context

The project uses manual dependency injection — classes construct their own dependencies or receive them via constructor parameters wired by hand. This works at the current size (one repository, one use case, one activity) but will become unwieldy as story 1.2 adds a ViewModel, UI state, and additional use cases. Hilt is the Android-recommended DI framework, built on Dagger, that generates the dependency graph at compile time via annotation processing.

The data and domain layers are stable after stories 2.1 and 2.2. No existing class signatures need breaking changes to introduce Hilt.

## Goals / Non-Goals

**Goals:**

- Introduce Hilt as the DI framework with minimal disruption to existing code.
- Provide `BikeInfoRepository` and `GetBikeInfoUseCase` through the Hilt dependency graph.
- Prepare `MainActivity` for ViewModel injection (story 1.2).
- Keep existing unit tests passing without modification.

**Non-Goals:**

- Introducing `hilt-android-testing` for test-specific modules (future story).
- Annotating `LocalBikeInfoRepository` with `@Inject constructor` (deferred — see Decision 1).
- Adding `@HiltViewModel` to any ViewModel (story 1.2 scope).
- Migrating existing tests to use Hilt test rules.

## Decisions

### Decision 1: `@Provides` for `LocalBikeInfoRepository` instead of `@Inject constructor`

`LocalBikeInfoRepository` takes `ioDispatcher: CoroutineDispatcher = Dispatchers.IO` as a constructor parameter. Using `@Inject constructor` would require Hilt to resolve every parameter, forcing a `@Qualifier` annotation and a `@Provides` method for the dispatcher anyway.

Using `@Provides` in `AppModule` lets us construct the repository with its default dispatcher, keeping the DI setup minimal.

**Alternative considered:** `@Inject constructor` + `@IoDispatcher` qualifier. Rejected because it adds boilerplate (qualifier annotation, provides method for the dispatcher) for no benefit at the current project size. Can be revisited if multiple dispatchers need injection.

### Decision 2: `@Inject constructor` for `GetBikeInfoUseCase`

`GetBikeInfoUseCase` takes a single `BikeInfoRepository` parameter — no defaults, no ambiguity. Hilt can auto-resolve it from the module binding. This is the simplest approach and avoids a redundant `@Provides` entry.

Adding `javax.inject.Inject` to the domain layer introduces a JSR-330 dependency (standard Java annotation spec, not Hilt-specific). This is widely accepted in Android clean architecture.

**Alternative considered:** `@Provides` in `AppModule`. Rejected because it would add unnecessary boilerplate when `@Inject constructor` works cleanly.

### Decision 3: `AssetManager` provided via `@Provides`, not `@Binds`

`AssetManager` is an Android framework class — it cannot have an `@Inject constructor`. A `@Provides` method in `AppModule` extracts it from `@ApplicationContext Context`. This is the standard Hilt pattern for framework-provided objects.

### Decision 4: `BikeInfoRepository` scoped as `@Singleton`

The repository holds a `Json` instance and reads from a bundled asset. Singleton scoping avoids redundant `Json` instance creation and is semantically correct — there is one data source.

### Decision 5: KSP over KAPT for annotation processing

KSP (Kotlin Symbol Processing) is the modern replacement for KAPT, offering faster build times and better Kotlin support. Dagger/Hilt has supported KSP since version 2.48. Version `2.2.10-1.0.33` matches the project's Kotlin `2.2.10`.

## Risks / Trade-offs

- **KSP version coupling** — KSP version must match the Kotlin version. If Kotlin is upgraded, KSP must be upgraded in lockstep. → Mitigated by documenting the version dependency in the version catalog.
- **Build time increase** — KSP annotation processing adds overhead to each build. → Acceptable at current project size; offset by eliminating manual wiring as the project grows.
- **JSR-330 in domain layer** — Purist clean architecture avoids any framework annotation in the domain layer. → `javax.inject.Inject` is a standard Java spec, not an Android/Hilt dependency. This is the pragmatic choice accepted across the Android community.
