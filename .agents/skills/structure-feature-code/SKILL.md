---
name: structure-feature-code
description: >-
  Structures feature UI and domain code: thin app navigation, state-only
  ViewModels, Screen Context for busy screens, shallow UI trees, multi-state
  previews, validation in the data layer exposed via API, and user-safe errors.
  Use when adding or refactoring a feature screen, placing validation, wiring
  app errors, or when the user asks to structure feature code for reuse.
---

# Structure feature code

Reusable conventions for feature modules (mobile or similar layered apps). Adapt names (`NavHost`, Koin, Compose) to the project stack; keep the **roles** the same.

## 1. Module & navigation

1. Split by role: **model** (types) · **api** (contracts) · **data** (impl) · **presentation** (UI) · **di** (bindings) — or the project’s equivalent.
2. Each feature owns its **in-feature** navigation (e.g. list ↔ create).
3. A screen that hosts **inner screens** (list ↔ detail ↔ progress) is a thin **NavHost** only: `AnimatedContent` / route enum + child screen calls — same shape as `ProjectsNavHost` / `SettingsScreen` / `BuildHistoryScreen`. Do **not** put observe/menu/install bodies in that host file; each child screen owns its wiring + `logic/`.
4. The app / integration navigator only wires **cross-feature** exits. No feature toasts, dialogs, or internal routes in the root host.
5. **Cross-feature event hooks use a producer-owned registry** in `:api` (`addListener` / `removeListener`). Consumers inject that registry and register in their own construction (e.g. `DefaultBuildService` + `ProjectEventHooks`). Do **not** bind consumer listeners into the producer via Koin `getAll()` multi-bind.
6. **`:model` / `:api` do not shape themselves around UI layout.** Progress/result types carry domain facts the caller needs for the next action (e.g. challenge code + URI to open). Do not add fields only so a later screen can redraw chrome that the presentation layer can retain from an earlier emission. UI state may keep display data; the public model must not.
7. **Comments describe the present.** KDoc/comments say what the type or function is for now. Do not narrate removed fields, old mistakes, “why we didn’t put X here,” or contrast against a discarded approach.

## 2. Errors

1. Planned, user-facing failures use a shared app error type with an explicit **UI message** field (e.g. `AppException(uiMessage)`).
2. UI shows **only** that UI message (or a fixed generic string after a user action).
3. Unexpected errors: **log** for debugging; never show raw `Throwable.message` / exception text to the user.
4. Mutating operations: **succeed or throw**. On failure, roll back partial side effects (e.g. restore DB row if file delete fails). No silent no-ops for “must work” actions.
5. **Cancellation is not failure.** When a coroutine is cancelled (`CancellationException`), do not map it to a user-facing Failed/error state. Re-throw or return; let the cancel path set Cancelled (or equivalent). Catch `CancellationException` before broad `catch (Exception)`.
6. On user-facing / product paths, throw the shared app error type — not `require` / `check` / `IllegalArgumentException` whose messages are not UI-safe.

## 3. Validation

1. Implement validation **once** in the data/domain layer.
2. Expose it on the feature API so the UI can show field errors.
3. Do **not** duplicate business rules (regex, ranges, copy) in the UI.
4. Write/persist paths still run the same validation before mutating storage.

## 4. Screen package layout

### Prefer Screen Context for busy screens

When a screen has many components (list, menus, dialogs, bars), use **Screen Context** — see [`docs/agents/screen-context.md`](../../../docs/agents/screen-context.md).

```text
presentation/<screen>/
  <Name>ScreenContext.kt   # service, updateState, exits, scope
  <Name>ViewModel.kt       # state-only + UiState types
  <Name>Screen.kt          # Ctx.Screen(state)
  ui/                      # Ctx UI extensions (state param)
  logic/                   # Ctx logic extensions
presentation/preview/
  <Name>Previews.kt        # @Preview + fixtures — call real Screen/Content
```

