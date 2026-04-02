package aockt.y2023

import io.github.jadarma.aockt.AdventDay
import io.github.jadarma.aockt.AdventSpec

@AdventDay(2023, 13, "Point of Incidence")
class Y2023D13Test : AdventSpec<Y2023D13>({

    val exampleInput = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent()

    partOne { exampleInput shouldOutput 405 }
    partTwo { exampleInput shouldOutput 400 }
})
