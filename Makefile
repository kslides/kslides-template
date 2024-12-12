default: versioncheck

build-all: clean stage

clean:
	./gradlew clean

build: clean
	./gradlew build -xtest

uberjar:
	./gradlew uberjar

uber: uberjar
	java -jar build/libs/kslides.jar

dist:
	./gradlew installDist

stage:
	./gradlew stage

cleandocs:
	rm -rf docs/playground docs/plotly

versioncheck:
	./gradlew dependencyUpdates

upgrade-wrapper:
	./gradlew wrapper --gradle-version=8.11.1 --distribution-type=bin