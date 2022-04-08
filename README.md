# kslides Template

[![Build Status](https://app.travis-ci.com/kslides/kslides-template.svg?branch=master)](https://app.travis-ci.com/kslides/kslides-template)
[![Kotlin version](https://img.shields.io/badge/kotlin-1.6.20-red?logo=kotlin)](http://kotlinlang.org)

A [template repo](https://github.com/kslides/kslides-template/generate) for building
a [kslides](https://github.com/kslides/kslides) presentation.



## Deploy to GitHub Pages

1) Open your browser to your slides repo on GitHub 
2) Click on _Settings_ -> _Pages_ 
3) Under **Source**, choose the _master_ branch and the _/docs_ folder and click on _Save_
4) Open the _src/main/kotlin/Slides.ht_ file
5) Uncomment `enableFileSystem = true` in the _output_ section
6) Click on the green arrow next to the `fun main()` declaration to tun the program and generate the html content in the _/docs_ folder
7) Add the newly generated html files in the /docs folder to git 
8) Commit and push the changes to GitHub  
9) Wait a minute or so and your slides will be available at _https://username.github.io/repo_name/_

## Deploy to Netlify

1) Create an account on [Netlify](https://www.netlify.com/)
2) Click on _Add a new site_ -> _Import and existing project_
3) Click on the **GitHub** button and select your slides repo
4) Go to your Netlify dashboard and click on the newly added repo.
5) To edit your Netlify subdomain name, click on the **Domain settings** button and then click on the **Options** button 
6) Your repo already has a _netlify.toml_ file that will instruct Netlify to use your _/docs_ folder as a html source
7) Uncomment `enableFileSystem = true` in the _output_ section
8) Click on the green arrow next to the `fun main()` declaration to tun the program and generate the html content in the _/docs_ folder
9) Add the newly generated html files in the /docs folder to git
10) Commit and push the changes to GitHub
11) Wait a minute or so and your slides will be available at _https://site-name/netlify.app/_


## Deploy to Heroku

1) Create an account on [Heroku](https://www.heroku.com/)
2) Install the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli#install-the-heroku-cli)
3) Uncomment `enableHttp = true` in the _output_ section
4) Create a new Heroku app with: `heroku create slideshow_name`
5) Push the repo to Heroku with: `git push heroku master`
6) Open the app in a browser with: `heroku open`



