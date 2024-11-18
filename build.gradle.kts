import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
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
    val aocktVersion = "0.2.1"
    val kotestVersion = "5.9.1"
    val kotlinSerializationVersion = "1.7.3"

    implementation("io.github.jadarma.aockt:aockt-core:$aocktVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    testImplementation("io.github.jadarma.aockt:aockt-test:$aocktVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()

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
