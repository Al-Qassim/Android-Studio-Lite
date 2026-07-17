---
name: figma-design
description: >-
  General Figma design workflow: find the project's design contract, reuse the
  design system, edit via Figma MCP, finish with screenshot evidence for an
  independent critic. Use when making or fixing Figma designs, flows, or screens.
  Ignore for pure code/git with no design ask.
---

# Figma design

Portable workflow. Project rules live in the design contract (`docs/figma-design.md` here).

## Steps

1. **Load the project design contract** before any Figma write. Follow size, density, product copy, and Ready-gate rules there. If this is a redesign, follow references-first / locked direction in the contract — do not invent a new vibe after a rejected pass.

2. **Orient in Figma.** Resolve `fileKey` / `node-id`. Open the locked kit or reference the contract names.

3. **Load Figma skills before writing.** `/figma-use` before `use_figma`. Prefer design-system / kit search over freehand.

4. **Build by reuse.** Instance DS or kit components per contract (including **kit-local** exceptions). Promote missing primitives before inventing glyphs.

5. **Edit incrementally.** One coherent pass at a time. Re-layout after text cuts. **Redesign in place** on the existing flow page — do not create a parallel page and leave the old one (e.g. `Editor` + leftover `Files editor`). Delete superseded pages/frames in the same pass.

6. **Self-check before evidence.** Phones: scan chrome for author/anim notes (`Anim:`, kit asides, timings) and leftover progress tracks on outcome states — notes live only under phones. Design System tokens: title/body/sample labels must match bound variable values; sample rows must not clip (`clipsContent` off, hug height, **and** no child overflowing the row — prefer auto-layout columns). Type samples must contrast with the dark surface (blank/near-invisible samples = FAIL). Then take fresh screenshots of candidates **and** the visual oracle (here: Int UI Kit). Post **Kit | Candidate** on the tracking issue. Comment **Awaiting critic**. Do **not** set board Ready — a different agent runs `/design-verify`.

7. **User correction = harden the contract.** Fix the whole failure class, then prune/update the project doc (and this skill only if the failure was process-general). Prefer delete sediment over stacking rules.

## Completion criteria (author)

- [ ] Contract read and followed  
- [ ] Kit \| Candidate posted; Awaiting critic  
- [ ] Ready **not** self-set  

## Related

- Critic: `/design-verify`  
- Device vs Figma after implement: `/design-review`
