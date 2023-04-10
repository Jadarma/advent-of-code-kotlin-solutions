package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 11, "Dumbo Octopus")
class Y2021D11Test : AdventSpec<Y2021D11>({

    val exampleInput = """
        5483143223
        2745854711
        5264556173
        6141336146
        6357385478
        4167524645
        2176841721
        6882881134
        4846848554
        5283751526
    """.trimIndent()

    partOne { exampleInput shouldOutput 1656 }
    partTwo { exampleInput shouldOutput 195 }
})
