# AGENTS.md

This file contains guidelines and commands for agentic coding agents working in this repository.

## External File Loading

CRITICAL: When you encounter a file reference (e.g., @docs/guidelines/guidelines-android.md), use your Read tool to load it on a need-to-know basis. They're relevant to the SPECIFIC task at hand.

Instructions:

- Do NOT preemptively load all references - use lazy loading based on actual need
- When loaded, treat content as mandatory instructions that override defaults
- Follow references recursively when needed
- If a file reference cannot be found, always notify the user.

### Android Guidelines

For Native Android code style and best practices: @docs/guidelines/guidelines-android.md

### Git Guidelines

For git operations and commit conventions: @docs/guidelines/guidelines-git.md

### General Guidelines

Read the following file immediately as it's relevant to all workflows: @docs/guidelines/guidelines-process.md


## Project Overview

Stark Future Technical Assessment — an Android rider dashboard app for the Stark VARG electric motorbike. Displays real-time bike telemetry (battery, motor, ride settings, session stats, diagnostics) loaded from a bundled JSON snapshot.

### Package Structure (`com.guidovezzoni.sfta`)

```
di/                 — AppModule (Hilt, SingletonComponent)
data/model/         — @Serializable DTO classes (8 files)
data/repository/    — LocalBikeInfoRepository (reads JSON asset)
data/mapper/        — BikeInfoMapper (DTO → domain)
domain/model/       — Domain data classes + enums (11 files)
domain/repository/  — BikeInfoRepository interface
domain/usecase/     — GetBikeInfoUseCase (@Inject constructor)
ui/                 — MainActivity (@AndroidEntryPoint), theme (Compose)
```

### Key Patterns

- **Clean Architecture**: data → domain → ui layers
- **MVI**: Model-View-Intent (UI layer, to be implemented in story 1.2)
- **Hilt DI**: Hilt with `@HiltAndroidApp` / `@AndroidEntryPoint`; `AppModule` binds `BikeInfoRepository` as `@Singleton`
- **kotlinx-serialization**: JSON parsing with `@Serializable` / `@SerialName`
- **Enum safety**: All enums have `UNKNOWN` fallback for unrecognised values
- **Error handling**: `Result` type throughout, no `!!` operator
