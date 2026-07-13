# Designing in Figma — Android Studio Lite

**Mandatory** whenever anyone (human or agent) is asked to make or change a design in Figma for this project. Use the `/figma-design` skill — read this file fully before editing Figma.

How to add or update UI in the [ASL Figma file](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite) so flow pages stay consistent with the Design System and with Compose.

Use this when creating a new feature page (e.g. **Run & build**), extending an existing flow page, or fixing mockups that look “empty”, washed-out, or out of sync with code.

---

## Source of truth

| Layer | Where | Role |
| --- | --- | --- |
| Tokens & components | **Design System** page | Colors, type, icons, buttons, top bars, menus |
| Happy-path screens | **Main Screens** | Product overview (may lag real features) |
| Feature flows | **Projects management**, **Files editor**, **Loading & error states**, **Run & build**, … | Cases, steps, notes — the contract for implementation |
| Compose | `:designsystem` + feature modules | What ships; Figma should match real UI, not demos |

**File:** `M2LGyXHC5YYJekr3Fq3oiP`

Before drawing anything new, open a strong existing page (**Projects management** or **Files editor**) and match its density, phone size, dark theme, and note style.

---

## Page structure (flow pages)

Follow the same skeleton as Projects management / Run & build:

1. **Title** + one-line subtitle  
2. **Flows index** — short numbered list of cases on the page  
3. **Case headers** — `1 · …`, `2 · …` with a one-line description  
4. **Phone mockups** in a row (and arrows `→` when order matters)  
5. **Notes under each phone** — title + subtitle, inside a **box**  
6. Optional later cases (outcomes, errors) — same pattern

Keep the canvas dark (`#0F1217`-class). Phones use ASL bg (`#12171C`).

### Phone size

| Page type | Size | Examples |
| --- | --- | --- |
| Flow / cases | **240 × 420** | Projects management, Files editor, Run & build |
| Dense state gallery | **320 × 640** | Loading & error states (acceptable there only) |

On a mixed flow page, **do not** mix 240×420 entry phones with 320×640 build phones — it looks sparse and inconsistent. Prefer **240 × 420** everywhere on flow pages.

---

## Reuse the Design System — do not invent UI

### Always instance these (do not redraw)

From the **Design System → Components** section (promoted to real Components where needed):

- **Buttons:** `Button / primary`, `Button / secondary` (and other variants as needed)  
- **Chrome:** `Status bar`, `TopBar / back+title`, `TopBar / title+action`, path/editor bars  
- **Icons:** e.g. `Icon / run` (`svg/run`) — never substitute `▶` text or a hand-drawn triangle  
- **Menus / cards / fields / dialogs** when the screen needs them  

### How to place buttons

1. Create an **instance** of `Button / primary` or `Button / secondary`.  
2. Override the label text only (`Cancel`, `Install app`, `Retry`, …).  
3. Prefer not resizing instances unless the 240-wide phone forces a slight scale; avoid homemade rounded rects + text.

### Icons (example: Run)

- Editor top bar Run = **ghost** control + **`Icon / run`** tinted **Primary** (`#38B873`) — matches `TopBarEditorMore` + `IconRun` in Compose.  
- Do **not** use a green filled chip with a black play glyph unless the Design System / Compose `IconButton` variant actually does that.  
- Clone or instance from **Design System → Icons**, then tint strokes to the token color.

If a Design System control is still a plain Frame (not a Component), **promote it to a Component on the Design System page first**, then instance it on flow pages so updates stay linked.

---

## Design for the real product, not the fake

Mockups should show the **real UX** users will get when the feature is honest/production-shaped:

- **Do not** put v0.1 / “demo APK” / “not from your sources” banners on screens. Demo honesty can live in notes or docs, not in the phone chrome.  
- Prefer real-case copy: e.g. `Waiting in queue…`, `Uploading project sources…`, `Building APK remotely…`, `Downloading APK…`, `APK ready to install`.  
- **Name the build provider on phones.** UI is fed provider name/logo from the API (not hardcoded forever in Compose), but mockups must show the **concrete current provider** — today that is **GitHub** (`Connect GitHub`, `github.com/login/device`, `via GitHub`). Do **not** leave vague “your provider” / “cloud account” wording on user-visible phone chrome when GitHub is the only shipping provider.  
- Labels like **Install app** (not “Install demo APK”) unless the shipping UI truly says otherwise.  
- Skip system-installer handoff screens — after **Install app**, Android’s package UI takes over; don’t mock that as case `4a`.

---

## Progress & status patterns

For multi-step progress (build, sync, long jobs):

| State | Indicator |
| --- | --- |
| Complete | Green check (Design System success treatment) |
| **In progress** | **Rotating / animated dots (`•••`)** next to the step — **not** a solid green circle |
| Upcoming | Muted empty/disabled dot |
| Failed step | Danger **✕** + step label in danger color |

Failed outcomes should **keep the steps box**, mark which step failed, and show a **clear error message** (what failed and why).  

