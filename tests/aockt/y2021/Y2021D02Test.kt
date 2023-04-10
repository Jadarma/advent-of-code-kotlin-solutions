package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 2, "Dive!")
class Y2021D02Test : AdventSpec<Y2021D02>({

    val exampleInput = """
        forward 5
        down 5
        forward 8
        up 3
        down 8
        forward 2
    """.trimIndent()

    partOne { exampleInput shouldOutput 150 }
    partTwo { exampleInput shouldOutput 900 }
})
