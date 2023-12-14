package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 14, "Parabolic Reflector Dish")
class Y2023D14Test : AdventSpec<Y2023D14>({

    val exampleInput = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent()

    partOne { exampleInput shouldOutput 136 }
    partTwo { exampleInput shouldOutput 64 }
})
