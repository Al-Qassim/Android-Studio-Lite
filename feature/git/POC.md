# JGit SCM proof of concept (`poc/jgit-scm`)

Local Git via Eclipse JGit over HTTPS, with product UI for clone / pull / push / checkout / merge / view branches / rename.

## Modules

- `:feature:git:api` — `GitService`, `GitScreens`, credentials / status types
- `:feature:git:data` — `JGitGitServiceAdapter` (JGit 6.10 + `desugar_jdk_libs_nio`)
- `:feature:git:presentation` — Clone screen + Project Git screen
- `:feature:git:di` — Koin `gitDiModule`

## UI entry points

- **Projects hub `+` → Clone from GitHub** — clone + register as ASL project
- **Open project → file browser → Git** — fetch / pull / push; branches in Recent / Local / Remote (create / checkout / merge / rename / delete)

## Auth

OAuth access token from the connected build account is passed as HTTPS password for clone/pull/push. Do **not** put the token in the remote URL.

## Verify

```bash
./gradlew :feature:git:data:testDebugUnitTest
./gradlew :feature:git:data:connectedDebugAndroidTest
./gradlew :app:compileDebugKotlin
```

## Out of scope

SSH, LFS, submodules, hooks, commit/stage UI, replacing cloud-build zip upload.
