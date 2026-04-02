package aockt.y2024

import io.github.jadarma.aockt.AdventDay
import io.github.jadarma.aockt.AdventSpec

@AdventDay(2024, 25, "Code Chronicle")
class Y2024D25Test : AdventSpec<Y2024D25>({

    val exampleInput = """
        #####
        .####
        .####
        .####
        .#.#.
        .#...
        .....

        #####
        ##.##
        .#.##
        ...##
        ...#.
        ...#.
        .....

        .....
        #....
        #....
        #...#
        #.#.#
        #.###
        #####

        .....
        .....
        #.#..
        ###..
        ###.#
        ###.#
        #####

        .....
        .....
        .....
        #....
        #.#..
        #.#.#
        #####
    """.trimIndent()

    partOne { exampleInput shouldOutput 3 }
})
