# Project overview (agents)

Brief orientation for this repo. Deeper detail lives in the linked files.

## What this is

**Android Studio Lite** — a native Kotlin / Jetpack Compose IDE that runs on the phone: create/list projects → browse/edit files → run a (currently fake) cloud build → install an APK.

- **UI source of truth:** `:designsystem` + Compose — `docs/design-system.md` (Figma track archived: `archive/figma/`)
- **Architecture / modules:** `project/architecture.md`
- **v0.1 status:** `project/progress.md`
- **Requirements / plan:** `project/requierments.md`, `project/v0.1-implementation-plan.md`

Stack highlights: Compose, Koin, feature modules (`model` / `api` / `data` / `presentation` / `di`), `:designsystem`, `:core:error` (`AppException`).

## How work is tracked

| Tool | Role |
| --- | --- |
| **GitHub Issues** | Tickets / PRDs — use `gh` (`docs/agents/issue-tracker.md`) |
| **GitHub Project board** | Status (Todo / In Progress / Done) — sync after pushes (`docs/agents/issue-tracker.md`) |
| **Pull requests** | Code review / merge — not a triage surface for external feature requests |
| **Labels** | Triage roles — `docs/agents/triage-labels.md` |
| **GitHub Actions** | Push to `release-apk` → build release APK → attach to a GitHub Release (`.github/workflows/release-apk.yml`). Does **not** run on `main`. App `minSdk` is **26** (Android 8+); higher minSdk shows “problem parsing the package” on older phones. |

## How code is organized (pointer)

- Feature modules own in-feature nav, data, and UI; app navigator only wires cross-feature exits.
- **Implementing features:** `/implement` — requires `/structure-feature-code` (NavHost-only multi-step hosts, `logic/` not inline in Screen, flat `*UiState` with `isLoading`/`loadError`, `*Adapter` naming). Busy screens: `docs/agents/screen-context.md`.
- Git / branches / PRs for agents: `docs/agents/git-workflow.md`
