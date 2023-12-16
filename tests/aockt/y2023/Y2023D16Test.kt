package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 16, "The Floor Will Be Lava")
class Y2023D16Test : AdventSpec<Y2023D16>({

    val exampleInput = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent()

    partOne { exampleInput shouldOutput 46 }
    partTwo { exampleInput shouldOutput 51 }
})
