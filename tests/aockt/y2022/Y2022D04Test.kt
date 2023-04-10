package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 4, "Camp Cleanup")
class Y2022D04Test : AdventSpec<Y2022D04>({

    val exampleInput = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()

    partOne { exampleInput shouldOutput 2 }
    partTwo { exampleInput shouldOutput 4 }
})
