package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D03 : Solution {

    private val mulRegex = Regex("""mul\((\d+),(\d+)\)""")
    private val dontRegex = Regex("""don't\(\).*?(?:do\(\)|$)""")

    override fun partOne(input: String) =
        mulRegex
            .findAll(input)
            .sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }

    override fun partTwo(input: String) =
        input
            .replace("\n", "|")
            .replace(dontRegex, "")
            .let(::partOne)
}
