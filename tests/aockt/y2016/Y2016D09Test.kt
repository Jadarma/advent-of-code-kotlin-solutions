package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 9, "Explosives in Cyberspace")
class Y2016D09Test : AdventSpec<Y2016D09>({

    partOne {
        "ADVENT" shouldOutput 6
        "A(1x5)BC" shouldOutput 7
        "(3x3)XYZ" shouldOutput 9
        "A(2x2)BCD(2x2)EFG" shouldOutput 11
        "(6x1)(1x3)A" shouldOutput 6
        "X(8x2)(3x3)ABCY" shouldOutput 18
    }

    partTwo {
        "(3x3)XYZ" shouldOutput 9
        "X(8x2)(3x3)ABCY" shouldOutput 20
        "(27x12)(20x12)(13x14)(7x10)(1x12)A" shouldOutput 241920
        "(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN" shouldOutput 445
    }
})
