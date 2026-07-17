# Git workflow for agents

The main checkout (`AndroidStudioLite/`) is reserved for the human. Agents must not casually `git checkout` / switch branches there — **except** the shared-branch case below.

## Default: no new branches or PRs

**Do not create git branches, worktrees for new branches, or pull requests unless the user explicitly asks** (e.g. “make a branch”, “open a PR”, “ship this as a PR”).

Default loop for ordinary work:

1. Stay on the human’s current branch in the main checkout (shared-branch).
2. Edit, then **commit** (see below).
3. **Push** only when allowed (see *Commit local edits*). Never invent a feature branch just to push.
4. Do **not** run `gh pr create` (or equivalent) unless asked.

When the user later asks for a branch/PR, use *Feature branch + PR (only when asked)* below.

## Commit local edits

Do not leave repo file changes uncommitted at the end of a turn. After creating or editing files, **commit** them (see also `AGENTS.md` → *Commit after changes*).

**Local and remote stay in sync (when push is allowed)**

- On a non-`main` tracking branch: after each commit, **push** to `origin` on the same branch so local and remote match.
- On `main`: **commit locally**; **do not push** unless the user explicitly asked to land work on `main`.
- Skip commit/push only when the user said not to, or leftovers are secrets / junk that must not be committed.

## Shared-branch vs worktree

Use this when the user has named a target branch (or asked for a PR/branch). Re-detect mode before each edit round.

1. **Choose mode by where the human is right now:**
   - **Shared-branch:** main checkout is already on the target branch → work **directly in the main checkout with the human**. Do **not** switch them to `main`, and do **not** create a worktree.
   - **Worktree:** main checkout is on a different branch (or the target branch is not checked out here) → create a dedicated worktree. Do **not** move the human off their current branch.

```bash
# Detect mode (run from main checkout) before each edit round:
git branch --show-current   # equals target → shared-branch; otherwise → worktree
```

## Feature branch + PR (only when asked)

Only after an explicit user request for a branch and/or PR:

1. **Start or resume the branch:**
   - Shared-branch: edit in place on the existing checkout.
   - Worktree — new branch (**always** from `origin/main`, never from local `main`):

```bash
git fetch origin
# Local main is often ahead of origin/main. Basing a PR branch on local main
# piles those unpushed commits into the PR (thousands of unrelated lines).
git worktree add "../AndroidStudioLite-wt-<short-name>" -b feature/<short-name> origin/main
```

   - Worktree — existing branch (including review feedback / follow-ups when the human is **not** on it):

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" feature/<short-name>
```

2. Edit and commit only in the chosen place (main checkout or worktree). Do not push to `main`.
3. **Before opening a PR**, confirm the branch is only the intended delta vs `origin/main`:

```bash
git fetch origin
git log --oneline origin/main..HEAD   # only your commits
git diff --stat origin/main...HEAD    # only your files
```

   If either shows unrelated history, reset onto `origin/main` and re-apply only your commits before pushing.
4. Push, then clean up according to mode:
   - **Worktree:** push, then **immediately remove** the local worktree:

```bash
git push -u origin HEAD
git worktree remove "../AndroidStudioLite-wt-<short-name>"
```

   - **Shared-branch:** push from the main checkout and leave the human on the branch (no worktree to remove).

5. Open or update the PR from the remote branch (via `gh`) **only if the user asked for a PR**.
6. **Update the linked GitHub Issue + Project board** for that work (Status, comment with PR/branch link). See `docs/agents/issue-tracker.md` → *Project board sync*. Then, if more edits are needed later, start again at step 1 of this section (or the default loop).
7. **Before calling the work finished:** run the Finish reviews checklist in `AGENTS.md`. Fix findings, then commit/push again per the rules above. Do not mark the ticket Done until reviews pass or the human waives them.

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
# Required: delete the remote head on merge
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

Prefer enabling GitHub **Automatically delete head branches** on the repo so UI merges also delete remotes.
