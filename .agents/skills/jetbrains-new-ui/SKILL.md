---
name: jetbrains-new-ui
description: >-
  Design ASL screens in the JetBrains New UI / Islands spirit using the Int UI
  Kit. Use when redesigning against JetBrains New UI, Int UI Kit, Android Studio
  family chrome, Islands theme, or when the locked visual direction is JetBrains.
---

# JetBrains New UI (ASL)

North star: **JetBrains New UI / Islands** — calm, clear, compact. Not empty polish, not glow-shiny.

**Ready is not yours.** After you finish, a **different** agent must `/design-verify` (Kit | Candidate). Only that critic may set Ready. Then human accepts before implement.

## Sources (open before drawing)

1. **Int UI Kit** — `fileKey=6whxXz3bbL8FG7dr83Oi4u`  
   Pages: Getting started `6222:73687`, Components `6204:73431`  
   https://www.figma.com/design/6whxXz3bbL8FG7dr83Oi4u/Int-UI-Kit--Community-
2. Principles — [New UI](https://www.jetbrains.com/help/idea/new-ui.html), [Islands](https://blog.jetbrains.com/platform/2025/12/meet-the-islands-theme-the-new-default-look-for-jetbrains-ides/)
3. ASL contract — `docs/figma-design.md` + `/figma-design` (phones, product copy, page skeleton)
4. `/ui-refactor` for spacing/type systems  
5. `/figma-use` before `use_figma`

## Kit-local vs Design System

- **kit-local** (ticket says so): redesign **on that flow page only** from Int UI Kit. ASL DS need not lead.
- **Design System (#36):** promote a **lean** kit-inspired set into the ASL Design System page — clear labeled sections; **delete** old/unused components. Not a full kit dump. Critic reviews zoomed (≤5 components per shot). After human accept, later flows instance ASL DS.

## Do / Don’t

**Do:** study kit screenshots first; content island vs quiet chrome; 240×420 density; sparse ASL green accent; progressive disclosure; short motion notes.

**Don’t:** desktop IDE 1:1; purple/glow/glass; near-noop 8px moves; mark board Ready yourself; invent icons when kit/drawables exist.

## Mapping

| JetBrains | Phone |
| --- | --- |
| Window header | Top bar |
| Editor / main island | Content island |
| Tool window | Phase list / secondary panel |
| Dialogs | Modal + scrim, kit button language |

## Author workflow

1. Orient kit (screenshot components you will reuse).  
2. Redesign the flow page (kit-local if ticket says so).  
3. Keep ASL page skeleton + product rules from `docs/figma-design.md`.  
4. Self-check: phone chrome = product copy only; author/anim notes only in the notes box; no stray progress bars on Ready.  
5. Post **Kit | Candidate** evidence on the issue (same shape as `/design-verify`).  
6. Comment **Awaiting critic** — Status stays **make ui/ux design**. Stop.  
7. Do **not** set Ready. Do **not** implement.

## Related

- Critic: `/design-verify`  
- ASL file: `M2LGyXHC5YYJekr3Fq3oiP`  
- Community kit: https://www.figma.com/community/file/1227732692272811382/int-ui-kit
