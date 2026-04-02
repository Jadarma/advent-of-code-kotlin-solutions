package aockt.y2021

import io.github.jadarma.aockt.AdventDay
import io.github.jadarma.aockt.AdventSpec

@AdventDay(2021, 1, "Sonar Sweep")
class Y2021D01Test : AdventSpec<Y2021D01>({

    val exampleInput = """
        199
        200
        208
        210
        200
        207
        240
        269
        260
        263
    """.trimIndent()

    partOne { exampleInput shouldOutput 7 }
    partTwo { exampleInput shouldOutput 5 }
})
