---
name: structure-feature-code
description: >-
  Structures feature UI and domain code: thin app navigation, state-only
  ViewModels, Screen vs Content split, shallow UI trees, multi-state previews,
  validation in the data layer exposed via API, and user-safe errors. Use when
  adding or refactoring a feature screen, placing validation, wiring app
  errors, splitting drawing from wiring, or when the user asks to structure
  feature code for reuse across projects.
---

# Structure feature code

Reusable conventions for feature modules (mobile or similar layered apps). Adapt names (`NavHost`, Koin, Compose) to the project stack; keep the **roles** the same.

## 1. Module & navigation

1. Split by role: **model** (types) · **api** (contracts) · **data** (impl) · **presentation** (UI) · **di** (bindings) — or the project’s equivalent.
2. Each feature owns its **in-feature** navigation (e.g. list ↔ create).
3. The app / integration navigator only wires **cross-feature** exits. No feature toasts, dialogs, or internal routes in the root host.

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

Per screen:

```text
presentation/<screen>/
  <Screen>ViewModel   # in-memory UI state only (survive lifecycle)
  <Screen>Screen      # wiring + private data helpers
  <Screen>Content     # drawing + previews
```

### ViewModel / state holder

- Holds UI state across configuration changes only.
- No service calls, validation, or navigation side effects unless the human explicitly asks otherwise.

### Screen (wiring)

- Depends on the feature service/API + navigation callbacks.
- Private helpers for load/mutate; update state holder.
- Renders by calling Content with state + event lambdas.
- **Never nest function declarations** (no `fun` / local function defined inside another function or composable). Put helpers at file/private top level (or in a sibling type) so call sites stay flat and testable.

### Content (drawing)

- Pure UI from state + callbacks.
- Nesting **≤ 2–3 levels** per composable/function; extract children when deeper.
- Many named previews: empty, filled, field errors, loading, menus, dialogs, action errors.
- Same rule: **no nested function declarations** inside composables — extract private `@Composable`s or file-level helpers instead.

## 5. Incremental delivery (when on a PR)

1. One focused change.
2. Commit with a clear message.
3. Push.
4. Comment on the PR for that slice.
5. Repeat.

## 6. Checklist

- [ ] Feature owns sub-navigation; root host stays thin
- [ ] Validation only in data/domain; API exposed for UI
- [ ] User-safe errors (UI message vs log-only unexpected)
- [ ] State holder is state-only
- [ ] Screen vs Content split; shallow UI trees
- [ ] No nested function declarations (helpers at file / private top level)
- [ ] Multi-state previews on Content
- [ ] Focused commit → push → PR comment (if on a PR)

## Portability

Copy this skill into another repo’s `.agents/skills/structure-feature-code/` or `~/.cursor/skills/structure-feature-code/`. Pair with project-local Cursor rules for stack-specific names (package paths, DI, design system).
