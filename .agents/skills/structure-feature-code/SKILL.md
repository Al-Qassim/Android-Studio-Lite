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

## 2. Errors

1. Planned, user-facing failures use a shared app error type with an explicit **UI message** field (e.g. `AppException(uiMessage)`).
2. UI shows **only** that UI message (or a fixed generic string after a user action).
3. Unexpected errors: **log** for debugging; never show raw `Throwable.message` / exception text to the user.
4. Mutating operations: **succeed or throw**. On failure, roll back partial side effects (e.g. restore DB row if file delete fails). No silent no-ops for “must work” actions.

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
  <Name>Previews.kt        # host, fakes, preview cases
  ui/                      # Ctx UI extensions (state param)
  logic/                   # Ctx logic extensions
```

Summary:

- Context holds **resources**; UI state is passed as **`state`**.
- Screen-specific UI/logic = context **extensions**; designsystem stays parameterized.
- Host builds context with **`remember(…keys)`** + ViewModel; screen does not depend on a concrete VM.
- **Never nest function declarations.**

### Thin screens (optional simpler shape)

Short forms / simple lists may stay smaller without a context type:

```text
presentation/<screen>/
  <Screen>ViewModel
  <Screen>Screen      # wiring + private helpers
  <Screen>Content     # optional pure drawing + previews
```

Do not grow a Content callback list past ~6–8 parameters — switch to Screen Context instead.

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

## 6. Checklist

- [ ] Feature owns sub-navigation; root host stays thin
- [ ] `:model` / `:api` carry domain facts only — not fields added solely for redrawing UI chrome
- [ ] Validation only in data/domain; API exposed for UI
- [ ] User-safe errors (UI message vs log-only unexpected)
- [ ] State holder is state-only
- [ ] Host builds context with `remember(…keys)` + VM; busy screen → Screen Context (`docs/agents/screen-context.md`); thin screen → small Screen/Content OK
- [ ] No nested function declarations (helpers at file / private top level)
- [ ] Multi-state previews (fixtures in `*Previews.kt`, thin stub on screen)
- [ ] Focused commit → push → PR comment (if on a PR)

## Portability

Copy this skill into another repo’s `.agents/skills/structure-feature-code/` or `~/.cursor/skills/structure-feature-code/`. Pair with project-local Cursor rules for stack-specific names (package paths, DI, design system). Ship `docs/agents/screen-context.md` with it when using Screen Context.
