package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 19, "Linen Layout")
class Y2024D19Test : AdventSpec<Y2024D19>({

    val exampleInput = """
        r, wr, b, g, bwu, rb, gb, br

        brwrr
        bggr
        gbbr
        rrbgbr
        ubwu
        bwurrg
        brgr
        bbrgwb
    """.trimIndent()

    partOne { exampleInput shouldOutput 6 }
    partTwo { exampleInput shouldOutput 16 }
})
