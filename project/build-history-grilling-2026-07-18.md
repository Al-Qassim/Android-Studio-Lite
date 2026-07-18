# In-app build history — grilling notes

**Date:** 2026-07-18  
**Status:** Closed — shared understanding confirmed (Q37). Spec: [`build-history-prd.md`](build-history-prd.md). Architecture notes updated in `architecture.md`.  
**Context:** Persist build jobs locally, show history, keep builds running when leaving the progress UI, resume after process death, support concurrent builds. Supersedes “in-app build history = out of scope” in `cloud-build-prd.md` for this feature track.

**Related:** `project/cloud-build-prd.md`, `project/cloud-build-github-actions-grilling-2026-07-11.md`, `:feature:buildapk`

---

## Goal (from discussion)

- Build History lists all build jobs so far; updates live for in-flight jobs.
- Accessible from Settings (all projects), Start Build, project overflow, and files browser menu.
- Leaving the build progress UI without Cancel does **not** cancel the job; job keeps running.
- Metadata saved in a local DB; non-terminal jobs resume tracking after process death.
- User can run more than one build at a time (in parallel).
- History UI owned by `buildapk`; each feature nav host that can open it has its own route embedding the same entry.

---

## Questions & conclusions

### Q1 — Process death

**Options:** (A) last-known status in DB only · (B) resume tracking non-terminal jobs on next launch · (C) abandon in-flight on process death  

**Conclusion: B** — On next launch, actively resume tracking any non-terminal jobs (re-attach to GitHub / keep updating).

---

### Q2 — Rejoining a build from history

**Options:** (A) read-only detail only · (B) always reopen full progress UI · (C) running → progress; terminal → detail  

**Conclusion: C** — Active/queued → reopen progress screen for that `jobId`. Terminal → detail/summary.

---

### Q3 — History list scope / filter

**User direction (not A/B/C as posed):** One History screen. Entry sets scope:

- Settings → **unfiltered** (all projects).
- Start Build (project defined) → **filtered** to that project.

**Later refined by Q18:** no on-screen filter control in v1 — scope is navigation-only.

---

### Q4 — Unfiltered entry point

**Options:** (A) Settings only · (B) Projects list only · (C) both  

**Conclusion: A** — Settings row (e.g. next to Build account) is the all-projects entry.  
**Later addition (after Q20):** Project overflow and Files top-bar menu also open History, but **scoped to that project** (Q21), not unfiltered.

---

### Q5 — Terminal job detail

**Options:** (A) summary + Install if APK still exists · (B) always keep APK copy in app storage · (C) minimal, no Install/log  

**Conclusion: A** — Summary: project, outcome/phases, times, error, optional log link. Install only while the APK file/URI still exists.  
**Also:** User can **delete** build items from history (see Q6, Q15, Q19).

---

### Q6 — Deleting a still-running build

**Options:** (A) block delete on active · (B) delete = cancel + remove row · (C) remove row only (orphan job)  

**Conclusion: B** — Delete cancels the job (same best-effort cloud cleanup as Cancel today), then removes the history row.

---

### Q7 — Notifications while builds run in background

**Options:** (A) none · (B) persistent active notification · (C) completion-only notification  

**Conclusion: A** for v1. Notifications explicitly deferred to a future iteration.

---

### Q8 — History when a project is deleted

**Options:** (A) keep rows + stored name; cancel in-flight · (B) cascade-delete history · (C) keep rows; do not cancel in-flight  

**Conclusion: A** — Keep history rows with stored project name; cancel any in-flight builds for that project on project delete.

---

### Q9 — Starting another build while one is already running

**Options:** (A) always allow, no warning · (B) allow + non-blocking note · (C) block second build for same project  

**Conclusion: A** — Always allow Start; no warning. Parallel builds; history is how you track them.

---

### Q10 — Module ownership

**Options:** (A) `:feature:buildapk` · (B) UI in settings · (C) new `:feature:buildhistory`  

**Conclusion: A** — History list/detail/progress-rejoin, DB, resume owned by buildapk.

---

### Q11 — Navigation shape (initial) → refined

