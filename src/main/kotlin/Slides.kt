import com.kslides.*
import kotlinx.html.*

fun main() {

  kslides {

    presentationDefault {
      history = true
      transition = Transition.SLIDE
      transitionSpeed = Speed.SLOW
      githubCornerHref = "https://github.com/kslides/kslides-template/"
      githubCornerTitle = "View presentation source on Github"
      enableMenu = true
      theme = Theme.SOLARIZED

      slideConfig {
        // Assign slide config defaults for all presentations here
        // backgroundColor = "blue"
      }
    }

    output {
      // Write the presentation html files to /docs for Github Pages or netlify.com
      enableFileSystem = true

      // Run locally or on Heroku
      enableHttp = true
    }

    presentation {
      css += """
        #githubCorner path { fill: #258BD2; }
      """

      presentationConfig {
        transition = Transition.CONCAVE

        slideConfig {
          // Assign slide config defaults for all slides in this presntation here
          //backgroundColor = "red"
        }
      }

      markdownSlide {

        slideConfig {
          transition = Transition.ZOOM
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
        content {
          """
          <h1>An HTML Slide 🐦</h1>
          <p>This is some text</p>
          """
        }
      }

      dslSlide {
        content {
          h1 { +"A DSL Slide 🐦" }
          p { +"This is some text" }
        }
      }


      verticalSlides {
        // code begin
        markdownSlide {
          slideConfig {
            // backgroundColor = "#4370A5"
          }

          val src = "kslides-examples/src/main/kotlin/examples/HelloWorldK.kt"
          content {
            """
            ## Code Presentation    
            ```kotlin [1,5|2,4|3]
            ${includeUrl(githubRawUrl("kslides", "kslides", src), "[3-7]")}
            ```
            """
          }
        }
        // code end

        markdownSlide {
          content {
            """            
            ## Slide Description    
            ```kotlin []
            ${includeFile("src/main/kotlin/Slides.kt", beginToken = "code begin", endToken = "code end")}
            """
          }
        }
      }

      verticalSlides {
        markdownSlide {
          content {
            """
            ## Other Presentations    
            <span style="text-align: left; text-indent: 25%;">

            [🐦 greattalk1/ Slides](/greattalk1) ${fragment(Effect.FADE_UP)}

            [🐦 greattalk1/other.html Slides](/greattalk1/other.html) ${fragment(Effect.FADE_UP)}

            [🐦 greattalk2.html Slides](/greattalk2.html) ${fragment(Effect.FADE_UP)}
            </span>
            """
          }
        }

        markdownSlide {
          content {
            """
            ## Other Presentations Description    
            ```kotlin
            ${includeFile("src/main/kotlin/Slides.kt", beginToken = "others begin", endToken = "others end")}
            ```
            """
          }
        }
      }
    }

    // others begin
    presentation {
      path = "greattalk1"

      dslSlide {
        content {
          h2 { +"greattalk1/index.html Slides" }
        }
      }
    }

    presentation {
      path = "greattalk1/other.html"

      dslSlide {
        content {
          h2 { +"greattalk1/other.html slides" }
        }
      }
    }

    presentation {
      path = "greattalk2.html"

      dslSlide {
        content {
          h2 { +"greattalk2.html slides" }
        }
      }
    }
    // others end
  }
}