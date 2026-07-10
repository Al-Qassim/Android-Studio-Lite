## Agent skills

### Issue tracker

GitHub Issues in this repo via `gh`; external PRs are not a triage surface. Keep linked Project board Status in sync when pushing branch/PR work. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

### Feature code structure

When adding or refactoring feature screens / modules, run `/structure-feature-code` (skill: `.agents/skills/structure-feature-code/SKILL.md`; also installed under `~/.cursor/skills/` for reuse in other projects). Project-specific Cursor rules under `.cursor/rules/` map the same ideas to this repo’s packages and types.

## Git worktrees

The main checkout (`AndroidStudioLite/`) is reserved for the human. Agents must not casually `git checkout` / switch branches there — **except** the shared-branch case below.

**Every** implementation, PR, review-fix, or follow-up round uses the same rules below (first push and later edits alike).

1. **Choose mode by where the human is right now** (re-check before every edit round):
   - **Shared-branch:** main checkout is already on the target branch → work **directly in the main checkout with the human**. Do **not** switch them to `main`, and do **not** create a worktree.
   - **Worktree:** main checkout is on a different branch (or the target branch is not checked out here) → create a dedicated worktree. Do **not** move the human off their current branch.

```bash
# Detect mode (run from main checkout) before each edit round:
git branch --show-current   # equals target → shared-branch; otherwise → worktree
```

2. **Start or resume the branch:**
   - Shared-branch: edit in place on the existing checkout.
   - Worktree — new branch:

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" -b feature/<short-name> origin/main
```

   - Worktree — existing branch (including review feedback / follow-ups when the human is **not** on it):

```bash
git fetch origin
git worktree add "../AndroidStudioLite-wt-<short-name>" feature/<short-name>
```

3. Edit and commit only in the chosen place (main checkout or worktree). Do not push to `main`.
4. Push, then clean up according to mode:
   - **Worktree:** push, then **immediately remove** the local worktree:

```bash
git push -u origin HEAD
git worktree remove "../AndroidStudioLite-wt-<short-name>"
```

   - **Shared-branch:** push from the main checkout and leave the human on the branch (no worktree to remove).

5. Open or update the PR from the remote branch (via `gh`) after the push.
6. **Update the linked GitHub Issue + Project board** for that work (Status, comment with PR/branch link). See `docs/agents/issue-tracker.md` → *Project board sync*. Then, if more edits are needed later, start again at step 1 (re-detect shared-branch vs worktree).
7. **Before calling the work finished** (ready for human merge / board → Done): run the full **Finish reviews** checklist below. Fix findings, then push again using steps 1–6. Do not mark the ticket Done until all three reviews pass or remaining issues are explicitly waived by the human.

## Finish reviews

Required after implementation work is “done” and before asking the human to merge. Run all three; fix issues in the same branch (shared-branch or worktree per rules above), then re-push.

### 1. Architecture / code review

Confirm the diff aligns with the intended project architecture:

- Read `project/architecture.md` (and `project/v0.1-implementation-plan.md` when relevant).
- Check module boundaries (`api` / `impl` or `model` / `api` / `data` / `presentation` / `di`), dependency direction, and that `:app` stays thin.
- Prefer `/code-review` against `main` (Standards + Spec), with architecture docs as the Spec/standards source for structure.
- Fix architecture violations before merge; do not leave “follow-up later” for boundary leaks.

### 2. Test review

Confirm the app runs without crashing and does what the ticket/spec requires:

- Build the affected modules (e.g. `./gradlew :app:assembleDebug` and any module compile tasks touched by the change).
- Run relevant unit/instrumentation tests if they exist for the change; add or extend tests when behavior is non-trivial and untested.
- Manually or via emulator/device: exercise the happy path and obvious failure paths for the feature (no crash on open, navigation, and primary actions).
- Record what was run (commands + result) in the PR/issue comment. If the environment cannot run the app, say so explicitly and still do everything possible offline (compile + unit tests).

### 3. Design review

Run `/design-review` (skill: `.agents/skills/design-review/SKILL.md`). Do not skip to code-only judgment.

**Default Figma:** [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite)

**Design review — steps**

Pull the source of truth — Figma frames for the feature (screenshots + design context), not code alone.
Capture the live app — install/run on emulator/device; take real screenshots of each relevant state (empty, filled, dialogs, menus, edge cases).
Diff visually — for each screen/state: app vs Figma (layout, spacing, colors, typography, copy, icons, system chrome/insets).
Fix gaps in code — prefer design-system tokens/components; include system bars (enableEdgeToEdge + insets/scrims).
Re-capture after — same states, same device framing.
Publish evidence outside the repo — upload before/after (and Figma exports if needed) to a temporary release/gist; do not commit screenshots.
Post a PR comment — one section per issue; equal-width table: Before | Figma | After; short note of what was wrong and what changed; link Figma frames; note the fix commit.
Clean up — delete local screenshot dirs; optional: delete the temp release after review.

**Principles**

Device truth over code review — judge pixels from the running app, not Compose previews alone.
Figma is the contract — match copy, hierarchy, colors, affordances (e.g. ⋮), and dialog button styles.
System UI counts — status/nav bars and insets are part of the design review.
Evidence stays out of git — screenshots live on the PR comment (hosted assets), not in the tree.
Side-by-side layout — equal-width columns so Before / Figma / After are comparable at a glance.
One issue per section — short diagnosis + fix; don’t dump a wall of images.
Durable hosting for app shots — GitHub release assets (or similar); avoid relying only on ephemeral Figma MCP URLs when possible.
Scope the comment to design — architecture/nav notes can be a short “also” list, not mixed into every image row.

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
