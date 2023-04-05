plugins {
    kotlin("jvm") version "1.8.10"
}

kotlin {
    jvmToolchain(17)
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
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    val aocktVersion = "0.1.0-SNAPSHOT"
    val kotestVersion = "5.5.5"

    implementation("io.github.jadarma.aockt:aockt-core:$aocktVersion")
    testImplementation("io.github.jadarma.aockt:aockt-test:$aocktVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}
