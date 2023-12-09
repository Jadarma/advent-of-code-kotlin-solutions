package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 9, "Mirage Maintenance")
class Y2023D09Test : AdventSpec<Y2023D09>({

    val exampleInput = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent()

    partOne { exampleInput shouldOutput 114 }
    partTwo { exampleInput shouldOutput 2 }
})
