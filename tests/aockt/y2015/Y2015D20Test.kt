package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 20, "Infinite Elves and Infinite Houses")
class Y2015D20Test : AdventSpec<Y2015D20>({

    val exampleInput = "130"
    partOne { exampleInput shouldOutput 8 }
    partTwo { exampleInput shouldOutput 6 }
})
