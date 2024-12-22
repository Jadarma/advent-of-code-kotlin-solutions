package aockt.y2024

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2024D22 : Solution {

    /** Generate a pseudo-random sequence of numbers from the starting [seed]. */
    private fun pseudoRandom(seed: Long): Sequence<Long> = generateSequence(seed) {
        var result = it
        result = result xor (result shl 6 ) and 0xFFFFFF
        result = result xor (result shr 5 ) and 0xFFFFFF
        result = result xor (result shl 11) and 0xFFFFFF
        result
    }

    /** Return the buyer prices from a given trader [seed], with a limit of how many secrets to generate [perDay]. */
    private fun buyerPrices(seed: Long, perDay: Int = 2000): Sequence<Int> =
        pseudoRandom(seed)
            .take(perDay + 1)
            .map { (it % 10).toInt() }

    /** Parse the [input] and return the initial seed for each monkey buyer. */
    private fun parseInput(input: String): List<Long> = parse { input.lines().map(String::toLong) }

    override fun partOne(input: String): Long = parseInput(input).sumOf { pseudoRandom(it).drop(2000).first() }

    override fun partTwo(input: String): Int =
        buildMap {
            for (seed in parseInput(input)) {
                val seen = mutableSetOf<List<Int>>()
                for ((a, b, c, d, e) in buyerPrices(seed).windowed(size = 5)) {
                    val sequence = listOf(b - a, c - b, d - c, e - d)
                        .takeIf { it !in seen }
                        ?.also(seen::add)
                        ?: continue

                    compute(sequence) { _, v -> (v ?: 0) + e }
                }
            }
        }.maxOf { it.value }
}
