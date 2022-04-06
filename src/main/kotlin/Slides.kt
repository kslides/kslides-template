import com.kslides.*
import kotlinx.html.*

fun main() {

  kslides {

    presentationDefault {
      history = true
      transition = Transition.SLIDE
      transitionSpeed = Speed.SLOW

      slideConfig {
        backgroundColor = "blue"
      }
    }

    output {
      // Write the presentation html files to /docs for Github Pages or netlify.com
      enableFileSystem = true

      // Run locally or on Heroku
      enableHttp = true
    }

    presentation {

      presentationConfig {
        transition = Transition.CONCAVE

        slideConfig {
          backgroundColor = "red"
        }
      }

      markdownSlide {

        slideConfig {
          transition = Transition.ZOOM
          transitionSpeed = Speed.SLOW
        }

        content {
          """
          # Markdown Slide
          ## 🍒   
          Press ESC to see presentation overview.
          """
        }
      }

      htmlSlide {
        slideConfig {
          backgroundColor = "green"
        }

        content {
          """
          <h1>Raw HTML Slide 🐦</h1>
          <h2>HTML Slide 🐦</h2>
          <h3>HTML Slide 🐦</h3>
          <p>This is a test</p>
          """
        }
      }

      dslSlide {

        slideConfig {
          backgroundColor = "black"
        }

        content {
          h1 { +"HTML Slide 🐦" }
          h2 { +"HTML Slide 🐦" }
          h3 { +"HTML Slide 🐦" }
          p { +"This is a test" }
        }
      }


      markdownSlide {
        slideConfig {
          backgroundColor = "#4370A5"
        }

        content {
          """
          ## Code Presentation    
          ```kotlin [1,5|2,4|3]
          ${includeUrl(githubRawUrl("kslides","kslides","kslides-examples/src/main/kotlin/examples/HelloWorldK.kt"), "3-7")}
          ```
          produced with:
          ```` []
          markdownSlide {
            content {
              ```kotlin [1,5|2,4|3]
              ${"$"}{includeUrl(githubRawUrl("kslides","kslides","kslides-examples/src/main/kotlin/examples/HelloWorldK.kt", "[3-7]"))}
              ```
            }
          }
          ````
          """
        }
      }
    }

    presentation {
      path = "subdir1"

      dslSlide {
        content {
          h1 { +"Subdir1/index.html Slides" }
        }
      }
    }

    presentation {
      path = "subdir1/other.html"

      dslSlide {
        content {
          h1 { +"Subdir1/other.html Slides" }
        }
      }
    }

    presentation {
      path = "subdir1/subdir2"

      dslSlide {
        content {
          h1 { +"Subdir2/index.html Slides" }
        }
      }
    }

    presentation {
      path = "other.html"

      dslSlide {
        content {
          +"other.html Slides"
        }
      }

      markdownSlide {
        filename = "markdown.md"
      }
    }
  }
}