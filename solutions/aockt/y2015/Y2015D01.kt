package aockt.y2015

import io.github.jadarma.aockt.core.Solution

object Y2015D01 : Solution {

    /**
     * Returns a [Sequence] of the floor number Santa is on as he follows the [input].
     * Starts with implicit 0.
     */
    private fun floors(input: String) =
        input
            .asSequence()
            .runningFold(0) { acc, c ->
                when (c) {
                    '(' -> acc + 1
                    ')' -> acc - 1
                    else -> throw IllegalArgumentException("Invalid character in input: '$c'.")
                }
            }

    override fun partOne(input: String): Int = floors(input).last()
    override fun partTwo(input: String): Int = floors(input).indexOfFirst { it == -1 }
}