**Initially posed:** (A) top-level `IdeRoute.BuildHistory` · (B) history-only mode under Build · (C) Settings sub-route + Build sub-route  

**User direction:** **C-style, expanded** — each feature that can open History has a route in **its own nav host**, e.g.:

- `SettingsRoute.BuildHistory`
- `BuildRoute.History`
- Projects feature route for History
- Files feature route for History

All embed the same buildapk History UI. **No** single shared `IdeRoute.BuildHistory` as the only path (integration may still only host feature roots as today).

---

### Q12 — Retention / size

**Options:** (A) forever until user deletes · (B) soft cap N · (C) time-based  

**Conclusion: A** — No automatic purge in v1.

---

### Q13 — List row content

**Options:** (A) compact · (B) richer · (C) mini phase checklist  

**Conclusion: A** — Project name · status/phase · relative time (started or finished). Detail/progress hold the rest.

**Persist at least (for resume + decisions):** `jobId`, project id/name, phase/status, timestamps, error, `apkLocalPath`, `logUrl`, plus provider re-attach fields (e.g. GitHub `runId` / release tag).

---

### Q14 — Drill-in from History (nested stack)

**Options:** (A) History owns nested stack List → Progress | Detail · (B) sibling routes in every host · (C) Progress promotes to top-level Build  

**Conclusion: A** — Shared History entry owns nested navigation; feature hosts only mount the History root.

---

### Q15 — Delete history row vs APK file

**Options:** (A) DB row only · (B) also delete APK · (C) ask each time  

**Conclusion: A** — Delete history metadata (and cancel if running); leave APK on disk (e.g. Downloads).

---

### Q16 — Concurrent builds (“synchronously”)

**Options:** (A) true parallel · (B) global one-at-a-time queue · (C) parallel UI / serialized GitHub  

**Conclusion: A** — Several jobs at once; each with its own progress; no global queue. (“Synchronously” in the original ask = in parallel / simultaneously.)

---

### Q17 — Open History from Start Build

**Options:** (A) top-bar action · (B) footer secondary · (C) body row  

**Conclusion: A** — Top-bar “History” (or equivalent) on Build start.

---

### Q18 — On-screen project filter control

**Options:** (A) picker · (B) chips · (C) none in v1 — entry sets scope only  

**Conclusion: C** — No in-screen filter for v1. Settings → all; project-context entries → that project. Changing scope = leave and re-enter via another entry.

---

### Q19 — How delete is offered

**Options:** (A) row overflow → Delete + confirm · (B) swipe · (C) detail-only  

**Conclusion: A** — Overflow on each row; confirm dialog; running jobs warn that delete cancels the build.

---

### Q20 — Back from Progress after Start

**Options:** (A) exit whole Build flow (today) · (B) back to Start · (C) back to History  

**Conclusion: A** — Back from Progress (started from Start) still dismisses the Build feature; job keeps running. Rejoin via History from any entry.

**Also decided here:** Project list overflow and Files browser top-bar dropdown each get “Build history” (scoped — Q21).

---

### Q21 — Scope for Projects / Files History entries

**Options:** (A) scoped to that project · (B) unfiltered · (C) mixed  

**Conclusion: A** — Same as Start Build. Settings remains the only all-projects entry.

---

### Q22 — Persistence vs `BuildService`

**Options:** (A) BuildService writes Room · (B) BuildHistoryStore is SoT, BuildService pure runner · (C) side observer + rehydrator  

**Conclusion: B** — `BuildHistoryStore` (name TBD) is source of truth; orchestrates persistence + lifecycle. `BuildService` (or equivalent runner) executes cloud work and reports progress into the store.

---

### Q23 — Cross-feature entry wiring

**Agent posed** IdeRoute-centric options; **user override:** each feature nav host has its own History route (see Q11). Projects/Files/Settings/Build all host History locally and embed buildapk UI.

---

### Q24 — Shared History API surface

**Options:** (A) `BuildScreens.History(projectIdFilter, onDismiss)` · (B) new `BuildHistoryScreens` · (C) integration-only helper  