Summary:

- Context holds **resources**; UI state is passed as **`state`**.
- Screen-specific UI/logic = context **extensions**; designsystem stays parameterized.
- Host builds context with **`remember(…keys)`** + ViewModel; screen does not depend on a concrete VM.
- **Never nest function declarations** (project-wide — see `AGENTS.md` → *Coding rules*).

### Thin screens (screen-context shape, no Context class)

Simple screens still follow the **same layout roles** as Screen Context — just without a `*ScreenContext` type:

```text
presentation/<screen>/
  <Name>ViewModel.kt   # state-only + UiState types
  <Name>Screen.kt      # host wiring + state-driven composition
  ui/                  # state-driven bodies (take state + only that piece’s actions)
  logic/               # top-level functions (service, updateState, exits)
presentation/preview/
  <Name>Previews.kt    # @Preview + cases / provider — call real Screen/Content
```

Rules (same as Screen Context, adapted):

1. **ViewModel holds UI state only** — no service calls, validation, or navigation.
2. **Screen wires resources** (service, exits, clipboard/uri) and calls **`logic/`**; compose **`ui/`** from `state`.
3. **Do not** use a mega `*Content(state, onA, onB, onC, …)` that funnels every callback through one parameter list.
4. **`ui/`** pieces take `state` (or the relevant subtype) plus only the actions that piece needs.
5. **`logic/`** is top-level functions (not nested `fun` inside composables). Screen composables only **call** those functions — no observe/map/menu/delete/install/navigate bodies inlined in the `@Composable`. Reference: `BuildProgressScreen` + `progress/logic/`, `BuildHistoryScreen` + `history/logic/`.
6. Design-system stays parameterized.
7. Prefer this shape over a single Screen+Content file even when the screen is small (Connect, Settings hub, Build account).

### Extract meaningful units

Split long methods into **named steps that own a real phase of work** (prepare sandbox, upload, poll run). Do **not** extract tiny wrappers that only rename a single `update` / `catch` / one-liner — keep those inline at the call site.

### Kotlin control-flow traps

In `repeat` / `forEach` / similar inline loops, `return@label` exits **only that iteration** (like `continue`), not the whole loop. To stop after success, use `for` + `break`, or `return@outer` from a wrapping `run { … }`. Mistaking this for `break` can fire side effects (e.g. `workflow_dispatch`) once per attempt.

When the screen grows many components (list + menus + dialogs), **add** a `*ScreenContext` and turn `ui/` / `logic/` into context extensions — see `docs/agents/screen-context.md`.

When a feature can swap backends (auth, cloud build):

1. **Identifiers and APIs** in `:presentation` / feature `:api` / `:model` use generic names (`openVerificationUri`, `providerDisplayName`, `ConnectAccount`) — not a vendor (`openGitHub…`, `onConnectGitHubClick`).
2. **User-visible chrome** interpolates API-supplied fields (`"Open ${state.providerName}"`, paste host from `verificationUri`). Do not bake a vendor into production Compose strings.
3. **Vendor lives in** `:data` and dedicated vendor modules (e.g. `:feature:github`). They emit the concrete display name / URIs.
4. **Previews** may use the current provider as fixture copy so phones look real.

When the screen grows many components (list + menus + dialogs), **add** a `*ScreenContext` and turn `ui/` / `logic/` into context extensions — see `docs/agents/screen-context.md`.

### ViewModel / state holder

- Holds UI state across configuration changes only.
- No service calls, validation, or navigation side effects unless the human explicitly asks otherwise.
- May take route/args in the constructor (host passes them via DI) so the screen does not need a `LaunchedEffect` to seed state.
- **Screen `*UiState` is a flat `data class`** with flags/fields (`isLoading`, `loadError`, content fields) — same shape as `EditorUiState` / `BuildHistoryDetailUiState`. Do **not** model screen lifecycle as a sealed class/interface (`Loading` / `Ready` / `Failed`). Content switches with `when { state.isLoading → …; state.loadError != null → …; else → … }`.
- **Async load is visible:** start `isLoading = true`; on miss set a user-safe `loadError` (not-found copy is fine); on failure set `loadError` from `userMessageOrNull` / generic string; never leave a blank body while `collectAsState(initial = null)` races.

