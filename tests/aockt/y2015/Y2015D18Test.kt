package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 18, "Like a GIF For Your Yard")
class Y2015D18Test : AdventSpec<Y2015D18>({

    val exampleInput = """
        ###...
        .#....
        ......
        ..#.#.
        ..###.
        ...#..
    """.trimIndent()

    partOne { exampleInput shouldOutput 4 }
    partTwo { exampleInput shouldOutput 7 }
})
