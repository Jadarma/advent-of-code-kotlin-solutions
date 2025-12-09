package aockt.y2025

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2025, 9, "Movie Theater")
class Y2025D09Test : AdventSpec<Y2025D09>({
    val exampleInput = """
        7,1
        11,1
        11,7
        9,7
        9,5
        2,5
        2,3
        7,3
    """.trimIndent()

    partOne { exampleInput shouldOutput 50 }
    partTwo { exampleInput shouldOutput 24 }
})
