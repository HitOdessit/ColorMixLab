# ADR-0004: Render celebration particles directly in `DrawScope`

## Status

Accepted

## Context

The end-game celebration animates 50+ particles across a 10-second multi-phase choreography (appear → fly → spiral → explode). Initial implementations dropped frames on mid-tier Android devices.

Profiling showed two main causes:

1. **Per-particle `animateFloatAsState`.** Each particle's position was driven by its own animation. With 50 particles × 4 properties (x, y, alpha, rotation), that's 200 animations triggering recompositions every frame.
2. **Allocation churn.** Each frame allocated new `Offset`, `Color`, and intermediate objects, putting pressure on the young-generation GC and causing frame stutters.

Options considered:

1. **Optimize the existing per-particle animation.** Reduce particle count, batch updates.
2. **Single master clock + per-particle state arrays.** One `Animatable` driving the master timeline; particle positions computed inline from pre-allocated `FloatArray` buffers; rendering inside `Canvas { }` via `DrawScope`.
3. **Use Lottie.** Pre-rendered After Effects animation.

## Decision

Adopt **option 2**.

`GameCompletionCelebration.kt` uses a single `Animatable<Float>` driving a 0.0-1.0 master progress value. Particle positions are computed inline inside the `Canvas { }` block from pre-allocated `FloatArray` buffers (`xs`, `ys`, `alphas`, `rotations`). Rendering happens entirely within `DrawScope` — no Compose recomposition per frame.

## Consequences

**Positive:**

- Stable 60fps on mid-tier devices.
- Zero per-frame allocations once the animation is set up.
- All animation timing logic is centralized in one master clock, easy to tune.

**Negative:**

- The implementation is denser and harder to read than per-particle animations.
- Magic numbers for phase boundaries (0.0-0.2 appear, 0.2-0.5 fly, etc.) sit inline. They could move to constants.
- No designer-friendly editing path. Tweaking the choreography requires editing Kotlin, not a JSON or Lottie file.

**Rejected alternatives:**

- **Optimize the existing per-particle animation.** Reducing particle count from 50 to ~20 hurt the visual impact too much. Batching animations across particles is essentially what option 2 does, just done via a shared clock from the start.
- **Lottie.** Adds a third-party dependency. Lottie is fantastic for designer-driven animations imported from After Effects, but the celebration was prompt-engineered, not designed. Lottie's runtime is also non-trivial in size for a single animation.
