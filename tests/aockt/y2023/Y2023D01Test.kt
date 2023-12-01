package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 1, "Trebuchet?!")
class Y2023D01Test : AdventSpec<Y2023D01>({

    partOne {
        val exampleInput = """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()

        exampleInput shouldOutput 142
    }

    partTwo {
        val exampleInput = """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()

        exampleInput shouldOutput 281
    }
})
