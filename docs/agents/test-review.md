# Test review

Confirm the app runs without crashing and does what the ticket/spec requires. Run this as part of Finish reviews before asking the human to merge.

## Steps

1. Build the affected modules (e.g. `./gradlew :app:assembleDebug` and any module compile tasks touched by the change).
2. Run relevant unit/instrumentation tests if they exist for the change; add or extend tests when behavior is non-trivial and untested.
3. Manually or via emulator/device: exercise the happy path and obvious failure paths for the feature (no crash on open, navigation, and primary actions).
4. Record what was run (commands + result) in the PR/issue comment. If the environment cannot run the app, say so explicitly and still do everything possible offline (compile + unit tests).

## Pass criteria

- Build succeeds for touched modules.
- No crash on the primary flows for the ticket.
- Behavior matches the issue/spec acceptance criteria (or gaps are listed for the human).
