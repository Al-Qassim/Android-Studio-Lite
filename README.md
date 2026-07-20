# Android Studio Lite

Open-source, phone-first IDE for Android — create and manage projects, edit files, run cloud builds, and install APKs, all on device.

## Features

- **Projects** — create, list, open, delete; import/export ZIP
- **Files** — sandboxed browser (create, rename, move, copy, delete)
- **Editor** — edit sources with save, auto-save, and wrap text
- **Build** — GitHub Actions cloud build → download APK → install
- **Build history** — past and running jobs
- **Settings** — theme (Dark / Light / Dracula), build account, about

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
- Feature modules (`model` / `api` / `data` / `presentation` / `di`) + `:designsystem`

## Docs

| Doc | What |
| --- | --- |
| [`project/architecture.md`](project/architecture.md) | Module map and dependency rules |
| [`docs/design-system.md`](docs/design-system.md) | UI tokens and Compose components |
| [`project/progress.md`](project/progress.md) | Shipping status |
| [`project/cloud-build-prd.md`](project/cloud-build-prd.md) | Cloud build product notes |
| [`docs/agents/project-overview.md`](docs/agents/project-overview.md) | Short orientation for contributors/agents |

## Contributing

Issues and pull requests are welcome on [GitHub](https://github.com/Al-Qassim/Android-Studio-Lite).
