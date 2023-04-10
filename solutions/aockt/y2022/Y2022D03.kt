package aockt.y2022

import io.github.jadarma.aockt.core.Solution

object Y2022D03 : Solution {

    /** The priority value of a knapsack item type. */
    private val Char.priority: Int
        get() = when (this) {
            in 'a'..'z' -> this - 'a' + 1
            in 'A'..'Z' -> this - 'A' + 27
            else -> error("Char '$this' is not a valid item.")
        }

    override fun partOne(input: String) =
        input
            .lineSequence()
            .map { it.substring(0..it.length / 2) to it.substring(it.length / 2..it.lastIndex) }
            .map { (a, b) -> a.first { it in b } }
            .sumOf { it.priority }

    override fun partTwo(input: String) =
        input
            .lineSequence()
            .chunked(3)
            .map { group -> group.sortedBy { it.length } }
            .map { (a, b, c) -> a.first { it in b && it in c } }
            .sumOf { it.priority }
}
