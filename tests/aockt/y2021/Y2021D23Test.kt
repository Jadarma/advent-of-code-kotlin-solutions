package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 23, "Amphipod")
class Y2021D23Test : AdventSpec<Y2021D23>({

    val exampleInput = """
        #############
        #...........#
        ###B#C#B#D###
          #A#D#C#A#
          #########
    """.trimIndent()

    partOne { exampleInput shouldOutput 12_521 }
    partTwo { exampleInput shouldOutput 44_169 }
})
