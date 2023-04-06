package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 15, "Science for Hungry People")
class Y2015D15Test : AdventSpec<Y2015D15>({

    val exampleInput =
        """
        Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
        Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3
        """.trimIndent()

    partOne { exampleInput shouldOutput 62_842_880 }
    partTwo { exampleInput shouldOutput 57_600_000 }
})
