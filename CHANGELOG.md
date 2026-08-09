# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Ten feature demonstrations in the sample deck, each following the existing pattern of a demo slide plus a "Slide Definition" companion that includes its own source:
  - **Fragments** — `fragment()` with `Effect.FADE_UP` / `FADE_LEFT` / `GROW`, revealing a line per keypress.
  - **Speaker notes** — a `Notes:` line, plus one on the opening Markdown slide. The presentations already declared `data-separator-notes="^Notes?:"`, so this was configured but never shown.
  - **Slide backgrounds** — `slideBackground()` for a markdown slide, and `slideConfig { backgroundImage / backgroundSize / backgroundPosition / backgroundOpacity }` for a dslSlide.
  - **Slide-level `fontSize`** — the non-code sibling of `codeFontSize`.
  - **Lists** — `unorderedList`, `orderedList`, and `listHref`.
  - **Tables** — `headRow` / `bodyRow`.
  - **Mermaid diagrams** — `mermaid()`, rendered client-side from the bundled runtime. `docs/revealjs/plugin/mermaid/mermaid.min.js` has shipped since 1.42.0 with no deck referencing it.
  - **Kotlin Playground** — `playground()` over the new `src/main/kotlin/playground/HelloWorld.kt`, sized by a `playgroundConfig { fontSize = "20px" }` block (kslides 1.4.0).
  - **Lets-Plot** — a scatter plot via `letsPlot { }`.
  - **Kroki diagrams** — `diagram("graphviz")` with `DiagramOutputType.SVG` and edge options.
- `customTheme { }` on the `greattalk2.html` presentation (base theme, background/heading/link colors, code font, a custom CSS property, and a corner `logo()`), with a demo and definition slide in the main deck pointing at it. Applied to a secondary deck so the two looks can be compared.
- `src/main/kotlin/playground/HelloWorld.kt` — the source the Playground slide loads and runs in the browser.
- Generated output for the new slides: `docs/playground/`, `docs/letsPlot/`, and `docs/kroki/`.

### Changed
- Enabled the `kslides-letsplot` dependency in `build.gradle.kts`; it was present but commented out. **This grows `build/libs/kslides.jar` by roughly 17 MB** (to ~31 MB), which matters for the Heroku dyno. Remove the dependency and the Lets-Plot slide together if you do not want the weight.
- `make pdf` and `fun main()` now require network access on a clean run: `diagram()` calls kroki.io while the slides are generated and caches the result into `docs/kroki/`. `mermaid()` does not — it renders in the browser from the bundled runtime.

- The sample deck sizes its code blocks through `slideConfig { }` instead of hand-written CSS. kslides 1.2.0 added `fontSize` / `codeFontSize` / `codeWrap` specifically to replace the raw-CSS approach, and this template kept the CSS. The two `css += """…"""` blocks (`.reveal pre { font-size: 0.60em; }`, the `pre-wrap` / `break-word` rule, and `.reveal .smallcode pre { font-size: 0.45em; }`) are gone; the presentation-level `slideConfig { }` now sets `codeFontSize = "0.60em"` and `codeWrap = true`, and the six slide-definition slides carry their own `slideConfig { codeFontSize = "0.45em" }`. The `classes += "smallcode"` opt-in is removed along with the rule it selected.

### Fixed
- Wrapped code blocks no longer break two-digit line numbers across rows. The hand-written `word-break: break-word` inherited into the highlight plugin's line-number gutter, which let the table squeeze `10` onto two lines. kslides pairs its generated wrap rule with `.reveal pre code .hljs-ln-numbers { white-space: nowrap; word-break: normal; }` — mirroring reveal.js' own print CSS — so moving to `codeWrap` picks the pinning up. Visible in the regenerated `docs/index.html`, which previously carried the wrap rule with no gutter rule beside it.

