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
3. The app / integration navigator only wires **cross-feature** exits. No feature toasts, dialogs, or internal routes in the root host.
4. **`:model` / `:api` do not shape themselves around UI layout.** Progress/result types carry domain facts the caller needs for the next action (e.g. challenge code + URI to open). Do not add fields only so a later screen can redraw chrome that the presentation layer can retain from an earlier emission. UI state may keep display data; the public model must not.
5. **Comments describe the present.** KDoc/comments say what the type or function is for now. Do not narrate removed fields, old mistakes, or “why we didn’t put X here.”

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
  <Name>Screen.kt          # Ctx.Screen(state) + thin @Preview stub
  ui/                      # Ctx UI extensions (state param)
  logic/                   # Ctx logic extensions
presentation/preview/
  <Name>Previews.kt        # host, fakes, preview cases — call real Screen/Content
```

Summary:

- Context holds **resources**; UI state is passed as **`state`**.
- Screen-specific UI/logic = context **extensions**; designsystem stays parameterized.
- Host builds context with **`remember(…keys)`** + ViewModel; screen does not depend on a concrete VM.
- **Never nest function declarations.**

### Thin screens (screen-context shape, no Context class)

Simple screens still follow the **same layout roles** as Screen Context — just without a `*ScreenContext` type:

```text
presentation/<screen>/
  <Name>ViewModel.kt   # state-only + UiState types
  <Name>Screen.kt      # host wiring + state-driven composition + thin @Preview stub
  ui/                  # state-driven bodies (take state + only that piece’s actions)
  logic/               # top-level functions (service, updateState, exits)
presentation/preview/
  <Name>Previews.kt    # preview cases / provider — call real Screen/Content
```

Rules (same as Screen Context, adapted):

1. **ViewModel holds UI state only** — no service calls, validation, or navigation.
2. **Screen wires resources** (service, exits, clipboard/uri) and calls **`logic/`**; compose **`ui/`** from `state`.
3. **Do not** use a mega `*Content(state, onA, onB, onC, …)` that funnels every callback through one parameter list.
4. **`ui/`** pieces take `state` (or the relevant subtype) plus only the actions that piece needs.
5. **`logic/`** is top-level functions (not nested `fun` inside composables).
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

## 5. Incremental delivery (when on a PR)

1. One focused change.
2. Commit with a clear message.
3. Push.
4. Comment on the PR for that slice.
5. Repeat.

## 6. Finish checks

Before calling UI/code work done:

1. Compile the touched modules (and install when the change is user-visible).
2. Clear **deprecation / error diagnostics in files you touched** — prefer current public APIs over `@Deprecated` replacements the IDE already flags.
3. Match existing Kotlin style in the file/module: **import types** and use short names; avoid inline fully-qualified names (`android.net.Uri.parse(…)`) except when disambiguating a clash.
4. Walk the changed flow on device when UX changed.

## 7. Checklist

- [ ] Provider-shaped screens: no vendor in presentation identifiers or hardcoded chrome; name/URI from API (previews may fixture the current provider)
- [ ] Feature owns sub-navigation; root host stays thin
- [ ] `:model` / `:api` carry domain facts only — not fields added solely for redrawing UI chrome
- [ ] Validation only in data/domain; API exposed for UI
- [ ] User-safe errors (UI message vs log-only unexpected)
- [ ] State holder is state-only
- [ ] Host builds context with `remember(…keys)` + VM; busy screen → Screen Context (`docs/agents/screen-context.md`); thin screen → small Screen/Content OK
- [ ] No nested function declarations (helpers at file / private top level)
- [ ] Multi-state previews (fixtures in `presentation/preview/`, thin stub on screen; no product screens in `:designsystem`)
- [ ] Touched files free of deprecation/error diagnostics; compile (+ install if UI) before done
- [ ] Types imported (no unnecessary fully-qualified names in call sites)
- [ ] Focused commit → push → PR comment (if on a PR)

## Portability

Copy this skill into another repo’s `.agents/skills/structure-feature-code/` or `~/.cursor/skills/structure-feature-code/`. Pair with project-local Cursor rules for stack-specific names (package paths, DI, design system). Ship `docs/agents/screen-context.md` with it when using Screen Context.
