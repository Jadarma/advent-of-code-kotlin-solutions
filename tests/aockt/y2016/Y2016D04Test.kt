package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 4, "Security Through Obscurity")
class Y2016D04Test : AdventSpec<Y2016D04>({

    val exampleInput =
        """
        aaaaa-bbb-z-y-x-123[abxyz]
        a-b-c-d-e-f-g-h-987[abcde]
        not-a-real-room-404[oarel]
        totally-real-room-200[decoy]
        """.trimIndent()

    partOne { exampleInput shouldOutput 1514 }
    partTwo()
})
