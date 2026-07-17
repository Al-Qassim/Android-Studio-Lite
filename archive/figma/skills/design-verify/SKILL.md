---
name: design-verify
description: >-
  Independent visual critic for Figma against Int UI Kit. Use after a design
  author finishes a flow or Design System page, before board Ready, or when
  verifying Kit vs Candidate. Never run as the same agent that drew the UI.
---

# Design verify (critic)

Authors cannot set **Ready**. A **different** agent runs this skill → **PASS** (Ready) or **FAIL** (send back).

**Gate:** critic PASS → Ready. Human accept before **implement** unless the coordinator/ticket says otherwise.  
When the ticket or epic says **auto-ready** / **no human design gate**: on PASS set Ready and stop — do not wait for human.  
**Oracle file:** Int UI Kit `6whxXz3bbL8FG7dr83Oi4u` only.  
**Candidate file:** ASL `M2LGyXHC5YYJekr3Fq3oiP` only.

This is an **audit**, not a spot-check. Missing inventory rows or missing Kit node ids → **cannot PASS** (fail-closed).

## Steps

1. **Confirm you are not the author.**

2. **Inventory (legwork — do this before any verdict).**  
   From Figma metadata on the candidate page/frame, list every review unit:
   - **Flow page:** each phone / key state frame.
   - **Design System:** every labeled section **and** every leftover top-level frame/group on the page (including unlabeled scrap).  
   Post the inventory on the issue as a table: `unit | keep+compare | delete | restyle`.  
   **Completion:** every on-canvas unit has a row. No silent skips.

3. **Pass A — Cleanliness (page / phones as artifacts).**  
   Judge the candidate alone: dark designed page, no scrapyard, no default white boxes, no clipped samples, notes not on phone chrome.  
   **FAIL** full-bleed / near-full-phone **gray content slabs** (large rounded panel wrapping empty/list/form/**editor**/**build phase lists** under the top bar — looks like a gray card filling the UI, even with small side/bottom insets). Content should sit on the shell background; islands are tight (rows, fields, dialogs, menus, toasts)—not a second screen / code card / phase card inside the phone. **FAIL** muddy full-row **active washes** on phase/step lists (prefer icon + type weight only).  
   **FAIL** **duplicate flow pages** — e.g. both `Files editor` and `Editor` (or `Build` + `Build v2`) still in the file for the same ticket. Redesign must replace in place; superseded page must be gone.  
   For DS: if a unit is still on the page, it must belong (sectioned) or the inventory says **delete** and it is gone.  
   **FAIL** Pass A → stop (board make ui/ux design). Do not claim family PASS.

4. **Pass B — Family (Kit vs Candidate).**  
   Only for inventory rows marked **keep+compare**.  
   For each unit (or ≤5 comps per shot):  
   - Screenshot **Kit** via `get_screenshot` on `fileKey=6whxXz3bbL8FG7dr83Oi4u` + `kitNodeId`.  
   - Screenshot **ASL** via `fileKey=M2LGyXHC5YYJekr3Fq3oiP` + `aslNodeId`.  
   - Host as `kit-….png` / `asl-….png`; table must print both **fileKeys + nodeIds**.  
   **FAIL** if: left side is ASL; both sides same file (DS↔DS); node ids missing; full-page-only evidence; image too zoomed-out to read detail; glow/purple/wrong family; product rules broken (e.g. Connect Waiting strips code+Open).  
   **Completion:** every **keep+compare** row has a Kit|ASL evidence block with ids.

5. **Verdict + board.**  
   - **PASS** only if inventory complete **and** Pass A ok **and** every keep+compare has honest Kit|ASL evidence. Ready for human; implement held.  
   - **FAIL** otherwise → make ui/ux design; list failed inventory ids.

## Evidence shape

```markdown
### Inventory
| Unit | Decision | aslNodeId |
| --- | --- | --- |
| … | keep+compare / delete / restyle | … |

### Pass A — Cleanliness
**PASS | FAIL** — …

### Pass B — Design verify · <unit> (≤5)
| Kit `6whx…` node `…` | ASL `M2LG…` node `…` |
| :---: | :---: |
| <img width="320" src="…/kit-….png" /> | <img width="320" src="…/asl-….png" /> |

**Verdict:** PASS | FAIL
```

## Related

- Contract: `docs/figma-design.md` · author: `/jetbrains-new-ui` · device: `/design-review`
- Kit: https://www.figma.com/design/6whxXz3bbL8FG7dr83Oi4u/Int-UI-Kit--Community-
