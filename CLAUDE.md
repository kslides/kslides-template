# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A GitHub template for authoring [kslides](https://github.com/kslides/kslides) presentations. End users hit "Use this template" to fork it, then edit `src/main/kotlin/Slides.kt` to author their deck. Changes here flow downstream to every fork, so keep the template minimal and back-compat where possible — see `CHANGELOG.md` (per-version diff) and `RELEASE_NOTES.md` (narrative) for the running history users are expected to track.

## Build & run

The Makefile is a thin wrapper over `./gradlew`:

- `make` (no args) defaults to `help` (prints the colorized target list generated from the `##` comments)
- `make build` — `./gradlew clean build -x test` (tests are intentionally skipped here)
- `make build-all` — alias for `make stage`; the Gradle `stage` task already declares `dependsOn("clean", "shadowJar")`
- `make uberjar` — invokes `./gradlew shadowJar`, which produces `build/libs/kslides.jar` directly with the custom manifest (there is no separate `uberjar` Gradle task — that wrapper was collapsed into `shadowJar` in 1.40.0)
- `make uber` — build the uberjar and run it (`java -DPORT=$${PORT:-8080} -jar build/libs/kslides.jar`); honours `$PORT` if set, otherwise falls back to 8080
- `make stage` — `./gradlew stage`; this is what Heroku invokes (see `Procfile`)
- `make dist` — `./gradlew installDist`
- `make sync-revealjs` — runs the `syncRevealJs` task (see Architecture)
- `make pdf` — runs `./gradlew exportPdf`; `make pdf DECK=<name>` forwards `-Pdeck=<name>` to export a single deck. Writes to `build/pdf`
- `make clean-pdf` — `rm -rf build/pdf`
- `make clean-docs` — deletes the generated decks (`docs/greattalk1`, `docs/greattalk2.html`, `docs/index.html`). It deliberately carries no `##` comment, so it stays out of `make help`; it hard-codes the sample deck's output paths and must be updated whenever a presentation is renamed or added.
- `make versions` — `./gradlew dependencyUpdates` (ben-manes plugin); must use `--no-configuration-cache --no-parallel`
- `make upgrade-wrapper` — re-pin the Gradle wrapper version (reads `gradle-wrapper = "..."` from `gradle/libs.versions.toml`; bump that key in lockstep with `gradle/wrapper/gradle-wrapper.properties`)

Run `fun main()` in `Slides.kt` directly from IntelliJ (green arrow) to generate slides — that's the primary author workflow, not a Gradle task.

There are no tests in this template. `make build` passes `-x test` deliberately.

## Architecture

**Two output modes, configured per-presentation in `Slides.kt` via the `output {}` block:**
- `enableFileSystem = true` — generates static HTML into `/docs/` for GitHub Pages or Netlify.
- `enableHttp = true` — runs an embedded HTTP server (used for Heroku via the uberjar).

Both can be enabled simultaneously.

**Static asset roots differ by mode:**
- HTTP-served presentations load static assets from `src/main/resources/public/`
- Filesystem-generated presentations load static assets from `/docs/`

This is the single most surprising thing about the project — the same logical asset lives in two different locations depending on output mode. Run `./gradlew clean build` after editing `src/main/resources/public/`.

**reveal.js asset sync.** The reveal.js distribution is the single source of truth in the `kslides-core` JAR (at classpath `revealjs/**`). The `syncRevealJs` Gradle task unpacks those into `docs/revealjs/` so static-output decks deployed to GitHub Pages / Netlify have working JS/CSS references. Re-run `make sync-revealjs` after a `kslides-core` upgrade — a core bump can add whole plugin directories (1.2.0 added `plugin/mermaid/`), and the new files have to be committed or statically-published decks break. It is often a no-op: 1.3.0 and 1.4.0 both ship the distribution unchanged from 1.2.0, so run it and check `git status` rather than assuming either way.

**Link paths are depth-sensitive; asset paths are not.** The template is published under a subpath on GitHub Pages (`https://username.github.io/repo_name/`), so a leading `/` resolves to the account root and misses. As of kslides 1.4.0 the library resolves *asset* paths against the output root from any deck depth — `logo(...)`, `topLeftSvgSrc`/`topRightSvgSrc`, `slideConfig` background image/iframe/video, `favicon`, and the `playground`/`letsPlot`/`diagram` iframes — so those are written relative to `/docs` and must **not** carry a hand-written `../`. Corner *links* (`topLeftHref`, `topRightHref`, `logo(href = )`), paths inside Markdown/HTML slide content, and `menuConfig { themesPath }` are still emitted verbatim, so they are written relative to the deck. That is why the sample deck's three 🔙 links do not share a value: `"../#/otherslides"` for the two presentations under `greattalk1/`, `"./#/otherslides"` for `greattalk2.html` at the root. Don't "simplify" them into one, and use `"./"` rather than `"/"` for the default `topRightHref` — it is identical under HTTP, where the deck is already served from the root.

