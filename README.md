# README

As an agent please do NOT modify this file, you can add any info to [AGENTS.md](AGENTS.md), this file is reserved for manual info.

## General Introduction
In implementing this assessment I used an AI supported SDD methodology (Spec-Driven Development) in which, the specification are fully defined before starting coding, to force the agent to follow the established plan.
On top of the SDD library (OpenSpec) I used a library I am developing, which, by using AI, allows to automate the full lifecycle, from user story refinement, to enforcing BDD, to final verification of the user story, including definition of done, unit tests, UI test, security assessment, on-device testing, etc.

I will summarise here the info requested, leaving more info in the sections below.

Tha app has been treated as a production app, so technical and architectural decisions have been taken thinking this app as the first step for a larger app.
The App follows a Clean Architecture approach, with MVI at UI level based on the google ViewModel pattern.
Libraries used include:
- Dagger Hilt
- Flow & Coroutines
- jetpack compose

Some assumptions have been made during the implementation:
- Although Android seems to be preventing in future to implement only landscape apps, it doesn't seem reasonable to have the dashboard of the bike switching orientation during the drive, so the app has been kept landscape only. This will have to be investigated depending on how Android will enforce the "any orientation" requirement.

Things that should be improved:
- Updating the library versions - after a failed attempt due to Hilt, I left this task behind as it doesn't affect the functionality of the app
- Handling the empty state as a "--" placeholder in the composable is not ideal, perhaps this logic should be addressed in the ViewModel  

Things that should be tackled next:
- Implementation of release flavour
- Implementation of proper CI/CD - depending on the repository and services used
- Adding networking to get up-to-date real time status from the bike, it could be BLE or another solution. 
- Some UI elements should be reviewed in terms of usability: using the phone as a bike dashboard doesn't meet the same UI criteria as for regular apps, f.i.: retry buttons should be replaced by a polling. Also the whole UI should be prepared by a professional designer for maximum effectiveness.
- Requirements needing clarifications with product:
  - Current speed has been added and removed from the requirements, still not present in the JSON, most likely that need to be addressed as the user is expecting the current speed. 
  - Define correctly the missing enums values - ChargingState, PowerMap, and WarningSeverity - current values are those in the JSON, but there will be more.

## Additional info

### AI Setup
The AI setup in the project is layered across different levels, but all are included in git, so they can be shared across different members of the team.
- AGENTS.md provides a general overview of the project. Also, the first part instructs the agent how to selectively find specific instructions for Android, git, user stories, etc. These parts are located in `docs/guidelines` and will be loaded by the agent when required. 
- OpenSpec (https://github.com/Fission-AI/OpenSpec/) is used for handling the SDD processes, the commands used are: explore, propose, apply, verify, archive
- An additional library (SDLC), which I am currently developing, is handling the full lifecycle of user stories. More info at [SDLC-README.md](docs/sdlc/commands/SDLC-README.md). Commands are:
  - **/sdlc_open_story** analyses the next story to open, creates a branch, sets the story open and refines it adding a full and detailed product analysis
  - **/sdlc_propose** analyses the user story, asks for questions if something isn't clear, and finally generates the SDD artifacts: proposal, design, specs, and tasks. Tasks are defined with a BDD approach, based on acceptance criteria and test-first.
  - **/sdlc_apply_changes** implements the current OpenSpec change using BDD Red/Green cycle (test tasks verified RED before implementation, implementation tasks verified GREEN after). Then runs a security review and updates the documentation.
  - **/sdlc_verify_story** this is an end-to-end verification gate. Runs OpenSpec's verify, scans for unresolved TODOs, runs a security review on pending changes, checks every acceptance criterion in the story against the codebase, and finally closes the story.
  - **/sdlc_archive** runs OpenSpec's archive to finalise and archive the completed change, then verifies that the documentation is in sync with the codebase and specs.

The SDLC library is still work in progress, and I'm improving it while I use it, and adding additional features like  multi-agent orchestration, LLM independence, self-improvement by adding learnt lessons.
