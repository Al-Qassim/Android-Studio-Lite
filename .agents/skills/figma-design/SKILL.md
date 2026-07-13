---
name: figma-design
description: >-
  Figma design workflow — follow docs/figma-design.md; finish with screenshot,
  evaluate, and improve if needed. Use when the user asks to make, update,
  polish, or fix a Figma design, flow, mockup, or screen. Ignore for pure
  code/git with no design ask.
---

# Figma design

**Applies when** the user asks to make, create, update, polish, or fix a **design** in Figma. Ignore for pure code/git with no design ask.

1. **Read and follow** [`docs/figma-design.md`](../../../docs/figma-design.md) **before** any Figma MCP / `use_figma` work.
2. Treat that doc as the project contract — do not invent alternate layout, button, or icon patterns.
3. After edits, run the checklist at the end of `docs/figma-design.md`.
4. **Finish with a screenshot pass (required):** take an image of the finished work (full page and/or key phones), **evaluate** it against Projects management / Files editor density and this doc’s non-negotiables, then **edit again** if anything looks weak, sparse, off-DS, or inconsistent. Do not call the design done until a fresh screenshot looks good.
5. **If the user points out an error:** re-review the **whole latest design pass** (all new/edited pages) for the same failure class — collapsed index boxes, overlapping case subtitles, homemade CTAs, etc. — fix them all, then harden `docs/figma-design.md` / this skill if the checklist was missing the catch.

## Non-negotiables (summary)

- Reuse **Design System** components/icons — never homemade `▶` or freehand buttons.
- Match **Projects management** / **Files editor** density; flow phones **240×420**.
- Real product UI — no v0.1/demo banners on phones.
- Phone copy names the concrete provider (**GitHub** today); don’t leave vague “your provider” alone.
- **Instructional phones stay short:** prefer two short lines (what to do + where) + one primary CTA. Do not stack a headline, long body, and a third sentence that all say the same thing (device-flow Connect is the reference). Apply the same cut across **all new flow phones** (Waiting / Connected / Failed / Settings / gate / onboarding) — status + one action is enough when the control already says what to do.
- **After cutting copy, re-layout** so content + CTA are one tight group, **vertically centered** under the top bar on short instructional phones (no mid-band void, no dead lower half; Settings lists stay top-aligned).
- In-progress = **•••**; complete = check; failed = ✕ + clear error.
- Blank phone frame names; notes in **boxes**.
- **Obvious actions are icon-only** (copy, more, back, run, add, settings) — no redundant text labels on icon controls. Sibling top-bar actions must match type (all icon buttons in the same cluster).
- Permanent Settings entry lives on the **Projects** top bar (gear icon), not a one-off text link.
- **App ↔ Figma stay in sync** whenever shipping UI changes (see `docs/figma-design.md` → Align Figma with Compose).

Figma: [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite)
