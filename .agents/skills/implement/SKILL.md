---
name: implement
description: >-
  Implement a piece of work from a spec or tickets. Use when the user asks to
  implement, build, code, or ship a feature/ticket — especially feature UI,
  navigation, or data layers. Mandates /structure-feature-code for screen shape.
disable-model-invocation: true
---

# Implement

Implement the work described by the user in the spec or tickets.

## Mandatory before coding feature UI / navigation

**Open and follow** [structure-feature-code](../structure-feature-code/SKILL.md) end-to-end. For busy screens also open [`docs/agents/screen-context.md`](../../../docs/agents/screen-context.md). Do **not** invent a different screen shape.

### Presentation non-negotiables (do these first; do not “fix later”)

1. **Package layout** — `ViewModel` (state-only) · `Screen` · `ui/` · `logic/` · `presentation/preview/*Previews.kt`. Busy screens add `*ScreenContext` (see screen-context doc).
2. **No logic bodies in `@Composable` screens** — observe/map/menu/delete/install/navigate live in `logic/` as top-level (or context-extension) functions. The Screen only wires resources and **calls** them. Reference: `BuildProgressScreen` + `progress/logic/`, `BuildHistoryListScreen` / `BuildHistoryDetailScreen` + `history/logic/`.
3. **Inner-screen host = thin NavHost** — list ↔ detail ↔ progress (or similar) is `AnimatedContent` + route enum + child screen calls only — same shape as `ProjectsNavHost` / `SettingsScreen` / `BuildHistoryScreen`. No observe/menu/install code in the host file.
4. **Flat `*UiState` data class** — `isLoading`, `loadError` (or equivalent flags), plus content fields. Same shape as `EditorUiState` / `BuildHistoryDetailUiState`. **Never** sealed `Loading` / `Ready` / `Failed` for screen UI state. Content: `when { state.isLoading → …; state.loadError != null → …; else → … }`.
5. **Load + error are first-class** — async screens show Loading, a user-safe load error (with retry when useful), and not-found/empty when the resource is gone. Do not leave a blank composable or silently pop on first `null` from `collectAsState(initial = null)`.
6. **No nested `fun`** — project-wide (`AGENTS.md` → Coding rules). No mega `*Content(…onA, onB, onC…)` callback tables.
7. **Previews** — multi-state cases in `presentation/preview/` only; call the real Screen/Content.

### Data / ports (when the ticket adds seams)

1. Logic owns **ports** (interfaces + domain types); Room / network / filesystem are **`*Adapter`** types with that word in the name (e.g. `RoomBuildJobRepositoryAdapter`).
2. Feature services **wire** adapters; history/list stores must not depend on services they don’t need.
3. Cross-feature hooks: producer-owned registry in `:api` — consumers register themselves (no Koin `getAll()` multi-bind for listeners).

## Ticket / UX gates

Before coding UI/navigation work, read the ticket’s acceptance criteria against `docs/agents/writing-acceptance-criteria.md`. If the ticket is thin (e.g. missing system Back or designed primary affordances), **add those criteria to the issue** (comment or edit) before treating them as optional — do not silently ship toolbar-only behaviour.

**UI source of truth is `:designsystem` + Compose** (`docs/design-system.md`). Do **not** sync to Figma (archived under `archive/figma/`). Prefer DS tokens/components for shared look.

## Delivery

Use /tdd where possible, at pre-agreed seams.

Run typechecking regularly, single test files regularly, and the full test suite once at the end.

**Verify before done.** After edits, run the checks that would catch the change class (Gradle sync/compile for build or Kotlin edits; install + walk the flow for UI; `/design-review` screenshots for UI; tests for logic). If verification fails and you fix it, harden docs per `AGENTS.md` → *User correction = system error*.

Once done, use /code-review to review the work. For Finish reviews, also follow `docs/agents/test-review.md` and the design-review skill.

**Do not commit** unless the user explicitly asked to commit in this turn (see `AGENTS.md` → *When to commit*). Implementing after “commit the plan/previews” does **not** authorize committing the implementation.

## Pre-merge self-check (feature UI)

- [ ] `/structure-feature-code` followed (layout + NavHost + flat UiState + logic/)
- [ ] Loading / loadError / ready (or empty) all handled in UI + previews
- [ ] Adapters named `*Adapter` when ports were introduced
- [ ] Compile (+ install if user-visible); no nested `fun`; no sealed screen UiState
