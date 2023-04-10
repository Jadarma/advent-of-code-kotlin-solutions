package aockt.y2022

import io.github.jadarma.aockt.core.Solution

object Y2022D01 : Solution {

    /** Parse the input and return a list containing the total calories of each elf at a given index. */
    private fun parseInput(input: String): List<Int> = buildList {
        var index = 0
        var calories = 0
        input.lineSequence().forEach { line ->
            if (line.isBlank()) {
                add(index, calories)
                index += 1
                calories = 0
            } else {
                calories += line.toInt()
            }
        }
        add(index, calories)
    }

    override fun partOne(input: String) = parseInput(input).max()
    override fun partTwo(input: String) = parseInput(input).sortedDescending().take(3).sum()
}
