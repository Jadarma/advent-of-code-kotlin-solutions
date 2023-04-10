package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 9, "Smoke Basin")
class Y2021D09Test : AdventSpec<Y2021D09>({

    val exampleInput = """
        2199943210
        3987894921
        9856789892
        8767896789
        9899965678
    """.trimIndent()

    partOne { exampleInput shouldOutput 15 }
    partTwo { exampleInput shouldOutput 1134 }
})