Cancelled can be minimal (message + back); **no** obligatory primary action if dismiss-via-back is enough.

---

## Frames, rectangles, and canvas clutter

Figma draws **frame names** on the canvas. That caused most of the “ghosting / overlap” problems on Run & build.

### Rules

1. **Phone shell** — one outer frame (for clip + size) is enough. Prefer blank name `" "` so the canvas doesn’t repeat `2a · Queued` next to a text label.  
2. **Inside the phone** — prefer **rectangles + text** (and DS **instances**) over nested named frames (`Status`, `Body`, `PhaseList`, …). Nested frames litter titles and often get **default white fills** (washed-out phones).  
3. **Structural auto-layout frames** — if you must use them, set **`fills = []`** immediately. Never leave the default white fill.  
4. **Labels** — put case labels (`2a · Queued`) as **page text above** the phone. Do **not** also name the phone frame the same string (double title).  
5. **Notes** — title + subtitle text sitting in a **rounded rect box** (surface + light border), same as other flow pages. Don’t leave bare floating captions only.

### White-fill checklist

If a phone looks light on a dark page, inspect children: `Status` / `TopBar` / `Body` with `fills: white` is the usual bug. Clear fills or rebuild with rectangles.

---

## Align Figma with Compose

Before locking a screen:

1. Read the real composable (e.g. `BuildProgressContent`, `TopBarEditorMore`, `ProjectMenu`).  
2. Match **structure** (back title bar vs cancel-in-top-bar), **actions**, **phase names**, and **icons**.  
3. Prefer Design System + `:designsystem` names over Main Screens copy if Main Screens is outdated.  
4. **Obvious actions are icon-only** (copy, more, back, run): DS `IconButton` / icon — no “Copy”/“More” text beside an icon that already means that.  
5. After Figma changes that affect shipping UI, expect a **design-review** pass (device screenshots vs Figma) — see `.agents/skills/design-review/SKILL.md`.

---

## Working with agents / MCP

When an agent edits Figma:

1. Load **figma-use** (and **figma-generate-design** when assembling screens from the DS).  
2. **`search_design_system` / list Design System Components** before drawing buttons or icons.  
3. Screenshot after each meaningful pass; compare to Projects management / Files editor, not only to the previous broken frame.  
4. One page switch per `use_figma` call; return created/mutated node IDs.  
5. After layout work, audit for: duplicate labels, named frames, white fills, homemade buttons, demo banners, missing note boxes.
6. **Layout sanity (catch these in the screenshot pass):**
   - **Flows index** auto-layout must `primaryAxisSizingMode = 'AUTO'` (hug content). A FIXED ~10px-tall index box means the list is clipped/invisible.
   - **Case subtitles** under side-by-side phones must be **≤ phone column width (~240)** — never leave 500px-wide text that overlaps the next case.
   - **Main action on a phone** = DS **`Button / primary`**. Do not use a homemade chip/rect for the primary CTA (e.g. “Open device page”).
   - **Text must stay inside its card/box.** Do not paste long body copy into a small surface card; use a short subtitle (or grow the card). After text edits, check that `text.y + text.height` ≤ card bottom.
7. **Before calling done:** take a fresh screenshot of the finished design, evaluate it, and improve anything that fails the checklist or looks worse than Projects management / Files editor. Repeat until the screenshot looks right.

---

## Quick checklist (new flow page)

- [ ] Cloned density/size from Projects management or Files editor (240×420)  
- [ ] Flows index + case headers + notes in boxes  
- [ ] Flows index height hugs content (not collapsed/clipped)  
- [ ] Case subtitles do not overlap adjacent columns (≤ ~240 wide)  
- [ ] Buttons / top bars / status / icons are **DS instances**; main CTA is **primary**  
- [ ] Run (and other icons) use **Icon / …** from Design System, correct tint  
- [ ] Phone copy names the concrete provider (**GitHub** today) — not vague “provider” / “cloud account” only  
- [ ] Instructional screens stay short (two short lines + primary CTA; no triple-repeated instructions)  
- [ ] Obvious actions (copy / more / back / run) are **icon-only** — no redundant text label on the control  
- [ ] Text inside cards/boxes does not overflow the card bounds  
- [ ] In-progress = dots; complete = check; failed step = ✕ + error copy  
- [ ] No nested frame title clutter; phone frame name blank if labeled in text  
- [ ] No white fills on structural frames  
- [ ] Copy and structure match Compose (or an explicit ADR if Figma leads)  
- [ ] **Screenshot finish pass:** capture the full page (and a phone close-up); evaluate vs Projects management / Files editor; fix gaps; re-screenshot until it looks right — do not skip 

---

## Related

- Figma file: [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite)  
- Design review against device: `.agents/skills/design-review/SKILL.md`  
- Compose screen conventions: `docs/agents/screen-context.md`  
- Requirements / architecture: `project/requierments.md`, `project/architecture.md`
