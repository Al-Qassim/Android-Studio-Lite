# Writing acceptance criteria (requirements)

Use this when **generating or refining** requirements — `/to-spec`, `/to-tickets`, triage agent briefs, and hand-written GitHub issues. Test review (`docs/agents/test-review.md`) verifies the same ideas on device; this doc is the **upstream** contract so tickets do not omit them.

Criteria must be **user-observable and independently checkable**. Prefer “user does X → sees Y” over “implement Z.”

## Always ask (UI / navigation / multi-step flows)

When a ticket or story ships screens, hierarchy, dialogs, menus, or multi-step actions, run this checklist **before** publishing ACs. Add an explicit criterion for every item that applies; do not assume “happy path” covers it.

**Reachability:** for each new screen, ACs must name (1) how a user opens it from the shipping app, and (2) how they re-open it after the happy path without clearing app data. If the only entry is consumed after success (e.g. login gate), include a **second entry** (Settings / account management) in the same ticket or an explicit blocker that ships first. Pair **System Back** with in-app back where nested.

### 1. Designed primary affordances

- Name the **visible** control the user is meant to use (toolbar, bottom bar, dialog button, Figma component — e.g. `MoveBar` after Move/Copy).
- ACs must not be satisfiable only via a **hidden alternate** (e.g. Paste buried under `+` while the designed bar never appears).
- If designsystem / Figma defines a component for the state, the criterion should require that component (or its equivalent copy/layout) to show.

### 2. System / platform chrome (not only in-app buttons)

- **System Back** (3-button nav and/or gesture back) must match intended in-app back/up for nested navigation (one level at a time; leave the feature only at the feature root).
- Overlays (dialogs, menus, sheets): system Back dismisses the **top** overlay first, then navigates.
- Do **not** write ACs that only mention the toolbar/chevron back. Pair them: in-app **and** system Back when both exist.
- Call out other platform controls when relevant (IME Done/Enter, share sheet, install prompts, permission dialogs).

### 3. Hierarchy and return paths

- Enter at least one nested level; return via **each** supported path (in-app back, system Back).
- At feature root, system Back leaves the feature (or exits the app only if that screen is the app root) — state which.

### 4. Empty, error, and conflict states

- Empty list / empty folder when the feature has one.
- Invalid input and name conflicts when mutations exist — user-visible feedback, no crash.

### 5. Mutating actions claimed in the story

- Each create / rename / move / copy / delete / paste / save / run named in the ticket gets its **own** criterion (or an explicit “out of scope” line).

## Anti-patterns (do not publish)

- “Browse / navigate works” with no nested + back detail.
- “Move/copy/paste works” with no mention of the designed confirm UI.
- “Back works” meaning only the in-app chevron.
- Criteria that only unit tests can satisfy while the ticket ships UI.

## Where this applies

| Process | How to use |
| --- | --- |
| `/to-spec` | User stories + Testing Decisions must cover checklist items that apply; Implementation Decisions may name designsystem components. |
| `/to-tickets` | Every UI ticket’s Acceptance criteria section must pass this checklist (or mark items out of scope). |
| `/triage` → agent brief | Same checklist before `ready-for-agent`. |
| Hand-filed issues | Same; see also `docs/agents/issue-tracker.md`. |

Device verification of these criteria is defined in `docs/agents/test-review.md`.
