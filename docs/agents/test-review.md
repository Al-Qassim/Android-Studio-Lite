# Test review

Confirm the app runs without crashing **and is functionally usable** for the ticket/spec. Run this as part of Finish reviews before asking the human to merge.

Unit tests and a clean assemble are necessary but **not sufficient** when the change includes UI or navigation. Device/emulator truth decides pass/fail for those flows.

## Steps

1. **Build** the affected modules (e.g. `./gradlew :app:assembleDebug` and any module compile/test tasks touched by the change).
2. **Unit / instrumentation tests** — run relevant tests if they exist; add or extend them when behavior is non-trivial and untested. Note: green unit tests do **not** replace step 3.
3. **Functional device check (required when UI or nav is in scope)** — install the debug APK on an emulator or device and exercise the ticket’s acceptance criteria as a user would:
   - Open every screen the ticket owns (or newly wires).
   - For each **primary action** (open item, navigate into a folder/child, create, rename, delete, save, back, etc.): perform it and **verify the visible UI state changed as expected** (new list contents, path/title update, dialog appeared, destination screen shown).
   - For hierarchical UIs (file tree, nested lists): open at least one **child** and confirm its contents are shown; use **back** and confirm the parent listing returns.
   - Exercise one obvious failure / empty path if the ticket defines one (still no crash; user-visible feedback if specified).
   - Do **not** mark this step done after “app launched” or “list appeared once.” Navigation and content updates must be observed.
4. **Record evidence** in the PR/issue comment:
   - Commands run + results (build, unit tests).
   - Device/emulator steps performed and **what was observed** (pass or fail per acceptance criterion).
   - If the environment cannot run the app, say so explicitly and still do everything possible offline — then **fail** the functional part of the review (do not claim the feature works).

## Pass criteria

- Build succeeds for touched modules.
- Relevant unit/instrumentation tests pass (or gaps are explicitly listed).
- **On device/emulator:** every acceptance criterion that is user-visible is demonstrated, including navigation into children and back when the feature is hierarchical.
- No crash on those flows.
- Behavior matches the issue/spec — or the review **fails** and lists concrete gaps (do not soft-pass with “manual QA not fully exercised”).

## Fail criteria (examples)

- App opens and shows a root list, but tapping a folder/row does not show that child’s content.
- Primary create/rename/delete/save action appears to run but the UI does not update.
- Reviewer only ran assemble + unit tests while the PR ships new screens or nav wiring.
- Reviewer notes “happy path works” without having performed the ticket’s main navigation steps.
