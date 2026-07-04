# Release Notes

Human-friendly highlights for each release of `kslides-template`. For a
structured per-version diff, see [CHANGELOG.md](CHANGELOG.md).

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
