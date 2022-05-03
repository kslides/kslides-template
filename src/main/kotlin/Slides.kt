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

      topLeftHref = "https://github.com/kslides/kslides-template/" // Assign to "" to turn this off
      topLeftTitle = "View presentation source on Github"

      topRightHref = "/"  // Assign to "" to turn this off
      topRightTitle = "Go to 1st Slide"
      topRightText = "🏠"

      enableMenu = true
      theme = Theme.SOLARIZED
      slideNumber = "c/t"

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
        // code1 begin
        markdownSlide {
          val src = "kslides-examples/src/main/kotlin/examples/HelloWorldK.kt"
          content {
            """
            ## Code with a markdownSlide     
            ```kotlin [1,5|2,4|3]
            ${includeUrl(githubRawUrl("kslides", "kslides", src), "[3-7]")}
            ```
            """
          }
        }
        // code1 end

        markdownSlide {
          content {
            """            
            ## Slide Definition    
            ```kotlin []
            ${includeFile(slides, beginToken = "code1 begin", endToken = "code1 end")}
            ```
            """
          }
        }
      }

      verticalSlides {
        // code2 begin
        dslSlide {
          val src = "kslides-examples/src/main/kotlin/examples/HelloWorldK.kt"
          val url = githubRawUrl("kslides", "kslides", src)
          content {
            h2 { +"Code with a dslSlide" }
            codeSnippet(
              "kotlin",
              includeUrl(url, "[3-7]", indentToken = "", escapeHtml = false),
              linePattern = "[1,5|2,4|3]",
            )
          }
        }
        // code2 end

        dslSlide {
          content {
            h2 { +"Slide Definition" }
            codeSnippet(
              "kotlin",
              includeFile(
                slides, beginToken = "code2 begin", endToken = "code2 end",
                indentToken = "",
                escapeHtml = false
              ),
            )
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
            ## Slide Definition    
            ```kotlin []
            ${includeFile(slides, beginToken = "image begin", endToken = "image end")}
            ```
            """
          }
        }
      }

      verticalSlides {
        markdownSlide {
          id = "otherslides"
          content {
            """
            ## Other Presentations Defined In Slides.kt   
            <span style="text-align: left; text-indent: 25%;">

            [🐦 greattalk1/ Slides](/greattalk1) 

            [🐦 greattalk1/other.html Slides](/greattalk1/other.html) 

            [🐦 greattalk2.html Slides](/greattalk2.html) 
            </span>
            """
          }
        }

        markdownSlide {
          content {
            """
            ## Other Slide Definitions    
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

      presentationConfig {
        topRightHref = "/#/otherslides"
        topRightTitle = "Go back to main presentation"
        topRightText = "🔙"
      }

      dslSlide {
        content {
          h2 { +"greattalk1/index.html Slides" }
        }
      }
    }

    presentation {
      path = "greattalk1/other.html"

      presentationConfig {
        topRightHref = "/#/otherslides"
        topRightTitle = "Go back to main presentation"
        topRightText = "🔙"
      }

      dslSlide {
        content {
          h2 { +"greattalk1/other.html slides" }
        }
      }
    }

    presentation {
      path = "greattalk2.html"

      presentationConfig {
        topRightHref = "/#/otherslides"
        topRightTitle = "Go back to main presentation"
        topRightText = "🔙"
      }

      dslSlide {
        content {
          h2 { +"greattalk2.html slides" }
        }
      }
    }
    // others end
  }
}