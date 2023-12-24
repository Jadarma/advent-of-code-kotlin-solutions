package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 24, "Never Tell Me The Odds")
class Y2023D24Test : AdventSpec<Y2023D24>({

    val exampleInput = """
        19, 13, 30 @ -2, 1, -2
        18, 19, 22 @ -1, -1, -2
        20, 25, 34 @ -2, -2, -4
        12, 31, 28 @ -1, -2, -1
        20, 19, 15 @ 1, -5, -3
    """.trimIndent()

    partOne { exampleInput shouldOutput 2 }
})
