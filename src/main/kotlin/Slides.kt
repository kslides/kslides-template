import com.kslides.KSlides
import com.kslides.PresentationTheme
import com.kslides.Speed
import com.kslides.Transition
import com.kslides.codeSnippet
import com.kslides.config.CopyCodeButton
import com.kslides.config.CopyCodeDisplay
import com.kslides.githubRawUrl
import com.kslides.include
import com.kslides.kslides
import com.kslides.toLinePatterns
import com.pambrose.srcref.Api.srcrefUrl
import kotlinx.html.a
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.p

// Click the green arrow here in IntelliJ to generate the slides.
fun main() {
  kslides(templateSlides())
}

/**
 * The deck definition, kept in one place so it can be reused. [main] runs it for filesystem and
 * HTTP output; the PDF-export entry point (`src/export/kotlin/Export.kt`, run via
 * `./gradlew exportPdf` or `make pdf`) runs the same block through headless Chromium.
 */
fun templateSlides(): KSlides.() -> Unit =
  {
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

    output {
      // Write the presentation's html files to /docs for GitHub Pages or netlify.com
      enableFileSystem = true

      // Run locally or on Heroku
      enableHttp = true

      // Turn on followAlong mode for the live-reload dev server. This is a convenience for development; it is not needed for production.
      // It is not compatible with the PDF export task, which runs in a separate process
      followAlong = true

      // PDF export settings, used by `make pdf` / `./gradlew exportPdf`. All optional.
      pdf {
        // outputDir = "build/pdf"      // Where the PDFs are written
        // previewPng = true            // Also save a PNG of each deck's first slide
        // browserChannel = "chrome"    // Use an installed browser instead of downloading Chromium
        // exclude("greattalk2.html")   // Skip a deck (an explicit -Pdeck=<name> overrides this)
      }
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

      // Shrink code blocks so long lines fit the slide window (reveal's default is 0.55em). This
      // fits the ~85-92 char lines of the slideDefinition slides; the rare extra-long line (e.g. a
      // full URL) wraps instead of overflowing horizontally rather than forcing an unreadable size.
      css += """
      .reveal pre { font-size: 0.60em; }
      .reveal pre code { white-space: pre-wrap; word-break: break-word; }
      """

      // Per-slide override: the "highlighted code" slideDefinitions (classes = "smallcode") render their
      // code smaller than the global 0.60em. ".reveal .smallcode pre" (two classes) outranks ".reveal pre"
      // on specificity, so it wins regardless of order; long lines still wrap via the global pre-wrap rule.
      css += """
      .reveal .smallcode pre { font-size: 0.45em; }
      """

      presentationConfig {
        transition = Transition.CONCAVE

        copyCodeConfig {
          button = CopyCodeButton.ALWAYS
          display = CopyCodeDisplay.ICONS
          copy = "Copy"
          copied = "Copied!"
          timeout = 2000
        }

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
          // Use the smaller font for this code
          classes += "smallcode"
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
          classes += "smallcode"
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
          classes += "smallcode"
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
                lineOffset = lines.second
                +include(file, linePattern = lines.first)
              }
            }
          }
        }
        // code4 end

        markdownSlide {
          classes += "smallcode"
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
          classes += "smallcode"
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
          classes += "smallcode"
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
