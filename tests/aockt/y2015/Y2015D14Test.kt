package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 14, "Reindeer Olympics")
class Y2015D14Test : AdventSpec<Y2015D14>({

    val exampleInput = """
        Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
        Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
    """.trimIndent()

    partOne { exampleInput shouldOutput 2660 }
    partTwo { exampleInput shouldOutput 1564 }
})