### Documentation
- Replaced the _Styling Slides_ CSS example in `README.md` with a _Font Sizes_ section documenting the three `slideConfig` properties, the generated-class mechanism, and the caveat that they resolve per-slide (so a `verticalSlides { slideConfig { } }` block does nothing with them). _Styling Slides_ now covers only what the config blocks don't.
- Recorded the same rule in `CLAUDE.md` and `llms.txt`, including the gutter pairing as the concrete reason not to hand-roll the CSS.
- Added a _What the Sample Deck Demonstrates_ section to `README.md` mapping each feature to the API it uses and flagging the two that cost something (Lets-Plot's jar weight, Kroki's build-time network call).
- Documented two markdown-slide constraints found while building these slides, in `CLAUDE.md` and as code comments beside the slides they affect: markdown content must avoid em dashes, because the renderer emits `&mdash;` and the XML parse step then fails on the undeclared entity; and a `fragment()` line must not contain inline code spans or other inline markup, because the emitted reveal.js comment attaches to the last element on the line and the span swallows the class.

## [1.44.0] - 2026-08-08

A path-resolution release: everything the decks link to now resolves from wherever the deck sits, so a fork published under a GitHub Pages subpath works the same as one at a domain root.

### Changed
- Upgraded kslides to 1.4.0, the Gradle wrapper to 9.7.0, and the ben-manes versions plugin to 0.60.0. kslides-core 1.4.0 ships the same reveal.js distribution as 1.3.0, so `make sync-revealjs` produces no changes under `docs/revealjs/`.
- kslides 1.4.0 is source-compatible but **not binary-compatible** — `CssFile` and `JsFile` gained an `origin` parameter, which moves their constructor, `copy()`, and `componentN()` signatures. Recompile rather than swapping the jar.
- Regenerated `/docs`. Three differences, all from kslides 1.4.0: the favicon `<link>` is relative (`favicon.ico` at the root, `../favicon.ico` under `greattalk1/`) instead of the root-absolute `/favicon.ico`; the duplicate `rel="shortcut icon"` link is gone; and the viewport meta is now `width=device-width, initial-scale=1.0`, dropping `maximum-scale=1.0, user-scalable=no` (which blocked pinch-zoom and failed WCAG 2.1 SC 1.4.4) along with the two inert `apple-mobile-web-app-*` metas.

### Fixed
- `docs/greattalk1/other.html` now links its reveal.js assets as `../revealjs/…` instead of the bare `revealjs/…` it emitted before. kslides derived the `../` prefix only for directory-style deck paths, so a deck declared as `greattalk1/other.html` requested `<site>/greattalk1/revealjs/dist/reveal.css` and 404'd — the page rendered unstyled with `Can't find variable: Reveal` in the console. Fixed upstream in kslides 1.4.0; this template picks it up by regenerating. The other three decks are byte-identical in this respect.
- Every inter-deck link in the sample deck is now relative, so the decks work when published under a subpath. On GitHub Pages a fork lives at `https://username.github.io/repo_name/`, where the previous root-absolute links (`/`, `/#/otherslides`, `/greattalk1`) all resolved to the *account* root rather than to the deck. Changed: the default `topRightHref` (`"/"` → `"./"`), the 🔙 link in each of the three `greattalk` presentations, and the three cross-deck links on the "Other Presentations" slide.
- Note that the 🔙 prefix differs by deck depth — `"../#/otherslides"` for the two decks under `greattalk1/`, `"./#/otherslides"` for `greattalk2.html`, which sits at the root. Copying one value into all three would break the other. Corner links stay verbatim in kslides 1.4.0 (they are navigation targets, not assets), so this hand-written depth compensation is still required — unlike `logo(...)` / `topLeftSvgSrc` / `topRightSvgSrc`, where 1.4.0 now resolves the path for you and a hand-written `../` should be dropped.

### Documentation
- Added a _Publishing Under a Subpath_ section to `README.md` covering which paths kslides resolves for you as of 1.4.0 and which you still write relative to your deck.
- Documented the new `favicon` presentation-config knob (including `favicon = ""` to omit the link) in `README.md`.
- Recorded the deck-depth link convention in `CLAUDE.md` and `llms.txt`, so the three `topRightHref` values are not "simplified" into one.

## [1.43.0] - 2026-08-02

