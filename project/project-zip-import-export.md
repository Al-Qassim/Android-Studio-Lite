# Project zip import / export (v1)

Simple on-device backup/share for ASL projects. Owned by `:feature:projects`.

## Behavior

| Action | Entry | Result |
| --- | --- | --- |
| Export | Project overflow → **Export…** | Zip to cache (skip `build/`, `.gradle/`, etc.) + copy under `Downloads/AndroidStudioLite/` (API 29+) + system sharesheet |
| Import | Projects **+** → **Import project** | SAF zip picker → validate → register in Room + files dir → appears in list |

## Rules

- **Valid zip:** contains `settings.gradle` or `settings.gradle.kts` (root or single wrapper folder).
- **Name / package:** from `rootProject.name` and `applicationId` / `namespace`; fallbacks `ImportedProject` / `com.imported.app`.
- **Name collision:** auto-suffix ` (2)`, ` (3)`, …
- **Errors:** `AppException` UI messages; no overwrite of existing projects.
