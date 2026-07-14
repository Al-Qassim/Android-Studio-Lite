---
name: figma-design
description: >-
  General Figma design workflow: find the project's design contract, reuse the
  design system, edit via Figma MCP, finish with a screenshot evaluate-fix
  loop. Use when the user asks to make, update, polish, or fix a Figma design,
  flow, mockup, or screen. Ignore for pure code/git with no design ask.
---

# Figma design

Portable workflow for designing in Figma. **Project-specific** layout, density,
tokens, copy, and product rules live in the repo’s design contract — not here.

**Applies when** the user asks to make, create, update, polish, or fix a
**design** in Figma. Ignore for pure code/git with no design ask.

## Steps

1. **Load the project design contract** before any Figma write.
   - Prefer (first match): `docs/figma-design.md`, `docs/design.md`,
     `docs/design/figma.md`, or a path named by the project overview / AGENTS.
   - If none exists, ask where the Figma file and design-system conventions live,
     or create a short project contract under `docs/` from existing screens
     before inventing patterns.
   - Follow that contract for size, density, pages, DS component names, and
     product-specific UI rules. Do not invent alternate patterns when the
     contract already states them.

2. **Orient in Figma.** Resolve `fileKey` / `node-id` from the user URL or the
   contract. Open a strong existing screen/page the contract names as the
   density/reference target and match it.

3. **Load Figma skills/tools before writing.**
   - `/figma-use` before every `use_figma` call.
   - `/figma-generate-design` when assembling a full page/flow from the design
     system.
   - Prefer MCP `search_design_system` / library tools over freehand drawing.

4. **Build by reuse.** Instance design-system components, variables, and icons.
   Prefer positive rules: clone/instance what exists; promote missing primitives
   into the DS first, then instance them. Homemade buttons, freehand icons, and
   placeholder glyphs are last resorts only when the contract and DS truly lack
   the control — and then harden the contract/DS afterward.

5. **Edit incrementally.** One coherent pass at a time (page switch, section,
   or phone row). Return created/mutated node IDs. After text cuts or content
   changes, re-layout so spacing still matches the reference screens.

6. **Screenshot finish pass (required).** Capture the finished work (full page
   and/or key frames). Evaluate against the project contract and the reference
   screens. Fix gaps; re-screenshot. Do not call the design done until a fresh
   screenshot looks right.

7. **User correction = harden the contract.** If the user points out an error,
   re-review the **whole latest design pass** for the same failure class, fix
   everything that fails, then update the **project** design doc (and this skill
   only if the failure was process-general). Prefer generalized prevention over
   one-off bans.

## Completion criteria

Done when all of the following hold:

- [ ] Project design contract was read and followed (or explicitly created)
- [ ] New/changed UI uses DS instances (or DS was extended first)
- [ ] Contract checklist (if any) passes
- [ ] Fresh screenshot evaluated and gaps fixed
- [ ] Project contract updated if a new failure class was discovered

## Agent / MCP tips (general)

- Search the design system before drawing buttons or icons.
- Prefer blank phone/frame names when labels already live as page text — avoid
  double titles and default white fills on structural frames.
- Put notes/captions in consistent containers the reference pages use.
- Keep app implementation and Figma in sync when the project says so (code↔design
  in the same change set unless an ADR picks a temporary leader).

## Portability

Copy this skill into another repo’s `.agents/skills/figma-design/` (or a personal
skills folder). Pair it with a project-local `docs/figma-design.md` (or equivalent)
that names the Figma file, reference pages, phone sizes, DS component names, and
product-specific rules.
