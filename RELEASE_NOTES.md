# Release Notes

Human-friendly highlights for each release of `kslides-template`. For a
structured per-version diff, see [CHANGELOG.md](CHANGELOG.md).

---

## v1.44.0 — 2026-08-08

**Your deck works under a subpath now.** That is the whole release. If you publish to GitHub Pages, your fork lives at `https://username.github.io/repo_name/` — not at a domain root — and this template had links that assumed otherwise. Every one of them is fixed, half in the sample deck and half upstream in kslides **1.4.0**.

**A nested `.html` deck was loading no reveal.js at all.** `docs/greattalk1/other.html` is declared as `path = "greattalk1/other.html"`, and kslides derived its `../` asset prefix only for directory-style paths. So the page asked for `<site>/greattalk1/revealjs/dist/reveal.css`, got a 404, and rendered as unstyled markup with `Can't find variable: Reveal` in the console. kslides 1.4.0 derives the prefix from the deck's own path depth for every path style; regenerating `/docs` picks it up. The other three decks are unaffected — their asset links were already correct.

**Inter-deck links are relative.** Seven of them were root-absolute, which sent visitors to your GitHub *account* root:

```kotlin
topRightHref = "./"                    // was "/"  — the default 🏠 home link
[🐦 greattalk1/ Slides](./greattalk1)  // was /greattalk1, and two more like it
```

The 🔙 link in each `greattalk` deck needs a prefix that matches its own depth, and the sample deck now carries a comment saying so:

```kotlin
presentation {
  path = "greattalk1"
  presentationConfig { topRightHref = "../#/otherslides" }   // one level down
}

presentation {
  path = "greattalk2.html"
  presentationConfig { topRightHref = "./#/otherslides" }    // at the root — no ../
}
```

Worth knowing why you still write these by hand: kslides 1.4.0 resolves *asset* paths for you — `customTheme { logo(...) }`, `topLeftSvgSrc` / `topRightSvgSrc`, slide background images and videos, `playground`/`letsPlot`/`diagram` iframes — against the output root, from any depth. Corner **links** are navigation targets rather than assets, so they are deliberately left verbatim. The flip side: if your fork hand-compensated an *asset* path with a `../` for a nested deck, 1.4.0 will now double it. Drop the compensation.

**Regenerated `/docs`, three visible differences.** All three come from kslides 1.4.0:

- The favicon link is relative — `favicon.ico` from the root decks, `../favicon.ico` from the ones under `greattalk1/` — instead of the root-absolute `/favicon.ico`, which missed entirely on a project site.
- One favicon `<link>` instead of two. The `rel="shortcut icon"` companion was an IE ≤10 alias; every browser since tokenizes `shortcut` to plain `icon`, so it was the same link emitted twice.
- Viewers can pinch-zoom. The viewport meta dropped `maximum-scale=1.0, user-scalable=no`, which blocked zoom and fails WCAG 2.1 SC 1.4.4 — a deliberate divergence from reveal.js's own template, which still ships it. The two `apple-mobile-web-app-*` metas went with it: reveal.js 3-era carry-over, inert without a web-app manifest.

kslides 1.4.0 also makes the favicon configurable, which the sample deck does not use but your fork might:

```kotlin
presentationConfig {
  favicon = "images/icon.png"   // resolves from any deck depth; "" omits the link entirely
}
```

**Dependency bumps.** kslides **1.4.0**, the Gradle wrapper **9.7.0**, and the ben-manes versions plugin **0.60.0**. kslides-core 1.4.0 ships the same reveal.js distribution as 1.3.0, so `make sync-revealjs` produces no changes this time. One caveat if you keep a jar around: 1.4.0 is source-compatible but **not binary-compatible** — `CssFile` and `JsFile` are data classes that gained an `origin` parameter, moving their constructor, `copy()`, and `componentN()` signatures. Recompile rather than dropping the jar in.

> **Forks:** re-run `fun main()` and commit the regenerated `/docs` — that is what picks up the favicon, viewport, and nested-deck asset fixes. Then audit your own links: any `href` starting with `/` breaks under a GitHub Pages subpath, and a deck below the output root needs `../` on its corner links. If you previously wrote `../` into a `logo()`, `topLeftSvgSrc` / `topRightSvgSrc`, or a slide background path to work around the old behavior, remove it — kslides 1.4.0 resolves those itself now. `make sync-revealjs` is a no-op for this bump.

---

## v1.43.0 — 2026-08-02

**Dependency bumps.** kslides **1.3.0** and the ben-manes versions plugin **0.58.0**. kslides-core 1.3.0 ships a byte-for-byte identical reveal.js distribution to 1.2.0, so unlike the last release there is nothing new to pick up under `docs/revealjs/`.

