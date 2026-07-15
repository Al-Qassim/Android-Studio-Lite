# Designing in Figma — Android Studio Lite

**Mandatory** for Figma work on this project. Process: `/figma-design`. Visual direction: `/jetbrains-new-ui`. **Ready gate:** `/design-verify` (critic ≠ author) → human accept → implement.

**File:** [Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite) (`M2LGyXHC5YYJekr3Fq3oiP`)

| Layer | Role |
| --- | --- |
| **Design System** | Shared tokens/components (default for non-pilot flows) |
| **Feature flow pages** | Cases + phones — implement contract |
| **Compose** | What ships; Figma stays in sync |

**Kit (visual oracle):** [Int UI Kit](https://www.figma.com/design/6whxXz3bbL8FG7dr83Oi4u/Int-UI-Kit--Community-) `6whxXz3bbL8FG7dr83Oi4u`

### Design System first (after kit-local pilots)

Build / Connect / Settings may be kit-local on their pages. **Before Editor / Projects / Files:** redesign the **Design System** page from Int UI Kit (`/jetbrains-new-ui`), critic PASS (zoomed, ≤5 comps/shot), human accept — then those flows instance ASL DS.

### Design System page (lean + reviewable)

**Fewer components, not the whole kit.** Promote only what ASL flows need. Delete old/unused DS components and wave leftovers (glow, duplicate buttons, dead icons).

**The DS page itself must look good** — dark canvas, clear labeled sections, consistent gaps, no scrap piles. Default white frames, random white cards, and unlabeled orphans are not “optional extras”: **delete them** or restyle into a proper dark section. If it’s on the page, it’s in scope for `/design-verify`.

**Clear sections** on the page (labeled), for example:

1. Color / type tokens  
2. Buttons (primary, secondary, danger-text)  
3. Fields / code well  
4. Chrome (status, top bars)  
5. Lists / rows / project card  
6. Progress / phases / banners  
7. Dialogs / menus  
8. Identity / chips / group header  
9. Icons (only icons we ship)

**Critic:** `/design-verify` is an **audit** — inventory every on-page unit first, Pass A cleanliness, Pass B Kit|ASL with **fileKey + nodeId** on both sides (never DS↔DS). Fail-closed: missing rows or missing kit node ids cannot PASS.

**Author self-check (Design System tokens):** Before Kit | Candidate, for every token block (color, space, radius, type) verify **title copy = body copy = sample labels = bound variable values** (one coherent scale — no stale Wave leftovers). Turn **Clip content off** on token sample rows and set the row to hug so every sample is fully visible in a zoomed shot. **Do not trust `clipsContent=false` alone:** if a child’s right/bottom edge exceeds the row (absolute `x`/`width` overflow), the zoomed shot still clips — use horizontal auto-layout (label · sample · spec) so every column fits inside the row. Sample text must be **readable on the section surface** (light on dark; never near-black `#0D140F`-class on island). Scan the full page for solid white / near-white structural fills and remove or restyle them.

---

## Page structure (flow pages)

1. Title + one-line subtitle  
2. Flows index  
3. Case headers (`1 · …`)  
4. Phones **240 × 420** (arrows when order matters)  
5. Notes under phones in a box — **never** put author/anim notes inside the phone chrome (that is a `/design-verify` FAIL)

Canvas dark (`#0F1217`-class). Prefer blank phone frame names; no white fills on structural frames; no nested frame title clutter.

**Dark phone surfaces — no solid white fills.** On dark phones, identity cards, group headers, action rows, and similar chrome must stay kit-dark (island `#2B2D30`-class, or transparent label+rule). A solid `#FFFFFF` / near-white plate on those surfaces is a `/design-verify` FAIL — match kit shelf (`Kit / Identity card`, `Kit / Group header`) and Build dark islands, not a light Material card.

**Author self-check (before Kit | Candidate):** for every phone, scan on-chrome text for `Anim:`, `kit …`, motion timings, or implementation asides — those belong only in the notes box. Also scan outcome phones for leftover progress tracks (full-bleed bars between banner and phase list). Before posting evidence, scan candidate phones for solid near-white fills on identity/headers/rows (not button label glyphs).

---

## Components

**Default:** instance ASL Design System (buttons, top bars, icons, menus). Promote missing icons from drawables/kit before inventing glyphs.

**Kit-local:** clone/instance from Int UI Kit onto the flow page; homemade controls that don’t read as kit family fail `/design-verify`.

Primary CTA reads as primary. Sibling top-bar icons match size/family. Obvious actions are icon-only.

---

## Product / copy

- No demo/v0.1 banners on phone chrome.  
- Build copy: real phase titles + short status lines matching product (`Preparing workspace…`, `Waiting in queue…`, `Uploading…` / sources line, `Building…`, `Downloading…`, `APK ready to install`). Subtitles on progress phones are user copy only — not author notes. Provider on phones = **GitHub** today; Compose stays provider-agnostic.  
- **Connect:** code + Open stay until Connected/Failed (no Waiting phone that strips actions).  
- Instructional phones: two short lines + one CTA; content+CTA one tight centered group under the top bar.  
- Settings gear on Projects top bar. Labels like **Install app**. Skip system installer as a mock case.  
- Progress: complete = check; in progress = dots (`•••`); failed = ✕ + error; keep steps on failure.

Match Compose structure/actions before locking a screen. After implement: `/design-review` (app vs Figma).

---

## Done (author)

- [ ] Kit studied; page matches contract skeleton + product rules  
- [ ] Self-check passed: no notes/Anim on phone chrome; no stray progress chrome on Ready/Failed  
- [ ] Kit \| Candidate screenshots on the issue  
- [ ] Status left at **make ui/ux design**; comment **Awaiting critic**  
- [ ] Did **not** set Ready (critic does that via `/design-verify`)

## Related

- `/figma-design` · `/jetbrains-new-ui` · `/design-verify` · `/design-review`  
- `docs/agents/screen-context.md` · `project/requierments.md`