### Data ports & adapters

When feature logic depends on Room / network / filesystem behind a seam:

1. Define **ports** next to the logic that owns them (interfaces + domain types).
2. Name concrete implementations **`…Adapter`** explicitly (`RoomBuildJobRepositoryAdapter`, `GitHubBuildEngineAdapter`).
3. Feature services wire adapters; do not make unrelated stores depend on services they only need for side effects — use event hooks instead.
4. Engine-agnostic job lifecycle: credentials and account observation stay **inside** the engine adapter (e.g. `GitHubBuildEngineAdapter` + `AuthSession`). Do **not** put `AuthSession` / access tokens on `DefaultBuildService`. Eager resume runs without an account; engines optionally emit `observeResumeHints()` for cloud sign-in re-attach. Bind `BuildJobRepository` + `BuildEngine` in DI; do not nest a separate `BuildJobLogic` type beside the service.

## 5. Incremental delivery (when on a PR)

Only when the user asks to commit / ship a slice (see `/incremental-pr-delivery`):

1. One focused change.
2. Commit with a clear message.
3. Push.
4. Comment on the PR for that slice.
5. Repeat only after another explicit ask (unless they asked for a multi-slice loop).

## 6. Finish checks

Before calling UI/code work done:

1. Compile the touched modules (and install when the change is user-visible).
2. Clear **deprecation / error diagnostics in files you touched** — prefer current public APIs over `@Deprecated` replacements the IDE already flags.
3. Match existing Kotlin style in the file/module: **import types** and use short names; avoid inline fully-qualified names (`android.net.Uri.parse(…)`) except when disambiguating a clash.
4. Walk the changed flow on device when UX changed.

## 7. Checklist

- [ ] Provider-shaped screens: no vendor in presentation identifiers or hardcoded chrome; name/URI from API (previews may fixture the current provider)
- [ ] Feature owns sub-navigation; root host stays thin
- [ ] Inner multi-step host is NavHost-only; child screens own observe/actions
- [ ] Screen calls `logic/` — no observe/map/menu/delete/install bodies inlined in `@Composable`
- [ ] Flat `*UiState` (`isLoading` / `loadError` + fields) — no sealed Loading/Ready/Failed
- [ ] Async screens show Loading, loadError (retry when useful), empty/not-found — not blank/`null` flash
- [ ] Ports ↔ `*Adapter` naming when introducing data seams
- [ ] `:model` / `:api` carry domain facts only — not fields added solely for redrawing UI chrome
- [ ] Validation only in data/domain; API exposed for UI
- [ ] User-safe errors (UI message vs log-only unexpected)
- [ ] State holder is state-only
- [ ] Host builds context with `remember(…keys)` + VM; busy screen → Screen Context (`docs/agents/screen-context.md`); thin screen → `ui/` + `logic/`
- [ ] No nested function declarations (helpers at file / private top level)
- [ ] Multi-state previews (`@Preview` + fixtures in `presentation/preview/` only; no product screens in `:designsystem`)
- [ ] Touched files free of deprecation/error diagnostics; compile (+ install if UI) before done
- [ ] Types imported (no unnecessary fully-qualified names in call sites)
- [ ] Focused commit → push → PR comment (if on a PR)

## Portability

Copy this skill into another repo’s `.agents/skills/structure-feature-code/` or `~/.cursor/skills/structure-feature-code/`. Pair with project-local Cursor rules for stack-specific names (package paths, DI, design system). Ship `docs/agents/screen-context.md` with it when using Screen Context.