### Added
- PDF export, ported from the kslides repo. `make pdf` writes one PDF per presentation to `build/pdf`; `make pdf DECK=<name>` (forwarded as `-Pdeck=<name>`) exports a single deck, and `make clean-pdf` removes the output. The decks are served from an ephemeral-port HTTP server and printed through Playwright's headless Chromium by the new `com.kslides:kslides-export` artifact, so export works regardless of which `output {}` modes the deck enables.
- An `export` source set (`src/export/kotlin/Export.kt`) with its own `exportImplementation` / `exportRuntimeOnly` configurations, plus an `exportPdf` `JavaExec` task. Keeping the entry point out of `src/main/kotlin` is what stops the Playwright dependency from reaching `build/libs/kslides.jar`.
- A `check` → `exportClasses` dependency, so `./gradlew build` compiles the export source set. Gradle does not wire custom source sets into `check` on its own, which would otherwise let a broken `Export.kt` survive a green build until someone ran `make pdf`.
- `followAlong = true` in the sample deck's `output { }` block, enabling kslides 1.3.0's follow-along presenting. The presenter opens a deck with `?present=<token>` and every other visitor's browser tracks the presenter's slide and fragment live; a viewer who navigates away breaks off and can rejoin with one click, and late joiners land on the current position. The presenter URLs, token included, are logged at server startup. HTTP-only — it has no effect on the static `/docs` output, so nothing is injected into published decks. Set `presenterToken` alongside it if you need a stable URL instead of the random per-launch one; the token travels in the URL, so treat it as demo-grade auth rather than a secret.
- A commented `pdf { }` block inside `output { }` in the sample `Slides.kt` documenting `outputDir`, `previewPng`, `browserChannel`, and `exclude(...)`.
- An _Exporting to PDF_ section in `README.md`.

### Changed
- Upgraded kslides to 1.3.0 and the ben-manes versions plugin to 0.58.0. kslides-core 1.3.0 ships the same reveal.js distribution as 1.2.0, so `make sync-revealjs` produces no changes under `docs/revealjs/`.
- The sample `Slides.kt` now exposes its deck as `fun templateSlides(): KSlides.() -> Unit`, with `main()` reduced to `kslides(templateSlides())`. Both the normal run and the PDF exporter consume that one block. The deck's contents are otherwise unchanged.
- Regenerated `/docs`. The only difference is that kslides 1.3.0 emits embedded `<style>` blocks without the `media="screen"` attribute, so deck CSS now applies when printing. Previously all custom styling silently dropped out of reveal.js' `?print-pdf` view — unstyled corner links rendered full-width and pushed every deck down a page, yielding a blank leading PDF page. This affects readers who print a *published* deck from the browser; `make pdf` renders from an in-memory server rather than from `/docs`, so its output is unaffected by whether these files are regenerated.

### Fixed
- Shadow's resource transformers now actually merge. `shadowJar` sets `duplicatesStrategy = DuplicatesStrategy.INCLUDE`; Gradle applies the strategy before the transformers run, so the default `EXCLUDE` had been dropping duplicate copies of `META-INF/services/*` and `*.kotlin_module` and silently reducing `mergeServiceFiles()` and the built-in `KotlinModuleMetadataTransformer` to first-copy-wins. Shadow 9.6.1 surfaced this as roughly forty warnings during `make build`.
- Narrowed that strategy back to `EXCLUDE` for `public/**`, `META-INF/LICENSE*`, and `META-INF/NOTICE*` via `filesMatching`. Those paths have no transformer to merge them, so `INCLUDE` alone left genuinely duplicated entries in the uberjar (`public/favicon.ico` twice, `META-INF/LICENSE.txt` three times). The template's own `src/main/resources/public/` assets are packed ahead of kslides-core's, so a fork's `favicon.ico` still wins.

### Documentation
- Added a _The Uberjar_ section to `README.md` covering `make uberjar` / `make uber` and the rationale behind the two `duplicatesStrategy` lines, so forks that extend the `shadowJar` block know not to drop them.
- Documented the PDF-export wiring in `CLAUDE.md` and `llms.txt`, including the two things that surprise: custom source sets need an explicit `check` dependency to be compiled by a normal build, and `templateSlides()` exists so both entry points share one deck definition.
- Documented the same two-tiered duplicate handling in `CLAUDE.md`, including the two verification gotchas: `filesMatching` actions are not tracked as Gradle task inputs (so a plain `make build` can return a stale cached jar — use `./gradlew shadowJar --rerun-tasks`), and Shadow prints `Duplicate entries found in the shadowed JAR` separately from the transformer warnings.
- Extended the `build.gradle.kts` entry in `llms.txt` with the duplicate-strategy summary.

