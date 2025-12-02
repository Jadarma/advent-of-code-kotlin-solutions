package aockt.y2025

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2025D02 : Solution {

    /** Parse the [input] and return the product ID ranges. */
    private fun parseInput(input: String): List<LongRange> = parse {
        input
            .splitToSequence(',')
            .map { it.split('-', limit = 2) }
            .map { (start, end) -> start.toLong()..end.toLong() }
            .onEach { require(!it.isEmpty()) { "Empty product range: $it" } }
            .toList()
    }

    /**
     * Calculate the sum of all invalid IDs in the [productRanges].
     *
     * @param productRanges The product ranges to consider.
     * @param singleRepeat  If enabled, check for invalid IDs that repeat the same group exactly twice.
     *                      Otherwise, check for any amount of group repeats.
     */
    private fun solve(productRanges: List<LongRange>, singleRepeat: Boolean): Long {
        val repeatingRegex = Regex("""^(\d+)\1${if (singleRepeat) "" else "+"}$""")
        return productRanges
            .asSequence()
            .flatMap(LongRange::asSequence)
            .filter { id -> id.toString().matches(repeatingRegex) }
            .sum()
    }

    override fun partOne(input: String) = solve(parseInput(input), singleRepeat = true)
    override fun partTwo(input: String) = solve(parseInput(input), singleRepeat = false)
}
