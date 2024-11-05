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
    val aocktVersion = "0.1.0"
    val kotestVersion = "5.5.5"
    val kotlinSerializationVersion = "1.6.2"

    implementation("io.github.jadarma.aockt:aockt-core:$aocktVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    testImplementation("io.github.jadarma.aockt:aockt-test:$aocktVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}
