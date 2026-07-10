---
name: design-review
description: >-
  Device-truth design review against Figma for mobile/Compose UI. Pull Figma
  frames, capture live app screenshots, visually diff, fix with design-system
  tokens, re-capture, host evidence outside git, and post a Before|Figma|After
  PR comment. Use when finishing UI work, comparing screens to Figma, running
  the Finish reviews design step, or when the user asks for a design review.
---

# Design review

Run this for any UI-facing change before calling the work finished. Prefer this skill over ad-hoc screenshot comments.

**Default Figma (this repo):** [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite) — also linked from `project/requierments.md` / `project/architecture.md`. Prefer `:designsystem` tokens/components when fixing gaps.

## Design review — steps

Pull the source of truth — Figma frames for the feature (screenshots + design context), not code alone.
Capture the live app — install/run on emulator/device; take real screenshots of each relevant state (empty, filled, dialogs, menus, edge cases).
Diff visually — for each screen/state: app vs Figma (layout, spacing, colors, typography, copy, icons, system chrome/insets).
Fix gaps in code — prefer design-system tokens/components; include system bars (enableEdgeToEdge + insets/scrims).
Re-capture after — same states, same device framing.
Publish evidence outside the repo — upload before/after (and Figma exports if needed) to a temporary release/gist; do not commit screenshots.
Post a PR comment — one section per issue; equal-width table: Before | Figma | After; short note of what was wrong and what changed; link Figma frames; note the fix commit.
Clean up — delete local screenshot dirs; optional: delete the temp release after review.

## Principles

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
