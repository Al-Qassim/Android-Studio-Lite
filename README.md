# Android Studio Lite

Open-source, phone-first IDE for Android — create and manage projects, edit files, run cloud builds, and install APKs, all on device.

> **Play / competition project.** This was built for a small competition. It is shared as-is and **may not receive continued support**, fixes, or feature work after the event.

Built with **[Cursor](https://cursor.com)** and **Grok 4.5**.

Most of the codebase was **reviewed by the author**. The **Git integration** work is the main exception and may be less thoroughly reviewed.

## Features

- **Projects** — create, list, open, delete; import/export ZIP
- **Files** — sandboxed browser (create, rename, move, copy, delete)
- **Editor** — edit sources with save, auto-save, wrap text, and syntax coloring
- **Git** — status, stage/commit, history, branches, remotes, merge conflict resolve (on-device via JGit)
- **Build** — GitHub Actions cloud build → download APK → install
- **Build history** — past and running jobs
- **Auth / connect** — GitHub device-flow for cloud builds
- **Settings** — theme (Dark / Light / Dracula), build account, about
- **Onboarding** — first-launch welcome and optional account connect

## Architecture (short)

The app is split into **feature modules**, each typically:

`model` · `api` · `data` · `presentation` · `di`

Cross-feature navigation lives in `:integration:navigation` (`IdeNavHost`). Features own their **inner** multi-step flows (list ↔ detail ↔ progress, etc.) as thin NavHosts. Shared UI tokens and components live in `:designsystem`.

**Busy screens** follow a Screen Context shape: a small context holds services and exits; UI and logic are extensions under `ui/` and `logic/`, with a state-only ViewModel — not one mega-Composable wiring table.

### Big screen structure

| Area | What you navigate |
| --- | --- |
| **Projects** | Project list and create/import flows |
| **Files** | File browser; Project Git (Changes / History / Branches) as an inner route |
| **Editor** | Single-file editor opened from Files or Git |
| **Build** | Start → progress → install; history list/detail |
| **Settings** | Theme, build account, about |
| **Onboarding** | Welcome → connect or skip |

Deeper module map: [`project/architecture.md`](project/architecture.md).

## Requirements

- Android **8.0+** (`minSdk` 26)
- Android Studio / JDK 11+ for building from source
- GitHub account for cloud builds (device-flow connect)

## Build & run

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Open the project in Android Studio and run the `app` configuration on a device or emulator.

Release APKs can be produced via the `release-apk` GitHub Actions workflow (see `.github/workflows/`).

## Stack

- Kotlin, Jetpack Compose, Material 3
- Koin, Room, Coroutines
- JGit (on-device Git)
- Feature modules + `:designsystem` + `:core:error`

## Docs

| Doc | What |
| --- | --- |
| [`project/architecture.md`](project/architecture.md) | Module map and dependency rules |
| [`docs/design-system.md`](docs/design-system.md) | UI tokens and Compose components |
| [`project/progress.md`](project/progress.md) | Shipping status |
| [`project/cloud-build-prd.md`](project/cloud-build-prd.md) | Cloud build product notes |
| [`docs/agents/project-overview.md`](docs/agents/project-overview.md) | Short orientation for contributors/agents |

## Contributing

Issues and pull requests are welcome on [GitHub](https://github.com/Al-Qassim/Android-Studio-Lite). Given the play/competition nature of the project, expect limited maintainer bandwidth.
