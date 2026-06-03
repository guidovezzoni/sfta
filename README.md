# README

As an agent please do NOT modify the below section [Human managed part of the README] and its subsections, this is only for manually added info.
Any automatically added info can be added to the other specific section [## LLM managed part of the README].

## Human managed part of the README

### TL;DR
In implementing this assessment I used an AI supported SDD methodology (Spec-Driven Development) in which, the specification are fully defined before starting coding, to force the agent to follow the established plan.
On top of the SDD library (OpenSpec) I used a library I am developing, which, by using AI, allows to automate the full lifecycle, from user story refinement, to enforcing BDD, to final verification of the user story, including definition of done, unit tests, UI test, security assessment, on-device testing, etc.

I will summarise here the info requested, leaving more info in the sections below.

Tha app has been treated as a production app, so technical and architectural decision have been taken thinking this app as the first step for a larger app.
The App follows a Clean Architecture approach, with MVI at UI level based on the google ViewModel pattern.
Libraries used include:
- Dagger Hilt
- Flow & Coroutines
- jetpack compose

Things that should be tackled next:
- Implementation of release flavour
- Implementation of proper CI/CD - depending on the repository and services used
- Requirements clarification:
  - Current speed has been added and removed from the requirements, still not present in the JSON, most likely that need to be addressed as the user is expecting the current speed. 
  - Define correctly the missing enums values - ChargingState, PowerMap, and WarningSeverity - current values are those in the JSON, but there will be more.


### AI Setup
The AI setup in the project is layered across different levels, but all are included in git, so they can be shared across different members of the team.
- AGENTS.md provides a general overview of the project. Also, the first part instructs the agent how to selectively find specific instructions for Android, git, user stories, etc. These parts are located in `docs/guidelines` and will be loaded by the agent when required. 
- OpenSpec (https://github.com/Fission-AI/OpenSpec/) is used for handling the SDD processes, the commands used are:
  - explore
  - explore + propose
  - apply
  - verify
  - archive
- An additional library (SDLC), which I am currently working on, is handling the full lifecycle of user stories. More info at [SDLC-README.md](docs/sdlc/commands/SDLC-README.md). Commands are:
  - **/sdlc_open_story** which analyse the next story to open, creates a branch, sets the story open and refines it adding a full and detailed analysis
  - **/sdlc_propose** analyses the user story, asks for questions if something isn't clear, and finally generates the SDD artifacts: proposal, design, specs, and tasks. These are defined with a BDD approach, based on acceptance criteria and fail-first
  - **/sdlc_apply_changes** implements the current OpenSpec change using BDD Red/Green cycle (test tasks verified RED before implementation, implementation tasks verified GREEN after). Then runs a security review and updates the documentation
  - **/sdlc_verify_story** this is an end-to-end verification gate. Runs OpenSpec's verify, scans for unresolved TODOs, runs a security review on pending changes, checks every acceptance criterion in the story against the codebase, and finally closes the story.
  - **/sdlc_archive** runs OpenSpec's archive to finalise and archive the completed change, then verifies that he documentation is in sync with the codebase and specs.

The SDLC library is still work in progress, I'm working on multi-agent orchestration, LLM independence, self-improvement and other features.
However ultimately it will have to be tailored for each project/company, although I'm trying to abstract this by defining how each interaction happens, for instance Jira user story should be handled via the atlassian mcp.

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
| Data | `data/mapper/` | `BikeInfoMapper` — `BikeInfoSnapshotDto.toDomain()` extension |
| Domain | `domain/usecase/` | `GetBikeInfoUseCase` — delegates to repository |

### Tech Stack

- **Kotlin** 2.2.10, **Compose BOM** 2026.02.01, **AGP** 9.2.1
- **kotlinx-serialization-json** 1.8.1 for JSON parsing
- **MockK** 1.14.4 + **kotlinx-coroutines-test** 1.10.2 for unit testing
- App locked to **landscape orientation** (`sensorLandscape`)

### Data Flow

`JSON asset` → `LocalBikeInfoRepository` (parses on `Dispatchers.IO`, maps DTO → domain) → `GetBikeInfoUseCase` (delegates to repository) → `Result<BikeInfo>`
