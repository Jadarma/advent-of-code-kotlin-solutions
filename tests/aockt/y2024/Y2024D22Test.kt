package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 22, "Monkey Market")
class Y2024D22Test : AdventSpec<Y2024D22>({

    val example1 = listOf(1, 10, 100, 2024).joinToString(separator = "\n")
    val example2 = listOf(1, 2, 3, 2024).joinToString(separator = "\n")

    partOne {
        example1 shouldOutput 37_327_623
        example2 shouldOutput 37_990_510
    }

    partTwo {
        example1 shouldOutput 24
        example2 shouldOutput 23
    }
})
