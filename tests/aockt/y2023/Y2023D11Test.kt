package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 11, "Cosmic Expansion")
class Y2023D11Test : AdventSpec<Y2023D11>({

    val exampleInput = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent()

    partOne { exampleInput shouldOutput 374 }
    partTwo { exampleInput shouldOutput 82000210 }
})
