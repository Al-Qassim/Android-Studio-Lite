# Design system (code) — Android Studio Lite

**UI source of truth:** the Compose module `:designsystem` and feature screens that use it.

Figma is **archived** (`archive/figma/`). Do not sync UI to Figma, open Figma tickets, or run Figma skills.

## Where to work

| Layer | Location |
| --- | --- |
| Tokens / components | `designsystem/` — tokens + reusable Compose components only (no product screens) |
| Composable UI | `designsystem/.../component/` — bars, rows, dialogs, scaffold, etc. |
| Modifier helpers | `designsystem/.../modifier/` — e.g. `insetClickable` (not under `component/`) |
| Editor helpers | `designsystem/.../editor/` — e.g. `highlightCode` |
| Feature UI | `feature/*/presentation/` — real screens; compose DS components |
| Screen previews | `feature/*/presentation/.../preview/` — multi-state Compose previews that call **real** Screen/Content (not duplicated fake screens). Preview `backgroundColor` `0xFF2B2D30` (canvas). Do not name a folder `build` — gitignores `**/build/`. |
| Visual direction | JetBrains New UI / Islands Dark — radial canvas glow + rounded islands; `IslandScaffold` for product chrome |
| Screen chrome | `IslandScaffold(topBar, body, footer?)` — top bar on canvas; one body island; optional footer under an inset divider (same island). Canvas glow: solid sea + radial primary that **soft-fades to transparent inside the scaffold** (never hard-clip a non-zero alpha at the edges). |
| Code coloring | `highlightCode(code)` / `CodeHighlightTransformation` — simple Kotlin syntax colors for the editor |

## Process

1. Change tokens/components in `:designsystem` first when the look is shared.
2. Wire features to DS components; avoid one-off colors/type when a token exists.
3. Verify on device/emulator (`/design-review` — app screenshots vs DS intent / prior device shots, **not** Figma).
4. Track work on implement tickets (e.g. Design System #37, then feature implement issues). No separate Figma design gate.

### Component approval while iterating

When the user says **commit** and then gives instructions about a **different** component than the one just fixed, treat that as approval of the previous component: **commit** (and push per `AGENTS.md`), then start the new work. Do not keep polishing the previous component unless they ask.
