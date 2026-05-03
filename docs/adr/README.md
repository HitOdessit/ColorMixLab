# Architecture Decision Records

This directory contains ADRs documenting significant technical decisions made during the project. Each ADR captures the context, the choice made, the rejected alternatives, and the trade-offs accepted.

ADRs are immutable: once accepted, they are not edited. If a decision is reversed, a new ADR supersedes the old one.

Format: [Michael Nygard's template](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions).

## Index

| # | Title | Status |
|---|-------|--------|
| [0001](0001-adopt-kotlin-multiplatform.md) | Adopt Kotlin Multiplatform for cross-platform game logic | Accepted |
| [0002](0002-atomic-stateflow-updates.md) | Atomic state mutations via `MutableStateFlow.update {}` | Accepted |
| [0003](0003-ios-stateflow-polling-bridge.md) | Bridge Kotlin StateFlow to SwiftUI via 100ms polling | Accepted |
| [0004](0004-drawscope-particle-rendering.md) | Render celebration particles directly in `DrawScope` | Accepted |
| [0005](0005-sealed-screen-routing.md) | Sealed `Screen` hierarchy for top-level navigation | Accepted |
| [0006](0006-r8-proguard-disabled.md) | Disable R8 / ProGuard for hobby distribution | Accepted |
