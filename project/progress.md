# Android Studio Lite — Progress (brief)

**As of:** 2026-07-14 · **Track:** cloud build (post-v0.1)

On-device Compose IDE: manage projects → browse/edit files → Run → build → install APK.

## Status: real GitHub Actions builds shipped

| Area | Status | Notes |
| --- | --- | --- |
| Architecture + DS | Done | `#1`, `#3`, `#6` · `project/architecture.md`, `:designsystem` |
| Projects | Done | `#7` · create / list / delete · empty Compose template |
| Files | Done | `#8` · sandbox browser · create / rename / move / delete |
| Editor | Done | `#9` · save · dirty leave · auto-save preference |
| Fake build → install | Done | `#10` / [PR #16](https://github.com/Al-Qassim/Android-Studio-Lite/pull/16) · superseded on product path by `#25` |
| IDE nav wiring | Done | `#11` · `:integration:navigation` `IdeNavHost` |
| Parent plan | Done | `#5` · `project/architecture.md` · `project/v0.1-implementation-plan.md` |
| Cloud-build PRD + design | Done | `#19`–`#21` · `project/cloud-build-prd.md` · Figma Connect / Settings / Onboarding / Run & build |
| Connect + build UI | Done | `#22` + `#24` / [PR #26](https://github.com/Al-Qassim/Android-Studio-Lite/pull/26) |
| Onboarding + real GHA | Done | `#23` + `#25` / [PR #28](https://github.com/Al-Qassim/Android-Studio-Lite/pull/28) · public sandbox; private choice backlog `#27` |

## Product path (shipping)

**Onboarding (first launch)** → **Projects** → **Files** → **Editor** → **Run** → **Build** (Connect gate if needed) → GitHub Actions → **Install app**.

## Not done yet

- Optional private build sandbox — `#27` (Backlog)
- Git, AI, syntax highlighting
- User Documents storage; Gradle wrapper jars in generated projects

## Useful links

- UI: `:designsystem` + Compose — `docs/design-system.md` (Figma track archived: `archive/figma/`)
- Architecture: `project/architecture.md`
- Cloud build PRD: `project/cloud-build-prd.md`
