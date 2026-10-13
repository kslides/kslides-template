.PHONY: default help build-all clean build uberjar uber dist stage docs clean-docs sync-revealjs \
        pdf clean-pdf versions versions-all upgrade-wrapper _require-gradle-version

# The wrapper's current Gradle version; `make upgrade-wrapper GRADLE_VERSION=x.y.z` moves to another one.
# gradle-wrapper.properties is the one place the version is kept, so Dependabot's wrapper PRs leave
# nothing else to bump.
GRADLE_VERSION ?= $(shell sed -n 's/^distributionUrl=.*gradle-\(.*\)-bin\.zip$$/\1/p' gradle/wrapper/gradle-wrapper.properties)

default: help

help:  ## Show this help (list of targets)
	@awk 'BEGIN {FS = ":.*?## "; printf "Usage: make <target>\n\nTargets:\n"} \
		/^[a-zA-Z0-9_-]+:.*?## / {printf "  \033[36m%-22s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# stage already depends on clean in Gradle (see build.gradle.kts).
build-all: stage ## Alias for stage

clean: ## Run ./gradlew clean
	./gradlew clean

build: clean ## Clean build, skipping tests
	./gradlew build -x test

uberjar: ## Build the shaded uberjar (build/libs/kslides.jar)
	./gradlew shadowJar

# Matches Procfile (-DPORT=$PORT); falls back to 8080 for local runs.
uber: uberjar ## Build and run the uberjar (honours $PORT, defaults to 8080)
	java -DPORT=$${PORT:-8080} -jar build/libs/kslides.jar

dist: ## Build a runnable distribution (./gradlew installDist)
	./gradlew installDist

stage: ## Heroku stage build (clean + shadowJar)
	./gradlew stage

# The same output `fun main()` in Slides.kt writes, without the HTTP server it also starts.
# docs is phony because the docs/ directory exists: make would otherwise call it up to date.
docs: ## Generate the presentations into docs/ without starting the HTTP server
	./gradlew generateDocs

clean-docs:
	rm -rf docs/greattalk1 docs/greattalk2.html docs/index.html

sync-revealjs: ## Sync reveal.js assets from kslides-core into docs/revealjs
	./gradlew syncRevealJs

# The first run downloads Playwright's Chromium; set browserChannel in the deck's
# output { pdf { } } block to use an already-installed browser instead.
pdf:  ## Export the presentations to PDF in build/pdf (optionally DECK=<name>)
	./gradlew exportPdf $(if $(DECK),-Pdeck=$(DECK))

clean-pdf:  ## Remove the exported PDFs
	rm -rf build/pdf

# dependencyUpdates is marked incompatible with the configuration cache (build.gradle.kts), so a
# run with the cache on builds an entry only to discard it, and says so. --no-configuration-cache
# skips the cache for this run instead. build.gradle.kts gives the task a formatter that lists only
# what is behind; versions-all names the plugin's own plain report, which also lists everything up
# to date.
versions: ## List the dependencies with newer versions, and any that could not be checked
	./gradlew dependencyUpdates --no-configuration-cache --no-parallel

versions-all: ## The plugin's full dependency report, up-to-date dependencies included
	./gradlew dependencyUpdates --no-configuration-cache --no-parallel --output-formatter=plain

# Gradle's documented upgrade procedure: the first run rewrites
# gradle-wrapper.properties using the *old* wrapper jar; the second run
# regenerates the wrapper itself with the new version.
# Example: make upgrade-wrapper GRADLE_VERSION=9.8.1
upgrade-wrapper: _require-gradle-version ## Upgrade the Gradle wrapper (GRADLE_VERSION=x.y.z; default: the current one)
	./gradlew wrapper --gradle-version=$(GRADLE_VERSION) --distribution-type=bin
	./gradlew wrapper --gradle-version=$(GRADLE_VERSION) --distribution-type=bin

_require-gradle-version:
	@[ -n "$(GRADLE_VERSION)" ] || { echo "ERROR: Could not determine the Gradle version from gradle/wrapper/gradle-wrapper.properties" >&2; exit 1; }
