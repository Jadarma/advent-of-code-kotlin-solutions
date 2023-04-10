package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 12, "Hill Climbing Algorithm")
class Y2022D12Test : AdventSpec<Y2022D12>({

    val exampleInput = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent()

    partOne { exampleInput shouldOutput 31 }
    partTwo { exampleInput shouldOutput 29 }
})
