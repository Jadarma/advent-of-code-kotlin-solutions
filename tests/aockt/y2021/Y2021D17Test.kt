package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 17, "Trick Shot")
class Y2021D17Test : AdventSpec<Y2021D17>({

    val exampleInput = "target area: x=20..30, y=-10..-5"
    partOne { exampleInput shouldOutput 45 }
    partTwo { exampleInput shouldOutput 112 }
})