## [1.42.0] - 2026-08-01

### Changed
- Upgraded kslides to 1.2.0, Kotlin to 2.4.10, the Shadow plugin to 9.6.1, and the ben-manes versions plugin to 0.57.0.
- The ben-manes versions plugin id moved from `com.github.ben-manes.versions` to `io.github.ben-manes.versions`; `gradle/libs.versions.toml` uses the new id.
- Moved `copyCodeConfig { }` in the sample `Slides.kt` out of the global `presentationConfig { }` defaults block and into the per-presentation `presentationConfig { }` block. In kslides 1.2.0 the block also takes `button` (`CopyCodeButton.ALWAYS` / `HOVER` / `FALSE`) and `display` (`CopyCodeDisplay.TEXT` / `ICONS` / `BOTH`) alongside the existing `copy` / `copied` / `timeout` settings; the sample uses `ALWAYS` + `ICONS`.
- Replaced the wildcard imports in `Slides.kt` (`com.kslides.*`, `kotlinx.html.*`) with explicit imports. The copy-code enums live in `com.kslides.config`.
- `make clean-docs` now removes the decks this template actually generates (`docs/greattalk1`, `docs/greattalk2.html`, `docs/index.html`) instead of the stale `docs/playground` path, which no longer exists.
- Regenerated `/docs`. kslides 1.2.0 emits `<style media="screen">` without the obsolete `type="text/css"` attribute, and only emits a reveal.js `copycode` option block for presentations that configure one.

### Added
- Code-block sizing CSS in the sample deck: `.reveal pre { font-size: 0.60em; }` with `white-space: pre-wrap` so an occasional over-long line wraps instead of overflowing the slide horizontally.
- A `smallcode` slide class (`.reveal .smallcode pre { font-size: 0.45em; }`) applied via `classes += "smallcode"` to the slide-definition slides, whose code is wider than the rest of the deck.
- `docs/revealjs/plugin/mermaid/mermaid.min.js`, picked up by `make sync-revealjs` from the kslides-core 1.2.0 JAR — 1.2.0 adds a mermaid diagram DSL and ships the plugin with the reveal.js distribution.

## [1.41.0] - 2026-07-03

### Added
- Colorized `make help` target that lists every target from its trailing `##` comment. `make` with no arguments now defaults to `help` instead of running a dependency check.
- `.editorconfig` codifying the project's formatting (2-space indent, LF endings, trailing-whitespace trim) so forks and IDEs stay consistent.
- `-Xreturn-value-checker=check` on the production Kotlin compilation to flag ignored return values. It is deliberately not applied to the test source set, where Kotest's chained-receiver DSL (`shouldBe`, etc.) would only produce false positives.

### Changed
- Upgraded Kotlin to 2.4.0, kslides to 1.1.0, the Shadow plugin to 9.4.3, and the Gradle wrapper to 9.6.1.
- Renamed the `make versioncheck` target to `make versions`.
- Moved `group` and `version` out of `build.gradle.kts` into `gradle.properties` (Gradle auto-binds them to `Project.group` / `Project.version`).
- Centralized the JVM toolchain version and the Gradle wrapper version in `gradle/libs.versions.toml` as `jvm` and `gradle-wrapper`. `build.gradle.kts` reads `jvm` via `libs.versions.jvm.get().toInt()`; the `Makefile`'s `upgrade-wrapper` target reads `gradle-wrapper` via a `sed` shell expansion.
- Consolidated repeated string literals in `build.gradle.kts` (`shadowJar`, `clean`, `kslides.jar`, `revealjs`, `docs/revealjs`) into named `val`s and grouped the build wiring into `configureKotlin` / `configureShadowJar` / `configureRevealSync` / `configureVersions` functions.
- Renamed the `lineOffSet` parameter to `lineOffset` in the sample `Slides.kt` to match the kslides 1.1.0 API.

### Removed
- The committed copy of `revealjs.png` under `src/main/resources/public/images/`; it is now generated at build time from `docs/images/` via `processResources`, so the image lives in exactly one place.
- Unused reveal.js sample assets (`beeping.wav`, `beeping.txt`, `video.mp4`) from `docs/revealjs/assets/`.

