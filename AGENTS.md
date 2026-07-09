## Agent skills

### Issue tracker

GitHub Issues in this repo via `gh`; external PRs are not a triage surface. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

## Git worktrees

The main checkout (`AndroidStudioLite/`) is reserved for the human. Agents must not `git checkout` / switch branches there.

For any implementation or PR work:

1. Create a dedicated worktree + branch from `origin/main` (or the agreed base):

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" -b feature/<short-name> origin/main
```

2. Do all edits, commits, and pushes only inside that worktree.
3. Open a PR from that branch; do not push to `main`.
4. When finished, leave cleanup to the human (or remove the worktree only if asked):

```bash
git worktree remove "../AndroidStudioLite-wt-<short-name>"
```

Existing example: `../AndroidStudioLite-architecture` on `docs/architecture-v0.1`.
