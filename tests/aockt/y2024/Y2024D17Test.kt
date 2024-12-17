package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 17, "Chronospatial Computer")
class Y2024D17Test : AdventSpec<Y2024D17>({

    val example1 = """
        Register A: 729
        Register B: 0
        Register C: 0

        Program: 0,1,5,4,3,0
    """.trimIndent()

    val example2 = """
        Register A: 2024
        Register B: 0
        Register C: 0

        Program: 0,3,5,4,3,0
    """.trimIndent()

    partOne { example1 shouldOutput "4,6,3,5,6,3,5,2,1,0" }
    partTwo { example2 shouldOutput 117440 }
})
