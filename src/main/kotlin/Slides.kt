import com.kslides.*
import kotlinx.html.*

fun main() {

  kslides {

    output {
      // Write the presentation html files to /docs for Github Pages or netlify.com
      enableFileSystem = true

      // Run locally or on Heroku
      enableHttp = true
    }

    // CSS values assigned here are applied to all the presentations
    css +=
      """
      #githubCorner path { fill: #258BD2; }
      """

    presentationDefault {
      history = true
      transition = Transition.SLIDE
      transitionSpeed = Speed.SLOW
      githubCornerHref = "https://github.com/kslides/kslides-template/"
      githubCornerTitle = "View presentation source on Github"
      enableMenu = true
      theme = Theme.SOLARIZED

      slideConfig {
        // Assign slide config defaults for all presentations
        // backgroundColor = "blue"
      }
    }

    presentation {

      css +=
        """
        img[alt=revealjs-image] { width: 1000px; }
        """

      val slides = "src/main/kotlin/Slides.kt"

      presentationConfig {
        transition = Transition.CONCAVE

        slideConfig {
          // Assign slide config defaults for all slides in this presentation
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
          val src = "kslides-examples/src/main/kotlin/examples/HelloWorldK.kt"
          content {
            """
            ## Code Slide    
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
            ## Code Slide Description    
            ```kotlin []
            ${includeFile(slides, beginToken = "code begin", endToken = "code end")}
            ```
            """
          }
        }
      }

      verticalSlides {
        // image begin
        markdownSlide {
          // Size of image is controlled by css above
          content {
            """
            ## Images 
               
            ![revealjs-image](images/revealjs.png)
            """
          }
        }
        // image end

        markdownSlide {
          content {
            """            
            ## Images Slide Description    
            ```kotlin []
            ${includeFile(slides, beginToken = "image begin", endToken = "image end")}
            ```
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
            ${includeFile(slides, beginToken = "others begin", endToken = "others end")}
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