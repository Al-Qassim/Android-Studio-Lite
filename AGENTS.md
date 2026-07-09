## Agent skills

### Issue tracker

GitHub Issues in this repo via `gh`; external PRs are not a triage surface. Keep linked Project board Status in sync when pushing branch/PR work. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

## Git worktrees

The main checkout (`AndroidStudioLite/`) is reserved for the human. Agents must not casually `git checkout` / switch branches there — **except** the shared-branch case below.

For any implementation or PR work:

1. **If the main checkout is already on the target branch:** work **directly in the main checkout with the human**. Do **not** switch them to `main`, and do **not** create a worktree.

```bash
# Detect shared-branch mode (run from main checkout):
git branch --show-current   # equals the branch the agent needs → stay and edit here
```

In this mode: edit, commit, and push in place; keep the human on that branch; skip worktree create/remove.

2. **Otherwise** (main checkout is on a different branch, or the target branch is not checked out here): create a dedicated worktree. Do **not** move the human off their current branch.

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" -b feature/<short-name> origin/main
```

For follow-up work on an existing branch the human is **not** on:

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" feature/<short-name>
```

3. In worktree mode: do all edits and commits only inside that worktree. Do not push to `main`.
4. In worktree mode: push the branch to the remote, then **immediately remove the local worktree** so the human can check out that branch in the main folder if they want:

```bash
git push -u origin HEAD
git worktree remove "../AndroidStudioLite-wt-<short-name>"
```

In shared-branch mode: push from the main checkout and leave the human on the branch (no worktree to remove).

5. Open or update the PR from the remote branch (via `gh`) after the push.
6. **Update the linked GitHub Issue + Project board** for that work (Status, comment with PR/branch link). See `docs/agents/issue-tracker.md` → *Project board sync*.
7. If more edits are needed (review feedback, follow-ups): if the human is already on that branch, continue in shared-branch mode; otherwise recreate a worktree, edit, push, remove the worktree, and update the ticket/board again.

## Keep open branches current with `main`

Whenever `main` advances (local commit pushed, or PR merged into `main`), **merge `origin/main` into every other open remote branch** that still has an open PR (or is actively in progress), then **push those merges to `origin`**. Local-only merges are not enough — the remote PR branches must be updated too.

After `main` is updated and pushed:

```bash
git fetch origin
# Open PR head branches on the remote (source of truth):
gh pr list --state open --json headRefName --jq '.[].headRefName' | while read -r branch; do
  [ "$branch" = "main" ] && continue
  # Skip if remote branch already contains origin/main
  if git merge-base --is-ancestor origin/main "origin/$branch" 2>/dev/null; then
    continue
  fi
  # Shared-branch mode: human is already on this branch → merge in place
  if [ "$(git branch --show-current)" = "$branch" ]; then
    git merge origin/main -m "Merge branch 'main' into $branch"
    git push
    continue
  fi
  git worktree add "../AndroidStudioLite-wt-sync-${branch//\//-}" "origin/$branch"
  git -C "../AndroidStudioLite-wt-sync-${branch//\//-}" checkout -B "$branch"
  git -C "../AndroidStudioLite-wt-sync-${branch//\//-}" merge origin/main -m "Merge branch 'main' into $branch"
  git -C "../AndroidStudioLite-wt-sync-${branch//\//-}" push -u origin "HEAD:$branch"
  git worktree remove "../AndroidStudioLite-wt-sync-${branch//\//-}"
done
```

Rules:

- Target **remote** branches (`origin/<branch>` / open PR heads). Always `git push` after a successful merge — the sync is incomplete until the remote is updated.
- Prefer **merge** (not rebase) so shared PR history stays intact.
- If a merge conflicts, stop on that branch, comment on the PR/issue with the conflict files, and leave Status/board note — do not force-push.
- If the human is already on the target branch, merge/push in the main checkout (shared-branch mode). Otherwise use a sync worktree and remove it after push.
- Skip branches whose remote tip already contains `origin/main`.

## After a PR is merged

When a PR is merged into `main`, **delete both the remote and local feature branches**. Do not leave merged branches around.

```bash
# Prefer letting GitHub delete the remote head on merge:
gh pr merge <number> --merge --delete-branch

# If the PR was already merged without deleting the branch:
BRANCH=<head-branch-name>
git fetch origin
git push origin --delete "$BRANCH" 2>/dev/null || true
# If the main checkout is on that branch, move to main first:
if [ "$(git branch --show-current)" = "$BRANCH" ]; then git checkout main; fi
git branch -d "$BRANCH" 2>/dev/null || git branch -D "$BRANCH" 2>/dev/null || true
git fetch --prune
```

Also:

- Remove any leftover worktree for that branch (`git worktree remove …`).
- Update the linked issue / Project board Status to **Done** (see `docs/agents/issue-tracker.md`).
- Then run the “Keep open branches current with `main`” sync so remaining open PR remotes pick up the merge.
