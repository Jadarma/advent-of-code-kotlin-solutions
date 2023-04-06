package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 6, "Probably a Fire Hazard")
class Y2015D06Test : AdventSpec<Y2015D06>({

    partOne {
        val exampleInput =
            """
            turn on 0,0 through 999,999
            toggle 0,0 through 999,0
            turn off 499,499 through 500,500
            """.trimIndent()
        exampleInput shouldOutput (1_000_000 - 1000 - 4)
    }

    partTwo {
        val exampleInput =
            """
            turn on 0,0 through 0,0
            toggle 0,0 through 999,999
            """.trimIndent()
        exampleInput shouldOutput 2_000_001
    }
})
