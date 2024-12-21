package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 21, "Keypad Conundrum")
class Y2024D21Test : AdventSpec<Y2024D21>({

    val exampleInput = """
        029A
        980A
        179A
        456A
        379A
    """.trimIndent()

    partOne { exampleInput shouldOutput 126_384 }
    partTwo { exampleInput shouldOutput 154_115_708_116_294 }
})
