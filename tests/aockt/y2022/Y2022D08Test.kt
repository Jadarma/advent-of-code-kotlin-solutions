package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 8, "Treetop Tree House")
class Y2022D08Test : AdventSpec<Y2022D08>({

    val exampleInput = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()

    partOne { exampleInput shouldOutput 21 }
    partTwo { exampleInput shouldOutput 8 }
})
