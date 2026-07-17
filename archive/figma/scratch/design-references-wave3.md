# Design references — wave 3 moodboard

**Issue:** [#38](https://github.com/Al-Qassim/Android-Studio-Lite/issues/38)  
**Goal:** Lock a visual north star before redesign. Wave 1 was too boring; wave 2 was too shiny (neon glow / over-polished).  
**Target vibe:** Modern, rich, cool dark UI that still feels like a serious IDE — clear actions, calm surfaces, sparse accents (ASL green-friendly), pleasing density. Not flat-boring, not casino-shiny.  
**Scope:** Research only. No ASL Figma edits. No Compose.

---

## Anti-patterns (explicitly out)

Do **not** use these as direction references:

| Anti-pattern | Why it fails ASL |
| --- | --- |
| **Glow-on-everything / neon accents** | Wave 2 failure mode — looks like a casino HUD, not an IDE |
| **Purple AI dashboard kits** | Generic “AI SaaS” look; fights ASL green; screams template |
| **Glassmorphism spam** | Frosted blur everywhere reduces hierarchy and readability on phone |
| **Bento / cyberpunk / neon code-art kits** | Pretty marketing, wrong product job |
| **Concept art with no real product chrome** | Hard to translate into connect / build / editor density |

When browsing Figma Community, skip files whose covers are purple gradients, particle glows, or “AI Copilot Dashboard” hero frames.

---

## Recommended shortlist (5) — human pick

Pick **2–4** of these as the locked direction (mix of live product + openable Figma):

| # | Reference | Type | Why it’s on the shortlist |
| --- | --- | --- | --- |
| 1 | **[JetBrains Fleet Islands UI](https://blog.jetbrains.com/fleet/2024/12/introducing-fleets-new-islands-ui/)** | Product | Serious IDE chrome: panel separation, calm dark, sparse accents — rich without neon |
| 2 | **[Zed](https://zed.dev/)** | Product | Cool dark, high density, restraint; modern without “dashboard AI” polish |
| 3 | **[Linear — calmer interface](https://linear.app/now/behind-the-latest-design-refresh)** | Product | Hierarchy lessons: dim chrome, content first, fewer separators — anti–wave-2 |
| 4 | **[Now In Android Case Study](https://www.figma.com/community/file/1164313362327941158/now-in-android-case-study)** (Material Design) | Figma Community | Compose-first M3 on phone: tonal surfaces, green-friendly accents, real mobile density |
| 5 | **[Tasks & Project Management App UI Kit](https://www.figma.com/community/file/1346953300150808535/tasks-project-management-app-ui-kit)** (Vlad Solomakha) | Figma Community | Linear-like calm dark **on mobile** — lists, settings, dense rows without glow |

**Honorable (if shortlist needs a sixth):** [IntelliJ Platform UI Kit](https://www.figma.com/community/file/938505862996154830/intellij-platform-ui-kit) (JetBrains) — closest “Android Studio family” component vocabulary in Figma.

---

## Product references (real apps)

### Desktop IDEs

#### 1. JetBrains Fleet — Islands UI
- **URL:** https://blog.jetbrains.com/fleet/2024/12/introducing-fleets-new-islands-ui/  
- **Also:** https://www.jetbrains.com/fleet/
- **Steal:** Island/panel separation (editor vs tools); transparent-but-legible chrome; balanced dark palette; clear focus hierarchy; restrained motion on rearrange/resize.
- **Avoid:** Shipping their optional purple theme as ASL default; copying desktop multi-panel density 1:1 onto phone.

#### 2. IntelliJ IDEA — Islands Dark / New UI
- **URL:** https://www.jetbrains.com/help/idea/user-interface-themes.html  
- **Steal:** Tool-window vs editor background contrast; “serious IDE” iconography and toolbar restraint; familiar Android Studio lineage.
- **Avoid:** Desktop clutter (too many tool windows); Darcula muddiness if it reads as Wave‑1 boring.

#### 3. Zed
- **URL:** https://zed.dev/ · appearance docs https://zed.dev/docs/appearance · theme builder https://zed.dev/theme-builder  
- **Steal:** Cool dark surfaces; pleasing editor density; sparse accent use; modern without glass glow.
- **Avoid:** Desktop-only layouts; theme-gallery maximalism (one calm system > 40 skins).

#### 4. Visual Studio Code — Dark Modern
- **URL:** https://code.visualstudio.com/ · toolkit notes in Community section below  
- **Steal:** Activity-bar / side-bar / editor hierarchy; status strip patterns; command-palette density.
- **Avoid:** Blue accent everywhere; webview “extension dashboard” looks; outdated Fluent chrome from old kits.

#### 5. Android Studio (desktop New UI)
- **URL:** JetBrains / Android Studio product pages and release notes (same Islands/New UI family as IntelliJ)  
- **Steal:** Device Manager / Run / Build status vocabulary; project tree density; “this is an Android tool” credibility.
- **Avoid:** Porting the full desktop IA; over-chrome toolbars that feel empty on phone.

### Mobile code / developer tools

#### 6. Termius (mobile SSH)
- **URL:** https://apps.apple.com/us/app/termius-modern-ssh-client/id549039908 · https://play.google.com/store/apps/details?id=com.server.auditor.ssh.client  
- **Steal:** Dark terminal-first chrome; connection lists; host cards; calm “connected / connecting / failed” states without neon.
- **Avoid:** Theme packs that lean cyberpunk; over-decorated terminal skins as UI direction.

#### 7. GitHub Mobile
- **URL:** App Store / Play “GitHub” · design system context https://primer.style/  
- **Steal:** Dense issue/PR lists; clear secondary actions; dark surfaces with restrained green/accent; settings depth.
- **Avoid:** Copying web GitHub chrome 1:1; marketing purple moments from non-product pages.

#### 8. Obsidian (mobile dark)
- **URL:** https://obsidian.md/  
- **Steal:** Calm dark reading/editing surfaces; file-tree + editor split thinking; sparse accents.
- **Avoid:** Plugin-theme chaos (Dracula neon, glow callouts) as the product look.

### Calm dark productivity

#### 9. Linear (desktop + design writing)
- **URL:** https://linear.app/now/behind-the-latest-design-refresh · earlier redesign https://linear.app/now/how-we-redesigned-the-linear-ui  
- **Steal:** “Don’t compete for attention you haven’t earned”; dimmer sidebar; fewer separators; warmer-neutral dark still crisp; sparse accent.
- **Avoid:** Copying issue-tracker IA into an IDE; purple custom themes from community theme sites.

#### 10. Raycast
- **URL:** https://www.raycast.com/  
- **Steal:** Command-first hierarchy; calm dark panels; strong primary action clarity; minimal chrome.
- **Avoid:** Desktop launcher metaphors that don’t map to ASL flows; flashy extension store cards.

### Progress / connect patterns

#### 11. Android wireless debugging / pairing (system + Studio)
- **URL:** Android developer docs for wireless debugging / pairing code flows (primary: developer.android.com)  
- **Steal:** Step clarity (code → waiting → connected/failed); one primary CTA; status without celebration fireworks.
- **Avoid:** Animated radar/glow “scanning” loops as brand identity.

#### 12. Tailscale (app connect UX)
- **URL:** https://tailscale.com/ · mobile apps on App Store / Play  
- **Steal:** Device list + connection state; calm success/error; sparse green “online” accents.
- **Avoid:** Marketing site gradients as in-app UI.

#### 13. Expo Go / Expo dashboard (build & project status — use carefully)
- **URL:** https://expo.dev/  
- **Steal:** Build/progress status chips; project cards; log-ish density when calm.
- **Avoid:** Any neon “AI build” packaging; overly playful marketing UI as IDE chrome.

---

## Public Figma Community files

Openable Community links (`figma.com/community/file/...`). Prefer **Open in Figma** from the Community page (viewable; duplicate to study). Note: some kits are old or unofficial recreations — steal structure, not pixels 1:1.

### Dark IDE / editor chrome

#### F1. Visual Studio Code Toolkit
- **Author:** Microsoft (`@microsoft`)  
- **URL:** https://www.figma.com/community/file/786632241522687494/visual-studio-code-toolkit  
- **Why for ASL:** Canonical editor chrome vocabulary (sidebar, tabs, status, buttons).  
- **Steal:** Hierarchy of activity bar → side bar → editor; restrained component states.  
- **Avoid:** Stale Fluent corners; Copilot/glow add-ons people ask for in comments; blue accent lock-in.  
- **Note:** Last updated ~5 years — use as structure reference, not pixel source of truth.

#### F2. IntelliJ Platform UI Kit
- **Author:** JetBrains (`@jetbrains`)  
- **URL:** https://www.figma.com/community/file/938505862996154830/intellij-platform-ui-kit  
- **Why for ASL:** Closest “Android Studio family” component kit in Community.  
- **Steal:** Toolbar/button/list patterns; IDE-native density; plugin-UI consistency language.  
- **Avoid:** Desktop-only spacing; aging visuals vs current Islands UI (pair with Fleet blog screenshots).  
- **Note:** Older kit — cross-check against live Islands screenshots.

#### F3. Diamond — VSCode Inspired Code Editor Design
- **Author:** Koll (`@koll`)  
- **URL:** https://www.figma.com/community/file/1247868281929098053/diamond-vscode-inspired-code-editor-design  
- **Why for ASL:** Full editor composition concepts (not only atoms).  
- **Steal:** Layout of editor + panels; dark composition balance.  
- **Avoid:** Concept-art extras; any glow/glass if present in frames; don’t treat as official VS Code.

### Android / Compose / Material

#### F4. Material 3 Design Kit
- **Author:** Material Design (`@materialdesign`)  
- **URL:** https://www.figma.com/community/file/1035203688168086460/material-3-design-kit  
- **Why for ASL:** Official Compose-first component + token baseline for Android.  
- **Steal:** Tonal surfaces; progress indicators; lists; nav; dark mode roles — map ASL green via Theme Builder.  
- **Avoid:** Default purple seed if it reads “AI template”; overusing FABs/cards for IDE chrome.  
- **Note:** Prefer enabling via Figma UI Kits / libraries for updates when possible.

#### F5. Now In Android Case Study
- **Author:** Material Design (`@materialdesign`)  
- **URL:** https://www.figma.com/community/file/1164313362327941158/now-in-android-case-study  
- **Why for ASL:** Real Compose app layouts + M3 theme story (not empty kit pages).  
- **Steal:** Mobile keyscreen density; surface treatment; accent restraint in a green-friendly M3 world.  
- **Avoid:** Content-app patterns (feeds) applied blindly to IDE; decorative gradients called out in their own comments as non-guideline experiments.  
- **Related write-up:** https://medium.com/androiddevelopers/now-in-android-a-material-3-case-study-21e44bdfd2bc

#### F6. Android Onboarding kit
- **Author:** Android Design (`@androiddesign`)  
- **URL:** https://www.figma.com/community/file/1638424504350167811/android-onboarding-kit  
- **Why for ASL:** Official-flavored onboarding / auth education journeys for Connect gate.  
- **Steal:** Step structure; educate → authenticate sequencing; Android platform tone.  
- **Avoid:** Long consumer signup wizards ASL doesn’t need; playful illustration overload.

### Calm dark productivity (Linear-like, mobile)

#### F7. Tasks & Project Management App UI Kit
- **Author:** Vlad Solomakha (`@vlad`)  
- **URL:** https://www.figma.com/community/file/1346953300150808535/tasks-project-management-app-ui-kit  
- **Why for ASL:** Explicitly Linear-inspired **mobile** dark/light — lists, filters, settings.  
- **Steal:** Dense rows; calm dark; sparse accent; settings hierarchy.  
- **Avoid:** Issue-tracker IA copy-paste; any frame that drifts into purple SaaS dashboard.

#### F8. Listodo — Todo List App UI Kit
- **URL:** https://www.figma.com/community/file/1631206131567046317/listodo-todo-list-app-ui-kit  
- **Why for ASL:** Minimal dark mobile productivity — focus, lists, settings.  
- **Steal:** Quiet surfaces; clear primary actions; low visual noise.  
- **Avoid:** Lifestyle-app softness that undercuts “serious IDE”; generic todo metaphors for Build.

### Settings, lists, developer-web chrome

#### F9. Primer Web
- **Author:** Primer / GitHub (`@primer`)  
- **URL:** https://www.figma.com/community/file/854767373644076713  
- **Docs:** https://primer.style/product/getting-started/figma/  
- **Why for ASL:** High-trust dense lists, forms, settings patterns from a real developer product system.  
- **Steal:** Neutral dark rigor; status colors used sparingly; form/settings clarity.  
- **Avoid:** Web-only density; Primer Mobile is **internal-only** — don’t expect a public mobile library.

#### F10. Primer Primitives
- **Author:** Primer (`@primer`)  
- **URL:** https://www.figma.com/community/file/854766928300977832  
- **Why for ASL:** Color/type/spacing discipline for calm dark systems.  
- **Steal:** Token thinking; contrast roles; neutral ramps.  
- **Avoid:** Importing GitHub greens as ASL brand without intent.

#### F11. Github UI — Free UI Kit (Recreated)
- **URL:** https://www.figma.com/community/file/1235155662725718346/github-ui-free-ui-kit-recreated  
- **Why for ASL:** Repo/settings/issues page compositions for dense information UI.  
- **Steal:** Page hierarchy; table/list density; settings sections.  
- **Avoid:** Unofficial recreation drift; web layouts forced onto phone; treat as **remix**, not official Primer.

### Build / progress (use carefully)

#### F12. SaaS Build Automation & Security Dashboard Kit
- **URL:** https://www.figma.com/community/file/1633848675505719670/saas-build-automation-security-dashboard-kit  
- **Why for ASL:** Explicit build-pipeline stages (configuring → validating → … → completed) for Build screen metaphors.  
- **Steal:** Step/progress tracker structure; history/filter patterns; status cards **if** kept calm.  
- **Avoid:** Desktop SaaS dashboard chrome; charts bloat; blue “security product” skin; any glow.  
- **Note:** Inspiration for **progress semantics**, not overall ASL visual language.

---

## Community files to skip (examples of anti-pattern gravity)

Listed so humans don’t waste time:

- **Bento UI Kit — Dark Components for SaaS & AI** — https://www.figma.com/community/file/1639503244259822935/… — AI/SaaS bento marketing; high risk of shiny dashboard language.
- Generic **purple AI dashboard** / **cyberpunk code editor** Community hits — same failure mode as Wave 2.
- **Plus UI** and similar “2026 AI design system” kits — useful engineering for tokens maybe, wrong vibe north star for ASL.

---

---

## Round 2 — free complete kits (human liked VS Code Toolkit + Flamingo; Appetite paid)

| Kit | Free? | Link | Notes |
| --- | --- | --- | --- |
| VS Code Toolkit | Free | https://www.figma.com/community/file/786632241522687494/visual-studio-code-toolkit | **Liked** — IDE chrome + components |
| Flamingo | Free | https://www.figma.com/community/file/1289838515258407105/flamingo-mobile-design-kit | **Liked** — mobile DS, variables, dark |
| Webview UI Toolkit (VS Code) | Free | https://www.figma.com/community/file/1071566662997054792/Webview-UI-Toolkit-for-Visual-Studio-Code | Calm IDE-adjacent controls |
| Int UI Kit (JetBrains New UI) | Free | https://www.figma.com/community/file/1227732692272811382/int-ui-kit | Android Studio family chrome |
| IntelliJ Platform UI Kit | Free | https://www.figma.com/community/file/938505862996154830/intellij-platform-ui-kit | Older JetBrains kit; still useful |
| Friendly UI | Free | https://www.figma.com/community/file/1173905012746425347/friendly-ui | Large free DS, mobile+desktop, dark |
| Universal Design System | Free (incl. commercial) | https://www.figma.com/community/file/1515890621122401030/universal-design-system | Tokens + many components + dark |
| Coherent Design System | Free | https://www.figma.com/community/file/1311381775078816263/coherent-design-system-cds | 160 components, dark+light |
| Buzzvil Apps DS | Free | https://www.figma.com/community/file/966523898114949728/apps-buzzvil-design-system | Mobile-only, dark-ready |
| Cabana Free | Free slice | https://www.figma.com/community/file/1378793657293890326/cabana-free | Free sample; full Cabana paid |
| Untitled UI Free | Free tier | https://www.untitledui.com/figma | High craft; free is lighter / dark may be limited |
| Material 3 Design Kit | Free | https://www.figma.com/community/file/1035203688168086460/material-3-design-kit | Full Android/Compose; aesthetic lead ≠ IDE cool |
| Appetite UI | **Paid** | https://www.appetiteui.com/ | Liked look — skip unless buying |

**Suggested lock:** VS Code Toolkit + Flamingo as primary; add Friendly UI *or* Universal DS *or* Int UI Kit if you want more component depth.

---

## How to use this board

1. Human opens shortlist (5) + skims 2–3 extra product refs.  
2. Human opens shortlisted Community files in Figma (duplicate if needed).  
3. Human comments on #38: “Lock = A + B (+ optional C)” with names.  
4. Only then: design agents redo DS (#36) + Connect (#30) + Build (#31) + Editor (#32).

---

## Sources (primary)

- JetBrains Fleet Islands announcement — https://blog.jetbrains.com/fleet/2024/12/introducing-fleets-new-islands-ui/  
- IntelliJ UI themes docs — https://www.jetbrains.com/help/idea/user-interface-themes.html  
- Zed — https://zed.dev/ · https://zed.dev/docs/appearance  
- Linear design refresh — https://linear.app/now/behind-the-latest-design-refresh  
- Material 3 kit / Now in Android Community files — Figma `@materialdesign`  
- Primer Figma libraries — https://primer.style/product/getting-started/figma/ · https://www.figma.com/@primer  
- Termius App Store / Play listings  
- Figma Community file pages linked above (authors as shown on each page)
