import com.kslides.*
import com.kslides.Presentation.Companion.servePresentations
import kotlinx.html.*

fun main() {

  presentationDefaults {
    history = true
    transition = Transition.SLIDE
    transitionSpeed = Speed.SLOW
  }

  presentation {

    markdownSlide {
      """
      # Markdown Slide
      ## 🍒   
      Press ESC to see presentation overview.
      """
    }.config {
      transition = Transition.ZOOM
      transitionSpeed = Speed.SLOW
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

    markdownSlide {
      """
        # Code Highlights    
        ```kotlin [1,6|2,5|3-4]
        fun main() {
            repeat(10) {
                println("Hello")
                println("World")
            }
        }
        ```
        produced with:
        ````
        ```kotlin [1,6|2,5|3-4]
        fun main() {
            repeat(10) {
                println("Hello")
                println("World")
            }
        }
        ```
        ````
      """
    }.config {
      backgroundColor = "#4370A5"
    }
  }

  presentation("subdir1") {
    htmlSlide {
      h1 { +"Subdir1/index.html Slides" }
    }
  }

  presentation("subdir1/other.html") {
    htmlSlide {
      h1 { +"Subdir1/other.html Slides" }
    }
  }

  presentation("subdir1/subdir2") {
    htmlSlide {
      h1 { +"Subdir2/index.html Slides" }
    }
  }

  presentation("other.html") {
    htmlSlide {
      +"other.html Slides"
    }
  }

  // Uncomment this to run locally or on Heroku
  servePresentations()

  // Write the presentations to files in /docs for Github Pages or netlify.com
  //outputPresentations()
}