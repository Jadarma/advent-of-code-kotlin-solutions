package aockt.y2021

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

object Y2021D07 : Solution {

    /** Return the median value of this collection of numbers, or `null` if the collection is empty. */
    private fun Collection<Int>.medianOrNull(): Double? {
        if (isEmpty()) return null
        return sorted().run {
            when (size % 2) {
                0 -> (this[size / 2] + this[size / 2 - 1]) / 2.0
                else -> this[size / 2].toDouble()
            }
        }
    }

    /** Parse the input and return the sequence of the crab's horizontal positions. */
    private fun parseInput(input: String): List<Int> =
        input
            .splitToSequence(',')
            .map { it.toIntOrNull() ?: throw IllegalArgumentException() }
            .toList().also { require(it.isNotEmpty()) }

    override fun partOne(input: String): Int {
        val positions = parseInput(input)
        val median = positions.medianOrNull()!!.roundToInt()
        return positions.sumOf { (it - median).absoluteValue }
    }

    override fun partTwo(input: String): Any {
        val positions = parseInput(input).sorted()
        return (positions.first()..positions.last()).minOf { candidate ->
            positions
                .sumOf { (it - candidate).absoluteValue.let { n -> n * (n + 1) / 2.0 } }
                .roundToInt()
        }
    }
}
