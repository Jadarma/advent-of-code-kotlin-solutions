package aockt.y2025

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2025, 1, "Secret Entrance")
class Y2025D01Test : AdventSpec<Y2025D01>({

    val exampleInput = """
            L68
            L30
            R48
            L5
            R60
            L55
            L1
            L99
            R14
            L82
        """.trimIndent()

    partOne { exampleInput shouldOutput 3 }
    partTwo { exampleInput shouldOutput 6 }
})
