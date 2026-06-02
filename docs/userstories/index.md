# User Stories Index

## Epic 1: Rider Dashboard

### Feature 1.1: Data Infrastructure
- [1.1 Data Infrastructure](1.1-data-infrastructure.md) -- Project setup, data layer, domain layer, and associated unit tests

### Feature 1.2: Dashboard UI
- [1.2 Dashboard UI](1.2-dashboard-ui.md) -- Theme, MVI contract, composables, wiring, and associated tests

## Epic 2: Architecture Improvements

> Stories 2.1–2.3 should be completed before story 1.2.

### Feature 2.1: Clean Architecture Fix
- [2.1 Fix Repository Clean Architecture](2.1-fix-repository-clean-architecture.md) -- Move DTO out of domain layer, relocate mapper, simplify use case

### Feature 2.2: Domain Model Refinements
- [2.2 Domain Model Refinements](2.2-domain-model-refinements.md) -- Parse timestamp to Instant, add faultCodes to DiagnosticsInfo

### Feature 2.3: Dependency Injection
- [2.3 Introduce Hilt DI](2.3-introduce-hilt-di.md) -- Add Hilt, create AppModule, annotate existing classes for injection
