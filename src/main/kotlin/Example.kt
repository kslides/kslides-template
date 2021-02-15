import com.github.pambrose.Presentation.Companion.present
import com.github.pambrose.presentation
import kotlinx.html.h1
import kotlinx.html.p

fun main() {
    presentation {
        htmlSlide {
            h1 { +"HTML Slide 🐦" }
            p { +"This is a test" }
        }
    }

    present()
}