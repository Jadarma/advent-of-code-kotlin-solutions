package aockt.y2021

import aockt.util.parse
import aockt.util.validation.assume
import io.github.jadarma.aockt.core.Solution
import kotlin.math.abs
import kotlin.math.pow

/**
 * A difficult puzzle, requires reverse engineering instead of simulation.
 * This solution is adapted from the community solutions, kudos to:
 * - u/etotheipi1 for the [explanation](https://reddit.com/r/adventofcode/comments/rnejv5/2021_day_24_solutions/hps5hgw/).
 * - u/4HbQ for the [elegant implementation](https://reddit.com/r/adventofcode/comments/rnejv5/2021_day_24_solutions/hpsjfis/).
 */
object Y2021D24 : Solution {

    /**
     * Parse the input and extract the relevant constants in the repeating patterns:
     * - The number added to x in the 5th instruction, assumed to be in (-15..15) range.
     * - The number added to y in the 15th instruction, assumed to always be positive.
     */
    private fun parseInput(input: String): List<Pair<Int, Int>> = parse {
        val regex = """
            ^mul x 0\nadd x z\nmod x 26\ndiv z (?:1|26)\nadd x (-?\d+)
            eql x w\neql x 0\nmul y 0\nadd y 25\nmul y x\nadd y 1
            mul z y\nmul y 0\nadd y w\nadd y (\d+)\nmul y x\nadd z y$
            """.trimIndent().toRegex()

        input
            .splitToSequence("inp w")
            .filterNot { it.isEmpty() }
            .map { chunk -> regex.matchEntire(chunk.trim())?.destructured }
            .map {
                val (a, b) = it?.toList()?.map(String::toInt) ?: error("Invalid MONAD program")
                assume(abs(a) <= 15) { "Expected first constant to be in -15..15. " }
                assume(b > 0) { "Extracted second constant expected to be positive." }
                a to b
            }
            .toList()
            .also { require(it.size == 14) { "Expected to find exactly 14 chunks." } }
    }

    /** Given the [input] MONAD program, find the [biggest] or smallest compatible serial number. */
    private fun solve(input: String, biggest: Boolean): Long {
        var n = if (biggest) 99999999999999L else 11111111111111L
        val sign = if (biggest) -1L else 1L
        val stack = ArrayDeque<Pair<Int, Int>>()

        for ((i, constants) in parseInput(input).withIndex()) {
            val (a, b) = constants
            if (a > 0) {
                stack.addFirst(i to b)
                continue
            }

            val (j, c) = stack.removeFirst()

            val digit = abs(a + c).also { check(it in 1..9) { "Expected digit." } }
            val exp = 13 - if (a > -c == biggest) j else i
            n += sign * digit * 10.0.pow(exp).toLong()
        }

        return n
    }

    override fun partOne(input: String): Long = solve(input, biggest = true)
    override fun partTwo(input: String): Long = solve(input, biggest = false)
}
