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
- **Design System (#36):** promote a **lean** kit-inspired set into the ASL Design System page — clear labeled sections; **delete** old/unused components. Not a full kit dump. Critic reviews via inventory audit. After DS is accepted, later flows **instance ASL DS** (not page-local kit shelves), unless the ticket still says kit-local.

## Do / Don’t

**Do:** study kit screenshots first; **tight** islands only where they earn it (cards, dialogs, small tool panels) — not a second full-screen gray sheet; 240×420 density; sparse kit-blue accent; progressive disclosure; short motion notes. Editor code sits on **shell** under a flush top bar (gutter wash OK); do not promote the code area into a large rounded panel.

**Don’t:** wrap the whole phone body — or **most of the phone under the top bar** — in a large rounded gray “content island” / code card / **build phase list card** (human reject). Put list/empty/form/**editor code**/**build phases** on the **page shell bg** under the top bar; use smaller surfaces for rows/fields/dialogs/menus. A near-full-phone `#2B2D30` rounded frame (even if the top bar is outside it) still **FAIL**s Pass A. Don’t paint the **phone shell** with field/island gray (`#1E1F22` / `#2B2D30`) under `#12151A` chrome — that reads as a full-phone slab; shell fill must match chrome. Don’t paint muddy **full-row active washes** on phase lists (active = dots/check + type weight only). Don’t: desktop IDE 1:1; purple/glow/glass; near-noop 8px moves; mark board Ready yourself; invent icons when kit/drawables exist.

## Mapping

| JetBrains | Phone |
| --- | --- |
| Window header | Top bar on shell bg |
| Main area | Content on shell — **not** a full-bleed / near-full-phone inset gray panel |
| Editor code | Flat on shell (optional muted gutter) — **not** a large rounded code card |
| Tool / dialog island | Small raised surface only (menus, dialogs, toasts, error cards) |
| Dialogs | Modal + scrim, kit button language |

## Author workflow

1. Orient kit (screenshot components you will reuse).  
2. Redesign the **existing** flow page in place (kit-local if ticket says so). **Never** create a parallel page (`Editor` while `Files editor` remains, `Build v2`, etc.) — edit the ticket’s page; delete superseded pages/frames in the same pass.  
3. Keep ASL page skeleton + product rules from `docs/figma-design.md`.  
4. Self-check: phone chrome = product copy only; author/anim notes only in the notes box; no stray progress bars on Ready; **file has one page per flow** (no old twin left).  
5. Post **Kit | Candidate** evidence on the issue (same shape as `/design-verify`).  
6. Comment **Awaiting critic** — Status stays **make ui/ux design**. Stop.  
7. Do **not** set Ready. Do **not** implement.

## Related

- Critic: `/design-verify`  
- ASL file: `M2LGyXHC5YYJekr3Fq3oiP`  
- Community kit: https://www.figma.com/community/file/1227732692272811382/int-ui-kit
