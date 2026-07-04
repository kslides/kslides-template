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

application {
  mainClass = mainName
}

dependencies {
  implementation(libs.kslides.core)

  // Include this dependency if you use lets-plot
  // implementation(libs.kslides.letsplot)
}

tasks.register<DefaultTask>("stage") {
  group = "build"
  description = "Clean and build $jarName — invoked by Heroku via Procfile."
  dependsOn(cleanTask, shadowJarTask)
}

configureKotlin()
configureShadowJar()
configureRevealSync()
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
