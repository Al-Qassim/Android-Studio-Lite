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
5. **Component handoff:** if the user says **commit** and then instructs on a *different* UI component than the last one you were fixing, that means the previous component is approved — commit/push it and move on (see `docs/design-system.md`).

## User correction = system error

Whenever the user corrects your work — **or you discover you broke something** (build, sync, design, UX, tests, docs, process) — treat that as a defect in **documents, skills, or rules**, not a one-off mistake to patch only in the immediate artifact.

1. **Review the latest work end-to-end** for the same class of mistakes (not only the one pointed-out spot). Fix everything that fails that review before moving on.
2. Find the **root cause** of why the work was wrong the first time (missing instruction, wrong instruction, ambiguous router, skill not triggered, conflicting docs, skipped verification, etc.).
3. **Generalize the error case** before editing docs. Do **not** write a narrow ban that only matches the exact failure (“don’t use API X”). Ask: *what process would have caught this and many similar mistakes?* Prefer **verification and workflow rules** that prevent whole classes of errors — across **code, Gradle/sync, design system, UX, testing, copy, tickets**, and anything else a developer would have to correct.
   - Weak (too specific): “Never call `java.util.Properties` in `build.gradle.kts`.”
   - Strong (general): “After editing build scripts or app code, run a compile/sync (and install when UI changed) before calling the work done.”
   - Same idea for design (screenshot finish pass), UX (walk the flow on device), tests (run the suite that covers the change), etc.
4. **Edit** the relevant docs/skills/rules so that **process** is mandatory next time: add missing process, fix wrong process, or delete harmful/outdated process. Specific examples may appear as tips under a general rule, but the rule itself must stay general.
5. Goal: **minimize future user corrections** by hardening prevention across every kind of work, not only the immediate bugfix.

A silent artifact-only fix (code or copy) without generalizing and updating process docs is **incomplete**.

**Figma is archived** — do not open Figma, sync to Figma, or use archived skills under `archive/figma/`. UI work goes through `:designsystem` (`docs/design-system.md`).
