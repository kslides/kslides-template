package playground

// Source for the playground{} slide in Slides.kt.
// Keep lines short: they show up verbatim in the slide.
fun main(args: Array<String>) {
  val names = if (args.isEmpty()) arrayOf("World") else args
  names.forEach { println("Hello, $it!") }
}
