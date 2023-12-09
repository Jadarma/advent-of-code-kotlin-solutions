package aockt.y2023

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D09 : Solution {

    /** A history report from an OASIS Scanner, with the [values] of the sensor readings. */
    private data class History(val values: List<Int>) : List<Int> by values {

        /** Predict the next value based on the existing list of [values]. */
        private fun prediction(values: List<Int>) = generateSequence(values) { it.windowed(2) { (l, r) -> r - l } }
            .takeWhile { numbers -> numbers.any { it != 0 } }
            .sumOf { it.last() }

        /** Predict the next value of this report based on previously gathered [values]. */
        val predictNext: Int by lazy { prediction(values) }

        /** Predict the last value before this report started gathering the [values]. */
        val predictPrevious: Int by lazy { prediction(values.reversed()) }
    }

    /** Parse the [input] and return the Oasis Report histories. */
    private fun parseInput(input: String): List<History> = parse {
        input
            .lineSequence()
            .map { it.split(' ').map(String::toInt) }
            .map(::History)
            .toList()
    }

    override fun partOne(input: String) = parseInput(input).sumOf(History::predictNext)
    override fun partTwo(input: String) = parseInput(input).sumOf(History::predictPrevious)
}
