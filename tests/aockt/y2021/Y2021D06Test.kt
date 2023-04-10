package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 6, "Lanternfish")
class Y2021D06Test : AdventSpec<Y2021D06>({
    partOne { "3,4,3,1,2" shouldOutput 5934 }
    partTwo { "3,4,3,1,2" shouldOutput 26984457539L }
})
