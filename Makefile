default: versioncheck

build-all: clean stage

clean:
	./gradlew clean

build: clean
	./gradlew build -xtest

uberjar:
	./gradlew shadowJar

uber: uberjar
	java -jar build/libs/kslides.jar

dist:
	./gradlew installDist

stage:
	./gradlew stage

#clean-docs:
#	rm -rf docs/playground docs/letplot

sync-revealjs:
	./gradlew syncRevealJs

versioncheck:
	./gradlew dependencyUpdates --no-configuration-cache --no-parallel

upgrade-wrapper:
	./gradlew wrapper --gradle-version=9.5.0 --distribution-type=bin