**Conclusion: A** — Extend `BuildScreens` with History composable; nested list → progress/detail inside. Features depend on `:feature:buildapk:api` only (same idea as existing `BuildProgress` / auth screens injection).

---

### Q25 — Who UI calls for start / cancel / observe-one

**Options:** (A) UI → store only; BuildService internal · (B) UI → BuildService; store list-only · (C) split public APIs  

**Conclusion: B** — Start/Progress keep using public `BuildService` (`startBuild` / `cancelBuild` / `observeBuild`). History list/detail/delete use the store.

---

### Q26 — How `BuildService` and the store stay in sync

**Options:** (A) GHA impl writes store directly · (B) separate observeBuild mirror · (C) store-backed facade implements BuildService  

**Conclusion: C** — Thin `DefaultBuildService` (or equivalent) implements `BuildService` and orchestrates store + pure runner (today’s GHA logic extracted). Not one god-file: facade + `BuildHistoryStore` + runner as separate types. Presentation keeps depending on `BuildService` + store (for history list/delete).

---

### Q27 — When resume runs

**Options:** (A) eagerly on process/DI start · (B) lazily on first History / BuildService use · (C) eager work, no proactive UI  

**Conclusion: A** — Resume non-terminal jobs when the `BuildService` singleton is created (app process start). Tracking continues even if History is never opened.

---

### Q28 — Resume before a GitHub run exists

**Options:** (A) fail interrupted pre-run jobs · (B) auto-restart upload/dispatch · (C) leave stuck until user acts  

**Conclusion: A** — On resume, jobs still in Preparing/Uploading (no `runId`) → **Failed** with an interrupted message. Resume polling/download only when persisted provider fields include at least `runId` (+ repo / release id+tag as needed). Persist those fields as the runner learns them.

---

### Q29 — Cancel in-flight builds when a project is deleted

**Options:** (A) ProjectService → buildapk · (B) UI calls both · (C) observeProjects diff · (hook) `ProjectListener` after delete  

**Conclusion:** Explicit hook — `ProjectListener` in `:feature:projects:api`.

- `ProjectService.deleteProject` deletes the project **first**, then notifies listeners.
- Buildapk registers a `ProjectListener` that cancels in-flight jobs for that id (history rows kept per Q8).
- Listener exceptions are **isolated** (`runCatching` per listener): must not fail or roll back project delete; cancel remains best-effort.
- Naming: `ProjectListener` (not `ProjectDeletionListener`); start with `onProjectDeleted` (can grow later).

---

### Q30 — Public history store API

**Options:** (A) separate `BuildHistoryStore` · (B) list/delete only on store · (C) history methods on `BuildService`  

**Conclusion: A** — Public API:

- `BuildService` — unchanged job ops (`startBuild` / `cancelBuild` / `observeBuild`).
- `BuildHistoryStore` — `observeHistory(projectId?)`, `observeJob` / `getJob`, `delete(jobId)` (cancel if active, remove row, keep APK file).
- Shared list/detail model e.g. `BuildHistoryItem` (jobId, project id/name, phase, times, message/error, apk path, logUrl, providerName, …).

Start/Progress use `BuildService`; History list/detail/delete use `BuildHistoryStore`. Facade keeps Room + runner in sync.

---

### Q31 — `observeBuild` after resume / read path

**Options:** (A) in-memory Flow per job; store for history · (B) store-backed observeBuild · (C) hybrid memory + store fallback  

**Conclusion: B** — Room is the read SoT for job progress. Runner writes phase updates to Room; `BuildService.observeBuild(jobId)` maps `store.observeJob` → `BuildProgress`. History list uses the same rows. Live History and Progress stay consistent across process death without a second in-memory read path.

---

### Q32 — Cancel/delete orchestration (avoid cycles)

**Options:** (A) store → BuildService.cancel · (B) delete only via BuildService · (C) internal coordinator  

**Conclusion: C** — Internal `BuildJobCoordinator` (name TBD) owns start / cancel / resume / delete-history / Room writes. Public `BuildService` and `BuildHistoryStore` are thin wrappers. Avoids `Store → BuildService → Store` dependency cycles.

---

