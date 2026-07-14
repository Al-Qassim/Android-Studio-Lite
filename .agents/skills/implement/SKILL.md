---
name: implement
description: "Implement a piece of work based on a spec or set of tickets."
disable-model-invocation: true
---

Implement the work described by the user in the spec or tickets.

Before coding UI/navigation work, read the ticket’s acceptance criteria against `docs/agents/writing-acceptance-criteria.md`. If the ticket is thin (e.g. missing system Back or designed primary affordances), **add those criteria to the issue** (comment or edit) before treating them as optional — do not silently ship toolbar-only behaviour.

**Keep app UI and Figma in sync.** When you add, change, or remove on-screen UI (controls, copy, screens, states), update the matching frames in the [ASL Figma file](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite) in the same change set (follow `/figma-design` + this project’s [`docs/figma-design.md`](../../../docs/figma-design.md)). Do not leave Compose ahead of Figma or Figma ahead of Compose unless an ADR explicitly says one leads.

**Verify before done.** After edits, run the checks that would catch the change class (Gradle sync/compile for build or Kotlin edits; install + walk the flow for UI; screenshot pass for Figma; tests for logic). If verification fails and you fix it, harden docs per `AGENTS.md` → *User correction = system error* (generalize the process — don’t only ban the one API that broke).

Use /tdd where possible, at pre-agreed seams.

Run typechecking regularly, single test files regularly, and the full test suite once at the end.

Once done, use /code-review to review the work. For Finish reviews, also follow `docs/agents/test-review.md` and the design-review skill.

Commit your work to the current branch.