**PDF export lives in a separate source set.** `src/export/kotlin/Export.kt` calls
`exportPdf(deck, templateSlides())` from the `com.kslides:kslides-export` artifact (new in kslides
1.3.0), which serves the decks from an ephemeral-port HTTP server and prints them through
Playwright's headless Chromium. The `export` source set exists solely to keep the Playwright
dependency off the main runtime classpath — `shadowJar` bundles only main, and
`exportImplementation` extends `implementation` one-way, so the uberjar stays Playwright-free
(verify with `unzip -l build/libs/kslides.jar | grep -c playwright` → `0`). Two consequences worth
remembering: Gradle does not wire custom source sets into `check`, so `configurePdfExport()` adds
`check → exportClasses` explicitly — without that a broken `Export.kt` stays invisible until
someone runs `make pdf`; and `Slides.kt` exposes the deck as
`fun templateSlides(): KSlides.() -> Unit` rather than inlining it in `main()` precisely so both
entry points share one definition — don't "simplify" that back into `main()`.

**Config nesting in `Slides.kt`.** `kslides { presentationConfig { … } }` sets defaults for *all* presentations; `presentation { presentationConfig { … } }` configures one. `copyCodeConfig { }` and `menuConfig { }` exist only on `PresentationConfig`, so they can appear in either place — but as of 1.2.0 the sample deck sets copy-code per presentation, and its `button` / `display` enums (`CopyCodeButton`, `CopyCodeDisplay`) are imported from `com.kslides.config`, not the root `com.kslides` package.

**Font sizes go through `slideConfig`, not CSS.** `fontSize`, `codeFontSize`, and `codeWrap` cascade global → presentation → slide (added in kslides 1.2.0 explicitly to replace raw-CSS code sizing). The sample deck sets `codeFontSize = "0.60em"` / `codeWrap = true` in the presentation-level `slideConfig { }` and overrides `codeFontSize = "0.45em"` on the six slide-definition slides. kslides generates one class per distinct `(codeFontSize, codeWrap)` pair and — when wrapping is on — also emits `.reveal pre code .hljs-ln-numbers { white-space: nowrap; word-break: normal; }`, pinning the highlight plugin's line-number gutter so `break-word` doesn't split two-digit line numbers across rows. That pairing is the concrete reason not to hand-roll these rules — the template previously did, and shipped without the gutter fix. Note the properties resolve per-slide, so a `verticalSlides { slideConfig { } }` block does nothing with them — set them on each child slide or deck-wide.

**Two markdown-slide constraints, both discovered by hitting them.** `markdownSlide` content must contain **no em dashes**: the renderer converts them to `&mdash;`, and the XML parse step in `rawHtml` then throws `SAXParseException: The entity "mdash" was referenced, but not declared`, failing the whole generation run. Use a comma, colon, or parentheses. Kotlin comments and `dslSlide` content are unaffected. Separately, a line carrying `fragment()` must be **plain text with no inline markup** — the reveal.js `<!-- .element -->` comment it emits attaches to the last element on the line, so a trailing backtick span takes the fragment class and only the code span animates while the rest of the paragraph stays visible. Both are noted in comments beside the slides that would otherwise tempt an edit.

**Generation has one network dependency.** `diagram("graphviz")` posts to kroki.io while the slides are generated and caches the SVG into `docs/kroki/`, so a clean `fun main()` or `make pdf` needs network access. `mermaid()` does not — it renders client-side from a runtime bundled in kslides-core, which is why the mermaid slide is the offline-safe one. The `kslides-letsplot` dependency needed by the Lets-Plot slide adds ~17 MB to the uberjar (~31 MB total); if a fork drops that slide, drop the dependency with it.

**Slide-level CSS.** For what the config blocks don't cover, deck CSS is accumulated with `css += """…"""` — at the `kslides { }` level for all presentations, at the `presentation { }` level for one. Individual slides opt into a rule via `classes += "name"`, which lands as a class on the generated `<section>`. Reserve this for genuinely custom styling; if a config property exists, use it.

