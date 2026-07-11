# Cloud build via GitHub Actions — grilling notes

**Date:** 2026-07-11  
**Status:** Decisions locked in grill; PRD / architecture.md / implementation plan still to be split out later.  
**Context:** Real cloud APK builds on the user’s GitHub account, low friction (no manual repo-per-project), after v0.1 fake `BuildService`.

---

## Goal (from discussion)

- User logs into **their** GitHub account; builds consume **their** Actions minutes.
- No requiring the user to create a repo for each ASL project.
- UI should work with **any** build provider (dynamic provider name/logo); GitHub is the first data implementation.
- Sequence: **generalize design + app UI first**, then swap Koin bind to a GitHub-backed `BuildService` (replace fake in production).

---

## Questions & conclusions

### Q1 — Where does the Actions job live?

**Options:** (A) one hidden build repo per user · (B) one repo per ASL project · (C) user picks a repo · (D) product-owned runners  

**Conclusion: A** — One auto-created private sandbox repo per GitHub user, reused for all ASL projects. User never manually creates a project repo for builds.

---

### Q2 — How does each build’s source get into that repo?

**Options:** (A) Git Data API commits · (B) JGit push · (C) zip drop · (D) force-push single branch  

**Conclusion: C / C1** — Zip the project → upload as a **GitHub Release asset** → run workflow → **delete the zip** (cleanup). Not committing the full tree as normal source history for each build.

---

### Q3 — Release lifecycle (how releases map to builds)

**Options:** (A) one Release per build · (B) one long-lived Release · (C) draft-only variants  

**Conclusion: A** — Ephemeral release per job (`asl-build-<jobId>`). **Concurrent Runs supported.**

---

### Q4 — How does the workflow start for each Run?

**Options:** (A) `release` published event · (B) `workflow_dispatch` · (C) both  

**Conclusion: B** — App calls `workflow_dispatch` and passes **which release** was created (tag / release id as input).

---

### Q5 — How does the built APK get back to the phone?

**Options:** (A) Actions artifact · (B) attach APK to the same Release · (C) both  

**Conclusion: B** — Workflow uploads the APK as another asset on the same per-job Release; phone downloads it. (Workflow needs `contents: write` in YAML for release asset upload — **no extra per-build user permission click**; configured in the sandbox workflow the app installs.)

---

### Q6 — How does the user “log in with GitHub”?

**Options:** (A) OAuth App · (B) GitHub App · (C) pasted PAT  

**Conclusion: A** — GitHub **OAuth App**.

---

### Q7 — Where does the OAuth token exchange happen? (refined)

Research note: GitHub supports PKCE (2025) but **still documents `client_secret` as required** for the web authorization-code token exchange and does **not** treat public vs confidential clients separately. “PKCE-only, no secret in APK” is **not** a clean GitHub web-flow option.

**Refined options:** (A1) device flow · (A2) Custom Tab + secret in APK · (B) tiny backend  

**Conclusion: A1 / device flow** — ASL shows a user code + clear UI instructions; user authorizes at `github.com/login/device`. **No client secret in the APK.** Enable device flow on the OAuth App.

---

### Q8 — When do we require GitHub login?

**Options:** (A) first Run only · (B) settings before Run · (C) required at first launch  

**Conclusion: All three surfaces, one shared login process**

- **Onboarding:** optional “Connect” (skip allowed).
- **Settings:** GitHub / build-account section — login / logout.
- **Build start:** Start build disabled when logged out; show Login (same flow).
- No duplicated login implementations.

---

### Q9 — When do we create the private sandbox repo?

**Options:** (A) right after login · (B) lazily on first Start build · (C) both  

**Conclusion: B** — Part of a **Preparing** step in the build process (`ensureSandbox`).

---

### Q10 — How does the runner get a Gradle toolchain?

**Options:** (A) workflow installs/uses Gradle · (B) inject wrapper at zip time · (C) put wrapper jars in project template  

**Conclusion: A** — Workflow owns JDK / Android SDK / Gradle (e.g. setup actions). Zip stays template sources **without** `gradle-wrapper.jar` (aligned with current v0.1 template).

---

### Q11 — How do real steps map to `BuildPhase`?

**Options:** (A) add `Preparing` · (B) fold into Uploading · (C) fold into Queued  

**Conclusion: A**

`Preparing` → `Uploading` → `Queued` → `Building` → `Downloading` → `ReadyToInstall`  
(+ `Failed` / `Cancelled`)

---

### Q12 — Cleanup of the per-job Release

**Options:** (A) delete after phone has APK · (B) keep forever · (C) delete zip only, keep APK releases  

**Conclusion: A** — GitHub is a **build station only**; build history may live **in the app later**, not as GitHub release history. After APK is local (or on failure/cancel): delete release/tag (best-effort). Optional sweep of stale `asl-build-*` releases.

---

### Q13 — When Gradle fails, what does the user see?

**Options:** (A) short in-app summary + log tail · (B) full CI log in app · (C) generic message + link to workflow on GitHub  

**Conclusion: C** — Generic in-app failure + open workflow run on github.com (for now). Later generalized as optional **log URL** from provider data so UI isn’t hardcoded to GitHub.

---

### Q14 — OAuth scopes

**Options:** (A) `repo` + `workflow` · (B) `public_repo` + `workflow` · (C) GitHub App later for least privilege  

**Conclusion: A** — `repo` + `workflow`. Consent copy should be honest (`repo` is broad). GitHub App = future least-privilege upgrade.

---

### Q15 — Sandbox repo identity

