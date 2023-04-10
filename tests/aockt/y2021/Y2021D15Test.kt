package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 15, "Chiton")
class Y2021D15Test : AdventSpec<Y2021D15>({

    val exampleInput = """
        1163751742
        1381373672
        2136511328
        3694931569
        7463417111
        1319128137
        1359912421
        3125421639
        1293138521
        2311944581
    """.trimIndent()

    partOne { exampleInput shouldOutput 40 }
    partTwo { exampleInput shouldOutput 315 }
})
