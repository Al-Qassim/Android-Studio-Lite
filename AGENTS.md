# Agent router

1. **Orient:** read [`docs/agents/project-overview.md`](docs/agents/project-overview.md) when you need a brief picture of the product, tracker tools, or where code/docs live.
2. **Skills:** inspect available skills under `.agents/skills/*/SKILL.md` (name + `description`). For each task, **open and follow every skill whose description matches** the work. Skip the rest. Prefer `/skill-name` when you already know which one applies.
3. **Docs:** skills and overview point at longer contracts under `docs/` and `project/` — open those when a skill or the overview says to.

Do not invent alternate process paths when a matching skill or linked doc exists.

## Commit after changes

Do **not** leave local file edits uncommitted when you finish a turn of work. **Commit is always followed by push** so local and remote branches stay in sync.

1. After you create or modify files in the repo, **commit** them, then **push** to `origin` on the same branch (follow `docs/agents/git-workflow.md`).
2. Exception: skip only if the user explicitly said not to commit/push, or the only leftovers are secrets / generated junk that must not be committed.
3. Prefer one focused commit per logical change; include the docs/skills/rules you hardened for the same correction in that commit when they belong together.
4. Do not push to `main` unless the user explicitly asked for that.

## User correction = system error

Whenever the user corrects your work, treat that as a defect in **documents, skills, or rules** — not a one-off mistake to patch only in code.

1. **Review the latest work end-to-end** for the same class of mistakes (not only the one pointed-out spot). Fix everything that fails that review before moving on.
2. Find the **root cause** of why the work was wrong the first time (missing instruction, wrong instruction, ambiguous router, skill not triggered, conflicting docs, etc.).
3. **Edit** the relevant docs/skills/rules so the same failure is unlikely next time: add missing process, fix wrong process, or delete harmful/outdated process.
4. Goal: **minimize future user corrections** by hardening the system, not only the immediate artifact.