**Follow-along presenting.** The sample deck now sets `followAlong = true` in its `output { }` block, turning on the other headline kslides 1.3.0 feature. You open a deck with `?present=<token>` and every other browser viewing it tracks your slide and fragment position live. A viewer who clicks ahead on their own breaks away and gets a one-click rejoin; someone arriving late lands on wherever you are.

```kotlin
output {
  enableHttp = true
  followAlong = true
  // presenterToken = "…"   // optional: a stable presenter URL instead of a random per-launch token
}
```

The presenter URLs — token and all — are logged when the server starts, so you can copy one at launch. Two things worth knowing: this is HTTP-only, so nothing is injected into the static `/docs` output and published decks are untouched; and the token rides in the URL, which makes it demo-grade access control rather than a secret. It also composes fine with PDF export — `make pdf` runs its own server and exports all four decks with `followAlong` left on.

**Export your decks to PDF.** kslides 1.3.0 publishes a new `com.kslides:kslides-export` artifact, and this template now wires it up:

```
make pdf                     # every deck -> build/pdf
make pdf DECK=greattalk1     # just one
make clean-pdf
```

Each presentation is served from a temporary HTTP server on an ephemeral port, loaded with reveal.js' `?print-pdf` mode, and printed through headless Chromium — so it works whether or not your `output {}` block turns on HTTP. Settings go in a `pdf { }` block inside `output { }`; the sample deck ships it commented with the four knobs worth knowing:

```kotlin
pdf {
  outputDir = "build/pdf"      // where the PDFs are written
  previewPng = true            // also save a PNG of each deck's first slide
  browserChannel = "chrome"    // use an installed browser instead of downloading Chromium
  exclude("greattalk2.html")   // skip a deck (an explicit DECK=<name> overrides this)
}
```

The first `make pdf` downloads Playwright's bundled Chromium and caches it per user; `browserChannel = "chrome"` or `"msedge"` skips that download.

Two structural notes, because they are visible in files you edit. The entry point is `src/export/kotlin/Export.kt`, in its own `export` source set — that is deliberate, and it is what keeps Playwright out of `build/libs/kslides.jar`, which Heroku runs and which would otherwise gain tens of megabytes for a feature it never uses. Gradle does not compile custom source sets during a normal build, so `check` is given an explicit dependency on `exportClasses`; a broken `Export.kt` fails `./gradlew build` rather than lurking until the next `make pdf`. And `Slides.kt` now hands its deck back as a function:

```kotlin
fun main() {
  kslides(templateSlides())
}

fun templateSlides(): KSlides.() -> Unit =
  {
    // ...the same deck as before
  }
```

`main()` and the PDF exporter run that one block, so the two can never drift apart. The deck's contents are otherwise untouched.

**Regenerated `/docs`, and it matters for printing.** The regenerated HTML differs in exactly one way: kslides 1.3.0 stopped scoping embedded `<style>` blocks to `media="screen"`. That attribute meant every bit of custom deck styling silently disappeared the moment a deck was printed — unstyled corner links rendered full-width, pushing the content down and producing a blank leading page in the PDF. Building the exporter upstream is what surfaced it.

Worth being precise about who this helps: `make pdf` serves your decks from a temporary in-memory server and never reads `/docs`, so its PDFs are identical either way. The regeneration is for readers of your *published* deck who hit Print, or open reveal.js' `?print-pdf` view, on GitHub Pages or Netlify — until the HTML is regenerated, they get the unstyled result.

**The uberjar's resource transformers actually run now.** Shadow 9.6.1 started warning — roughly forty lines of it during `make build` — that paths matched by a resource transformer were subject to the task's default `DuplicatesStrategy.EXCLUDE`. That is not cosmetic: Gradle drops the duplicate copies before the transformer ever sees them, so `mergeServiceFiles()` and the built-in Kotlin module metadata transformer were quietly keeping the first copy rather than merging. `shadowJar` now runs with:

```kotlin
duplicatesStrategy = DuplicatesStrategy.INCLUDE
filesMatching(listOf("public/**", "META-INF/LICENSE*", "META-INF/NOTICE*")) {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
mergeServiceFiles()
```

`INCLUDE` on its own silences the warnings but trades them for genuinely duplicated jar entries — `public/favicon.ico` twice, `META-INF/LICENSE.txt` three times — because those paths have no transformer to merge them. The `filesMatching` block restores first-copy-wins for exactly those, and since the template's own `src/main/resources/public/` is packed ahead of kslides-core's, a fork's `favicon.ico` still overrides the stock one.

