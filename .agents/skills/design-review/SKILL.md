---
name: design-review
description: >-
  Device-truth design review against Figma. Screenshot comparison of running app
  UI vs Figma reference is required. Fix gaps, re-capture, post Before|Figma|After.
  Use when finishing UI work, before merge (Finish reviews), comparing screens to
  Figma, or the user asks for a design review.
---

# Design review

Run this for any UI-facing change before calling the work finished. Prefer this skill over ad-hoc screenshot comments.

**Required:** screenshot comparison between the **running app UI** and the **Figma design reference** is mandatory — not optional, not “code looks fine,” not Compose previews alone. Every relevant screen/state must have side-by-side app shot vs Figma (and after fixes, Before | Figma | After).

**Default Figma (this repo):** [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite) — also linked from `project/requierments.md` / `project/architecture.md`. Prefer `:designsystem` tokens/components when fixing gaps.

## Design review — steps

1. Pull the source of truth — Figma frames for the feature (screenshots + design context), not code alone.
2. Capture the live app — install/run on emulator/device; take real screenshots of each relevant state (empty, filled, dialogs, menus, edge cases).
3. **Compare screenshots** — for each screen/state, place app UI next to the Figma reference and visually diff (layout, spacing, colors, typography, copy, icons, system chrome/insets). Do not skip this step.
4. Fix gaps in code — prefer design-system tokens/components; include system bars (enableEdgeToEdge + insets/scrims).
5. Re-capture after — same states, same device framing; compare again to Figma.
6. Publish evidence outside the repo — upload before/after (and Figma exports if needed) to a temporary release/gist; do not commit screenshots.
7. Post a PR comment — one section per issue; equal-width table: Before | Figma | After; short note of what was wrong and what changed; link Figma frames; note the fix commit.
8. Clean up — delete local screenshot dirs; optional: delete the temp release after review.

## Principles

**Screenshot comparison is necessary** — pass/fail is decided by app pixels vs Figma pixels, not by reading Compose or “I matched the tokens in code.”
Device truth over code review — judge pixels from the running app, not Compose previews alone.
Figma is the contract — match copy, hierarchy, colors, affordances (e.g. ⋮), and dialog button styles.
System UI counts — status/nav bars and insets are part of the design review.
Evidence stays out of git — screenshots live on the PR comment (hosted assets), not in the tree.
Side-by-side layout — equal-width columns so Before / Figma / After are comparable at a glance.
One issue per section — short diagnosis + fix; don’t dump a wall of images.
Durable hosting for app shots — GitHub release assets (or similar); avoid relying only on ephemeral Figma MCP URLs when possible.
Scope the comment to design — architecture/nav notes can be a short “also” list, not mixed into every image row.

## PR comment shape

For each design issue:

```markdown
### <short issue title>

| Before | Figma | After |
| :---: | :---: | :---: |
| <img width="240" alt="before" src="HOSTED_BEFORE_URL" /> | <img width="240" alt="figma" src="HOSTED_FIGMA_URL" /> | <img width="240" alt="after" src="HOSTED_AFTER_URL" /> |

**Wrong:** …
**Changed:** … (commit `abc1234`)
**Figma:** <frame link>
```

Use equal `width` (or equivalent) so columns stay comparable. Keep architecture/nav notes in a short **Also** list at the end of the comment, not inside each image row.

## Tooling hints

- Figma: MCP `get_screenshot` / `get_design_context` (or export) for frames; re-host durable copies when posting.
- App: install debug APK on emulator/device; capture with `adb exec-out screencap -p` (or equivalent) into a **local temp dir outside the repo** (or a gitignored path you delete in Clean up).
- Hosting: `gh release upload` to a temporary prerelease tag, or a secret gist with raw URLs — never `git add` screenshots.
- After fixes: push via the repo’s Git worktree / shared-branch rules in `AGENTS.md`, then post the PR comment.
