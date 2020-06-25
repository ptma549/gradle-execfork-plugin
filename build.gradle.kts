import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("com.gradle.plugin-publish").version("0.12.0")
    id("org.jetbrains.kotlin.jvm").version("1.3.41")
    id("idea")
    id("maven")
}

group = "com.github.psxpaul"
version = File("VERSION").readText().trim()
buildDir = File("build/gradle")

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.41")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.41")

    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("io.mockk:mockk:1.10.0")
}

pluginBundle {
    website = "https://github.com/hesch/gradle-execfork-plugin"
    vcsUrl = "https://github.com/hesch/gradle-execfork-plugin"
    description = "Execute Java or shell processes in the background during a build. Forked from psxpaul/execfork with a small logging bugfix."
    tags = listOf("java", "exec", "background", "process")

    (plugins) {
        create("execForkPlugin") {
            id = "com.github.hesch.execfork"
            displayName = "Gradle Exec Fork Plugin"
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks {
    val sampleProjects by creating(GradleBuild::class) {
        buildFile = File("${project.rootDir}/sample_projects/build.gradle")
        tasks = listOf("clean", "build")
    }
    sampleProjects.dependsOn("install")
    "test" { finalizedBy(sampleProjects) }
    named<Test>("test") {
        testLogging.exceptionFormat = TestExceptionFormat.FULL
    }
}

val javadocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from("javadoc")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

artifacts {
    add("archives", javadocJar)
    add("archives", sourcesJar)
}
