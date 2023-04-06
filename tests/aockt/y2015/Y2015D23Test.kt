package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 23, "Opening the Turing Lock")
class Y2015D23Test : AdventSpec<Y2015D23>({

    val exampleInput =
        """
        inc b
        jio b, +2
        tpl b
        inc b
        """.trimIndent()

    partOne { exampleInput shouldOutput 2 }
    partTwo()
})
