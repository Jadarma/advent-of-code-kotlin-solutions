package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 11, "Plutonian Pebbles")
class Y2024D11Test : AdventSpec<Y2024D11>({
    val exampleInput = "125 17"
    partOne { exampleInput shouldOutput 55_312 }
    partTwo { exampleInput shouldOutput 65_601_038_650_482 }
})
