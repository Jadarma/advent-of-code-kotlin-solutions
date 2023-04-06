package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 8, "Matchsticks")
class Y2015D08Test : AdventSpec<Y2015D08>({

    val exampleInput =
        """
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
        """.trimIndent()

    partOne { exampleInput shouldOutput 12 }
    partTwo { exampleInput shouldOutput 19 }
})
