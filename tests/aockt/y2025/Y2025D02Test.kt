package aockt.y2025

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2025, 2, "Gift Shop")
class Y2025D02Test : AdventSpec<Y2025D02>({

    val exampleInput = """
        11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
        1698522-1698528,446443-446449,38593856-38593862,565653-565659,
        824824821-824824827,2121212118-2121212124
    """.trimIndent().replace("\n", "").trim()

    partOne { exampleInput shouldOutput "1227775554" }
    partTwo { exampleInput shouldOutput "4174379265" }
})
