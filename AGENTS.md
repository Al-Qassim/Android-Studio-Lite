# Agent router

1. **Orient:** read [`docs/agents/project-overview.md`](docs/agents/project-overview.md) when you need a brief picture of the product, tracker tools, or where code/docs live.
2. **Skills:** inspect available skills under `.agents/skills/*/SKILL.md` (name + `description`). For each task, **open and follow every skill whose description matches** the work. Skip the rest. Prefer `/skill-name` when you already know which one applies.
3. **Docs:** skills and overview point at longer contracts under `docs/` and `project/` — open those when a skill or the overview says to.

Do not invent alternate process paths when a matching skill or linked doc exists.

## User correction = system error

Whenever the user corrects your work, treat that as a defect in **documents, skills, or rules** — not a one-off mistake to patch only in code.

1. Find the **root cause** of why the work was wrong the first time (missing instruction, wrong instruction, ambiguous router, skill not triggered, conflicting docs, etc.).
2. **Edit** the relevant docs/skills/rules so the same failure is unlikely next time: add missing process, fix wrong process, or delete harmful/outdated process.
3. Goal: **minimize future user corrections** by hardening the system, not only the immediate artifact.
