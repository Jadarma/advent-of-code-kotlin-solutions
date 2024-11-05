plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "advent-of-code-kotlin-solutions"

// Used when testing. If enabled, will include the local repo as a composite build instead of downloading from Maven.
val useLocal = true
if(useLocal && file("../advent-of-code-kotlin").exists())
    includeBuild("../advent-of-code-kotlin") {
        name = "aockt-local"
        dependencySubstitution {
            substitute(module("io.github.jadarma.aockt:aockt-core")).using(project(":aockt-core"))
            substitute(module("io.github.jadarma.aockt:aockt-test")).using(project(":aockt-test"))
        }
    }
