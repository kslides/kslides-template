# kslides Template

![GitHub release (latest by date)](https://img.shields.io/github/v/release/kslides/kslides-template)
[![Build Status](https://app.travis-ci.com/kslides/kslides-template.svg?branch=master)](https://app.travis-ci.com/kslides/kslides-template)
[![Kotlin version](https://img.shields.io/badge/kotlin-1.7.10-red?logo=kotlin)](http://kotlinlang.org)
[![Netlify Status](https://api.netlify.com/api/v1/badges/ed16ddd9-ab47-4e9d-8e37-807edded7a6e/deploy-status)](https://app.netlify.com/sites/kslides-template/deploys)

A template repo for authoring [kslides](https://github.com/kslides/kslides) presentation.

## Getting Started

[![Template](https://img.shields.io/endpoint?color=%232A9EEE&logo=github&style=flat&url=https%3A%2F%2Fraw.githubusercontent.com%2Fkslides%2Fkslides%2Fmaster%2Fdocs%2Fshields%2Ftemplate.json)](https://github.com/kslides/kslides-template/generate)

To create a kslides presentation, generate a new repository using this
repo as a [template](https://github.com/kslides/kslides-template/generate).

All changes to this template are documented in the 
[CHANGELOG.md](https://github.com/kslides/kslides-template/blob/master/CHANGELOG.md) file.
Check back occasionally for any changes that should to be incorporated into your kslides repo.

## Creating Slide Content

Open your newly created kslides repo with [IntelliJ](https://www.jetbrains.com/idea/download/) and edit
the `src/main/kotlin/Slides.kt` file. The [kslides](https://github.com/kslides/kslides) repo 
[READEME.md](https://github.com/kslides/kslides/blob/master/README.md) 
describes the various _kslides_ blocks.

### Static Content

Presentations served by HTTP load static content from `/src/main/resources/public`, whereas
filesystem presentations load static content from `/docs`.

Make sure to run `./gradlew clean build` after making changes to `/src/main/resources/public`.

## Deployment Options

### Deploy to GitHub Pages

1) Go to your kslides content repo on GitHub 
2) Click on _Settings_ -> _Pages_ 
3) Under **Source**, choose the _master_ branch and the _/docs_ folder and click on _Save_
4) Open the _src/main/kotlin/Slides.ht_ file
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
7) Open the _src/main/kotlin/Slides.ht_ file
8) Insure `enableFileSystem = true` is set in the _output{}_ block
9) Click on the green arrow next to the `fun main()` declaration to run the program and generate the html content in the _/docs_ folder
10) Add the newly generated html files in the `/docs` folder to git
11) Commit and push the changes to GitHub
12) Wait a minute or so and your slides will be available at _https://site-name/netlify.app/_


### Deploy to Heroku

1) Create an account on [Heroku](https://www.heroku.com/)
2) Install the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli#install-the-heroku-cli)
3) Open the _src/main/kotlin/Slides.ht_ file
4) Ensure the _output{}_ block contains: `enableHttp = true`
5) Create a new Heroku app with: `heroku create slideshow_name`
6) Commit and push the changes to Heroku with: `git push heroku master`
7) Open the app in a browser with: `heroku open`

## build.gradle

The `build.gradle` file uses these repositories:

```groovy
repositories {
    google()
    mavenCentral()
    maven { url = 'https://maven.pkg.jetbrains.space/mipt-npm/p/sci/maven' }
    maven { url = 'https://jitpack.io' }
}
```

and has these dependencies:

```groovy
dependencies {
    implementation "com.github.kslides.kslides:kslides-core:$kslides_version"
    implementation "io.ktor:ktor-server-html-builder:$ktor_version"
    implementation "org.jetbrains.kotlin-wrappers:kotlin-css:$css_version"
    implementation "com.github.pambrose:srcref:$srcref_version"
    implementation "space.kscience:plotlykt-core:$plotly_version"
}
```