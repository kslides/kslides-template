import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

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

tasks.register<DefaultTask>("stage") {
    group = "build"
    description = "Clean and build $jarName — invoked by Heroku via Procfile."
    dependsOn(cleanTask, shadowJarTask)
}

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