One Gradle wrinkle worth knowing if you tweak this further: `filesMatching` actions are not tracked as task inputs, so editing that block alone can hand you a stale `shadowJar` FROM-CACHE. Run `./gradlew shadowJar --rerun-tasks` once after changing it.

**Docs.** The README gained a _The Uberjar_ section — what `make uberjar` and `make uber` produce, and why those two `duplicatesStrategy` lines are there — so the block is not mistaken for boilerplate by anyone extending it. `CLAUDE.md` and `llms.txt` carry the same explanation for contributors and tooling.

> **Forks:** re-run `fun main()` in your `Slides.kt` and commit the regenerated `/docs` so published decks pick up the `media="screen"` print fix. Follow-along presenting is opt-in — add `followAlong = true` to your own `output { }` block if you want it. To pick up PDF export, copy `src/export/kotlin/Export.kt`, the `export` source set / `configurePdfExport()` wiring in `build.gradle.kts`, the `kslides-export` entry in `gradle/libs.versions.toml`, and the `pdf` / `clean-pdf` targets in the `Makefile` — then wrap your own deck in a `fun …Slides(): KSlides.() -> Unit` and point `Export.kt` at it. If your fork customized the `shadowJar` block, also copy the two `duplicatesStrategy` lines across to clear the Shadow warnings.

---

## v1.42.0 — 2026-08-01

**Dependency bumps.** kslides **1.2.0**, Kotlin **2.4.10**, the Shadow plugin **9.6.1**, and the ben-manes versions plugin **0.57.0**. That last one also changed plugin id — it is published as `io.github.ben-manes.versions` now, not `com.github.ben-manes.versions` — so `gradle/libs.versions.toml` points at the new coordinate.

**Copy-code configuration is per-presentation.** The sample deck's `copyCodeConfig { }` block moved out of the global `presentationConfig { }` defaults and into the presentation's own `presentationConfig { }` block. kslides 1.2.0 also widens what the block accepts: `button` chooses when the button appears (`CopyCodeButton.ALWAYS`, `HOVER`, or `FALSE`) and `display` chooses what it renders (`CopyCodeDisplay.TEXT`, `ICONS`, or `BOTH`). The sample now asks for an always-visible icon button:

```kotlin
copyCodeConfig {
  button = CopyCodeButton.ALWAYS
  display = CopyCodeDisplay.ICONS
  copy = "Copy"
  copied = "Copied!"
  timeout = 2000
}
```

Both enums live in `com.kslides.config`. The generated HTML changed to match: only presentations that configure copy-code emit a `copycode` reveal.js option block, and kslides 1.2.0 no longer writes the obsolete `type="text/css"` attribute on embedded `<style>` tags.

**Code blocks that fit the slide.** The sample deck now sets `.reveal pre { font-size: 0.60em; }` and lets long lines wrap (`white-space: pre-wrap`) rather than run off the side of the slide. The slide-definition slides — the ones showing the wider Kotlin source behind each example — carry a `smallcode` class (`classes += "smallcode"`) that drops them to `0.45em`. Because `.reveal .smallcode pre` matches on two classes, it outranks the global rule on specificity regardless of declaration order, which makes it a convenient per-slide override to copy.

**Sample deck cleanup.** `Slides.kt` uses explicit imports instead of `com.kslides.*` / `kotlinx.html.*`, so it is obvious where each DSL entry point comes from.

**Housekeeping.** `make clean-docs` deleted `docs/playground`, a path this template has not generated for some time; it now removes the decks it actually produces (`docs/greattalk1`, `docs/greattalk2.html`, `docs/index.html`). Running `make sync-revealjs` against kslides-core 1.2.0 also brings down a new `docs/revealjs/plugin/mermaid/mermaid.min.js` — 1.2.0 adds a mermaid diagram DSL and ships the plugin with its reveal.js distribution.

> **Forks:** run `make sync-revealjs` after pulling this in and commit the resulting `docs/revealjs/plugin/mermaid/` so statically-published decks match the new core JAR. If your deck sets `copyCodeConfig { }` in the global `presentationConfig { }` defaults block, move it into the presentation's own `presentationConfig { }` block. If you invoke the ben-manes plugin by id anywhere outside the version catalog, switch to `io.github.ben-manes.versions`. And if you renamed the sample presentations, update the `clean-docs` target in the `Makefile` to match.

---

## v1.41.0 — 2026-07-03

**Dependency and toolchain bumps.** Kotlin **2.4.0**, kslides **1.1.0**, the Shadow plugin **9.4.3**, and the Gradle wrapper **9.6.1**.

