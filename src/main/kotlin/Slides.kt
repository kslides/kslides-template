import com.kslides.*
import com.pambrose.srcref.Api.srcrefUrl
import kotlinx.html.*

fun main() {

  val slides = "src/main/kotlin/Slides.kt"

  fun srcrefLink(token: String, escapeHtml4: Boolean = false) =
    srcrefUrl(
      account = "kslides",
      repo = "kslides-template",
      path = slides,
      beginRegex = "\\s+// $token begin",
      beginOffset = 1,
      endRegex = "\\s+// $token end",
      endOffset = -1,
      escapeHtml4 = escapeHtml4,
    )

  kslides {

    output {
      // Write the presentation's html files to /docs for GitHub Pages or netlify.com
      enableFileSystem = true

      // Run locally or on Heroku
      enableHttp = true
    }

    // CSS values assigned here are applied to all the presentations
    css +=
      """
      #githubCorner path { 
        fill: #258BD2; 
      }
      """

    presentationConfig {
      history = true
      transition = Transition.SLIDE
      transitionSpeed = Speed.SLOW

      topLeftHref = "https://github.com/kslides/kslides-template/" // Assign to "" to turn this off
      topLeftTitle = "View presentation source on Github"

      topRightHref = "/"  // Assign to "" to turn this off
      topRightTitle = "Go to 1st Slide"
      topRightText = "🏠"

      enableMenu = true
      theme = PresentationTheme.SOLARIZED
      slideNumber = "c/t"

      menuConfig {
        numbers = true
      }

      copyCodeConfig {
        timeout = 2000
        copy = "Copy"
        copied = "Copied!"
      }

      slideConfig {
        // Assign slide config defaults for all presentations
        // backgroundColor = "blue"
      }
    }

    presentation {

      css +=
        """
        #ghsrc {
          font-size: 30px;
          text-decoration: underline;
        }  
        img[alt=revealjs-image] { 
          width: 1000px; 
        }
        """

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
          val src = "kslides-examples/src/main/kotlin/content/HelloWorldK.kt"
          content {
            """
            ## Code with a markdownSlide     
            ```kotlin [1,5|2,4|3]
            ${include(githubRawUrl("kslides", "kslides", src), "[3-7]")}
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
            ${include(slides, beginToken = "code1 begin", endToken = "code1 end")}
            ```
            <a id="ghsrc" href="${srcrefLink("code1", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // code2 begin
        dslSlide {
          val src = "kslides-examples/src/main/kotlin/content/HelloWorldK.kt"
          val url = githubRawUrl("kslides", "kslides", src)
          content {
            h2 { +"Code with a dslSlide" }
            // Display lines 3-7 of the url content and highlight lines 1 and 5, 2 and 4, and finally 3
            codeSnippet {
              language = "kotlin"
              highlightPattern = "[1,5|2,4|3]"
              +include(url, "[3-7]")
            }
          }
        }
        // code2 end

        dslSlide {
          content {
            h2 { +"Slide Definition" }
            codeSnippet {
              language = "kotlin"
              +include(slides, beginToken = "code2 begin", endToken = "code2 end")
            }
            a {
              id = "ghsrc"
              href = srcrefLink("code2")
              target = "_blank"
              +"GitHub Source"
            }
          }
        }
      }

      verticalSlides {
        // code3 begin
        for (lines in "[8-12|3-12|2-13|]".toLinePatterns()) {
          dslSlide {
            autoAnimate = true
            slideConfig {
              transition = Transition.NONE
            }
            content {
              h2 { +"Animated Code without Line Numbers" }
              val file = "src/main/resources/json-example.json"
              codeSnippet {
                dataId = "code-animation"
                language = "json"
                highlightPattern = "none"
                +include(file, linePattern = lines)
              }
            }
          }
        }
        // code3 end

        markdownSlide {
          content {
            """            
            ## Slide Definition    
            ```kotlin []
            ${include(slides, beginToken = "code3 begin", endToken = "code3 end")}
            ```
            <a id="ghsrc" href="${srcrefLink("code3", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // code4 begin
        for (lines in "[8-12|3-12|2-13|]".toLinePatterns().zip(listOf(3, 3, 2, 1))) {
          dslSlide {
            autoAnimate = true
            slideConfig {
              transition = Transition.NONE
            }
            content {
              h2 { +"Animated Code with Line Numbers" }
              val file = "src/main/resources/json-example.json"
              codeSnippet {
                dataId = "code-animation"
                language = "json"
                lineOffSet = lines.second
                +include(file, linePattern = lines.first)
              }
            }
          }
        }
        // code4 end

        markdownSlide {
          content {
            """            
            ## Slide Definition    
            ```kotlin []
            ${include(slides, beginToken = "code4 begin", endToken = "code4 end")}
            ```
            <a id="ghsrc" href="${srcrefLink("code4", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // image begin
        markdownSlide {
          // Image size is controlled by css above
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
            ${include(slides, beginToken = "image begin", endToken = "image end")}
            ```
            <a id="ghsrc" href="${srcrefLink("image", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // others begin
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
        // others end

        markdownSlide {
          content {
            """
            ## Slide Definition    
            ```kotlin
            ${include(slides, beginToken = "others begin", endToken = "others end")}
            ```
            <a id="ghsrc" href="${srcrefLink("others", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }
    }

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
  }
}
