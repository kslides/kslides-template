import com.github.pambrose.*
import com.github.pambrose.Presentation.Companion.present
import com.github.pambrose.SlideConfig.Companion.slideConfig
import kotlinx.html.*

fun main() {
    presentation {
        htmlSlide {
            h1 { +"HTML Slide 🐦" }
            h2 { +"HTML Slide 🐦" }
            h3 { +"HTML Slide 🐦" }
            p { +"This is a test" }
        }

        rawHtmlSlide {
            """
        <h1>Raw HTML Slide 🐦</h1>
        <h2>HTML Slide 🐦</h2>
        <h3>HTML Slide 🐦</h3>
        <p>This is a test</p>
      """
        }

        markdownSlide(
            config = slideConfig { transition(Transition.Zoom, Speed.Slow) },
            content = """
          # Markdown Slide 🍒 
          
          Press ESC to see presentation overview.
      """
        )

        markdownSlide(
            config = slideConfig { backgroundColor = "#4370A5" },
            content = """
          # Code Highlights    
          ```kotlin [1|2,5|3-4]
          fun main() {
              repeat(10) {
                  println("Hello")
                  println("World")
              }
          }
          ```
          """
        )
    }

    present()
}