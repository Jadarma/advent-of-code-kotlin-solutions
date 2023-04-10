package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 3, "Binary Diagnostic")
class Y2021D03Test : AdventSpec<Y2021D03>({

    val exampleInput = """
        00100
        11110
        10110
        10111
        10101
        01111
        00111
        11100
        10000
        11001
        00010
        01010
    """.trimIndent()

    partOne { exampleInput shouldOutput 198 }
    partTwo { exampleInput shouldOutput 230 }
})
