# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Migrated build to Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`) and the Gradle version catalog (`gradle/libs.versions.toml`).
- Upgraded Gradle wrapper to 9.4.1.
- Upgraded Shadow plugin to `com.gradleup.shadow` 9.4.1.
- Adopted property-assignment syntax in build scripts and bound the `shadowJar` task provider once for the `uberjar` task.
- Added `syncRevealJs` task that unpacks reveal.js assets from the `kslides-core` JAR into `docs/revealjs/`.

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

[Unreleased]: https://github.com/kslides/kslides-template/compare/1.30.0...HEAD
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
