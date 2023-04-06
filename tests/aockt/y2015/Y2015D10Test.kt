package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 10, "Elves Look, Elves Say")
class Y2015D10Test : AdventSpec<Y2015D10>({

    partOne {
        "1" shouldOutput 82350
        "11" shouldOutput 107312
        "21" shouldOutput 139984
        "1211" shouldOutput 182376
        "111221" shouldOutput 237746
    }

    partTwo {
        "1" shouldOutput 1166642
        "11" shouldOutput 1520986
        "21" shouldOutput 1982710
        "1211" shouldOutput 2584304
        "111221" shouldOutput 3369156
    }
})
