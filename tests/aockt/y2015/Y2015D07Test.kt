package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 7, "Some Assembly Required")
class Y2015D07Test : AdventSpec<Y2015D07>({

    partOne {
        val exampleInput =
            """
            123 -> x
            456 -> y
            x AND y -> d
            x OR y -> e
            x LSHIFT 2 -> f
            y RSHIFT 2 -> g
            NOT x -> h
            NOT y -> a
            """.trimIndent()
        exampleInput shouldOutput 65079
    }

    partTwo()
})
