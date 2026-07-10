# Android Studio Lite — Architecture (v0.1)

> Design-only document. No implementation yet.  
> Sources: `project/requierments.md`, Figma (`Android-Studio-Lite`), existing `:designsystem`.

---

## 1. Product snapshot

**Android Studio Lite** is a native Kotlin / Jetpack Compose IDE that runs on the phone:


| Capability                                                  | v0.1               |
| ----------------------------------------------------------- | ------------------ |
| Create Android project (single Activity + Compose template) | Yes                |
| List / open projects                                        | Yes                |
| Browse & manage project files/folders                       | Yes                |
| Edit source files                                           | Yes (basic editor) |
| Build APK via cloud / GitHub Actions (or equivalent)        | Yes                |
| Download APK → system install screen                        | Yes                |
| Edit → rebuild → reinstall loop                             | Yes                |


**Later:** Git, AI assistant, syntax highlighting.

**UI source of truth:** [Figma — Android Studio Lite](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite)

Known Figma pages (from prior work):

- **Main Screens** — Projects, Create Project, File Browser, Editor, Run/Build
- **file management flows** — create / rename / move / delete / copy / conflicts / sandbox rules
- **Architecture** — module dependency flowchart + create→edit→run product flow ([open](https://www.figma.com/design/M2LGyXHC5YYJekr3Fq3oiP/Android-Studio-Lite?node-id=59-2))

Existing foundation: `:designsystem` (colors, typography, shared Compose primitives).

---



## 2. Architectural goals

1. **Capability modules are self-contained** — each owns its data + presentation for its domain.
2. **Public surface is thin** — outside consumers see **interfaces + immutable data types only**, never implementations.
3. **Integration modules wire capabilities** — they do not invent domain logic; they compose APIs into product flows.
4. `:app` **stays thin** — Application class, DI graph, root navigation host, permissions, install intents.
5. **Replaceable backends** — especially Build (GHA today, other cloud later) behind one interface.
6. **Safe file sandbox** — all file ops stay under a project root (`projectDir`).

---



## 3. Module map

```text
AndroidStudioLite/
├── app                         # shell: start Koin, permissions, install
├── designsystem                # tokens + UI primitives
├── feature/
│   ├── projects/
│   │   ├── model               # Project, ProjectId, CreateProjectRequest
│   │   ├── api                 # ProjectService + ProjectsScreens
│   │   ├── data                # Room entities/DAOs + service impl
│   │   ├── presentation        # Compose screens
│   │   └── di                  # feature Koin module
│   ├── files/                  # model / api / data / presentation / di
│   ├── editor/                 # model / api / data / presentation / di
│   └── buildapk/               # model / api / data / presentation / di
└── integration/
    ├── database                # RoomDatabase assembly (wires feature entities/DAOs)
    └── ide                     # product nav + includes feature DI modules
```



### Why split `model` / `api` / `data` / `presentation` / `di`?

| Module | Owns |
| --- | --- |
| `:model` | Feature data classes / value types |
| `:api` | Interfaces only (`*Service`, `*Screens`) — depends on `:model` |
| `:data` | Persistence & domain impl (entities, DAOs, FS, repositories) |
| `:presentation` | Compose UI implementing screen launchers |
| `:di` | Feature Koin module binding api ← data/presentation |

Outside a feature, consumers depend on **`:api` (+ `:model` as needed)**. `:integration:ide` pulls `:di` modules; `:integration:database` assembles Room from feature `:data` entities/DAOs — it does not own feature schemas.

```text
:feature:projects:model         →  Project, ProjectId, …
:feature:projects:api           →  ProjectService, ProjectsScreens
:feature:projects:data          →  ProjectEntity, ProjectDao, ProjectService impl
:feature:projects:presentation  →  ProjectsScreens impl
:feature:projects:di            →  projectsDiModule
:integration:database           →  AslDatabase (entities from feature data modules)
```

---



## 4. Dependency rules



### Allowed direction

```mermaid
flowchart TB
  app[":app"]
  ide[":integration:ide"]
  ds[":designsystem"]
  core["(removed — per-feature :model)"]

  pApi[":feature:projects:api"]
  pImpl[":feature:projects:di"]
  fApi[":feature:files:api"]
  fImpl[":feature:files:di"]
  eApi[":feature:editor:api"]
  eImpl[":feature:editor:di"]
  bApi[":feature:buildapk:api"]
  bImpl[":feature:buildapk:di"]

  app --> ide
  app --> pImpl
  app --> fImpl
  app --> eImpl
  app --> bImpl
  app --> ds

  ide --> pApi
  ide --> fApi
  ide --> eApi
  ide --> bApi
  ide --> ds

  pImpl --> pApi
  pImpl --> ds
  pImpl --> core

  fImpl --> fApi
  fImpl --> ds
  fImpl --> core

  eImpl --> eApi
  eImpl --> ds
  eImpl --> core

  bImpl --> bApi
  bImpl --> ds
  bImpl --> core

  pApi --> core
  fApi --> core
  eApi --> core
  bApi --> core
```





### Hard rules


| Rule                                                                         | Reason                                                    |
| ---------------------------------------------------------------------------- | --------------------------------------------------------- |
| `*:api` must not depend on `*:impl`                                          | Keeps public surface pure                                 |
| Feature `impl`s must not depend on other feature `impl`s                     | Avoid spaghetti; talk via APIs or integration             |
| Feature `impl`s should not depend on other feature `api`s unless unavoidable | Prefer integration module for cross-feature orchestration |
| Only `:app` / `:integration:ide` compose multiple features                   | Clear ownership of product flows                          |
| `:designsystem` depends on nothing in `feature/`                             | UI kit stays reusable                                     |


---



## 5. Layering inside a capability module

Each `impl` follows the same internal shape:

```text
feature/files/impl/
  ui/           # Compose screens, screens impl, ViewModels (presentation)
  data/         # FileSystemDataSource, service implementations
```

```mermaid
flowchart LR
  PRESENTATION["PRESENTATION<br/>Screens impl / ViewModels"]
  API["Public API<br/>interfaces + models<br/>+ screen launchers"]
  DATA["DATA<br/>FS / network / cache /service impl"]

  PRESENTATION --> API
  DATA --> API
```



**Outside the module**, only `API` is visible. The API should provide an interface to open screens (e.g., via a navigation entry or a dedicated screen launcher), instead of leaking ViewModels or internal presentation logic.

---

## 6. Capability contracts (public APIs)

These are **design sketches** — names can change; the shape is what matters.

### 6.1 Projects — `:feature:projects:api`

**Owns:** project identity, metadata, create-from-template, open/delete/list.

```kotlin
// Conceptual — not implemented yet

data class ProjectId(val value: String)
data class Project(
    val id: ProjectId,
    val name: String,
    val packageName: String,
    val rootPath: String,          // absolute sandbox root
    val lastOpenedAt: Long?,
)

data class CreateProjectRequest(
    val name: String,
    val packageName: String,
)

interface ProjectService {
    fun observeProjects(): Flow<List<Project>>
    suspend fun getProject(id: ProjectId): Project?
    suspend fun createProject(request: CreateProjectRequest): Project
    suspend fun deleteProject(id: ProjectId)
    suspend fun markOpened(id: ProjectId)
}

/**
 * Screen launcher — public UI entry points for this feature.
 * Implemented in :impl; consumed by :integration:ide / :app.
 * Never expose ViewModels or internal Composables through this API.
 */
interface ProjectsScreens {
    @Composable
    fun ProjectsList(
        onOpenProject: (projectId: String) -> Unit,
        onCreateProject: () -> Unit = {},
    )

    @Composable
    fun CreateProject(
        onCreated: (projectId: String) -> Unit,
        onCancel: () -> Unit,
    )
}
```

**UI owned by** `impl`**:** Projects list, Create Project dialog/screen (Figma Main Screens) — reached only via `ProjectsScreens`.

**Does not own:** browsing files inside a project (that’s Files).

---



### 6.2 Files — `:feature:files:api`

**Owns:** navigation + CRUD under a project root. Matches Figma **file management flows**.

```kotlin
data class ProjectRoot(val absolutePath: String)

sealed class FsNode {
    abstract val name: String
    abstract val relativePath: String
    data class File(...) : FsNode()
    data class Folder(...) : FsNode()
}

data class DirectoryListing(
    val currentRelativePath: String,
    val entries: List<FsNode>,
)

sealed class FileOpError {
    data object OutsideSandbox : FileOpError()
    data object NameConflict : FileOpError()
    data object InvalidName : FileOpError()
    data object InvalidMove : FileOpError()   // into self/child
    data class Io(val message: String) : FileOpError()
}

interface FileExplorerService {
    fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing>

    suspend fun createFile(root: ProjectRoot, parentRelative: String, name: String): Result<FsNode.File, FileOpError>
    suspend fun createFolder(root: ProjectRoot, parentRelative: String, name: String): Result<FsNode.Folder, FileOpError>
    suspend fun rename(root: ProjectRoot, relativePath: String, newName: String): Result<FsNode, FileOpError>
    suspend fun move(root: ProjectRoot, fromRelative: String, toParentRelative: String): Result<FsNode, FileOpError>
    suspend fun copy(root: ProjectRoot, fromRelative: String, toParentRelative: String): Result<FsNode, FileOpError>
    suspend fun delete(root: ProjectRoot, relativePath: String): Result<Unit, FileOpError>

    suspend fun readText(root: ProjectRoot, relativePath: String): Result<String, FileOpError>
    suspend fun writeText(root: ProjectRoot, relativePath: String, content: String): Result<Unit, FileOpError>
}

/**
 * Screen launcher — file browser / management UI for a project root.
 * Cross-feature exits (open file → editor) are callbacks wired by integration.
 */
interface FilesScreens {
    @Composable
    fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String = "",
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
    )
}
```

**UI owned by** `impl`**:** path bar, folder/file rows, create/rename/move/delete dialogs, empty states, context menus — using `:designsystem` components (`PathBar`, `FileRow`, `DialogForm`, …). Reached only via `FilesScreens`.

**Shared UI state model (from Figma notes):**

```text
currentPath + selectedItem + clipboard{cut|copy, path}
create / paste always target currentPath
all ops clamped to projectDir
```

---



### 6.3 Editor — `:feature:editor:api`

**Owns:** open document, dirty flag, save. Kept separate so Files stays about tree ops, not editing UX.

```kotlin
data class DocumentId(val projectId: ProjectId, val relativePath: String)

data class OpenDocument(
    val id: DocumentId,
    val content: String,
    val isDirty: Boolean,
)

interface EditorSession {
    val document: StateFlow<OpenDocument?>
    fun open(id: DocumentId, initialContent: String)
    fun updateContent(content: String)
    fun markSaved(content: String)
    fun close()
}

interface DocumentStore {
    suspend fun load(root: ProjectRoot, relativePath: String): String
    suspend fun save(root: ProjectRoot, relativePath: String, content: String)
}

/**
 * Screen launcher — editor UI for an open document.
 * Save / Run / close are callbacks or session ops; integration wires Run → Build.
 */
interface EditorScreens {
    @Composable
    fun Editor(
        documentId: DocumentId,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)? = null,
    )
}
```

`DocumentStore` may be implemented by adapting `FileExplorerService.readText/writeText` inside `editor:impl` or via a binding in `:integration:ide`. Prefer **adapter in integration** so `editor` does not hard-depend on `files:api` if we want maximum isolation — or allow a soft `editor:impl → files:api` dependency for pragmatism in v0.1.

**Recommendation for v0.1:** `editor:impl` may depend on `files:api` for load/save. Integration still owns navigation and calls `EditorScreens` / `FilesScreens` / `BuildScreens` — never feature `impl` types.

---



### 6.4 Build APK — `:feature:buildapk:api`

**Owns:** remote build lifecycle + APK delivery. Clear, swappable interface.

```kotlin
data class BuildRequest(
    val projectId: ProjectId,
    val projectRoot: ProjectRoot,
    val projectName: String,
    val packageName: String,
)

enum class BuildPhase {
    Queued, Uploading, Building, Downloading, ReadyToInstall, Failed, Cancelled
}

data class BuildProgress(
    val jobId: String,
    val phase: BuildPhase,
    val message: String? = null,
    val apkLocalPath: String? = null,   // set when ReadyToInstall
    val error: String? = null,
)

interface BuildService {
    fun observeBuild(jobId: String): Flow<BuildProgress>
    suspend fun startBuild(request: BuildRequest): String   // returns jobId
    suspend fun cancelBuild(jobId: String)
}

/** Side-effect at the Android boundary — usually implemented in :app or buildapk:data / :integration:database */
interface ApkInstaller {
    fun requestInstall(apkLocalPath: String)
}

/**
 * Screen launcher — build progress / result UI.
 * Install hand-off uses ApkInstaller (often bound in :app).
 */
interface BuildScreens {
    @Composable
    fun BuildProgress(
        jobId: String,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)? = null,
    )
}
```

**UI owned by** `impl`**:** Run button states, progress sheet/screen, failure toast (Figma Run/Build) — reached only via `BuildScreens`.

**Impl details (hidden):** zip/upload project, trigger GHA/cloud, poll status, download artifact, write to cache dir. Swap provider without touching Projects/Files.

---

### 6.5 Screen-launcher rule (all feature `:api`s)

Every capability `:api` exposes **two** public surfaces:

| Surface | Examples | Purpose |
|---|---|---|
| Domain services | `ProjectService`, `FileExplorerService`, `BuildService` | Data / use-cases |
| Screen launchers | `ProjectsScreens`, `FilesScreens`, `EditorScreens`, `BuildScreens` | Open that feature’s UI |

**Rules:**

1. Launchers live in `:api` and are implemented in `:impl`.
2. Launchers accept **callbacks** for exits that leave the feature (open project → files, open file → editor, run → build). Integration wires those callbacks.
3. Launchers must not return or expose ViewModels, repositories, or other `impl` types.
4. `:integration:ide` / `:app` depend on launcher interfaces from `:api` only — never on feature screen classes in `:impl`.

---



## 7. Integration module — `:integration:ide`

This is the module you described: **not a domain specialist**, but the place that **connects** specialists into product behavior.

### Responsibilities

- Navigation graph for the IDE experience
- Calling feature **screen launchers** (`ProjectsScreens`, `FilesScreens`, …) — never `impl` screen types
- Mapping: open project → hand `ProjectRoot` to `FilesScreens.FileBrowser`
- Mapping: open file → hand path to `EditorScreens.Editor`
- Mapping: Run → `BuildService.startBuild` + `BuildScreens.BuildProgress`
- Coordinating “file deleted while open in editor” (Figma case 17)
- Providing entry points / routes consumed by `:app`



### Does **not**

- Implement filesystem algorithms
- Talk to GitHub Actions directly
- Own design tokens
- Duplicate project metadata storage

```mermaid
sequenceDiagram
  actor User
  participant IDE as integration:ide
  participant PS as ProjectsScreens
  participant P as ProjectService
  participant FS as FilesScreens
  participant F as FileExplorerService
  participant ES as EditorScreens
  participant E as EditorSession
  participant BS as BuildScreens
  participant B as BuildService
  participant I as ApkInstaller

  User->>IDE: Open Projects
  IDE->>PS: ProjectsList(...)
  User->>IDE: Create project
  IDE->>PS: CreateProject(...)
  IDE->>P: createProject(...)
  User->>IDE: Open project
  IDE->>P: markOpened(id)
  IDE->>FS: FileBrowser(root, ...)
  User->>IDE: Open file
  IDE->>ES: Editor(documentId, ...)
  User->>IDE: Save
  IDE->>E: (dirty content via session)
  IDE->>F: writeText(...)
  User->>IDE: Run
  IDE->>B: startBuild(request)
  IDE->>BS: BuildProgress(jobId, ...)
  B-->>IDE: ReadyToInstall + apk path
  IDE->>I: requestInstall(apk)
```





### Suggested façade (deferred past v0.1)

`IdeCoordinator` (command façade for open project / open file / run) is **optional and deferred**.  
v0.1 uses the **nav graph + screen-launcher callbacks** only. Revisit if a second entry point (deep link, notification, widget) appears.

```kotlin
// Deferred — not required for v0.1
interface IdeCoordinator {
    fun openProjects()
    fun openProject(id: ProjectId)
    fun openFile(projectId: ProjectId, relativePath: String)
    fun runProject(id: ProjectId)
}
```

---



## 8. App shell — `:app`


| Concern                                               | Owner                          |
| ----------------------------------------------------- | ------------------------------ |
| Start Koin; aggregate feature + integration modules   | `:app`                         |
| Per-feature Koin modules (`api` → `impl` bindings)    | each `:feature:*:impl`         |
| Integration wiring Koin module                        | `:integration:ide`             |
| IDE `NavHost` / nav graph                             | `:integration:ide`             |
| Host Activity that embeds the IDE graph               | `:app`                         |
| `REQUEST_INSTALL_PACKAGES` / install activity result  | `:app` (+ `ApkInstaller` impl) |
| Application / MainActivity                            | `:app`                         |
| Theme wrapper using `:designsystem`                   | `:app`                         |


`:app` depends on `impl` modules to start Koin with their modules; UI composition goes through `:integration:ide` routes and screen launchers.

---



## 9. Design system — `:designsystem`

Already present. Role stays:

- `AslColors`, `AslTypography`, `AslIcons`
- Primitives: buttons, text fields, dialogs, file/folder rows, path bar, project card, menus, toasts, top/status bars

**Rule:** feature UIs compose these; they do not redefine tokens. Feature-specific layouts live in feature `impl`, not in the design system (unless a pattern is reused 3+ times).

---



## 10. End-to-end product flows



### 10.1 Create → open → edit → save

```mermaid
flowchart TD
  A[Projects screen] -->|+ New| B[Create Project]
  B --> C[ProjectService.createProject]
  C --> D[Template written under rootPath]
  D --> A
  A -->|tap project| E[Files browser]
  E -->|tap .kt file| F[Editor]
  F -->|edit + save| G[FileExplorerService.writeText]
```





### 10.2 File management (Figma coverage → API)


| Figma case                     | API / behavior                                 |
| ------------------------------ | ---------------------------------------------- |
| Create file / folder           | `createFile` / `createFolder` at `currentPath` |
| Nested create                  | navigate first → create at new `currentPath`   |
| Rename                         | `rename`                                       |
| Delete file / non-empty folder | `delete` (+ confirm UI)                        |
| Move / copy                    | `move` / `copy`                                |
| Name conflict / invalid name   | `FileOpError`                                  |
| Breadcrumbs / up               | listing `relativePath` changes                 |
| Open → editor → save           | integration + Editor + `writeText`             |
| Empty folder                   | empty state UI                                 |
| Invalid move into self/child   | `InvalidMove`                                  |
| Delete/move while open         | integration closes or prompts EditorSession    |
| Sandbox guardrails             | `OutsideSandbox`                               |


---



## 11. Data ownership

```mermaid
flowchart TB
  subgraph device [On device]
    meta[Project metadata<br/>DB or index file]
    fs[Project trees<br/>app-private storage]
    apkCache[APK cache dir]
  end

  subgraph remote [Remote]
    ci[Cloud / GitHub Actions]
  end

  P[projects:impl] --> meta
  P --> fs
  F[files:impl] --> fs
  E[editor:impl] --> fs
  B[buildapk:data / :integration:database] --> fs
  B --> apkCache
  B --> ci
```



- **Projects** create the root folder + template + metadata row.
- **Files / Editor** mutate files under that root only.
- **Build** reads the tree for upload; writes APK only to cache; never mutates source as part of build.

---



## 12. Gradle include sketch

```kotlin
// settings.gradle.kts
include(":app")
include(":designsystem")

include(":feature:projects:model")
include(":feature:projects:api")
include(":feature:projects:data")
include(":feature:projects:presentation")
include(":feature:projects:di")
// … same five modules for files, editor, buildapk

include(":integration:database")
include(":integration:ide")
```

---



## 13. What stays out of v0.1 modules


| Later feature        | Likely module                                              |
| -------------------- | ---------------------------------------------------------- |
| Git push/pull/commit | `:feature:git:api` / `impl`                                |
| AI assistant         | `:feature:assistant:api` / `impl`                          |
| Syntax highlighting  | enhance `:feature:editor` (or `:feature:editor:highlight`) |


Integration grows new edges; existing APIs stay stable.

---



## 14. Implementation decisions

Resolved in grilling; recorded in **`project/v0.1-implementation-plan.md`** (and GitHub issue #5).  
Do **not** expand this architecture doc with ephemeral impl details (timings, asset paths, test matrix, etc.).

High-level locks that affect module shape:

- **Koin** — per-feature `:di` modules; `:integration:ide` includes them; `:app` starts Koin
- **App-private** project storage; **Room** assembled in **`:integration:database`**; feature tables/DAOs live in **`:feature:*:data`**
- **Models per feature** (`:feature:*:model`) — no shared `(removed — per-feature :model)`
- **`editor:data` may use `files:api`** for load/save
- **Fake `BuildService`** for v0.1 (real GitHub Actions later, same API)
- **IDE nav graph** in `:integration:ide` (no `IdeCoordinator` in v0.1)

---



## 15. Summary


| Module              | Role                   | Public surface                                      |
| ------------------- | ---------------------- | --------------------------------------------------- |
| `:designsystem`     | Visual language        | Compose components + tokens                         |
| `:feature:projects` | Project lifecycle | `model` + `api` + `data` + `presentation` + `di` |
| `:feature:files` | Tree navigation & CRUD | same five-way split |
| `:feature:editor` | Edit / dirty / save | same five-way split |
| `:feature:buildapk` | Remote APK pipeline | same five-way split |
| `:integration:database` | Room assembly | Wires feature entities/DAOs into `AslDatabase` |
| `:integration:ide` | Product wiring & nav | Includes feature DI + IDE nav graph |
| `:app` | Host | Start Koin + permissions/install |


This matches the intended shape: **specialist capability modules** with clean interfaces, plus **integration modules** that assemble them into Android Studio Lite — without leaking implementations across boundaries.