# kslides Template

![GitHub release (latest by date)](https://img.shields.io/github/v/release/kslides/kslides-template)
[![Kotlin version](https://img.shields.io/badge/kotlin-2.4.0-red?logo=kotlin)](http://kotlinlang.org)
[![Netlify Status](https://api.netlify.com/api/v1/badges/ed16ddd9-ab47-4e9d-8e37-807edded7a6e/deploy-status)](https://app.netlify.com/sites/kslides-template/deploys)

A template repo for authoring [kslides](https://github.com/kslides/kslides) presentation.

## Prerequisites

- **JDK 17.** The Gradle toolchain auto-provisions one if you don't have it locally.
- **[IntelliJ IDEA](https://www.jetbrains.com/idea/download/)** — recommended; the primary author workflow is the green-arrow run on `fun main()` in `Slides.kt`.
- **Git.**

## Getting Started

[![Template](https://img.shields.io/endpoint?color=%232A9EEE&logo=github&style=flat&url=https%3A%2F%2Fraw.githubusercontent.com%2Fkslides%2Fkslides%2Fmaster%2Fdocs%2Fshields%2Ftemplate.json)](https://github.com/kslides/kslides-template/generate)

To create a kslides presentation, generate a new repository using this
repo as a [template](https://github.com/kslides/kslides-template/generate).

All changes to this template are documented in
[CHANGELOG.md](https://github.com/kslides/kslides-template/blob/master/CHANGELOG.md)
(structured per-version diff) and
[RELEASE_NOTES.md](https://github.com/kslides/kslides-template/blob/master/RELEASE_NOTES.md)
(narrative highlights). Check back occasionally for any changes that should be incorporated into your kslides repo.

## Creating Slide Content

Open your newly created kslides repo with [IntelliJ](https://www.jetbrains.com/idea/download/) and edit
the `src/main/kotlin/Slides.kt` file. The [kslides](https://github.com/kslides/kslides) repo
[README.md](https://github.com/kslides/kslides/blob/master/README.md)
describes the various _kslides_ blocks.

### Project Structure

- `src/main/kotlin/Slides.kt` — your deck (the only file most users touch).
- `src/main/resources/public/` — static assets when serving over HTTP.
- `docs/` — generated HTML and static assets when publishing to GitHub Pages or Netlify.
- `gradle/libs.versions.toml` — kslides, Kotlin, plugin, JVM, and Gradle versions.
- `gradle.properties` — `group` and `version` (see _Customizing Your Fork_ below).
- `build.gradle.kts` — `mainName` (see _Customizing Your Fork_ below).

### Output Modes

Configured per-presentation in `Slides.kt` via the `output {}` block:

- `enableFileSystem = true` — write static HTML into `/docs` for GitHub Pages or Netlify.
- `enableHttp = true` — run an embedded HTTP server (used by Heroku and for local preview).

Both can be enabled at the same time.

### Static Content

Presentations served by HTTP load static content from `/src/main/resources/public`, whereas
filesystem presentations load static content from `/docs`.

Make sure to run `./gradlew clean build` after making changes to `/src/main/resources/public`.

### Images (`docs/images`)

`docs/images/` is the **single source of truth** for image assets. It is committed to git so
GitHub Pages and Netlify can serve the images directly alongside the generated HTML in `/docs`.

To avoid duplicating the same files under `src/main/resources/public/images/`, the Gradle
`processResources` task (in `build.gradle.kts`) copies `docs/images/` into the resources jar
at `public/images/` at build time. HTTP-served decks therefore load the same images without
a second committed copy.

To add or update an image:

1) Drop (or replace) the file in `docs/images/`.
2) Run `./gradlew clean build` so HTTP-served decks pick up the change via `processResources`.
3) Commit the file in `docs/images/` — do **not** add a parallel copy under `src/main/resources/public/images/`.

### reveal.js Assets (`docs/revealjs`)

The reveal.js distribution lives inside the `kslides-core` JAR (at classpath `revealjs/**`)
and that JAR is the single source of truth. The `syncRevealJs` Gradle task unpacks those
assets onto disk at `docs/revealjs/` so the static HTML in `/docs` has working JS/CSS
references when published to GitHub Pages or Netlify.

Run it whenever you bump `kslides-core` in `gradle/libs.versions.toml`:

```
make sync-revealjs        # or: ./gradlew syncRevealJs
```

Commit the refreshed `docs/revealjs/` along with the version bump so deployed decks load
matching reveal.js assets.

### Customizing Your Fork

In `gradle.properties`:

- `group=com.github.username` — change to your name or org.
- `version=...` — your template version, independent of the kslides library version (which lives in `gradle/libs.versions.toml`).

In `build.gradle.kts`:

- `mainName = "SlidesKt"` — only change if you rename `Slides.kt`. Required for HTTP-served decks.

## Deployment Options

### Deploy to GitHub Pages

1) Go to your kslides content repo on GitHub 
2) Click on _Settings_ -> _Pages_ 
3) Under **Source**, choose the _master_ branch and the _/docs_ folder and click on _Save_
4) Open the _src/main/kotlin/Slides.kt_ file
5) Ensure the _output{}_ block contains: `enableFileSystem = true` 
6) Click on the green arrow next to the `fun main()` declaration to run the program and generate the html content in the _/docs_ folder
7) Add the newly generated html files in the `/docs` folder to git 
8) Commit and push the changes to GitHub  
9) Wait a minute or so and your slides will be available at _https://username.github.io/repo_name/_

### Deploy to Netlify

1) Create an account on [Netlify](https://www.netlify.com/)
2) Click on _Add a new site_ -> _Import and existing project_
3) Click on the **GitHub** button and select your slides repo
4) Go to your Netlify dashboard and click on the newly added repo.
5) To edit your Netlify subdomain name, click on the **Domain settings** button and then click on the **Options** button 
6) Your repo already has a _netlify.toml_ file that will instruct Netlify to use your `/docs` folder as an html source
7) Open the _src/main/kotlin/Slides.kt_ file
8) Insure `enableFileSystem = true` is set in the _output{}_ block
9) Click on the green arrow next to the `fun main()` declaration to run the program and generate the html content in the _/docs_ folder
10) Add the newly generated html files in the `/docs` folder to git
11) Commit and push the changes to GitHub
12) Wait a minute or so and your slides will be available at _https://site-name.netlify.app/_


### Deploy to Heroku

1) Create an account on [Heroku](https://www.heroku.com/)
2) Install the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli#install-the-heroku-cli)
3) Open the _src/main/kotlin/Slides.kt_ file
4) Ensure the _output{}_ block contains: `enableHttp = true`
5) Create a new Heroku app with: `heroku create slideshow_name`
6) Commit and push the changes to Heroku with: `git push heroku master`
7) Open the app in a browser with: `heroku open`

## Updating kslides

- Bump library, plugin, JVM, and Gradle versions in `gradle/libs.versions.toml` (not in `build.gradle.kts` or the `Makefile`).
- After bumping `gradle-wrapper` in `libs.versions.toml`, run `make upgrade-wrapper` to re-pin `gradle/wrapper/gradle-wrapper.properties`.
- After bumping `kslides-core`, run `make sync-revealjs` (or `./gradlew syncRevealJs`) to refresh `docs/revealjs/` from the new core JAR — otherwise statically-published decks will load stale reveal.js assets.
- `make versions` lists out-of-date dependencies.
