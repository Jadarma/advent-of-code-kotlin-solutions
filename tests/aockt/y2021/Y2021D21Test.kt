package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 21, "Dirac Dice")
class Y2021D21Test : AdventSpec<Y2021D21>({

    val exampleInput = """
        Player 1 starting position: 4
        Player 2 starting position: 8
    """.trimIndent()

    partOne { exampleInput shouldOutput 739_785 }
    partTwo { exampleInput shouldOutput 444_356_092_776_315L }
})
