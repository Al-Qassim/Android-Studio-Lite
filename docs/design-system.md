# Design system (code) — Android Studio Lite

**UI source of truth:** the Compose module `:designsystem` and feature screens that use it.

Figma is **archived** (`archive/figma/`). Do not sync UI to Figma, open Figma tickets, or run Figma skills.

## Where to work

| Layer | Location |
| --- | --- |
| Tokens / components | `designsystem/` — tokens + reusable Compose components only (no product screens). UI type: Inter; code/gutter: JetBrains Mono (`Typography.Code` / `CodeGutter`). |
| Composable UI | `designsystem/.../component/` — bars, rows, dialogs, scaffold, etc. |
| Modifier helpers | `designsystem/.../modifier/` — e.g. `insetClickable`, `overlayEnter` (not under `component/`) |
| Overlay motion | Menus / dialogs use `overlayEnter` (short fade + scale) on their surfaces — feature Popup/Dialog hosts stay thin |
| Nav motion | Custom route hosts use `AnimatedContent` + `aslNavFade()` — enter/exit cross-fade; exit starts after a short delay |
| Editor helpers | `designsystem/.../editor/` — e.g. `highlightCode` |
| Feature UI | `feature/*/presentation/` — real screens; compose DS components |
| Screen previews | `feature/*/presentation/.../preview/` — `@Preview` composables + fixtures that call **real** Screen/Content (not on Screen/Content files; not duplicated fake screens). Preview `backgroundColor` `0xFF2B2D30` (canvas). Do not name a folder `build` — gitignores `**/build/`. |
| Visual direction | JetBrains New UI / Islands Dark — radial canvas glow + rounded islands; `IslandScaffold` for product chrome |
| Screen chrome | `IslandScaffold(topBar, body, footer?)` — edge-to-edge canvas under system bars; status/nav insets on top bar and island; one body island; optional footer under an inset divider; canvas glow soft-fades to transparent inside the scaffold |
| Toasts | `Toast` is the pill; bottom overlays use `ToastBottom` (nav-bar inset + gap) — never bottom-align a toast with only fixed `dp` padding |
| Code coloring | `highlightCode(code)` / `CodeHighlightTransformation` — simple Kotlin syntax colors for the editor |
| Code editor | `CodeEditorField` — editable code with line-number gutter; `wrapText` soft-wraps or scrolls horizontally. Editor menu: Auto save + Wrap text (persisted via `EditorPreferences`). |
| Activity shell | `MainActivity` enables edge-to-edge with transparent system bars and hosts the nav graph; screens / `IslandScaffold` own backgrounds and system-bar insets |

## Process

1. Change tokens/components in `:designsystem` first when the look is shared.
2. Wire features to DS components; avoid one-off colors/type when a token exists.
3. Put product backgrounds and `statusBars` / `navigationBars` / `systemBars` padding on screens or `IslandScaffold`, so canvas chrome can draw under the status bar. Overlays drawn **outside** the scaffold (toasts, floating bars) must apply their own system-bar insets — `IslandScaffold` does not inset siblings.
4. Verify on device/emulator (`/design-review` — app screenshots vs DS intent / prior device shots, **not** Figma). Confirm the status-bar area shows canvas/glow.
5. Track work on implement tickets (e.g. Design System #37, then feature implement issues). No separate Figma design gate.
6. Comments describe current behavior only — not removed approaches or mistakes.

### Component approval while iterating

Do **not** auto-commit after every polish turn. Commit only when the user explicitly asks (see `AGENTS.md`).
