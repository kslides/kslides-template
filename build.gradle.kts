import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ben.manes.versions)
  alias(libs.plugins.shadow)
}

// mainName is relevant only if you are hosting your slides via HTTP
// Change mainName to the name of Kotlin file that has the presentation you want to serve
val mainName = "SlidesKt"

val cleanTask = "clean"
val shadowJarTask = "shadowJar"
val jarName = "kslides.jar"
val revealJsPath = "revealjs"
val docsRevealJsDir = "docs/$revealJsPath"
val exportSourceSet = "export"
val exportMainName = "ExportKt"

application {
  mainClass = mainName
}

// The PDF-export entry point (src/export/kotlin/Export.kt) lives in its own source set so the
// Playwright dependency it pulls in never reaches the uberjar — shadowJar bundles only the main
// source set's runtime classpath, and "exportImplementation" extends "implementation" one way.
sourceSets {
  create(exportSourceSet) {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
  }
}

configurations {
  named("${exportSourceSet}Implementation") { extendsFrom(configurations.implementation.get()) }
  named("${exportSourceSet}RuntimeOnly") { extendsFrom(configurations.runtimeOnly.get()) }
}

dependencies {
  implementation(libs.kslides.core)

  // Include this dependency if you use lets-plot
  // implementation(libs.kslides.letsplot)

  // Headless-Chromium PDF export; used only by the export source set (see exportPdf task below)
  "${exportSourceSet}Implementation"(libs.kslides.export)
}

tasks.register<DefaultTask>("stage") {
  group = "build"
  description = "Clean and build $jarName — invoked by Heroku via Procfile."
  dependsOn(cleanTask, shadowJarTask)
}

configureKotlin()
configureShadowJar()
configureRevealSync()
configurePdfExport()
configureVersions()

fun Project.configureKotlin() {
  extensions.configure<KotlinJvmProjectExtension> {
    jvmToolchain(libs.versions.jvm.get().toInt())

    // Run the unused-return-value checker over production code only. Kotest's
    // assertion DSL (e.g. shouldBe) returns its receiver, and tests intentionally
    // discard that result, so applying the checker to the test source set would
    // emit only false-positive warnings.
    tasks.named<KotlinCompile>("compileKotlin") {
      compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=check")
      }
    }
  }

}

fun Project.configureShadowJar() {
  tasks.named<ShadowJar>(shadowJarTask) {
    mustRunAfter(cleanTask)
    isZip64 = true
    // Shadow's transformers (mergeServiceFiles() and the built-in Kotlin module metadata
    // transformer) only see duplicate entries if Gradle doesn't drop them first. The default
    // EXCLUDE keeps just the first copy of each META-INF/services/* and *.kotlin_module path,
    // which silently defeats the merge; INCLUDE hands every copy to the transformers.
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    // Plain resources that several jars happen to ship are not transformer-managed, so
    // including every copy would only bloat the jar. Keep first-wins for those: the
    // template's own public/ assets are packed ahead of kslides-core's, so a fork's
    // favicon still overrides the stock one.
    filesMatching(listOf("public/**", "META-INF/LICENSE*", "META-INF/NOTICE*")) {
      duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    mergeServiceFiles()
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    archiveFileName = jarName
    manifest {
      attributes(
        "Implementation-Title" to "kslides",
        "Implementation-Version" to version,
        "Built-JDK" to System.getProperty("java.version"),
        "Main-Class" to mainName,
      )
    }
  }
}

fun Project.configureRevealSync() {
// Unpack reveal.js assets from the kslides-core JAR into docs/revealjs/.
// Single source of truth lives in the kslides-core JAR (it ships them at
// classpath path revealjs/**); this task mirrors them onto disk so the
// generated docs/*.html have working JS/CSS references when published to
// Netlify / GitHub Pages.
  tasks.register<Sync>("syncRevealJs") {
    group = "kslides"
    description = "Unpacks reveal.js assets from the kslides-core JAR into $docsRevealJsDir/."

    val coreJar = configurations.runtimeClasspath.map { rc ->
      rc.files.single { it.name.startsWith("kslides-core-") }
    }

    from(coreJar.map { zipTree(it) }) {
      include("$revealJsPath/**")
      eachFile { relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray()) }
      includeEmptyDirs = false
    }
    into(layout.projectDirectory.dir(docsRevealJsDir))
  }

// Single source of truth for images assets: docs/images/ (committed for GitHub Pages).
  tasks.processResources {
    from(rootProject.layout.projectDirectory.dir("docs/images")) {
      into("public/images")
    }
  }
}

fun Project.configurePdfExport() {
  // Prints each presentation to PDF via headless Chromium. The decks are served from an
  // ephemeral-port HTTP server, so this works regardless of the output {} modes the deck enables.
  // Playwright downloads its own Chromium on first run; set browserChannel in the deck's
  // `output { pdf { } }` block to reuse an installed browser instead.
  tasks.register<JavaExec>("exportPdf") {
    group = "distribution"
    description = "Print the presentations to PDF via headless Chromium (-Pdeck=<name> for one deck)"
    mainClass = exportMainName
    classpath = sourceSets[exportSourceSet].runtimeClasspath
    providers.gradleProperty("deck").orNull?.let { systemProperty("kslides.export.deck", it) }
  }

  // Custom source sets are not wired into `check`, so without this a broken Export.kt would stay
  // invisible until someone ran `make pdf`. Compiling it as part of the normal build costs a
  // fraction of a second and keeps `Slides.kt` and the export entry point honest about each other.
  tasks.named("check") {
    dependsOn(sourceSets[exportSourceSet].classesTaskName)
  }
}

fun Project.configureVersions() {
  // A pre-release qualifier is a `.` or `-` delimiter followed by a known unstable
  // keyword. `m\d` matches milestones (`-M1`/`.M2`) without catching stable classifiers
  // like `-macos`/`-MR1`, and the `[.-]` delimiter catches both dash-style (`-alpha`)
  // and dot-style (Netty's `.Beta1`) qualifiers while leaving `-jre`/`.Final` stable.
  val preReleaseQualifier =
    Regex("""[.-](rc|beta|alpha|m\d|cr|snapshot|eap|dev|milestone|pre)""", RegexOption.IGNORE_CASE)

  fun isNonStable(version: String): Boolean = preReleaseQualifier.containsMatchIn(version)

  tasks.withType<DependencyUpdatesTask>().configureEach {
    notCompatibleWithConfigurationCache("the dependency updates plugin is not compatible with the configuration cache")
    // Reject a pre-release candidate only when the current version is stable. For
    // dependencies we intentionally track on a pre-release line (e.g. a detekt
    // alpha), newer pre-releases are still surfaced as available updates.
    rejectVersionIf {
      isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
  }
}
