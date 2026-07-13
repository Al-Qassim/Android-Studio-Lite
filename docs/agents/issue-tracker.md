# Issue tracker: GitHub

Issues and PRDs for this repo live as GitHub issues. Use the `gh` CLI for all operations.

## Conventions

- **Create an issue**: `gh issue create --title "..." --body "..."`. Use a heredoc for multi-line bodies.
- **Read an issue**: `gh issue view <number> --comments`, filtering comments by `jq` and also fetching labels.
- **List issues**: `gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'` with appropriate `--label` and `--state` filters.
- **Comment on an issue**: `gh issue comment <number> --body "..."`
- **Apply / remove labels**: `gh issue edit <number> --add-label "..."` / `--remove-label "..."`
- **Close**: `gh issue close <number> --comment "..."`

Infer the repo from `git remote -v` — `gh` does this automatically when run inside a clone.

## Pull requests as a triage surface

**PRs as a request surface: no.** _(Set to `yes` if this repo treats external PRs as feature requests; `/triage` reads this flag.)_

When set to `yes`, PRs run through the same labels and states as issues, using the `gh pr` equivalents:

- **Read a PR**: `gh pr view <number> --comments` and `gh pr diff <number>` for the diff.
- **List external PRs for triage**: `gh pr list --state open --json number,title,body,labels,author,authorAssociation,comments` then keep only `authorAssociation` of `CONTRIBUTOR`, `FIRST_TIME_CONTRIBUTOR`, or `NONE` (drop `OWNER`/`MEMBER`/`COLLABORATOR`).
- **Comment / label / close**: `gh pr comment`, `gh pr edit --add-label`/`--remove-label`, `gh pr close`.

GitHub shares one number space across issues and PRs, so a bare `#42` may be either — resolve with `gh pr view 42` and fall back to `gh issue view 42`.

## When a skill says "publish to the issue tracker"

Create a GitHub issue. Also add it to this repo's GitHub Project board (see *Project board sync* below) with Status **Todo** unless the user specifies otherwise.

## When a skill says "fetch the relevant ticket"

Run `gh issue view <number> --comments`.

## Project board sync

Every branch/PR push that advances work **must** update the linked GitHub Issue(s) and their card(s) on the repo's GitHub Project board. Do not leave board Status stale while code moved.

### Auth

Project commands need `read:project` and `project` scopes. If `gh project …` fails with missing scopes, tell the human to run:

```bash
gh auth refresh -h github.com -s read:project,project
```

### Required linkage

- Every implementation PR must reference its issue (`Fixes #N` / `Closes #N` / `Refs #N` in the PR body).
- If work starts without an issue, create one first, add it to the Project, then open the PR.

### Status mapping (Project field: Status)

| Event | Set Status to |
| --- | --- |
| Idea / not ready to spec | `Backlog` |
| Needs a written PRD / grilled decisions turned into requirements | `make prd` |
| PRD ready; needs Figma / flow design (or design still in progress) | `make ui/ux design` |
| Spec + design done; agent/human can start implementation | `Ready` (closest match for “Todo” / `ready-for-agent`) |
| Agent starts work / first push of the feature branch | `In progress` |
| PR opened or updated with new commits | `In progress` (comment on the issue with the PR URL) |
| PR open and waiting on review / design-review | `In review` |
| PR merged / issue closed | `Done` |
| Blocked on human decision | `On hold` (comment on the issue explaining the blocker) |

If the board uses different option names, map to the closest match after listing field options with `gh project field-list`.

### How to update (GitHub Projects v2 via `gh`)

Resolve the project (owner `Al-Qassim`, linked to `Android-Studio-Lite`):

```bash
gh project list --owner Al-Qassim
```

Add an issue to the project (if missing):

```bash
gh project item-add <PROJECT_NUMBER> --owner Al-Qassim --url https://github.com/Al-Qassim/Android-Studio-Lite/issues/<N>
```

Set Status (get field/option IDs from `gh project field-list <PROJECT_NUMBER> --owner Al-Qassim --format json`):

```bash
gh project item-edit --project-id <PROJECT_NODE_ID> --id <ITEM_NODE_ID> --field-id <STATUS_FIELD_ID> --single-select-option-id <OPTION_ID>
```

Also leave a short issue comment when Status changes because of a push/PR:

```bash
gh issue comment <N> --body "Moved to In Progress — PR: <url>"
```

### Checklist after every push that affects a PR/branch

1. Confirm the PR body links the issue.
2. Ensure the issue is on the Project board.
3. Set Status per the table above.
4. Comment on the issue with branch/PR URL and what changed.

## Ticket acceptance criteria

When filing, refining, or generating tickets (`/to-tickets`, `/to-spec`, triage briefs, hand-written issues), follow **`docs/agents/writing-acceptance-criteria.md`**.

That checklist is the requirements-generation contract for UI/navigation work. In short:

- Name **designed primary affordances** (not hidden workarounds).
- Include **system / platform chrome** (especially system Back) paired with in-app controls for hierarchy and overlays.
- Cover nested return paths, empty/error/conflict states, and each mutating action claimed by the ticket.

Test review (`docs/agents/test-review.md`) verifies the same items on device; tickets must state them so agents know they are in scope.

## Wayfinding operations

Used by `/wayfinder`. The **map** is a single issue with **child** issues as tickets.

- **Map**: a single issue labelled `wayfinder:map`, holding the Notes / Decisions-so-far / Fog body. `gh issue create --label wayfinder:map`.
- **Child ticket**: an issue linked to the map as a GitHub sub-issue (`gh api` on the sub-issues endpoint). Where sub-issues aren't enabled, add the child to a task list in the map body and put `Part of #<map>` at the top of the child body. Labels: `wayfinder:<type>` (`research`/`prototype`/`grilling`/`task`). Once claimed, the ticket is assigned to the driving dev.
- **Blocking**: GitHub's **native issue dependencies** — the canonical, UI-visible representation. Add an edge with `gh api --method POST repos/<owner>/<repo>/issues/<child>/dependencies/blocked_by -F issue_id=<blocker-db-id>`, where `<blocker-db-id>` is the blocker's numeric **database id** (`gh api repos/<owner>/<repo>/issues/<n> --jq .id`, _not_ the `#number` or `node_id`). Use **`-F`** (typed) so `issue_id` is sent as an integer — **`-f`** sends a string and GitHub returns 422. GitHub reports `issue_dependencies_summary.blocked_by` (open blockers only — the live gate). Where dependencies aren't available, fall back to a `Blocked by: #<n>, #<n>` line at the top of the child body. A ticket is unblocked when every blocker is closed.
- **Frontier query**: list the map's open children (`gh issue list --state open`, scoped to the map's sub-issues / task list), drop any with an open blocker (`issue_dependencies_summary.blocked_by > 0`, or an open issue in the `Blocked by` line) or an assignee; first in map order wins.
- **Claim**: `gh issue edit <n> --add-assignee @me` — the session's first write.
- **Resolve**: `gh issue comment <n> --body "<answer>"`, then `gh issue close <n>`, then append a context pointer (gist + link) to the map's Decisions-so-far.
