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

2. Do all edits and commits only inside that worktree. Do not push to `main`.
3. Push the branch to the remote, then **immediately remove the local worktree** so the human can check out that branch in the main folder:

```bash
git push -u origin HEAD
git worktree remove "../AndroidStudioLite-wt-<short-name>"
```

4. Open or update the PR from the remote branch (via `gh`) after the push. The remote branch stays; only the local worktree is removed.
5. If more edits are needed (review feedback, follow-ups), recreate the worktree on the **same** branch, edit, push, then remove it again:

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" feature/<short-name>
# ... edit, commit ...
git push
git worktree remove "../AndroidStudioLite-wt-<short-name>"
```
