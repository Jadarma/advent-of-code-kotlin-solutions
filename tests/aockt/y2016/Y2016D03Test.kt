package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 3, "Squares With Three Sides")
class Y2016D03Test : AdventSpec<Y2016D03>({

    partOne { "  5 10  25" shouldOutput 0 }

    partTwo {
        val exampleInput =
            """
            101 301 501
            102 302 502
            103 303 503
            201 401 601
            202 402 602
            203 403 603
            """.trimIndent()
        exampleInput shouldOutput 6
    }
})
