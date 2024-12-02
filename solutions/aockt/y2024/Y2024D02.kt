package aockt.y2024

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue
import kotlin.math.sign

object Y2024D02 : Solution {

    /** A Red-Nosed Reindeer nuclear fusion report. */
    @JvmInline
    private value class Report(private val values: List<Int>) : List<Int> by values {

        /** Determines if the report is safe. If running [withDampener], tolerates a single error. */
        fun isSafe(withDampener: Boolean = false): Boolean {
            if (values.size < 2) return true
            if (withDampener) {
                return values.indices.any { index ->
                    values
                        .toMutableList().apply { removeAt(index) }
                        .let(::Report).isSafe(withDampener = false)
                }
            }

            val trend = values
                .windowed(2) { (x, y) -> x - y }
                .onEach { if (it.absoluteValue !in 1..3) return false }
                .sumOf { it.sign }

            return trend.absoluteValue == values.size - 1
        }
    }

    /** Parse the [input] and return the list of [Report]s. */
    private fun parseInput(input: String): List<Report> = parse {
        input
            .lineSequence()
            .map { line -> line.split(' ').map(String::toInt) }
            .map(::Report)
            .toList()
    }

    override fun partOne(input: String) = parseInput(input).count { it.isSafe(withDampener = false) }
    override fun partTwo(input: String) = parseInput(input).count { it.isSafe(withDampener = true) }
}
