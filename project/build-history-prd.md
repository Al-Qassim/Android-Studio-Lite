# PRD: In-app build history

**Status:** Ready for implementation sequencing (UI visual design deferred)  
**Source decisions:** [`build-history-grilling-2026-07-18.md`](build-history-grilling-2026-07-18.md)  
**Related:** [`cloud-build-prd.md`](cloud-build-prd.md), [`architecture.md`](architecture.md)  
**UI:** Compose + `:designsystem` — History list / detail / nested progress; visual polish later

---

## Problem Statement

Cloud builds already keep running after the user leaves the progress screen, but there is no place to see past or in-flight jobs, rejoin a running build, or recover tracking after the process dies. Users cannot tell what is building, what failed, or install an APK from an earlier successful job once they have navigated away.

---

## Solution

Ship **in-app build history** backed by a local Room store:

1. Persist every build job’s metadata and phase updates locally; History and Progress both read through that store.
2. Show a live History list (all projects or one project, depending on entry point).
3. Rejoin active jobs on the existing progress UI; show a terminal detail (summary + Install if the APK still exists).
4. On process start, resume tracking of non-terminal jobs that have a provider resume payload; fail interrupted jobs that never got a cloud run id.
5. Allow parallel builds; leaving progress without Cancel does not cancel the job.
6. Embed one shared History UI (`BuildScreens.History`) from Settings, Build start, and Projects overflow via each feature’s own nav-host route.

---

## User Stories

1. As a user, I want a Build history entry in Settings, so that I can see builds across all projects.
2. As a user, I want History from the Build start top bar, so that I can see builds for the current project while about to start another.
3. As a user, I want “Build history” on a project’s overflow menu, so that I can open that project’s history without starting a build.
4. As a user, I want History scoped by entry (Settings = all; project contexts = that project), so that I am not forced to filter on-screen in v1.
5. As a user, I want each history row to show project name, phase/status, and a relative time, so that I can scan jobs quickly.
6. As a user, I want the History list to update live while jobs run, so that I do not need to refresh manually.
8. As a user, I want tapping an active job to open the progress screen for that job, so that I can watch phases, cancel, or install when ready.
9. As a user, I want tapping a terminal job to open a detail summary, so that I can see outcome, error, times, and optional log link.
10. As a user, I want Install from terminal detail when the APK file/URI still exists, so that I can install without rebuilding.
11. As a user, I want a clear state when the APK is missing, so that I know Install is unavailable.
12. As a user, I want to delete a history row from row overflow with confirmation, so that I can clean up the list.
13. As a user, I want deleting an active row to cancel the build and remove the row, so that delete means “I don’t want this job.”
14. As a user, I want deleting a row to leave the APK on disk, so that Downloads are not silently removed.
15. As a user, I want builds to keep running when I leave the progress screen without Cancel, so that Back does not abort cloud work.
16. As a user, I want Back from progress (started from Start) to still exit the Build feature, so that behavior stays familiar.
17. As a user, I want to start another build while others are running, so that projects are not blocked on one job.
18. As a user, I want history to survive app process death, so that I still see past jobs after relaunch.
19. As a user, I want in-flight jobs with a cloud run id to keep updating after relaunch, so that a kill does not orphan a GitHub run in the UI.
20. As a user, I want jobs interrupted before a cloud run id existed to show Failed (interrupted), so that I am not stuck on Preparing/Uploading forever.
21. As a user, I want jobs to keep their last phase if I relaunch while logged out, so that a still-running cloud job is not falsely failed.
22. As a user, I want tracking to resume when I sign in again, so that logged-out relaunch is recoverable.
23. As a user, I want project delete to keep history rows under the stored project name, so that past builds remain inspectable.
24. As a user, I want project delete to cancel in-flight builds for that project, so that we do not keep building a removed tree.
25. As a user, I want system Back and in-app back to leave History / nested progress / detail consistently within the hosting feature’s stack, so that navigation feels normal.
26. As a user, I want an empty History state, so that a first-time user understands there are no jobs yet.
27. As an implementer, I want `BuildHistoryStore` as the public history API, so that list/detail/delete do not overload `BuildService`.
28. As an implementer, I want `BuildService` to remain the job-ops seam for Start/Progress, so that existing screens keep a stable dependency.
29. As an implementer, I want an internal coordinator behind both public APIs, so that Room writes and cancel/delete cannot cycle dependencies.
30. As an implementer, I want `observeBuild` to be store-backed, so that Progress and History share one read path.
31. As an implementer, I want opaque `providerId` + `resumeJson` on the entity, so that GitHub resume fields do not hardcode the schema to one provider.
32. As an implementer, I want `ProjectEventHooks` + `ProjectEventsListener.onProjectDeleted` after a successful project delete (`BuildService` registers the listener; not Koin `getAll()`), with isolated listener errors, so that build cancel cannot block project deletion.
33. As an implementer, I want `BuildScreens.History(projectIdFilter, onDismiss)` with nested list → progress|detail, so that each feature host only mounts one route.
34. As an implementer, I want History entity/DAO in buildapk data registered on `AslDatabase`, so that persistence matches the projects pattern.

