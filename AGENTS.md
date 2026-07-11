# Agent guide

Short router only. For details, open the linked file or skill — do not duplicate rules here.

## If you face this case → read this

| Case | Read / use |
| --- | --- |
| New ticket, branch, PR, push, or follow-up edits | `docs/agents/git-workflow.md` |
| Keep open PR branches current after `main` updates | `docs/agents/git-workflow.md` → *Keep open branches current with `main`* |
| PR merged — delete remote + local branch, board Done | `docs/agents/git-workflow.md` → *After a PR is merged* |
| Issue tracker / Project board Status | `docs/agents/issue-tracker.md` |
| Writing / generating ticket ACs (UI, system Back, affordances) | `docs/agents/writing-acceptance-criteria.md` (+ `/to-tickets`, `/to-spec`, triage) |
| Triage labels | `docs/agents/triage-labels.md` |
| Domain docs layout (`CONTEXT.md`, ADRs) | `docs/agents/domain.md` |
| Feature screens / modules structure | `.agents/skills/structure-feature-code/SKILL.md` (also `.cursor/rules/`) |
| Busy Compose screens (Screen Context) | `docs/agents/screen-context.md` |
| Architecture / code review (before merge) | `/code-review` + `.agents/skills/structure-feature-code/SKILL.md` + `project/architecture.md` |
| Test review (before merge) | `docs/agents/test-review.md` |
| Design review (before merge) | `.agents/skills/design-review/SKILL.md` |
| Creating / updating Figma flow pages (any “make a design” ask) | **Required:** `docs/figma-design.md` + `.cursor/rules/figma-design.mdc` |

## Finish reviews (required before merge / Done)

Run all three; fix findings; re-push via `docs/agents/git-workflow.md`. Do not mark Done until they pass or the human waives them.

1. Architecture / code review — see table above  
2. Test review — `docs/agents/test-review.md`  
3. Design review — `.agents/skills/design-review/SKILL.md`

## Defaults

- Issues: GitHub via `gh`; external PRs are not a triage surface.
- Domain: single-context (`CONTEXT.md` + `docs/adr/`).
- Figma: [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite) — how to design in it: `docs/figma-design.md`
