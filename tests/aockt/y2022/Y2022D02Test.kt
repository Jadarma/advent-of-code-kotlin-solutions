package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 2, "Rock Paper Scissors")
class Y2022D02Test : AdventSpec<Y2022D02>({

    val exampleInput = """
        A Y
        B X
        C Z
    """.trimIndent()

    partOne { exampleInput shouldOutput 15 }
    partTwo { exampleInput shouldOutput 12 }
})
