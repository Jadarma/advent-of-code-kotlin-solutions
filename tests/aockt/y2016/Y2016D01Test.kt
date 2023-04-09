package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 1, "No Time for a Taxicab")
class Y2016D01Test : AdventSpec<Y2016D01>({

    partOne {
        "R2, L3" shouldOutput 5
        "R2, R2, R2" shouldOutput 2
        "R5, L5, R5, R3" shouldOutput 12
    }

    partTwo {
        "R8, R4, R4, R8" shouldOutput 4
    }
})
