package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 9, "All in a Single Night")
class Y2015D09Test : AdventSpec<Y2015D09>({

    val exampleInput =
        """
        London to Dublin = 464
        London to Belfast = 518
        Dublin to Belfast = 141
        """.trimIndent()

    partOne { exampleInput shouldOutput 605 }
    partTwo { exampleInput shouldOutput 982 }
})
