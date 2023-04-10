package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 9, "Rope Bridge")
class Y2022D09Test : AdventSpec<Y2022D09>({

    partOne {
        val exampleInput = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2
        """.trimIndent()
        exampleInput shouldOutput 13
    }

    partTwo {
        val exampleInput = """
            R 5
            U 8
            L 8
            D 3
            R 17
            D 10
            L 25
            U 20
        """.trimIndent()
        exampleInput shouldOutput 36
    }
})
