package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 14, "Extended Polymerization")
class Y2021D14Test : AdventSpec<Y2021D14>({

    val exampleInput = """
        NNCB

        CH -> B
        HH -> N
        CB -> H
        NH -> C
        HB -> C
        HC -> B
        HN -> C
        NN -> C
        BH -> H
        NC -> B
        NB -> B
        BN -> B
        BB -> N
        BC -> B
        CC -> N
        CN -> C
    """.trimIndent()

    partOne { exampleInput shouldOutput 1588 }
    partTwo { exampleInput shouldOutput 2_188_189_693_529L }
})
