package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 12, "Garden Groups")
class Y2024D12Test : AdventSpec<Y2024D12>({

    val example1 = """
        AAAA
        BBCD
        BBCC
        EEEC
    """.trimIndent()

    val example2 = """
        OOOOO
        OXOXO
        OOOOO
        OXOXO
        OOOOO
    """.trimIndent()

    val example3 = """
        EEEEE
        EXXXX
        EEEEE
        EXXXX
        EEEEE
    """.trimIndent()

    val example4 = """
        AAAAAA
        AAABBA
        AAABBA
        ABBAAA
        ABBAAA
        AAAAAA
    """.trimIndent()

    val example5 = """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent()

    partOne {
        example1 shouldOutput 140
        example2 shouldOutput 772
        example3 shouldOutput 692
        example4 shouldOutput 1184
        example5 shouldOutput 1930
    }

    partTwo {
        example1 shouldOutput 80
        example5 shouldOutput 1206
        example3 shouldOutput 236
        example4 shouldOutput 368
    }
})
