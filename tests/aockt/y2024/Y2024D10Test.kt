package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 10, "Hoof It")
class Y2024D10Test : AdventSpec<Y2024D10>({

    val example1 = """
        0123
        1234
        8765
        9876
    """.trimIndent()

    val example2 = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent()

    partOne {
        example1 shouldOutput 1
        example2 shouldOutput 36
    }

    partTwo {
        example1 shouldOutput 16
        example2 shouldOutput 81
    }
})
