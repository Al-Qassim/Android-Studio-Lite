# Test review

Confirm the app runs without crashing **and is functionally usable** for everything the PR claims to deliver. Run this as part of Finish reviews before asking the human to merge.

Unit tests and a clean assemble are necessary but **not sufficient** when the change includes UI, navigation, or user-visible behavior. Device/emulator truth decides pass/fail for those flows.

## 1. Build a functional checklist from the PR (required)

Before touching the device, write down every user-visible capability the PR says it ships. Sources (use all that apply):

1. Linked issue acceptance criteria (`Fixes #N` / `Closes #N`).
2. PR **Summary** / description (features, screens, actions named there).
3. PR **Test plan** manual items (unchecked or checked — still verify on device).
4. Architecture / API surface newly implemented if the PR exposes it in UI (e.g. create, rename, move, copy, delete, paste).
5. **Designed primary affordances** — designsystem components and Figma for the flow (e.g. `MoveBar` after Move/Copy, dialogs, path bar). If the kit has a dedicated control for the action, that control is what must appear.

Turn that into a **checklist of concrete actions**, one row per capability. Example shape:

| # | Capability (from PR/issue) | How to verify on device | Result |
| --- | --- | --- | --- |
| 1 | … | Tap X → expect Y | pass / fail / blocked |

Do **not** collapse multiple capabilities into “happy path works.” If the PR mentions browse, create, rename, move, copy, delete, paste, back, etc., each gets its own row.

Explicitly mark rows **blocked** only when a dependency is documented in the PR (e.g. editor handoff stubbed until #9) — still list them; do not pretend they passed.

## 2. Build and automated tests

1. Build the affected modules (e.g. `./gradlew :app:assembleDebug` and compile tasks for touched modules).
2. Run relevant unit/instrumentation tests if they exist; add or extend them when behavior is non-trivial and untested.
3. Green unit tests do **not** replace the device checklist in §3.

## 3. Functional device check (required when UI/nav/actions are in scope)

Install the debug APK on an emulator or device. Walk the checklist from §1 **in full**:

- Open every screen the PR owns or newly wires.
- For each checklist row: perform the action and **verify the visible UI state** (list contents, path/title, dialogs, toasts/errors, destination screen).
- Hierarchical UIs: open at least one child; confirm contents; **back** to parent.
- Mutating actions named in the PR (create / rename / move / copy / delete / paste / save / …): do each at least once; confirm the listing or document updates afterward.
- **Primary affordance check:** after starting an action, confirm the **designed** control is on screen (e.g. after Move/Copy, the bottom `MoveBar` with Cancel + Move here / Paste here — not only a hidden alternate like Paste buried under `+`). Passing via an undocumented workaround is a **fail**.
- At least one failure or validation path if the PR/issue implies it (invalid name, conflict, etc.) — no crash; user-visible feedback when specified.
- Do **not** stop after “app launched” or “root list appeared once.”

If a checklist item cannot be reached because of a bug earlier in the flow, mark later items **blocked by prior fail** — the review still **fails**.

## 4. Record evidence

Post on the PR/issue:

- Commands + results (build, unit tests).
- The **full checklist table** with pass / fail / blocked per row (quote the PR/issue wording where helpful).
- Short notes on what was observed for failures.
- If the environment cannot run the app: say so, do offline work, and **fail** the functional section (do not claim the feature works).

## Pass criteria

- Build succeeds for touched modules.
- Relevant automated tests pass (or gaps are explicitly listed).
- **Every** non-blocked checklist row from §1 is demonstrated on device/emulator **using the designed primary affordance**.
- No crash on those flows.
- Behavior matches the PR/issue — or the review **fails** with concrete gaps (never soft-pass with “manual QA not fully exercised” or “happy path OK” without the table).

## Fail criteria (examples)

- Any checklist capability from the PR/issue was skipped or only unit-tested.
- Root list works but folder/child navigation does not update content.
- Create/rename/move/copy/delete/paste claimed in the PR but not exercised on device.
- Action runs but UI does not update.
- Action only works through a hidden/alternate control while the designed control (e.g. `MoveBar`) never appears.
- Reviewer only ran assemble + unit tests while the PR ships screens or actions.
- Reviewer marked pass without publishing the checklist results.
