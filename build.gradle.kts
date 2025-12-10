import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
}

kotlin {
    jvmToolchain(21)
}

sourceSets {
    main.configure {
        kotlin.srcDir("$projectDir/solutions")
    }
    test.configure {
        kotlin.srcDir("$projectDir/tests")
        resources.srcDir("$projectDir/inputs")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val aocktVersion = "0.3.0"
    val kotestVersion = "6.0.7"
    val kotlinSerializationVersion = "1.9.0"

    implementation("io.github.jadarma.aockt:aockt-core:$aocktVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    testImplementation("io.github.jadarma.aockt:aockt-test:$aocktVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")

    // For those pesky Linear Algebra days... Am sorry.
    // Current list of shame: Y2025D10
    implementation("tools.aqua:z3-turnkey:4.14.1")
}

tasks.test {
    useJUnitPlatform()

    // Don't cache tests, make them run again every time.
    outputs.upToDateWhen { false }

    // Pass along system properties for Kotest.
    systemProperties = System.getProperties()
        .asIterable()
        .filter { it.key.toString().startsWith("kotest.") }
        .associate { it.key.toString() to it.value }

    // Configure Kotest. The FQN is required for locating the custom test config.
    systemProperty("kotest.framework.config.fqn", "aockt.TestConfig")

    // Display nicer test logs when running from CLI.
    testLogging {
        events = setOf(PASSED, FAILED, SKIPPED)
        exceptionFormat = FULL
        showStandardStreams = true
        showCauses = true
        showExceptions = true
        showStackTraces = false

        // Prints a summary at the end.
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent != null) return@KotlinClosure2
            with(result) {
                println(
                    "\nResults: $resultType (" +
                            "$testCount tests, " +
                            "$successfulTestCount passed, " +
                            "$failedTestCount failed, " +
                            "$skippedTestCount skipped" +
                            ")"
                )
            }
        }))
    }
}
