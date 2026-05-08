.PHONY: default build-all clean build uberjar uber dist stage sync-revealjs versioncheck upgrade-wrapper

# Versions are sourced from gradle/libs.versions.toml so there is one source of truth.
GRADLE_VERSION := $(shell awk -F' *= *' '/^gradle *=/ {gsub(/"/, "", $$2); print $$2; exit}' gradle/libs.versions.toml)

ifeq ($(strip $(GRADLE_VERSION)),)
$(error Could not determine gradle version from gradle/libs.versions.toml)
endif

default: versioncheck

# stage already depends on clean in Gradle (see build.gradle.kts).
build-all: stage

clean:
	./gradlew clean

build: clean
	./gradlew build -xtest

uberjar:
	./gradlew shadowJar

# Matches Procfile (-DPORT=$PORT); falls back to 8080 for local runs.
uber: uberjar
	java -DPORT=$${PORT:-8080} -jar build/libs/kslides.jar

dist:
	./gradlew installDist

stage:
	./gradlew stage

#clean-docs:
#	rm -rf docs/playground docs/letplot

sync-revealjs:
	./gradlew syncRevealJs

# ben-manes' dependencyUpdates is not configuration-cache compatible.
versioncheck:
	./gradlew dependencyUpdates --no-configuration-cache --no-parallel

upgrade-wrapper:
	./gradlew wrapper --gradle-version=$(GRADLE_VERSION) --distribution-type=bin