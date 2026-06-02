# README

As an agent please do NOT modify the below section [Human managed part of the README] and its subsections, this is only for manually added info.
Any automatically added info can be added to the other specific section [## LLM managed part of the README].

## Human managed part of the README
In implementing this test I used an AI supported SDD methodology (Spec-Driven Development) in which, the specification are fully defined before starting coding, to force the agent to follow the established plan.
On top of the SDD library (OpenSpec) I used a library I am developing, that by using AI allows to automate the full lifecycle, from user story refinement, to enforcing BDD, to final verification of the user story, including definition of done, unit tests, UI test, security assessment, on-device testing, etc.

I will quickly provide here the info requested, leaving more info in the sections above.

Tha app has been treated as a production app, so technical and architectural decision have been taken thinking this app as the first step for a larger app.
The App follows a Clean Architecture approach, with MVI at UI level based on the ViewModel implementation.
Libraries used include:
- Dagger Hilt
- Flow & Coroutines
- jetpack compose

Things that should be tackled next:
- Clarify the current speed issue - there isn't one in the JSON, only a max speed for the session. 
- Having access to the complete API documentation would help to define correctly the missing enums values - ChargingState, PowerMap, and WarningSeverity.


### Process followed
This section describes the process I followed to implement the project.
* Added the AI tooling: OpenSpec (SDD process), SDLC (user story creation and full life cycle - this is  a work in progress tool I'm working on), AGENTS.md and guidelines for Android, git, processes etc.
* Created a basic app
* Brainstorming with Claude about what we have and what we are trying to achieve, result in  [Rider Dashboard Plan](docs/brainstorming/rider-dashboard-plan.md)
* Two user stories created in docs/userstories

### Notes
* There seems to be a comma missing in the JSON snapshot, I have assumed it's a copy-paste issue in the test and added it.
* As for the speed, the JSON only has `session.max_speed_kmh` and there isn't a `current_speed_kmh`, I'm assuming it's missing for whichever reason, and adding it to the JSON.


## LLM managed part of the README

### Architecture

The app follows **Clean Architecture** with an **MVI** pattern (to be wired in UI story 1.2):

| Layer | Package | Contents |
|-------|---------|----------|
| Data | `data/model/` | 8 `@Serializable` DTO classes mapping the JSON schema |
| Data | `data/repository/` | `LocalBikeInfoRepository` — reads and parses the bundled JSON asset |
| Domain | `domain/model/` | 8 domain model data classes + 3 enums with `UNKNOWN` fallback |
| Domain | `domain/repository/` | `BikeInfoRepository` interface |
| Domain | `domain/mapper/` | `BikeInfoMapper` — `BikeInfoSnapshotDto.toDomain()` extension |
| Domain | `domain/usecase/` | `GetBikeInfoUseCase` — chains repository and mapper |

### Tech Stack

- **Kotlin** 2.2.10, **Compose BOM** 2026.02.01, **AGP** 9.2.1
- **kotlinx-serialization-json** 1.8.1 for JSON parsing
- **MockK** 1.14.4 + **kotlinx-coroutines-test** 1.10.2 for unit testing
- App locked to **landscape orientation** (`sensorLandscape`)

### Data Flow

`JSON asset` → `LocalBikeInfoRepository` (parses on `Dispatchers.IO`) → `GetBikeInfoUseCase` (maps DTO → domain) → `Result<BikeInfo>`
