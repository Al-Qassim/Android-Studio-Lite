# Screen Context

Pattern for Compose feature screens that have **many on-screen components** (lists, menus, dialogs, bars). Reference implementation: `feature/files/presentation/.../browser/`.

## Why

Splitting a busy screen into **Screen (all logic)** vs **Content (all drawing)** forces every component’s callbacks through one wiring table. That is hard to review, and logic sits far from the UI it drives.

**Screen Context** keeps resources on a small context type and makes screen-specific UI and logic **extensions** of that type, so each piece of UI can call the logic it needs without a mega parameter list.

## Name

| Term | Meaning |
| --- | --- |
| **Screen Context** | The pattern |
| `*ScreenContext` | The class holding screen resources (e.g. `FileBrowserScreenContext`) |
| Host | Feature entry (`FilesScreens.FileBrowser` / `DefaultFilesScreens`) that builds the context and owns the ViewModel |

## Shape

```text
presentation/<screen>/
  <Name>ScreenContext.kt   # resources: service, updateState, exits, scope
  <Name>ViewModel.kt       # UI state types + state-only ViewModel
  <Name>Screen.kt          # Ctx.<Name>Screen(state) + thin @Preview stub
  <Name>Previews.kt        # preview host, fakes, PreviewParameter cases
  ui/                      # Ctx UI extensions (take state)
  logic/                   # Ctx logic extensions (mutations, navigate, listing)
```

```mermaid
flowchart TB
  Host["Host: VM + ScreenContext"]
  Ctx["ScreenContext"]
  Screen["Ctx.Screen(state)"]
  UI["Ctx ui/* extensions"]
  Logic["Ctx logic/* extensions"]
  DS["designsystem composables"]
  Host --> Ctx
  Host --> Screen
  Screen --> UI
  UI --> Logic
  UI --> DS
  Screen --> Logic
  Screen --> DS
```

## Rules

1. **Context holds resources, not UI state.** Typical members: feature service/API, `updateState`, cross-feature callbacks (`onOpenFile`, `onNavigateBack`), `CoroutineScope`. UI state lives in the ViewModel / `UiState` and is passed as `state`.
2. **Screen-specific composables are context extensions** and take **`state`** so recomposition stays correct:
   `FileBrowserScreenContext.FileBrowserBody(state: FileBrowserUiState)`.
3. **Screen-specific logic is context extensions** under `logic/` (no nested `fun` inside composables).
4. **Design-system / shared UI stays parameterized** — not context extensions.
5. **Host owns the ViewModel.** Construct context outside the screen extension (pass root/name/path into the VM when needed). The screen must not depend on a concrete ViewModel type.
6. **`updateState` must apply to the flow’s current value** (`updater(it)`), never a composition-captured snapshot.
7. **Previews:** fixtures and fakes in `*Previews.kt`; keep a **thin** `@Preview` / `@PreviewParameter` stub on `*Screen.kt` so the IDE preview pane stays next to the screen.
8. **No nested function declarations** (same as the rest of feature structure).

## When to use

| Use Screen Context | Prefer a simpler screen |
| --- | --- |
| Many components (list + menus + dialogs + bars) | One form or a short list |
| Logic naturally belongs next to each UI piece | A handful of callbacks is still readable |
| File browser–scale screens | Projects list / create-style screens |

Do **not** force Screen Context onto every screen. Thin screens may still use a small Screen (+ optional Content) without a context type.

## Anti-patterns

- Reintroducing a `*Content` that takes dozens of lambdas.
- Putting service calls or navigation exits only in the ViewModel (unless explicitly changing that rule).
- Context extensions for design-system components.
- Omitting `state` from a context UI composable (breaks targeted recomposition).
- `remember`ing a context that closes over stale callbacks without keys — prefer rebuilding a cheap context each composition, or `rememberUpdatedState`.

## Checklist

- [ ] `*ScreenContext` holds resources only
- [ ] Screen is `Ctx.Screen(state)`; host builds context + VM
- [ ] Screen-specific UI under `ui/` as context extensions with `state`
- [ ] Logic under `logic/` as context extensions
- [ ] Designsystem stays normal parameters
- [ ] Previews: fixtures in `*Previews.kt`, thin stub on `*Screen.kt`
- [ ] No nested `fun`; no mega callback Content
