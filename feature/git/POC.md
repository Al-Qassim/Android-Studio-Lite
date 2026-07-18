# JGit SCM proof of concept (`poc/jgit-scm`)

Spike for local Git (init / status / commit / branch / clone / fetch / pull / push) via Eclipse JGit over HTTPS.

## Modules

- `:feature:git:api` — `GitService`, `GitCredentials`, `GitStatusSnapshot`
- `:feature:git:data` — `JGitGitServiceAdapter` (JGit 6.10 + `desugar_jdk_libs_nio`)
- `:feature:git:di` — Koin `gitDiModule` (wired into `:integration:di`)

No product UI yet — inject `GitService` where needed for further experiments.

## Auth

Pass OAuth access token as `GitCredentials(username, passwordOrToken)` on clone/fetch/pull/push. Do **not** put the token in the remote URL.

## Verify

```bash
# JVM (local ops)
./gradlew :feature:git:data:testDebugUnitTest

# ART on device/emulator (filesDir)
./gradlew :feature:git:data:connectedDebugAndroidTest
```

## Out of scope for this POC

SSH, LFS, submodules, hooks, IDE Git UI, replacing cloud-build zip upload.
