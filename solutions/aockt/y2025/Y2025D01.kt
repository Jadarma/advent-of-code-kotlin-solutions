package aockt.y2025

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

class Y2025D01 : Solution {

    /**
     * Calculate how many times the dial clicks on 0 when starting the lock at 50 and executing a series of [bumps].
     *
     * @param bumps     The list of dial movements as amplitude, positive for right turns, negative for left turns.
     * @param onlyExact If enabled, only the final dial location is considered, otherwise count all passes through zero.
     */
    private fun solve(bumps: List<Int>, onlyExact: Boolean): Int =
        bumps.fold(50 to 0) { (dial, zeroes), bump ->
            val nextDial = (dial + bump).mod(100)

            val untilNextZero = when {
                dial == 0 -> 100
                bump > 0 -> 100 - dial
                else -> dial
            }

            val nextZeroes = zeroes + when {
                onlyExact -> if (nextDial == 0) 1 else 0
                bump.absoluteValue >= untilNextZero -> 1 + (bump.absoluteValue - untilNextZero) / 100
                else -> 0
            }

            nextDial to nextZeroes
        }.second

    /** Parse the [input] and return the list of bump value. Negative means to the left. */
    private fun parseInput(input: String): List<Int> =
        input
            .lineSequence()
            .map { line -> line.replace("L", "-") }
            .map { line -> line.replace("R", "+") }
            .map(String::toInt)
            .toList()

    override fun partOne(input: String): Int = solve(parseInput(input), true)
    override fun partTwo(input: String): Int = solve(parseInput(input), false)
}
