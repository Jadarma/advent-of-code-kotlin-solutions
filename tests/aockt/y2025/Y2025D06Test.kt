package aockt.y2025

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2025, 6, "Trash Compactor")
class Y2025D06Test : AdventSpec<Y2025D06>({
    val exampleInput = """
        123 328  51 64
         45 64  387 23
          6 98  215 314
        *   +   *   +
    """.trimIndent()

    partOne { exampleInput shouldOutput 4277556 }
    partTwo { exampleInput shouldOutput 3263827 }
})
