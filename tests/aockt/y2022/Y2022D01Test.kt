package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 1, "Calorie Counting")
class Y2022D01Test : AdventSpec<Y2022D01>({

    val exampleInput = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent()

    partOne { exampleInput shouldOutput 24000 }
    partTwo { exampleInput shouldOutput 45000 }
})
