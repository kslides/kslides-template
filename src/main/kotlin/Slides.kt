import com.kslides.DiagramOutputType
import com.kslides.Effect
import com.kslides.KSlides
import com.kslides.PresentationTheme
import com.kslides.Speed
import com.kslides.Transition
import com.kslides.bodyRow
import com.kslides.by
import com.kslides.codeSnippet
import com.kslides.config.CopyCodeButton
import com.kslides.config.CopyCodeDisplay
import com.kslides.config.LogoPosition
import com.kslides.diagram
import com.kslides.fragment
import com.kslides.githubRawUrl
import com.kslides.headRow
import com.kslides.include
import com.kslides.kslides
import com.kslides.letsPlot
import com.kslides.listHref
import com.kslides.mermaid
import com.kslides.orderedList
import com.kslides.playground
import com.kslides.slideBackground
import com.kslides.toLinePatterns
import com.kslides.unorderedList
import com.pambrose.srcref.Api.srcrefUrl
import kotlinx.css.Color
import kotlinx.css.TextTransform
import kotlinx.css.px
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.thead
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import kotlin.random.Random

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

      // Audience browsers follow the presenter; presenter URL logged at startup
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

      topRightHref = "./"  // Assign to "" to turn this off
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

      presentationConfig {
        transition = Transition.CONCAVE

        copyCodeConfig {
          button = CopyCodeButton.ALWAYS
          display = CopyCodeDisplay.ICONS
          copy = "Copy"
          copied = "Copied!"
          timeout = 2000
        }

        // Sizes the code inside the Kotlin Playground iframe (kslides 1.4.0 and later). Prefer
        // absolute units — the Playground renders in its own document, so em resolves against
        // that document's root font size rather than the surrounding slide's.
        playgroundConfig {
          fontSize = "20px"
        }

        slideConfig {
          // Assign slide config defaults for all slides in this presentation
          //backgroundColor = "red"

          // Shrink code blocks so long lines fit the slide window (reveal's default is 0.55em). This
          // fits the ~85-92 char lines of the slide-definition slides; codeWrap makes the rare
          // extra-long line (e.g. a full URL) wrap instead of overflowing horizontally, rather than
          // forcing an unreadably small size on every slide. An individual slide overrides either
          // value with its own slideConfig{} — see the slide-definition slides below.
          codeFontSize = "0.60em"
          codeWrap = true
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

          Notes: Press S for the presenter view, which shows these notes 📝
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
          // This slide's code is wider than the rest of the deck, so drop below the deck-wide
          // codeFontSize set in presentationConfig{}. codeWrap is inherited.
          slideConfig {
            codeFontSize = "0.45em"
          }

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
          slideConfig {
            codeFontSize = "0.45em"
          }

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
          slideConfig {
            codeFontSize = "0.45em"
          }

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
          slideConfig {
            codeFontSize = "0.45em"
          }

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
          slideConfig {
            codeFontSize = "0.45em"
          }

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
        // fragments begin
        markdownSlide {
          // Keep fragment lines free of inline code spans and other inline markup. The
          // reveal.js comment that fragment() emits attaches to the last element on the line,
          // so a trailing backtick span would swallow the class and only the code would animate.
          content {
            """
            ## Fragments

            Reveal a line at a time: press the arrow key again. ${fragment()}

            This one fades up. ${fragment(Effect.FADE_UP)}

            This one fades in from the left. ${fragment(Effect.FADE_LEFT)}

            And this one grows once it is reached. ${fragment(Effect.GROW)}

            Notes: Fragments advance with the same arrow keys as slides, so a fragmented
            slide takes several presses to get through 📝
            """
          }
        }
        // fragments end

        markdownSlide {
          slideConfig {
            codeFontSize = "0.35em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "fragments begin", endToken = "fragments end")}
            ```
            <a id="ghsrc" href="${srcrefLink("fragments", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // notes begin
        markdownSlide {
          content {
            """
            ## Speaker Notes

            Press <kbd>S</kbd> to open the presenter view. This slide has a note waiting there.

            Any line starting with `Notes:` becomes a speaker note rather than slide content.
            The presentations in this template already declare the separator that makes it work,
            so no extra configuration is needed.

            Notes: This is the note. The presenter view shows it beside a timer, the current
            slide, and a preview of the next one 📝
            """
          }
        }
        // notes end

        markdownSlide {
          slideConfig {
            codeFontSize = "0.35em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "notes begin", endToken = "notes end")}
            ```
            <a id="ghsrc" href="${srcrefLink("notes", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // background begin
        markdownSlide {
          content {
            """
            ## Slide Backgrounds

            ${slideBackground("#2d5f7c")}

            A markdown slide has no `slideConfig { }` of its own for this, so `slideBackground()`
            emits the reveal.js comment instead. Put it on its own line anywhere in the content.

            Notes: dslSlide and htmlSlide use slideConfig { backgroundColor } directly 📝
            """
          }
        }

        dslSlide {
          slideConfig {
            // Relative asset paths resolve against the output root, so this one value works
            // from a deck at any depth (kslides 1.4.0 and later).
            backgroundImage = "images/revealjs.png"
            backgroundSize = "30%"
            backgroundPosition = "bottom right"
            backgroundRepeat = "no-repeat"
            backgroundOpacity = 0.4
          }

          content {
            h2 { +"A Background Image" }
            p { +"backgroundSize and backgroundPosition keep it clear of the text." }
          }
        }
        // background end

        markdownSlide {
          slideConfig {
            codeFontSize = "0.35em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "background begin", endToken = "background end")}
            ```
            <a id="ghsrc" href="${srcrefLink("background", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // fontsize begin
        markdownSlide {
          slideConfig {
            // The non-code sibling of codeFontSize: an inline font-size on this slide's <section>.
            fontSize = "0.7em"
          }

          content {
            """
            ## Slide Font Size

            This slide sets `fontSize = "0.7em"`, so everything on it renders smaller than the
            deck default, useful for the one slide with more text than the rest.

            Set it deck-wide in `presentationConfig { slideConfig { } }` and override it here,
            exactly like `codeFontSize`. Reach for these rather than hand-writing CSS: they
            cascade global to presentation to slide, and kslides generates the rules for you.

            Notes: fontSize is emitted as an inline style on the slide's section 📝
            """
          }
        }
        // fontsize end

        markdownSlide {
          slideConfig {
            codeFontSize = "0.35em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "fontsize begin", endToken = "fontsize end")}
            ```
            <a id="ghsrc" href="${srcrefLink("fontsize", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // lists begin
        dslSlide {
          content {
            h2 { +"Lists" }
            // Every reveal.js theme sets `.reveal ol, .reveal ul { display: inline-block }` so a
            // lone list centers on the slide. That makes sibling lists flow inline, side by side
            // like words. Wrapping each in a block-level div puts them back on their own lines.
            div { unorderedList("Unordered items", "Written as varargs", "One li per string") }
            div { orderedList("Ordered items", "Numbered automatically") }
            div {
              unorderedList(
                { listHref("#/otherslides", "A list item that is a link") },
                { listHref("https://github.com/kslides/kslides", "kslides on GitHub") },
              )
            }
          }
        }
        // lists end

        markdownSlide {
          slideConfig {
            codeFontSize = "0.35em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "lists begin", endToken = "lists end")}
            ```
            <a id="ghsrc" href="${srcrefLink("lists", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // tables begin
        dslSlide {
          content {
            h2 { +"Tables" }
            table {
              thead {
                headRow("Deck", "Path", "Slides")
              }
              tbody {
                bodyRow("Main", "index.html", "many")
                bodyRow("Great Talk 1", "greattalk1/", "2")
                bodyRow("Great Talk 2", "greattalk2.html", "2")
              }
            }
          }
        }
        // tables end

        markdownSlide {
          slideConfig {
            codeFontSize = "0.45em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "tables begin", endToken = "tables end")}
            ```
            <a id="ghsrc" href="${srcrefLink("tables", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // mermaid begin
        dslSlide {
          content {
            h2 { +"Mermaid Diagrams" }
            mermaid(
              """
              flowchart LR
                A[Slides.kt] --> B[kslides]
                B --> C[docs/]
                B --> D[HTTP server]
              """,
            )
          }
        }
        // mermaid end

        markdownSlide {
          slideConfig {
            fontSize = "0.65em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "mermaid begin", endToken = "mermaid end")}
            ```
            Rendered client-side from a runtime bundled with kslides, so it works offline.

            <a id="ghsrc" href="${srcrefLink("mermaid", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // playground begin
        dslSlide {
          content {
            h2 { +"Kotlin Playground" }
            // Path is relative to the repo root, like the include() calls above.
            playground("src/main/kotlin/playground/HelloWorld.kt") {
              args = "Kotlin kslides"
              height = "400px"
            }
          }
        }
        // playground end

        markdownSlide {
          slideConfig {
            fontSize = "0.65em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "playground begin", endToken = "playground end")}
            ```
            Editable and runnable in the browser. Size the code with
            `playgroundConfig { fontSize }`, set above for this presentation.

            <a id="ghsrc" href="${srcrefLink("playground", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // letsplot begin
        dslSlide {
          content {
            h2 { +"A Lets-Plot Figure" }
            letsPlot(
              dimensions = 801 by 400,
              configBlock = {
                style = "width: 85%; border: 2px solid #586E75;"
                height = "415px"
              },
            ) {
              val xs = (0..400).toList()
              val data = mapOf("x" to xs, "y" to xs.map { Random.nextDouble(10.0) })
              letsPlot(data) +
                geomPoint { x = "x"; y = "y" } +
                labs(title = "A Simple Random Plot", x = "x Axis Title", y = "y Axis Title")
            }
          }
        }
        // letsplot end

        markdownSlide {
          slideConfig {
            fontSize = "0.65em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "letsplot begin", endToken = "letsplot end")}
            ```
            Requires the `kslides-letsplot` dependency, enabled in `build.gradle.kts`.

            <a id="ghsrc" href="${srcrefLink("letsplot", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        // kroki begin
        dslSlide {
          content {
            h2 { +"Kroki Diagrams" }
            diagram("graphviz") {
              outputType = DiagramOutputType.SVG
              style = "zoom: 1.5"
              options =
                mapOf(
                  "graph-attribute-label" to "Rendered by Kroki",
                  "edge-attribute-color" to "#268BD2",
                )
              source = "digraph G {Slides->Kroki->SVG}"
            }
          }
        }
        // kroki end

        markdownSlide {
          slideConfig {
            fontSize = "0.65em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "kroki begin", endToken = "kroki end")}
            ```
            Covers the formats Mermaid does not: PlantUML, GraphViz, D2. Unlike `mermaid()`,
            this calls kroki.io while the slides are generated, so it needs network access.

            <a id="ghsrc" href="${srcrefLink("kroki", true)}" target="_blank">GitHub Source</a>
            """
          }
        }
      }

      verticalSlides {
        markdownSlide {
          slideConfig {
            fontSize = "0.95em"
          }

          content {
            """
            ## Custom Themes

            `customTheme { }` restyles one of the stock reveal.js themes (brand colors, fonts,
            and a corner logo) without hand-writing CSS.

            It is a presentation-level block, so it is applied to
            [greattalk2.html](./greattalk2.html) rather than to this deck, letting you compare
            the two. Press the down arrow for the definition 👇

            Notes: baseTheme picks the reveal.js theme the overrides are layered onto 📝
            """
          }
        }

        markdownSlide {
          slideConfig {
            codeFontSize = "0.35em"
          }

          content {
            """
            ## Slide Definition
            ```kotlin []
            ${include(slides, beginToken = "customtheme begin", endToken = "customtheme end")}
            ```
            <a id="ghsrc" href="${srcrefLink("customtheme", true)}" target="_blank">GitHub Source</a>
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

            [🐦 greattalk1/ Slides](./greattalk1)

            [🐦 greattalk1/other.html Slides](./greattalk1/other.html)

            [🐦 greattalk2.html Slides](./greattalk2.html)
            </span>
            """
          }
        }
        // others end

        markdownSlide {
          slideConfig {
            codeFontSize = "0.45em"
          }

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
        // Relative to this deck's directory: one level up lands on the root presentation.
        topRightHref = "../#/otherslides"
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
        topRightHref = "../#/otherslides"
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
        // This deck sits at the root, so no ../ here — unlike the greattalk1 decks above.
        topRightHref = "./#/otherslides"
        topRightTitle = "Go back to main presentation"
        topRightText = "🔙"

        // customtheme begin
        // Restyles a stock reveal.js theme without hand-writing CSS. Applied here rather than to
        // the main deck so the two looks can be compared side by side.
        customTheme {
          baseTheme = PresentationTheme.WHITE
          backgroundColor = Color("#f8f5ef")
          mainColor = Color("#1a1a2e")
          headingColor = Color("#0f4c81")
          headingTextTransform = TextTransform.none
          linkColor = Color("#b3550f")
          codeFont = "Menlo, Consolas, monospace"
          customProperty("--r-heading-letter-spacing", "0.02em")
          logo(
            // Resolves against the output root, so the same value works from any deck depth.
            "images/revealjs.png",
            position = LogoPosition.BOTTOM_RIGHT,
            size = 90.px,
            opacity = 0.6,
          )
        }
        // customtheme end
      }

      dslSlide {
        content {
          h2 { +"greattalk2.html slides" }
          p { +"This deck is restyled with a customTheme{} block." }
        }
      }
    }
  }
