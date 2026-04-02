package aockt.y2021

import io.github.jadarma.aockt.AdventDay
import io.github.jadarma.aockt.AdventSpec

@AdventDay(2021, 10, "Syntax Scoring")
class Y2021D10Test : AdventSpec<Y2021D10>({

    val exampleInput = """
        [({(<(())[]>[[{[]{<()<>>
        [(()[<>])]({[<{<<[]>>(
        {([(<{}[<>[]}>{[]{[(<()>
        (((({<>}<{<{<>}{[]{[]{}
        [[<[([]))<([[{}[[()]]]
        [{[{({}]{}}([{[{{{}}([]
        {<[[]]>}<{[{[{[]{()[[[]
        [<(<(<(<{}))><([]([]()
        <{([([[(<>()){}]>(<<{{
        <{([{{}}[<[[[<>{}]]]>[]]
    """.trimIndent()

    partOne { exampleInput shouldOutput 26_397 }
    partTwo { exampleInput shouldOutput 288_957 }
})