**Options:** (A) `asl-builds` · (B) longer fixed name · (C) random per install  

**Conclusion: B** — Fixed clear name such as **`asl-builds-android-studio-lite`** (private). Include **README**, **app version** marker, and **workflow check**. Don’t overwrite an unrelated pre-existing repo with that name — fail clearly.

---

### Q16 — Sandbox from an older ASL version

**Options:** (A) upgrade in place · (B) ask user to delete · (C) new versioned repo  

**Conclusion: A** — Preparing updates known files only (workflow, README, version marker e.g. `.asl-sandbox.json`).

---

### Q17 — Cancel after cloud build started

**Options:** (A) best-effort end-to-end · (B) local cancel only · (C) disable Cancel after dispatch  

**Conclusion: A** — Stop polling; try to cancel Actions run; delete job release/tag; mark `Cancelled`. Best-effort only.

---

### Q18 — What goes into the project zip?

**Options:** (A) denylist · (B) zip everything · (C) allowlist template paths  

**Conclusion: Use the project’s `.gitignore`** (same effect as A). Template already ignores `.gradle`, `local.properties`, `build/`, `.idea`, etc.

---

### Q19 — Fake vs real provider shipping

**Options:** (A) replace fake when real ships · (B) real default + debug fake · (C) user-visible demo vs cloud  

**Conclusion: A**, with an explicit **sequence**:

1. Update **design + current app UI** so it works with **any** provider (current UI is the blocker).
2. Then introduce GitHub **only in data** by replacing the Koin `BuildService` bind (fake goes away in the product path).

---

### Q20 — How provider-agnostic should the UI be?

**Options:** (A) generic cloud-build language · (B) generic phases, GitHub in settings copy · (C) GitHub hardcoded in UI  

**Conclusion: A**, plus: **data/api supplies provider name and optional logo** for the UI to render dynamically (clear who the provider is, no hardcoded provider strings in Compose). Optional “view build log” only if provider supplies a URL.

---

### Q21 — Where does “Connect account” live?

**Options:** (A) on buildapk `:api` · (B) separate auth/github feature · (C) auth only inside build data  

**Conclusion: B** — Separate auth-related feature module(s), not only inside buildapk.

---

### Q22 — Auth feature shape

**Options:** (A) `:feature:auth` agnostic API+impl · (B) `:feature:github` only · (C) `:feature:auth` API + `:feature:github` as impl  

**Conclusion: C** — Agnostic **`auth:api`** (login/logout/session/provider info for UI); **`:feature:github`** as the implementation side for GitHub specifics.

---

### Q23 — How does buildapk get credentials / talk to GitHub?

**Options refined by discussion:**

- Buildapk should **call GitHub APIs directly** via `:feature:github` as if it were an **external library** that does not know ASL.
- **`auth:api` is for login/logout UI**, not a façade over Releases/Actions for build.

**Then Q24/Q25 — session ownership:**

**Conclusion:**

- **`:feature:github` is stateless** (caller passes token).
- **`auth:data` owns session/token storage** (provider-agnostic ownership of secrets).
- **`buildapk:data` reads credentials only via `auth:api`** (`observeSession` / access token), then uses **stateless github** for build operations.

---

### Q27 — Separation of `Queued` vs `Building`

**Options:** (A) map to Actions `queued` / `in_progress` · (B) merge after dispatch · (C) Queued until Gradle step  

**Conclusion: A**

| Phase | Meaning |
| --- | --- |
| **Queued** | `workflow_dispatch` done; run exists; waiting for a runner (`queued`) |
| **Building** | Run is `in_progress` until terminal completion |

---

## End-to-end flow (locked picture)

1. User connects account (onboarding optional / settings / build gate) via **device flow** → auth UI → stateless github device APIs → **token in `auth:data`**.
2. **Start build** → **Preparing**: ensure sandbox `asl-builds-android-studio-lite` (README + version + workflow; upgrade in place).
3. **Uploading**: zip via **`.gitignore`** → create release `asl-build-<jobId>` → upload zip asset.
4. **`workflow_dispatch`** with release identifier.
5. **Queued** (waiting for runner) → **Building** (`in_progress`: download zip, Gradle via workflow toolchain, upload APK to same release).
6. **Downloading** → **ReadyToInstall**; then **delete release** (build station only).
7. **Failure:** generic message + provider log URL (GitHub → workflow run on github.com).
8. **Cancel:** best-effort cancel run + delete release.

---

## Architecture sketch (locked picture)

```text
UI (provider-agnostic)
  - phases including Preparing
  - dynamic provider name/logo from API
  - Connect account / logout via auth screens
  - optional “view build log” if URL present

:feature:auth          → api + presentation for login/logout/session UI
:feature:auth:data     → owns token/session storage
:feature:github        → stateless GitHub library (device flow, REST helpers)
:feature:buildapk:data → GitHubActionsBuildService: auth credentials + github APIs
                       → Koin replaces FakeBuildService when real ships

Sequence: generalize UI/design → then data bind swap
```

---

## Explicitly deferred (document later)

- Formal PRD / GitHub issues with ACs  
- Updates to `project/architecture.md`  
- Standalone implementation plan file  
- Exact OAuth App registration, redirect/device-flow settings checklist  
- Workflow YAML details (image, SDK packages, Gradle version pins)  
- In-app build history feature  

---

## Related existing docs

- `project/architecture.md` — `BuildService` API; fake for v0.1  
- `project/v0.1-implementation-plan.md` — fake provider decisions  
- `project/progress.md` — “Real cloud / GitHub Actions” out of v0.1  
