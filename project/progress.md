# Android Studio Lite — Progress (brief)

**As of:** 2026-07-11 · **Track:** v0.1

On-device Compose IDE: manage projects → browse/edit files → Run → fake remote build → install a demo APK.

## Status: v0.1 loop works end-to-end

| Area | Status | Notes |
| --- | --- | --- |
| Architecture + DS | Done | `#1`, `#3`, `#6` · `project/architecture.md`, `:designsystem` |
| Projects | Done | `#7` · create / list / delete · empty Compose template |
| Files | Done | `#8` · sandbox browser · create / rename / move / delete |
| Editor | Done | `#9` · save · dirty leave · auto-save preference |
| Fake build → install | Done | `#10` / [PR #16](https://github.com/Al-Qassim/Android-Studio-Lite/pull/16) · Build start → phases → Install app |
| IDE nav wiring | Mostly done | Lived in `#16` via `:integration:navigation` · `#11` still open (wording stale: says `:integration:ide`) |
| Parent plan | Open | `#5` · grilled decisions · `project/v0.1-implementation-plan.md` |

## Product path (shipped)

**Projects** → **Files** → **Editor** → **Run** (also from project menu / files top bar) → **Build start** → **Start build** → progress → **Install app** → system installer (bundled demo APK, not from project sources).

## Not in v0.1 (by design)

- Real cloud / GitHub Actions Gradle (same `BuildService` API later)
- Git, AI, syntax highlighting
- User Documents storage; Gradle wrapper jars in generated projects

## Useful links

- Figma: [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite)
- Design how-to: `docs/figma-design.md`
- Architecture: `project/architecture.md`