**Deployment artifacts already wired in:**
- `netlify.toml` — points Netlify at `docs/` as the publish base
- `Procfile` + `system.properties` + `app.json` — Heroku config (Java 17 runtime, `web` dyno runs the uberjar)
- `.nojekyll` — disables Jekyll on GitHub Pages so reveal.js's `_*` paths aren't filtered

## Build configuration

- Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`).
- Version catalog in `gradle/libs.versions.toml` — bump kslides/kotlin/shadow/jvm/gradle-wrapper there, not in `build.gradle.kts` or the `Makefile`. The `jvm` key drives `jvmToolchain(...)`; the `gradle-wrapper` key drives `make upgrade-wrapper` (extracted via `sed` in the `Makefile`).
- JVM toolchain: pulled from `libs.versions.jvm` (currently `17`; foojay resolver convention; users without a local JDK get one auto-provisioned).
- `group` and `version` live in `gradle.properties` (Gradle auto-binds them to `Project.group` / `Project.version`). `version` is the *template* version, not the kslides library version (which lives in `libs.versions.toml`). Keep them distinct.
- `mainName` in `build.gradle.kts` (currently `"SlidesKt"`) must match the Kotlin file users want to serve over HTTP. The comment above it is the documented extension point for forks.
- `shadowJar` is configured directly (no `Jar`-typed wrapper task) — it sets `archiveFileName = "kslides.jar"` and the `Implementation-*` / `Main-Class` manifest attributes itself. To customize the uberjar, edit the `tasks.named<ShadowJar>(shadowJarTask) { … }` block.
- **`shadowJar` duplicate handling is deliberately two-tiered.** The task sets `duplicatesStrategy = DuplicatesStrategy.INCLUDE`, then narrows it back to `EXCLUDE` for `public/**`, `META-INF/LICENSE*`, and `META-INF/NOTICE*` via `filesMatching`. The reason: Gradle applies the strategy *before* Shadow's transformers run, so the default `EXCLUDE` silently reduces `mergeServiceFiles()` and the built-in `KotlinModuleMetadataTransformer` to first-copy-wins — Shadow 9.6.1 emits ~40 warnings about exactly this. `INCLUDE` alone fixes the merge but leaves genuinely duplicated entries for the non-transformer paths (`public/favicon.ico` twice, `META-INF/LICENSE.txt` three times), hence the narrowing. Order matters for `public/**`: the template's own `src/main/resources/public/` is packed ahead of kslides-core's, so a fork's `favicon.ico` wins. Two gotchas when editing this: `filesMatching` actions are **not** tracked as task inputs, so a changed block can still hit the build cache — verify with `./gradlew shadowJar --rerun-tasks`, not a plain `make build`; and confirm a fix by grepping the build output for `Duplicate entries found in the shadowed JAR`, which Shadow prints separately from the transformer warnings.
- The `export` source set, its `exportImplementation`/`exportRuntimeOnly` configurations, and the `exportPdf` `JavaExec` task are wired in `configurePdfExport()`. `exportPdf` reads the optional `-Pdeck=<name>` Gradle property and forwards it as the `kslides.export.deck` system property; `JavaExec`'s working directory defaults to the project dir, which is what the deck's relative paths (`src/main/kotlin/Slides.kt`, `src/main/resources/json-example.json`) resolve against. The same function makes `check` depend on the source set's `classesTaskName` (`exportClasses`), derived rather than hardcoded so renaming `exportSourceSet` stays a one-line change.
- Configuration cache is on (`org.gradle.configuration-cache=true` in `gradle.properties`). New tasks must be CC-compatible; ben-manes' `dependencyUpdates` is the only known incompatible task and `make versions` opts out for it.
- Repositories are locked down: `settings.gradle.kts` uses `FAIL_ON_PROJECT_REPOS` and resolves only from `mavenCentral()` (no `mavenLocal()` — local snapshots can't be picked up without temporarily editing the file).

## Conventions for edits to this template

- Anything user-facing (groupId placeholder `com.github.username`, `mainName`, `Slides.kt` example content) is meant to be edited downstream — keep it obvious and commented, don't over-engineer.
- For any change a downstream fork would need to mirror, update **both** `CHANGELOG.md` (Keep-a-Changelog format, per-version structured entry) and `RELEASE_NOTES.md` (narrative highlights). Tag-driven releases also need the template `version` in `gradle.properties` bumped — keep all three in sync in the same commit.
