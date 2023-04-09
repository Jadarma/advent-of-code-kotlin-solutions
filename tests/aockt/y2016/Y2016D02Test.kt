package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 2, "Bathroom Security")
class Y2016D02Test : AdventSpec<Y2016D02>({

    val exampleInput =
        """
        ULL
        RRDDD
        LURDL
        UUUUD
        """.trimIndent()

    partOne { exampleInput shouldOutput "1985" }
    partTwo { exampleInput shouldOutput "5DB3" }
})