---

## Implementation Decisions

### Architecture / modules

- **Ownership:** `:feature:buildapk` owns History UI, Room entity/DAO, `BuildService` / `BuildHistoryStore` impls, GitHub cloud path, resume.
- **Public API:**
  - `BuildService` — `startBuild` / `cancelBuild` / `cancelBuildsForProject` / `observeBuild` (job ops).
  - `BuildHistoryStore` — `observeHistory(projectId?)`, `observeJob` / `getJob`, `delete(jobId)` (cancel if active, remove row, keep APK).
  - `BuildScreens.History(projectIdFilter: String?, onDismiss)` — nested navigation List → Progress | Detail.
- **Internals:** `DefaultBuildService` owns job lifecycle via injected `BuildJobRepository` + `BuildEngine` (Room/GitHub adapters bound in DI). `DefaultBuildHistoryStore` + `BuildHistoryEventHooks` (service registers cancel-on-delete). No coordinator façade / separate `BuildJobLogic`.
- **Reads:** Room is SoT. Service writes each phase update. `observeBuild` maps Room job → `BuildProgress`.
- **DI product path:** `BuildService` + `BuildHistoryStore` + Room + GitHub. No `FakeBuildService` product path; tests mock the public APIs.
- **Projects hook:** `ProjectEventHooks` + `ProjectEventsListener` in projects api; `DefaultBuildService` injects hooks and registers cancel-on-delete (not Koin `getAll()`); `deleteProject` succeeds first, then notifies; exceptions isolated per listener.
- **Hosts:** Settings, Build, and Projects each add a History route in their nav host and embed `BuildScreens.History` (Settings filter `null`; others pass project id).

### Persistence

- Entity/DAO in `:feature:buildapk:data`; register on `AslDatabase` (version bump) via `:integration:database`.
- Persist at least: jobId, project id/name, phase, timestamps, message/error, apkLocalPath, logUrl, providerName, `providerId`, `resumeJson`.
- Retention: until user deletes (no auto-cap in v1).
- Resume payload: opaque JSON owned by the runner (GitHub: repo, runId, release id/tag, …).

### Resume

- Eager on `BuildService` singleton init (process start).
- Non-terminal **with** usable resume payload → re-attach and keep updating.
- Non-terminal **without** run id (Preparing/Uploading interrupted) → Failed (interrupted); do not auto-restart upload.
- Missing token → keep last phase; resume when token available again.
- ReadyToInstall / Failed / Cancelled are terminal for cloud tracking.

### Navigation / UX behavior (not visual design)

| Entry | Scope |
| --- | --- |
| Settings | All projects |
| Build start top-bar | Current project |
| Project overflow | That project |

- Nested History stack owns Progress/Detail; feature hosts only the History root.
- Back from Progress after Start: still dismisses whole Build feature (job keeps running).
- Delete: row overflow + confirm; active delete = cancel + remove.
- Concurrent starts always allowed; no warning.
- No system notifications in v1.

### Primary UI (behavior)

| Surface | Role |
| --- | --- |
| History list | Compact rows; overflow Delete; live updates |
| Progress (from History) | Existing build progress capabilities for that jobId |
| Terminal detail | Summary + optional log + Install if APK exists |
| Empty list | Clear empty state |

Visual layout / Figma pass is **deferred**; wire behavior and copy to designsystem patterns when implementing.

### Testing Decisions

- Prefer external behavior at `BuildService`, `BuildHistoryStore`, and `ProjectEventHooks` notification after delete — not Room/runner internals.
- Mock public APIs in unit tests; no FakeBuildService product double required for this feature.
- Device/emulator: start build → leave progress → see job in History; kill app mid-build with run id → relaunch → phase continues; delete active row cancels; project delete cancels in-flight and keeps rows; Install from detail when APK present; system Back within History nested stack and hosting feature.
- Prior art: buildapk progress presentation; projects delete confirm; Room via projects data.

---

## Out of Scope

- System notifications for build progress/completion (explicit future)
- On-screen project filter picker (entry scope only in v1)
- Auto-cap / time-based history purge
- Deleting APK files when deleting history rows
- Visual design / Figma polish for History (behavior first)
- Second cloud provider product UI (schema stays provider-agnostic)
- Changing GitHub sandbox / workflow contract from cloud-build PRD

---

## Further Notes

- Grilling file is the decision log; this PRD is the implementation-facing contract.
- Update `cloud-build-prd.md` out-of-scope: in-app history is no longer deferred there — tracked by this PRD.
- Suggested seam for tickets: `BuildHistoryStore` + `BuildService` resume/delete behavior first; then `BuildScreens.History` + host routes; then runtime `ProjectEventHooks` wiring.
