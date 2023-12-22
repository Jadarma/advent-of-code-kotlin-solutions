package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 22, "Sand Slabs")
class Y2023D22Test : AdventSpec<Y2023D22>({

    val exampleInput = """
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
    """.trimIndent()

    partOne { exampleInput shouldOutput 5 }
    partTwo { exampleInput shouldOutput 7 }
})
