package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 2, "I Was Told There Would Be No Math")
class Y2015D02Test : AdventSpec<Y2015D02>({

    partOne {
        "2x3x4" shouldOutput 58
        "1x1x10" shouldOutput 43
    }

    partTwo {
        "2x3x4" shouldOutput 34
        "1x1x10" shouldOutput 14
    }
})
