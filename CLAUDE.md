# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A GitHub template for authoring [kslides](https://github.com/kslides/kslides) presentations. End users hit "Use this template" to fork it, then edit `src/main/kotlin/Slides.kt` to author their deck. Changes here flow downstream to every fork, so keep the template minimal and back-compat where possible — see `CHANGELOG.md` (per-version diff) and `RELEASE_NOTES.md` (narrative) for the running history users are expected to track.

## Build & run

The Makefile is a thin wrapper over `./gradlew`:

- `make` (no args) defaults to `versioncheck`
- `make build` — `./gradlew clean build -xtest` (tests are intentionally skipped here)
- `make build-all` — alias for `make stage`; the Gradle `stage` task already declares `dependsOn("clean", "shadowJar")`
- `make uberjar` — invokes `./gradlew shadowJar`, which produces `build/libs/kslides.jar` directly with the custom manifest (there is no separate `uberjar` Gradle task — that wrapper was collapsed into `shadowJar` in 1.40.0)
- `make uber` — build the uberjar and run it (`java -DPORT=$${PORT:-8080} -jar build/libs/kslides.jar`); honours `$PORT` if set, otherwise falls back to 8080
- `make stage` — `./gradlew stage`; this is what Heroku invokes (see `Procfile`)
- `make dist` — `./gradlew installDist`
- `make sync-revealjs` — runs the `syncRevealJs` task (see Architecture)
- `make versioncheck` — `./gradlew dependencyUpdates` (ben-manes plugin); must use `--no-configuration-cache --no-parallel`
- `make upgrade-wrapper` — re-pin the Gradle wrapper version (hardcoded; bump in lockstep with `gradle/wrapper/gradle-wrapper.properties`)

Run `fun main()` in `Slides.kt` directly from IntelliJ (green arrow) to generate slides — that's the primary author workflow, not a Gradle task.

There are no tests in this template. `make build` passes `-xtest` deliberately.

## Architecture

**Two output modes, configured per-presentation in `Slides.kt` via the `output {}` block:**
- `enableFileSystem = true` — generates static HTML into `/docs/` for GitHub Pages or Netlify.
- `enableHttp = true` — runs an embedded HTTP server (used for Heroku via the uberjar).

Both can be enabled simultaneously.

**Static asset roots differ by mode:**
- HTTP-served presentations load static assets from `src/main/resources/public/`
- Filesystem-generated presentations load static assets from `/docs/`

This is the single most surprising thing about the project — the same logical asset lives in two different locations depending on output mode. Run `./gradlew clean build` after editing `src/main/resources/public/`.

**reveal.js asset sync.** The reveal.js distribution is the single source of truth in the `kslides-core` JAR (at classpath `revealjs/**`). The `syncRevealJs` Gradle task unpacks those into `docs/revealjs/` so static-output decks deployed to GitHub Pages / Netlify have working JS/CSS references. Re-run `make sync-revealjs` after a `kslides-core` upgrade.

**Deployment artifacts already wired in:**
- `netlify.toml` — points Netlify at `docs/` as the publish base
- `Procfile` + `system.properties` + `app.json` — Heroku config (Java 17 runtime, `web` dyno runs the uberjar)
- `.nojekyll` — disables Jekyll on GitHub Pages so reveal.js's `_*` paths aren't filtered

## Build configuration

- Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`).
- Version catalog in `gradle/libs.versions.toml` — bump kslides/kotlin/shadow there, not in `build.gradle.kts`.
- JVM toolchain: 17 (foojay resolver convention; users without a local JDK 17 get one auto-provisioned).
- `mainName` in `build.gradle.kts` (currently `"SlidesKt"`) must match the Kotlin file users want to serve over HTTP. The comment above it is the documented extension point for forks.
- `version` in `build.gradle.kts` is the *template* version, not the kslides library version (which lives in `libs.versions.toml`). Keep them distinct.
- `shadowJar` is configured directly (no `Jar`-typed wrapper task) — it sets `archiveFileName = "kslides.jar"` and the `Implementation-*` / `Main-Class` manifest attributes itself. To customize the uberjar, edit the `tasks.named<ShadowJar>("shadowJar") { … }` block.
- Configuration cache is on (`org.gradle.configuration-cache=true` in `gradle.properties`). New tasks must be CC-compatible; ben-manes' `dependencyUpdates` is the only known incompatible task and `make versioncheck` opts out for it.
- Repositories are locked down: `settings.gradle.kts` uses `FAIL_ON_PROJECT_REPOS` and resolves only from `mavenCentral()` (no `mavenLocal()` — local snapshots can't be picked up without temporarily editing the file).

## Conventions for edits to this template

- Anything user-facing (groupId placeholder `com.github.username`, `mainName`, `Slides.kt` example content) is meant to be edited downstream — keep it obvious and commented, don't over-engineer.
- For any change a downstream fork would need to mirror, update **both** `CHANGELOG.md` (Keep-a-Changelog format, per-version structured entry) and `RELEASE_NOTES.md` (narrative highlights). Tag-driven releases also need the template `version` in `build.gradle.kts` bumped — keep all three in sync in the same commit.
