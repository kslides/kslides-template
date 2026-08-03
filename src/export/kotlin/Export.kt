import com.kslides.export.exportPdf

/**
 * PDF-export entry point: prints each presentation defined in `Slides.kt` to `build/pdf` via
 * headless Chromium. Run it with `make pdf` (or `./gradlew exportPdf`), optionally narrowing to a
 * single deck with `make pdf DECK=greattalk1` / `-Pdeck=greattalk1`.
 *
 * This lives in the `export` source set rather than `src/main/kotlin` so the Playwright dependency
 * stays out of the uberjar that Heroku runs. Configure the output via the `pdf { }` block inside
 * `output { }` in `Slides.kt`.
 */
fun main() {
  val deck: String? = System.getProperty("kslides.export.deck")
  exportPdf(deck, templateSlides())
}