**Single source of truth for versions and project coordinates.**

- `group` and `version` now live in `gradle.properties` instead of `build.gradle.kts`. Gradle auto-binds them to the `Project.group` / `Project.version` fields, so the manifest's `Implementation-Version` keeps working unchanged.
- The JVM toolchain version (`jvm = "17"`) and the Gradle wrapper version (`gradle-wrapper = "9.6.1"`) are now declared in `gradle/libs.versions.toml`. `build.gradle.kts` reads `jvm` through the version catalog (`libs.versions.jvm.get().toInt()`); the `Makefile`'s `upgrade-wrapper` target reads `gradle-wrapper` via a `sed` shell expansion so `make upgrade-wrapper` stays in sync without a manual edit.
- Repeated string literals in `build.gradle.kts` (`shadowJar`, `clean`, `kslides.jar`, `revealjs`, `docs/revealjs`) were extracted into named `val`s, and the build wiring was grouped into `configureKotlin` / `configureShadowJar` / `configureRevealSync` / `configureVersions` functions so a future rename only changes one place.

**Stricter build, consistent formatting.** The `src/main/kotlin` compilation now runs with `-Xreturn-value-checker=check`, so accidentally ignored return values surface as warnings. It is intentionally left off the test source set, where Kotest's chained-receiver assertions (`shouldBe`, etc.) would otherwise flood the output with false positives. A new `.editorconfig` pins the project's formatting — 2-space indent, LF endings, trailing-whitespace trim.

**Makefile help.** `make` with no arguments now prints a colorized list of targets — each generated from its trailing `##` comment — instead of running a dependency check. The dependency-update target was renamed from `make versioncheck` to `make versions`.

**Sample deck & assets.** The sample `Slides.kt` uses the kslides 1.1.0 `lineOffset` parameter (renamed from `lineOffSet`). `revealjs.png` is no longer committed twice — it lives only in `docs/images/` and is copied into the resources jar at build time via `processResources`. A few unused reveal.js sample assets (`beeping.wav`, `beeping.txt`, `video.mp4`) were removed.

> **Forks:** if your fork hand-edited `group`, `version`, or the `jvmToolchain(17)` line in `build.gradle.kts`, move those edits to `gradle.properties` (group/version) or `gradle/libs.versions.toml` (jvm) when you pull this in. If your tooling invoked `make versioncheck`, switch it to `make versions`. If you copied the `codeSnippet { lineOffSet = … }` pattern from the sample deck, rename it to `lineOffset`.

---

## v1.40.0 — 2026-04-29

**Build modernization.** The project has migrated to the Gradle Kotlin DSL
and the Gradle version catalog. All plugin and library versions now live in
`gradle/libs.versions.toml`, the Gradle wrapper has been upgraded to
**9.5.0**, and the Shadow plugin has moved to the maintained
`com.gradleup.shadow` fork (9.4.1). The `ben-manes-versions` plugin used by
`make versioncheck` is now at 0.54.0.

**Simpler fat-jar build.** The previous setup had a `shadowJar` task plus a
`Jar` wrapper called `uberjar` that re-zipped the shadow output to give it
a stable filename and a custom manifest. The wrapper is gone — `shadowJar`
itself now produces `build/libs/kslides.jar` with the
`Implementation-Title` / `Implementation-Version` / `Built-JDK` /
`Main-Class` manifest attributes. `make uberjar` now invokes the
`shadowJar` Gradle task directly. The output filename and manifest are
unchanged, so `Procfile` and `make uber` keep working. The
`shadowJar` task no longer excludes `LICENSE*`, so third-party license
files are preserved in the uberjar.

**`stage` task is now discoverable.** `gradle tasks` lists it under the
`build` group with a description.

A new `syncRevealJs` Gradle task unpacks the reveal.js assets bundled in
the `kslides-core` JAR into `docs/revealjs/`, so static publishing targets
like Netlify and GitHub Pages keep working when `kslides-core` ships
updated reveal.js content. Run `make sync-revealjs` after a `kslides-core`
upgrade.

**Makefile polish.** All targets are declared `.PHONY` so they keep working
once `build/` (or any other directory matching a target name) exists.
`make uber` now honours `$PORT` (matching `Procfile`) and falls back to
`8080` for local runs.

`mavenLocal()` has been removed from both repository blocks in
`settings.gradle.kts` — the build no longer resolves dependencies or
plugins from a developer's local Maven cache.

