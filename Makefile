.PHONY: default help build-all clean build uberjar uber dist stage clean-docs sync-revealjs \
        versions upgrade-wrapper _require-gradle-version

# Versions are sourced from gradle/libs.versions.toml so there is one source of truth.
GRADLE_VERSION := $(shell sed -n 's/^gradle-wrapper = "\(.*\)"/\1/p' gradle/libs.versions.toml)

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

clean-docs:
	rm -rf docs/playground

sync-revealjs: ## Sync reveal.js assets from kslides-core into docs/revealjs
	./gradlew syncRevealJs

versions: ## Check for dependency updates
	./gradlew dependencyUpdates --no-configuration-cache --no-parallel

# Gradle's documented upgrade procedure: the first run rewrites
# gradle-wrapper.properties using the *old* wrapper jar; the second run
# regenerates the wrapper itself with the new version.
upgrade-wrapper: _require-gradle-version ## Re-pin Gradle wrapper to version from libs.versions.toml
	./gradlew wrapper --gradle-version=$(GRADLE_VERSION) --distribution-type=bin
	./gradlew wrapper --gradle-version=$(GRADLE_VERSION) --distribution-type=bin

_require-gradle-version:
	@[ -n "$(GRADLE_VERSION)" ] || { echo "ERROR: Could not determine gradle version from gradle/libs.versions.toml" >&2; exit 1; }
