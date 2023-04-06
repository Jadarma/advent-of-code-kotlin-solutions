package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 19, "Medicine for Rudolph")
class Y2015D19Test : AdventSpec<Y2015D19>({

    val exampleInput =
        """
        H => HO
        H => OH
        O => HH
        e => H
        e => O

        HOH
        """.trimIndent()

    partOne { exampleInput shouldOutput 4 }
    partTwo { exampleInput shouldOutput 3 }
})
