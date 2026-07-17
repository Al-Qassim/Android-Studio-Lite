---
name: design-review
description: >-
  Device-truth design review against the in-repo design system. Screenshot the
  running app, compare to :designsystem tokens/components and intended states,
  fix gaps, re-capture, post Before|After. Use when finishing UI work, before
  merge (Finish reviews), or the user asks for a design review. Figma is archived
  — do not require Figma comparisons.
---

# Design review

Run this for any UI-facing change before calling the work finished.

**Required:** screenshots of the **running app** (emulator/device), not Compose Preview alone. Judge layout, spacing, colors, typography, copy, and insets against `:designsystem` and the ticket’s acceptance criteria.

**Figma is archived.** Do not pull Figma frames, post Figma columns, or block on Figma sync. See `docs/design-system.md` and `archive/figma/`.

**Reachability:** before asking the human for design review, ship an in-app navigation path to every screen/state under review.

## Steps

1. Read the ticket AC + `:designsystem` components/tokens used by the screen.
2. Install/run on emulator/device; capture each relevant state (empty, filled, dialogs, menus, errors).
3. Compare app pixels to DS intent (tokens, hierarchy, density) and AC — fix gaps in code.
4. Re-capture after fixes.
5. Publish evidence outside the repo (PR comment / temp release); do not commit screenshots.
6. Post a PR comment — one section per issue; Before | After table; note what changed.

## PR comment shape

```markdown
### <short issue title>

| Before | After |
| :---: | :---: |
| <img width="280" src="…" /> | <img width="280" src="…" /> |

**What was wrong:** …
**Fix:** … (`abc1234`)
```

## Related

- Contract: `docs/design-system.md`
- Structure: `/structure-feature-code`
