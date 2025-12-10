package aockt.y2025

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec
import io.github.jadarma.aockt.test.ExecMode
import io.kotest.matchers.shouldBe

@AdventDay(2025, 10, "Factory")
class Y2025D10Test : AdventSpec<Y2025D10>({
    val exampleInput = """
        [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
        [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
        [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
    """.trimIndent()

    partOne { exampleInput shouldOutput 7 }
    partTwo { exampleInput shouldOutput 33 }

//    debug { solution.partTwo(input) shouldBe 2 }
})
