package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 14, "Regolith Reservoir")
class Y2022D14Test : AdventSpec<Y2022D14>({

    val exampleInput = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent()

    partOne { exampleInput shouldOutput 24 }
    partTwo { exampleInput shouldOutput 93 }
})
