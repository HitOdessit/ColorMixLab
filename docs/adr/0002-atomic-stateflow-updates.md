# ADR-0002: Atomic state mutations via `MutableStateFlow.update {}`

## Status

Accepted

## Context

`GameController` mutates `GameState` from at least two contexts:

1. The UI thread, in response to user actions (add drop, check match, advance level).
2. A background coroutine on the platform side that calls `tickTimer()` once per second.

The simplest update pattern, `_gameState.value = _gameState.value.copy(field = newValue)`, is a non-atomic read-modify-write. If both contexts read the same value and copy concurrently, one update can be silently dropped.

## Decision

Every state mutation in `GameController` uses:

```kotlin
_gameState.update { state -> state.copy(...) }
```

`MutableStateFlow.update` is implemented internally as a CAS (compare-and-swap) loop: it reads the current value, computes the new value, and only commits if no other thread mutated the state in between. If contention is detected, the lambda re-runs against the new current value.

## Consequences

**Positive:**

- Concurrent updates from the timer coroutine and UI thread cannot lose mutations.
- The pattern is uniform across the controller, making it easy to reason about and review.

**Negative:**

- The lambda inside `update {}` may be invoked multiple times if there's contention. Side effects must be kept out — the lambda must be a pure function from old state to new state.
- Slightly more verbose than direct assignment.

**Rejected alternatives:**

- **Lock-based synchronization** (`synchronized`, `Mutex`). Heavier, blocks the calling thread, and is harder to combine with coroutines.
- **Single-threaded confinement** (route all mutations through a dedicated dispatcher). Adds dispatch overhead and complicates the API surface — callers would need to suspend or post.