### Q33 — Provider resume fields in Room

**Options:** (A) opaque `providerId` + `resumeJson` · (B) GitHub-specific columns · (C) per-provider side tables  

**Conclusion: A** — History entity stays provider-agnostic. Runner persists/reads a resume payload blob (`resumeJson` or equivalent) plus `providerId`. GitHub runner stores repo/runId/release id+tag inside that JSON.

---

### Q34 — Fake / test builds

**Options:** (A) FakeBuildRunner + same coordinator/Room · (B) keep standalone FakeBuildService + A for UI · (C) drop fake; mock APIs in tests  

**Conclusion: C** — Remove / stop maintaining a product-like `FakeBuildService` path for this feature. Tests mock `BuildService` / `BuildHistoryStore` (and related) as needed. Production DI: coordinator + Room + GitHub runner.

---

### Q35 — Room schema placement

**Options:** (A) buildapk data + AslDatabase · (B) separate DB · (C) not Room  

**Conclusion: A** — `BuildJobEntity` (name TBD) + DAO in `:feature:buildapk:data`; register on `AslDatabase` (version bump) and expose DAO via `DatabaseDiModule`, same pattern as projects.

---

### Q36 — Resume while logged out

**Options:** (A) fail jobs · (B) wait and resume on sign-in · (C) stuck until user acts  

**Conclusion: B** — Keep last known phase in Room; do not mark Failed solely due to missing token. When a build account token becomes available again, resume tracking for those non-terminal jobs that have resume payload (`runId`, etc.).

---

## Locked summary (quick reference)

| Topic | Decision |
| --- | --- |
| Process death | Resume non-terminal jobs |
| Tap row | Active → Progress; terminal → Detail |
| Scope | Entry-only; Settings = all; project entries = that project |
| Entries | Settings · Start Build top-bar · Project overflow · Files menu |
| Terminal detail | Summary + Install if APK exists |
| Delete | Overflow + confirm; active ⇒ cancel + remove; keep APK file |
| Notifications | None in v1 (future) |
| Project deleted | Keep history rows; cancel in-flight for that project |
| Concurrency | Parallel; always allow Start |
| Ownership | `:feature:buildapk` |
| Nav | Per-feature route + embed `BuildScreens.History` |
| History inner nav | Nested List → Progress \| Detail |
| Back from Progress (from Start) | Exit Build feature (unchanged) |
| Retention | Until user deletes |
| List row | Compact (name · phase · time) |
| Architecture | Store = SoT; thin `BuildService` facade + pure runner |
| UI APIs | Job ops → `BuildService`; history → store; `BuildScreens.History(...)` |
| Sync | Facade writes store on create/phase/terminal |
| Resume timing | Eager on `BuildService` singleton init |
| Resume pre-runId | Mark Failed (interrupted); don’t auto-restart |
| Persist for resume | `runId`, repo, release id/tag (+ phase fields / logUrl / apk path) |
| Project delete → builds | `ProjectListener.onProjectDeleted` after successful delete; errors isolated |
| History API | Public `BuildHistoryStore` (+ `BuildHistoryItem`); `BuildService` stays job-ops |
| Progress reads | `observeBuild` is store-backed (Room → `BuildProgress`) |
| Internals | `BuildJobCoordinator` behind `BuildService` + `BuildHistoryStore` |
| Resume payload | Opaque `providerId` + `resumeJson` on history row |
| Tests | Mock `BuildService` / `BuildHistoryStore`; no FakeBuildService product path |
| Room | Entity/DAO in buildapk data; registered on `AslDatabase` |
| Resume logged out | Keep phase; resume tracking when token returns |

---

## Closed

### Q37 — Close the grill?

**Conclusion: A** — Shared understanding confirmed. Follow-ups: PRD (`build-history-prd.md`), `architecture.md`, then tickets/implementation as separate work.

---

## Explicitly out of scope (this grill, for now)

- System notifications (deferred).
- On-screen project filter picker (v1).
- Auto-cap / time-based history purge.
- Deleting APK files when deleting history rows.
- Visual design of History / Detail (deferred; wiring & behavior first).
