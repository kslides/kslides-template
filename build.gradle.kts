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

application {
    mainClass = mainName
}

// Change this to your name
group = "com.github.username"
version = "1.4.0"

dependencies {
    implementation(libs.kslides.core)

    // Include this dependency if you use lets-plot
    // implementation(libs.kslides.letsplot)
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<ShadowJar> {
    isZip64 = true
    mergeServiceFiles()
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("LICENSE*")
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")

val uberjar = tasks.register<Jar>("uberjar") {
    dependsOn(shadowJar)
    mustRunAfter("clean")
    isZip64 = true
    archiveFileName = "kslides.jar"
    manifest {
        attributes(
            "Implementation-Title" to "kslides",
            "Implementation-Version" to version,
            "Built-JDK" to System.getProperty("java.version"),
            "Main-Class" to mainName,
        )
    }
    from(zipTree(shadowJar.flatMap { it.archiveFile }))
}

tasks.register("stage") {
    dependsOn("clean", uberjar)
}

// Unpack reveal.js assets from the kslides-core JAR into docs/revealjs/.
// Single source of truth lives in the kslides-core JAR (it ships them at
// classpath path revealjs/**); this task mirrors them onto disk so the
// generated docs/*.html have working JS/CSS references when published to
// Netlify / GitHub Pages.
tasks.register<Sync>("syncRevealJs") {
    group = "kslides"
    description = "Unpacks reveal.js assets from the kslides-core JAR into docs/revealjs/."

    val coreJar = configurations.runtimeClasspath.map { rc ->
        rc.files.single { it.name.startsWith("kslides-core-") }
    }

    from(coreJar.map { zipTree(it) }) {
        include("revealjs/**")
        eachFile { relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray()) }
        includeEmptyDirs = false
    }
    into(layout.projectDirectory.dir("docs/revealjs"))
}
