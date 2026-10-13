import com.kslides.buildKSlides
import com.kslides.kslides

/**
 * Static-docs entry point: writes the presentations defined in `Slides.kt` to the filesystem
 * (`docs/` by default) without starting the HTTP server that `main()` in `Slides.kt` also runs.
 * Run it with `make docs` (or `./gradlew generateDocs`).
 *
 * An `output { }` block replaces the deck's own rather than merging into it, so the deck is first
 * evaluated without emitting anything to read its filesystem settings, which are then carried
 * over. Only `enableHttp` is switched off, and `enableFileSystem` on.
 */
fun main() {
  val deck = templateSlides()
  val deckOutput = buildKSlides(deck).use { it.outputConfig }

  kslides {
    deck()
    output {
      enableFileSystem = true
      enableHttp = false
      outputDir = deckOutput.outputDir
      staticRootDir = deckOutput.staticRootDir
      playgroundDir = deckOutput.playgroundDir
      letsPlotDir = deckOutput.letsPlotDir
      krokiDir = deckOutput.krokiDir
    }
  }
}
