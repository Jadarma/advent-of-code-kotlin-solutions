package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 6, "Signals and Noise")
class Y2016D06Test : AdventSpec<Y2016D06>({

    val exampleInput =
        """
        eedadn
        drvtee
        eandsr
        raavrd
        atevrs
        tsrnev
        sdttsa
        rasrtv
        nssdts
        ntnada
        svetve
        tesnvt
        vntsnd
        vrdear
        dvrsen
        enarar
        """.trimIndent()

    partOne { exampleInput shouldOutput "easter" }
    partTwo { exampleInput shouldOutput "advent" }
})
