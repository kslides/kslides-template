# kslides Template

[![Build Status](https://app.travis-ci.com/kslides/kslides-template.svg?branch=master)](https://app.travis-ci.com/kslides/kslides-template)
[![Kotlin version](https://img.shields.io/badge/kotlin-1.6.21-red?logo=kotlin)](http://kotlinlang.org)

A template repo for authoring [kslides](https://github.com/kslides/kslides) presentation.

## Creating Slide Content

Open your newly created kslides repo with [IntelliJ](https://www.jetbrains.com/idea/download/) and edit
the `src/main/kotlin/Slides.kt` file. The [kslides](https://github.com/kslides/kslides) repo describes
the various _kslides_ sections.

## Deployment Options

### Deploy to GitHub Pages

1) Go to your kslides repo on GitHub 
2) Click on _Settings_ -> _Pages_ 
3) Under **Source**, choose the _master_ branch and the _/docs_ folder and click on _Save_
4) Open the _src/main/kotlin/Slides.ht_ file
5) Insure `enableFileSystem = true` is set in the _output_ section
6) Click on the green arrow next to the `fun main()` declaration to run the program and generate the html content in the _/docs_ folder
7) Add the newly generated html files in the /docs folder to git 
8) Commit and push the changes to GitHub  
9) Wait a minute or so and your slides will be available at _https://username.github.io/repo_name/_

### Deploy to Netlify

1) Create an account on [Netlify](https://www.netlify.com/)
2) Click on _Add a new site_ -> _Import and existing project_
3) Click on the **GitHub** button and select your slides repo
4) Go to your Netlify dashboard and click on the newly added repo.
5) To edit your Netlify subdomain name, click on the **Domain settings** button and then click on the **Options** button 
6) Your repo already has a _netlify.toml_ file that will instruct Netlify to use your _/docs_ folder as a html source
7) Open the _src/main/kotlin/Slides.ht_ file
8) Insure `enableFileSystem = true` is set in the _output_ section
9) Click on the green arrow next to the `fun main()` declaration to run the program and generate the html content in the _/docs_ folder
10) Add the newly generated html files in the /docs folder to git
11) Commit and push the changes to GitHub
12) Wait a minute or so and your slides will be available at _https://site-name/netlify.app/_


### Deploy to Heroku

1) Create an account on [Heroku](https://www.heroku.com/)
2) Install the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli#install-the-heroku-cli)
3) Open the _src/main/kotlin/Slides.ht_ file
4) Insure `enableHttp = true` is set in the _output_ section
5) Create a new Heroku app with: `heroku create slideshow_name`
6) Commit and push the changes to Heroku with: `git push heroku master`
7) Open the app in a browser with: `heroku open`



