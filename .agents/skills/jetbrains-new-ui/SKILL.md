---
name: jetbrains-new-ui
description: >-
  Design ASL screens in the JetBrains New UI / Islands spirit using the Int UI
  Kit. Use when redesigning against JetBrains New UI, Int UI Kit, Android Studio
  family chrome, Islands theme, or when the locked visual direction is JetBrains.
---

# JetBrains New UI (ASL)

Locked visual north star for Android Studio Lite redesigns: **JetBrains New UI / Islands** — serious IDE chrome that is modern and clear, **not** flat-boring and **not** glow-shiny.

## Mandatory sources (open before drawing)

1. **Int UI Kit (working copy)** — primary component bible  
   - File: https://www.figma.com/design/6whxXz3bbL8FG7dr83Oi4u/Int-UI-Kit--Community-  
   - `fileKey`: `6whxXz3bbL8FG7dr83Oi4u`  
   - Pages: **Getting started** (`6222:73687`), **Components** (`6204:73431`)  
   - Community entry node: `7884:49935` (if present in your copy; prefer the pages above)  
2. **Int UI Icons** — https://www.figma.com/community/file/1227729570033544559/int-ui-icons  
3. **Product principles** — [New UI](https://www.jetbrains.com/help/idea/new-ui.html), [Islands theme](https://blog.jetbrains.com/platform/2025/12/meet-the-islands-theme-the-new-default-look-for-jetbrains-ides/), [UI themes](https://www.jetbrains.com/help/idea/user-interface-themes.html)  
4. **ASL contract** — `docs/figma-design.md` + `/figma-design` (phone size, DS instance rules, screenshots on issues)  
5. **Systems** — `/ui-refactor` for spacing/type/hierarchy scales (no arbitrary values)

Also load `/figma-use` before any `use_figma` call.

## What “optimal use” means

### Do

- **Study the kit first.** Screenshot/orient: color styles, type, buttons, toolbars, lists, dialogs, tabs, icons. Prefer **instancing or cloning kit components** into ASL’s Design System page, then instance those ASL components on phones.
- **Islands grammar on phone:** clear separation between chrome (top bar / panels) and the **content island** (editor, list, instructional card). Soft rounded content surfaces; balanced spacing; active tab/selection easy to spot.
- **Compact density** for phone (ASL **240×420** flow phones). JetBrains Compact Mode inspiration: tighter bars, smaller icons — still legible.
- **Sparse accents.** One primary action color (map JetBrains accent → ASL Primary green family). Selection / focus / success use restrained tokens — not neon wallpaper.
- **Progressive disclosure.** Hide secondary actions in ⋮ / menus; keep toolbar simple (New UI principle).
- **Iconography.** Prefer Int UI Icons shapes (distinguishable, balanced). Promote missing icons into ASL DS from the kit/drawables — no homemade glyphs.
- **Motion notes** under phones: short, calm (fade/slide 150–250ms), never flashy glow pulses.

### Don’t

- Don’t paste desktop IDE chrome 1:1 (multi tool-window stripes, huge sidebars).
- Don’t invent purple AI / glass / glow-on-everything (wave 2 failure).
- Don’t ship near-noop “moved 8px” polish (wave 1 failure).
- Don’t bypass ASL Design System — kit informs DS; phones instance **ASL** components after promotion.
- Don’t hardcode vendor strings in production Compose; Figma fixtures may show GitHub.

## Mapping JetBrains → ASL surfaces

| JetBrains New UI | ASL phone treatment |
| --- | --- |
| Main toolbar / window header | Top bar (back + title + icon actions) |
| Editor island | Code buffer / main content card |
| Tool window | Secondary panel (file list, build phases) with distinct bg vs content |
| Tabs | Segmented or tab strip; active tab clearly stronger |
| Dialogs / balloons | Modal cards + scrim; primary + secondary buttons from kit language |
| Status bar | Thin footer status / provider line when needed |
| Notifications | Toast / inline banner — quiet |

## Workflow (every redesign ticket)

1. Open Int UI Kit (`6whxXz3bbL8FG7dr83Oi4u`) + ASL file (`M2LGyXHC5YYJekr3Fq3oiP`).  
2. Update **Design System** page first (tokens + components inspired by Int UI).  
3. Redesign flow phones by **instancing ASL DS** (after kit → DS promotion).  
4. Match ASL page structure (index, cases, notes, 240×420).  
5. Screenshot finish pass; **post images inline on the GitHub issue**.  
6. Comment ACs for implement + short animation notes.

## Completion criteria

- [ ] Int UI Kit consulted (not redesigned from memory)  
- [ ] ASL DS updated/aligned before or with the flow  
- [ ] Phones read as JetBrains New UI / Islands on mobile — calm, clear islands, sparse accent  
- [ ] No glow spam / purple AI / empty sparse chrome  
- [ ] Screenshots inline on the tracking issue  
- [ ] Functionality/affordances unchanged (redesign only)

## Related

- ASL Figma file: https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite  
- Process: `/figma-design`, `docs/figma-design.md`  
- Community source: https://www.figma.com/community/file/1227732692272811382/int-ui-kit  
