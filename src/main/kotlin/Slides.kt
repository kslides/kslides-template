import com.kslides.*
import com.kslides.Presentation.Companion.output
import com.kslides.SlideConfig.Companion.slideConfig
import kotlinx.html.*

fun main() {
  presentation {

    markdownSlide(slideConfig { transition(Transition.Zoom, Speed.Slow) }) {
      """
      # Markdown Slide
      ## 🍒   
      Press ESC to see presentation overview.
      """.trimIndent()
    }

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

    markdownSlide(slideConfig { backgroundColor = "#4370A5" }) {
      """
        # Code Highlights    
        ```kotlin [1|2,5|3-4]
        fun main() {
            repeat(10) {
                println("Hello")
                println("World")
            }
        }
        ```
        produced with:
        ````
        ```kotlin [1|2,5|3-4]
        fun main() {
            repeat(10) {
                println("Hello")
                println("World")
            }
        }
        ```
        ````
      """.trimIndent()
    }
  }

  presentation("demo1") {
    htmlSlide {
      +"Demo1 Slide 1"
    }

    htmlSlide {
      +"Demo1 Slide 2"
    }
  }

  presentation("demo2.html") {
    htmlSlide {
      +"Demo2 Slide 1"
    }

    htmlSlide {
      +"Demo2 Slide 2"
    }
  }

  //present()
  output()
}