The README has been rewritten to document the Kotlin DSL + version-catalog
setup (the old Groovy `build.gradle` snippet is gone). `LICENSE` was
renamed to `LICENSE.txt`. `CHANGELOG.md`, `RELEASE_NOTES.md`, and
`CLAUDE.md` are now part of the template.

The legacy `build.gradle`, `settings.gradle`, and `.travis.yml` have been
removed.

> **Forks:** mirror the new `build.gradle.kts`, `settings.gradle.kts`, and
> `gradle/libs.versions.toml` into your fork (or regenerate from this
> template) — there is no in-place migration path from the old Groovy
> build. If your downstream tooling invoked the `uberjar` Gradle task
> directly, switch to `shadowJar`.

---

## v1.32.0 — 2024-12-12

A maintenance roll-up covering 2024.

- Kotlin **2.1.0** (this release).
- Kotlin **2.0.0** and reveal.js **5.1.0** (landed mid-cycle and shipped
  here for the first tagged release on that combo).

No template structure changes — just dependency bumps.

---

## v1.30.0 — 2023-11-01

The big "modern reveal.js" refresh.

- Kotlin **1.9.10**.
- reveal.js **5.0.0**.
- CopyCode **1.1.2**.
- kslides-core **0.20.0**.
- Adds support for the reveal.js **Scroll View** layout.

This is the first release that ships against the reveal.js 5 line, so any
custom themes or plugins that were pinned to reveal.js 4 should be
re-tested.

---

## v1.10.0 — 2023-05-23

Static-hosting friendliness.

- Bundles reveal.js static content directly in the template, so you can
  publish the generated `docs/` directory to GitHub Pages or Netlify
  without an additional build step.
- kslides **0.19.0**.

---

## v1.9.0 — 2023-05-16

- Kotlin **1.8.21**.

---

## v1.8.0 — 2023-04-10

- Kotlin **1.8.20**.

---

## v1.7.0 — 2023-01-01

- Kotlin **1.8.0**. First release on the Kotlin 1.8 line.

---

## v1.6.0 — 2022-12-03

Maintenance release rolling up the 1.5.x jar refreshes into a new minor
version.

---

## v1.5.4 — 2022-10-17

- Bundled-jar refresh.

## v1.5.3 — 2022-10-03

- Updated the `srcref` jar (drives the GitHub source-link feature added in
  1.2.2).

## v1.5.2 — 2022-10-02

- kslides **0.15.1**.

## v1.5.1 — 2022-09-19

- kslides **0.14.1**.

## v1.5.0 — 2022-09-18

- kslides **0.14.0**.

---

## v1.4.4 — 2022-09-10

- kslides **0.13.3**.

## v1.4.2 — 2022-08-30

- kslides **0.13.2** (and 0.13.1 along the way).

## v1.4.0 — 2022-08-09

Rollup of mid-2022 jar refreshes and README polish.

---

## v1.3.0 — 2022-07-16

- kslides **0.11.0**.
- Kotlin **1.7.0**.

---

## v1.2.5 — 2022-06-11

- Kotlin **1.7.0** baseline (kslides held at the 1.2.x line).

## v1.2.4 — 2022-06-05

- Bundled-jar refresh.

## v1.2.3 — 2022-05-25

- kslides **0.10.3**.
- Adds a default `favicon.ico` to the template.

## v1.2.2 — 2022-05-25

- kslides **0.10.2**.
- Adds support for `srcref` GitHub source links — embed links to the exact
  file/line on GitHub from inside a slide.

## v1.2.1 — 2022-05-19 — first tagged release

- Adds support for specifying CSS inside playground content blocks, so
  embedded interactive examples can carry their own styling.

---

## Pre-1.2.1 history

These changes predate the first git tag and are reconstructed from the
commit log.

### May 2022 — the kslides 0.8.x → 0.10.x line
Rapid iteration through kslides 0.8.1 → 0.10.0, including the 0.9.0 jump.
The legacy `CHANGELOG.md` from this era was removed during cleanup.

### April 2022 — moved under the `kslides` GitHub org
The template repository was moved into the `kslides` organization. The
default `Slides.kt` was tidied up and given a sample image. Routine
upstream jar refreshes throughout the month.

### March 2022 — static-hosting support landed
This is when the template became deploy-ready for GitHub Pages and
Netlify:

- Renamed `/public` to `/docs` so GitHub Pages can serve from the default
  location.
- Checked in reveal.js assets for static serving.
- Added `netlify.toml` for Netlify deploys and `app.json` for Heroku.
- Added support for multiple levels of output and a static menu.
- Upgraded to kslides 0.6.4 and successive jars.

### 2021
README updates only.

### 2021-02-15 — initial commit
Repository created.
