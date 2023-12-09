package aockt.y2023

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D09 : Solution {

    /** A history report from an OASIS Scanner, with the [values] of the sensor readings. */
    private class History(val values: List<Int>) : List<Int> by values {

        private fun diffSequence() = generateSequence(values) { it.windowed(2) { (l, r) -> r - l } }
            .takeWhile { numbers -> numbers.all { it == 0 }.not() }

        /** Predict the next value of this report based on previously gathered [values]. */
        val predict: Int by lazy { diffSequence().sumOf { it.last() } }

        /** Predict the last value before this report started gathering the [values]. */
        val extrapolate: Int by lazy {
            diffSequence()
                .map { it.first() }
                .toList()
                .foldRight(0, Int::minus)
        }
    }

    /** Parse the [input] and return the Oasis Report histories. */
    private fun parseInput(input: String): List<History> = parse {
        input
            .lineSequence()
            .map { it.split(' ').map(String::toInt) }
            .map(::History)
            .toList()
    }

    override fun partOne(input: String) = parseInput(input).sumOf(History::predict)
    override fun partTwo(input: String) = parseInput(input).sumOf(History::extrapolate)
}
