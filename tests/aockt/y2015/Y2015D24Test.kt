package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 24, "It Hangs in the Balance")
class Y2015D24Test : AdventSpec<Y2015D24>({

    val exampleInput =
        """
        1
        2
        3
        4
        5
        7
        8
        9
        10
        11
        """.trimIndent()

    partOne { exampleInput shouldOutput 99 }
    partTwo { exampleInput shouldOutput 44 }
})
