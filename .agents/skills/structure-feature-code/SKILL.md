---
name: structure-feature-code
description: >-
  Structures Android feature modules (Compose screens, ViewModels, validation,
  errors, navigation) to match Android Studio Lite code-review conventions.
  Use when adding or refactoring a feature screen, splitting Content vs Screen,
  placing validation, wiring AppException, or when the user asks to structure
  feature UI/code the project way.
---

# Structure feature code

Apply this when building or refactoring a feature under `feature/<name>/` (model / api / data / presentation / di).

## 1. Module & navigation

1. Keep public types in `:model`, contracts in `:api`, impl in `:data`, UI in `:presentation`, Koin in `:di`.
2. Expose `*Screens` with a feature-owned `NavHost(...)` for in-feature routes.
3. `:integration:navigation` only injects `*Screens` and passes **cross-feature** callbacks — no feature toasts/dialogs/routes.

## 2. Errors

1. Planned failures: `throw AppException(uiMessage = "…")` from `:data` (and validation).
2. Presentation: `error.userMessageOrNull(TAG) ?: "Something went wrong"`.
3. Never show raw `Throwable.message`.
4. Mutating ops (`delete`, `markOpened`, etc.): succeed or throw; roll back partial side effects (e.g. restore DB row if file delete fails).

## 3. Validation

1. Implement rules once in `:data`.
2. Add API methods that return field errors (or similar) for forms.
3. Presentation calls the API; do not duplicate regex/range/copy checks in the UI layer.
4. Write paths still call the same validation before persisting.

## 4. Screen package layout

For each screen (e.g. list, create):

```text
presentation/<screen>/
  <Screen>ViewModel.kt   # state holder only
  <Screen>Screen.kt      # wiring + private helpers
  <Screen>Content.kt     # drawing + previews
```

### ViewModel

```kotlin
class FooViewModel : ViewModel() {
    val uiState = MutableStateFlow(FooUiState())
}
```

No service, no validation, no one-shot navigation channels unless the human explicitly asks otherwise.

### Screen (wiring)

- Takes feature `*Service` (+ navigation lambdas).
- `koinViewModel()` for state.
- Private suspend/helpers for fetch/mutate; update `viewModel.uiState`.
- Calls `*Content(...)` with state + callbacks.

### Content (drawing)

- Pure UI from state + event lambdas.
- Extract children so **each composable nests ≤ 2–3 levels**.
- Private `@Preview`s with **many** named cases (empty, filled, errors, loading, menus, dialogs).

## 5. Checklist before PR slice

- [ ] Feature owns sub-nav; IdeNavHost stays thin
- [ ] Validation only in data; API exposed for UI
- [ ] `AppException` / `userMessageOrNull` used correctly
- [ ] ViewModel is state-only
- [ ] Screen vs Content split; shallow composable trees
- [ ] Multi-state previews on Content
- [ ] One focused commit → push → PR comment

## Reference

Canonical example: `feature/projects/presentation/list/` and `…/create/`, plus `:core:error` and `ProjectService.validateCreateProject`.