## [1.40.0] - 2026-04-29

### Changed
- Migrated build to Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`) and the Gradle version catalog (`gradle/libs.versions.toml`).
- Upgraded Gradle wrapper to 9.5.0.
- Upgraded Shadow plugin to `com.gradleup.shadow` 9.4.1.
- Adopted property-assignment syntax in build scripts (`mainClass`, `archiveFileName`, `repositoriesMode`).
- Collapsed the standalone `uberjar` task into `shadowJar` itself — `./gradlew shadowJar` now produces `build/libs/kslides.jar` directly with the custom manifest.
- Gave the `stage` task an explicit `DefaultTask` type, the `build` group, and a description so it surfaces in `gradle tasks`.
- Removed `mavenLocal()` from both repository blocks in `settings.gradle.kts`.
- Alphabetized `[plugins]` entries in `gradle/libs.versions.toml`.
- Bumped `ben-manes-versions` plugin to 0.54.0.
- Rewrote the README's build section for Kotlin DSL + the version catalog.
- Renamed `LICENSE` to `LICENSE.txt`.

### Added
- `syncRevealJs` Gradle task that unpacks reveal.js assets from the `kslides-core` JAR into `docs/revealjs/`.
- `CHANGELOG.md` and `RELEASE_NOTES.md` covering history back to the initial commit.
- `CLAUDE.md` project guidance.
- `.PHONY` declarations in `Makefile` for every target.
- `make uber` now honours `$PORT` (matches `Procfile`, defaults to 8080 locally).

### Removed
- Legacy `build.gradle` / `settings.gradle` (Groovy DSL).
- `.travis.yml` (Travis CI config).
- The `LICENSE*` exclusion in `shadowJar` — third-party LICENSE files are now preserved in the uberjar.
- Standalone `uberjar` Gradle task (replaced by configuring `shadowJar` directly).

## [1.32.0] - 2024-12-12

### Changed
- Updated to Kotlin 2.1.0.
- Updated to Kotlin 2.0.0 and reveal.js 5.1.0 (rolled up from earlier in the cycle).

## [1.30.0] - 2023-11-01

### Changed
- Updated to Kotlin 1.9.10.
- Updated to reveal.js 5.0.0.
- Updated to CopyCode 1.1.2.
- Updated to kslides-core 0.20.0.

### Added
- Support for reveal.js Scroll View.

## [1.10.0] - 2023-05-23

### Added
- Bundled reveal.js static content for offline / static hosting.

### Changed
- Updated to kslides 0.19.0.

## [1.9.0] - 2023-05-16

### Changed
- Updated to Kotlin 1.8.21.

## [1.8.0] - 2023-04-10

### Changed
- Updated to Kotlin 1.8.20.

## [1.7.0] - 2023-01-01

### Changed
- Updated to Kotlin 1.8.0.

## [1.6.0] - 2022-12-03

### Changed
- 1.6.0 release.

## [1.5.4] - 2022-10-17

### Changed
- Updated bundled jars.

## [1.5.3] - 2022-10-03

### Changed
- Updated `srcref` jar.

## [1.5.2] - 2022-10-02

### Changed
- Updated to kslides 0.15.1.

## [1.5.1] - 2022-09-19

### Changed
- Updated to kslides 0.14.1.

## [1.5.0] - 2022-09-18

### Changed
- Updated to kslides 0.14.0.

## [1.4.4] - 2022-09-10

### Changed
- Updated to kslides 0.13.3.

## [1.4.2] - 2022-08-30

### Changed
- Updated to kslides 0.13.2.
- Earlier in the 1.4.x line: updated to kslides 0.13.1, then to a refreshed kslides jar build.

## [1.4.0] - 2022-08-09

### Changed
- README updates and ongoing kslides jar refreshes leading up to the 1.4.0 cut.

## [1.3.0] - 2022-07-16

### Changed
- Upgraded to kslides 0.11.0.
- Upgraded to Kotlin 1.7.0.

## [1.2.5] - 2022-06-11

### Changed
- Upgraded to Kotlin 1.7.0.

## [1.2.4] - 2022-06-05

### Changed
- Updated bundled jars.

## [1.2.3] - 2022-05-25

### Added
- Added `favicon.ico`.

### Changed
- Upgraded to kslides 0.10.3.

## [1.2.2] - 2022-05-25

### Added
- Support for `srcref` GitHub links.

### Changed
- Upgraded to kslides 0.10.2.

## [1.2.1] - 2022-05-19

### Added
- Support for specifying CSS in playground content.

## [Pre-1.2.1 history]

The following changes are reconstructed from the git log prior to the first
released tag (1.2.1).

### 2022-05 — kslides 0.8.x → 0.10.x line
- Updated to kslides 0.10.0, 0.9.0, and incremental 0.8.x releases (0.8.13 down to 0.8.1).
- Cleaned up `CHANGELOG.md` (legacy file, since removed).

### 2022-04 — Project moved under the `kslides` org
- Repository moved to the `kslides` GitHub organization.
- Adjusted the default `Slides.kt` (sample image, cleanup).
- Tracked upstream kslides jar refreshes.

### 2022-03 — Static-hosting / GitHub Pages support
- Renamed `/public` to `/docs` for GitHub Pages publishing.
- Added reveal.js files for static serving.
- Added `netlify.toml` for Netlify deployment.
- Added support for multiple levels of output.
- Added static menu support files.
- Renamed `Slides.kt` and added `app.json` for Heroku deploys.
- Upgraded to kslides 0.6.4 and tracked further jar updates.

### 2021 — Project bootstrap
- README updates.

### 2021-02-15 — Initial commit
- Repository created.

[Unreleased]: https://github.com/kslides/kslides-template/compare/1.44.0...HEAD
[1.44.0]: https://github.com/kslides/kslides-template/compare/1.43.0...1.44.0
[1.43.0]: https://github.com/kslides/kslides-template/compare/1.42.0...1.43.0
[1.42.0]: https://github.com/kslides/kslides-template/compare/1.41.0...1.42.0
[1.41.0]: https://github.com/kslides/kslides-template/compare/1.40.0...1.41.0
[1.40.0]: https://github.com/kslides/kslides-template/compare/1.32.0...1.40.0
[1.32.0]: https://github.com/kslides/kslides-template/compare/1.30.0...1.32.0
[1.30.0]: https://github.com/kslides/kslides-template/compare/1.10.0...1.30.0
[1.10.0]: https://github.com/kslides/kslides-template/compare/1.9.0...1.10.0
[1.9.0]: https://github.com/kslides/kslides-template/compare/1.8.0...1.9.0
[1.8.0]: https://github.com/kslides/kslides-template/compare/1.7.0...1.8.0
[1.7.0]: https://github.com/kslides/kslides-template/compare/1.6.0...1.7.0
[1.6.0]: https://github.com/kslides/kslides-template/compare/1.5.4...1.6.0
[1.5.4]: https://github.com/kslides/kslides-template/compare/1.5.3...1.5.4
[1.5.3]: https://github.com/kslides/kslides-template/compare/1.5.2...1.5.3
[1.5.2]: https://github.com/kslides/kslides-template/compare/1.5.1...1.5.2
[1.5.1]: https://github.com/kslides/kslides-template/compare/1.5.0...1.5.1
[1.5.0]: https://github.com/kslides/kslides-template/compare/1.4.4...1.5.0
[1.4.4]: https://github.com/kslides/kslides-template/compare/1.4.2...1.4.4
[1.4.2]: https://github.com/kslides/kslides-template/compare/1.4.0...1.4.2
[1.4.0]: https://github.com/kslides/kslides-template/compare/1.3.0...1.4.0
[1.3.0]: https://github.com/kslides/kslides-template/compare/1.2.5...1.3.0
[1.2.5]: https://github.com/kslides/kslides-template/compare/1.2.4...1.2.5
[1.2.4]: https://github.com/kslides/kslides-template/compare/1.2.3...1.2.4
[1.2.3]: https://github.com/kslides/kslides-template/compare/1.2.2...1.2.3
[1.2.2]: https://github.com/kslides/kslides-template/compare/1.2.1...1.2.2
[1.2.1]: https://github.com/kslides/kslides-template/releases/tag/1.2.1
