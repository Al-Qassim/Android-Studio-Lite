# Design system (code) — Android Studio Lite

**UI source of truth:** the Compose module `:designsystem` and feature screens that use it.

Figma is **archived** (`archive/figma/`). Do not sync UI to Figma, open Figma tickets, or run Figma skills.

## Where to work

| Layer | Location |
| --- | --- |
| Tokens / components | `designsystem/` |
| Feature UI | `feature/*/presentation/` |
| Visual direction | JetBrains New UI / Islands spirit — calm, compact, dark; implement in code |

## Process

1. Change tokens/components in `:designsystem` first when the look is shared.
2. Wire features to DS components; avoid one-off colors/type when a token exists.
3. Verify on device/emulator (`/design-review` — app screenshots vs DS intent / prior device shots, **not** Figma).
4. Track work on implement tickets (e.g. Design System #37, then feature implement issues). No separate Figma design gate.
