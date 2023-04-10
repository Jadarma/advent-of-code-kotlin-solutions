package aockt.y2021

import aockt.y2021.Y2021D01.Delta.*
import io.github.jadarma.aockt.core.Solution

object Y2021D01 : Solution {

    /** The possible Deltas between two consecutive inputs. */
    private enum class Delta { INCREASE, ZERO, DECREASE }

    /** Parses the input and returns the sequence of [Int]s read by the sonar scan. */
    private fun parseInput(input: String): Sequence<Int> =
        input
            .lineSequence()
            .map { it.toIntOrNull() ?: throw IllegalArgumentException("Invalid input.") }

    /** Given a sequence of numbers, returns a sequence of deltas between them. */
    private fun Sequence<Int>.deltas(): Sequence<Delta> =
        windowed(2)
            .map { (last, current) ->
                when (current - last) {
                    0 -> ZERO
                    in 1..Int.MAX_VALUE -> INCREASE
                    else -> DECREASE
                }
            }

    override fun partOne(input: String) =
        parseInput(input)
            .deltas()
            .count { it == INCREASE }

    override fun partTwo(input: String) =
        parseInput(input)
            .windowed(3)
            .map { it.sum() }
            .deltas()
            .count { it == INCREASE }
}
