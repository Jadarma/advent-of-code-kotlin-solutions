package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 7, "The Treachery of Whales")
class Y2021D07Test : AdventSpec<Y2021D07>({

    val exampleInput = "16,1,2,0,4,2,7,1,2,14"
    partOne { exampleInput shouldOutput 37 }
    partTwo { exampleInput shouldOutput 168 }
})
