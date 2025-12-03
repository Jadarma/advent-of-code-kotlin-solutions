package aockt.y2025

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2025, 3, "Lobby")
class Y2025D03Test : AdventSpec<Y2025D03>({

    val exampleInput = """
        987654321111111
        811111111111119
        234234234234278
        818181911112111
    """.trimIndent()

    partOne { exampleInput shouldOutput 357 }
    partTwo { exampleInput shouldOutput 3_121_910_778_619 }
})
