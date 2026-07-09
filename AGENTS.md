## Agent skills

### Issue tracker

GitHub Issues in this repo via `gh`; external PRs are not a triage surface. Keep linked Project board Status in sync when pushing branch/PR work. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

## Git worktrees

The main checkout (`AndroidStudioLite/`) is reserved for the human. Agents must not casually `git checkout` / switch branches there.

For any implementation or PR work:

1. **Before creating a worktree:** if the main checkout is already on the branch the agent needs (new or existing), move the human off it first — **only then**, and only to `main`:

```bash
# Run from the main checkout path, only when it is on the target branch:
git -C "/path/to/AndroidStudioLite" checkout main
```

If the main checkout is on any other branch, leave it alone.

2. Create a dedicated worktree + branch from `origin/main` (or the agreed base):

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" -b feature/<short-name> origin/main
```

For follow-up work on an existing branch:

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" feature/<short-name>
```

3. Do all edits and commits only inside that worktree. Do not push to `main`.
4. Push the branch to the remote, then **immediately remove the local worktree** so the human can check out that branch in the main folder:

```bash
git push -u origin HEAD
git worktree remove "../AndroidStudioLite-wt-<short-name>"
```

5. Open or update the PR from the remote branch (via `gh`) after the push. The remote branch stays; only the local worktree is removed.
6. **Update the linked GitHub Issue + Project board** for that work (Status, comment with PR/branch link). See `docs/agents/issue-tracker.md` → *Project board sync*.
7. If more edits are needed (review feedback, follow-ups), repeat: if the main checkout is on that branch, switch it to `main` first; recreate the worktree; edit; push; remove the worktree; update the ticket/board again.
