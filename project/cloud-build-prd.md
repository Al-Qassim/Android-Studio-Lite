# PRD: Cloud build via GitHub Actions

**Status:** Ready for design sign-off / implementation sequencing  
**Parent ticket:** [#19](https://github.com/Al-Qassim/Android-Studio-Lite/issues/19)  
**Source decisions:** [`cloud-build-github-actions-grilling-2026-07-11.md`](cloud-build-github-actions-grilling-2026-07-11.md)  
**UI:** [Figma — Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite) — pages **Connect account**, **Settings · Build account**, **Onboarding · Connect**, **Run & build**

---

## Problem Statement

v0.1 “builds” use a fake `BuildService` and a bundled demo APK. Users cannot compile **their** project sources in the cloud. They need a low-friction path: sign into **their** GitHub account, run Actions on **their** minutes, and install a real APK — without manually creating a repo per ASL project.

---

## Solution

Ship real cloud APK builds on the user’s GitHub account:

1. **Connect GitHub** once via OAuth **device flow** (shared from onboarding / settings / build gate).
2. On **Start build**, the app prepares a single private sandbox repo, uploads a project zip, dispatches a workflow, downloads the APK, then cleans up the ephemeral release.
3. UI stays **provider-shaped** (name/logo and optional log URL from API). **GitHub is the first and currently only provider** — phone copy must say GitHub, not vague “provider.”

**Shipping sequence (locked):**

1. Design + app UI (auth surfaces, Preparing phase, provider chrome, view log) — works with fake/stub data first.  
2. Replace the product Koin bind with a GitHub-backed `BuildService`.

---

## User Stories

1. As a user, I want to connect my GitHub account with a device code, so that builds use my Actions minutes without pasting a PAT or embedding a client secret.
2. As a user, I want **Open GitHub** as the primary control on Connect, so that I know exactly where to authorize.
3. As a user, I want to see my user code while waiting, so that I can finish authorization on another device.
4. As a user, I want clear success with my GitHub identity shown, so that I know which account is connected.
5. As a user, I want clear failure when authorization expires or is denied, with **Try again** and **Cancel**, so that I can recover without restarting the app.
6. As a user, I want in-app back and **system Back** to leave Connect safely without a half-stored session, so that navigation feels normal.
7. As a user, I want optional Connect during onboarding with **Skip for now**, so that first launch is not blocked.
8. As a user, I want Settings → GitHub to connect or log out, so that I can manage the build account later.
9. As a user, I want Start build disabled when logged out, with **Connect GitHub** as the primary CTA, so that I am not stuck on a dead Start button.
10. As a user, I want Start build enabled when connected, so that I can begin a cloud build of the open project.
11. As a user, I want build progress to show Preparing → Uploading → Queued → Building → Downloading → Ready to install, so that I understand each stage.
12. As a user, I want the in-progress step marked with `•••`, so that status matches the product progress pattern.
13. As a user, I want to see **GitHub** (and logo if supplied) on the build UI, so that I know who is building.
14. As a user, I want to Cancel a running build, so that I can stop waiting.
15. As a user, I want a cancelled state I can dismiss via back, so that I can return to editing.
16. As a user, I want Ready to install with **Install app**, so that I can install the APK via the system installer.
17. As a user, I want a generic failure message when the cloud build fails, so that I am not dumped raw CI noise in-app.
18. As a user, I want **View build log** when a log URL exists, so that I can inspect the GitHub Actions run.
19. As a user, I want Close and Retry after failure, so that I can leave or try again.
20. As a user, I do **not** want to create a GitHub repo per ASL project, so that setup stays low friction.
21. As a user, I want concurrent builds supported, so that one project does not block another job id.
22. As an implementer, I want `auth:api` / `auth:data` to own session storage and Connect UI, so that buildapk does not own OAuth UX.
23. As an implementer, I want a stateless `:feature:github` library, so that GitHub REST/device APIs are reusable without ASL session coupling.
24. As an implementer, I want `buildapk:data` to read credentials via `auth:api` then call github helpers, so that the BuildService remains the build seam.

---

## Implementation Decisions

### Architecture / modules

- **`auth:api` + presentation** — login/logout/session UI; observe session for gates.
- **`auth:data`** — owns token/session storage (provider-agnostic ownership of secrets).
- **`:feature:github`** — stateless GitHub library (device flow + REST helpers); caller passes token.
- **`buildapk:data`** — `GitHubActionsBuildService` uses auth credentials + github APIs; Koin replaces `FakeBuildService` on the product path when real ships.
- UI must not hardcode provider strings in Compose beyond rendering API-supplied name/logo (mockups still show **GitHub** as the concrete value).

### Auth

- GitHub **OAuth App** with **device flow** enabled (no client secret in the APK).
- Scopes: `repo` + `workflow` (consent copy honest that `repo` is broad).
- Surfaces sharing one Connect process: onboarding (optional), settings, build start gate.

### Build pipeline

| Phase | Meaning |
| --- | --- |
| Preparing | `ensureSandbox` — private repo `asl-builds-android-studio-lite` (README, version marker, workflow); upgrade known files in place; fail clearly if unrelated repo exists |
| Uploading | Zip project via **`.gitignore`**; create ephemeral release `asl-build-<jobId>`; upload zip asset |
| Queued | `workflow_dispatch` done; Actions run waiting for runner |
| Building | Run `in_progress` until terminal |
| Downloading | Fetch APK from same release |
| ReadyToInstall | APK local; then delete release/tag (build station only) |
| Failed / Cancelled | Generic message; optional log URL; cancel = stop poll + best-effort cancel run + delete release |

- Workflow owns JDK / Android SDK / Gradle toolchain; zip does not need `gradle-wrapper.jar`.
- Concurrent runs supported (one release per job).
- Failure: generic in-app copy + open workflow run URL when present.

### Primary UI (Figma)

| Flow | Primary controls |
| --- | --- |
| Connect | **Open GitHub**, Cancel; waiting `•••`; Continue on success; Try again on fail |
| Onboarding | **Connect GitHub**, **Skip for now** |
| Settings | **Connect GitHub** / **Log out** |
| Build gate | Disabled Start build + **Connect GitHub** |
| Progress | Cancel while running; **Install app** when ready; **View build log** + Close + Retry on fail |

### Test seams (prefer existing)

1. **`auth:api` session** — logged in/out drives gates and Settings.  
2. **`BuildService` / build progress states** — phases including Preparing, provider info, optional `logUrl`, cancel/fail/ready.  
3. **Stateless github helpers** — device flow + release/workflow operations behind the BuildService (not a second UI path).

Avoid new top-level seams; extend these.

---

## Testing Decisions

- Prefer tests of **external behavior** at `auth:api` and `BuildService` (and UI state), not GitHub HTTP internals.
- Fake/stub BuildService must be able to emit Preparing and optional log URL so UI tickets can ship before real GHA.
- Device/emulator review per `docs/agents/test-review.md`: **run the app as a normal user**; exercise Connect, Skip, Settings logout, build gate, progress, fail → View build log, system Back paired with in-app back.
- Prior art: existing `buildapk` fake phase machine and presentation previews.

---

## Out of Scope

- In-app build history  
- Exact OAuth App registration checklist / redirect settings runbook  
- Workflow YAML pin details (image, SDK packages, Gradle versions) — own later  
- GitHub App least-privilege upgrade  
- Switching to a second cloud provider in product (API shape allows it later)  
- Manual per-project GitHub repos  

---

## Further Notes

- Grilling file remains the decision log; this PRD is the implementation-facing contract.  
- Child tickets: #20–#25 (design → connect → surfaces → build UI → GitHub BuildService).  
- Update `project/architecture.md` when auth/github modules land (module map today still lists v0.1 fake build only).
