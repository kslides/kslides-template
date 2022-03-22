import com.kslides.*
import com.kslides.Presentation.Companion.servePresentations
import kotlinx.html.*

fun main() {

  presentationsDefaults {
    history = true
    transition = Transition.SLIDE
    transitionSpeed = Speed.SLOW
  }

  presentation {

    markdownSlide {
      config {
        transition = Transition.ZOOM
        transitionSpeed = Speed.SLOW
      }

      """
      # Markdown Slide
      ## 🍒   
      Press ESC to see presentation overview.
      """
    }

    dslSlide {
      content {
        h1 { +"HTML Slide 🐦" }
        h2 { +"HTML Slide 🐦" }
        h3 { +"HTML Slide 🐦" }
        p { +"This is a test" }
      }
    }

    htmlSlide {
      """
      <h1>Raw HTML Slide 🐦</h1>
      <h2>HTML Slide 🐦</h2>
      <h3>HTML Slide 🐦</h3>
      <p>This is a test</p>
      """
    }

    markdownSlide {
      config {
        backgroundColor = "#4370A5"
      }

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
    }
  }

  presentation("subdir1") {
    dslSlide {
      content {
        h1 { +"Subdir1/index.html Slides" }
      }
    }
  }

  presentation("subdir1/other.html") {
    dslSlide {
      content {
        h1 { +"Subdir1/other.html Slides" }
      }
    }
  }

  presentation("subdir1/subdir2") {
    dslSlide {
      content {
        h1 { +"Subdir2/index.html Slides" }
      }
    }
  }

  presentation("other.html") {
    dslSlide {
      content {
        +"other.html Slides"
      }
    }
  }

  // Uncomment this to run locally or on Heroku
  servePresentations()

  // Write the presentations to files in /docs for Github Pages or netlify.com
  //outputPresentations()
}