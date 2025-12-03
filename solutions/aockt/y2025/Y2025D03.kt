package aockt.y2025

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2025D03 : Solution {

    /**
     * A battery bank for the emergency escalator.
     * @param jolts The joltage value, in order, of the batteries contained in this bank.
     */
    private class BatteryBank(private val jolts: IntArray) {

        init {
            require(jolts.size >= 2) { "A battery bank needs at least two batteries." }
            require(jolts.all { it in 1..9 }) { "Joltage needs to be a single positive digit." }
        }

        /**
         * Calculates the maximum joltage that can be achieved by activating a specific number of batteries.
         * @param batteryCount How many batteries should be used.
         */
        fun maximumJoltage(batteryCount: Int): Long {
            require(batteryCount >= 0) { "Cannot use negative amount of batteries." }
            require(batteryCount <= jolts.size) { "Bank does not contain enough batteries." }
            val batteryValues: List<IndexedValue<Int>> = jolts.withIndex().toList()
            var maxJoltage = 0L
            var startIndex = 0

            for (i in 1..batteryCount) {
                val (index, jolts) = batteryValues
                    .slice(startIndex until (batteryValues.size - batteryCount + i))
                    .maxBy { it.value }
                startIndex = index + 1
                maxJoltage = maxJoltage * 10 + jolts
            }

            return maxJoltage
        }
    }

    /** Parse the [input] and return the [BatteryBank] configurations of the escalator. */
    private fun parseInput(input: String): List<BatteryBank> = parse {
        input
            .lineSequence()
            .map { it.map(Char::digitToInt).toIntArray() }
            .map(::BatteryBank)
            .toList()
    }

    override fun partOne(input: String) = parseInput(input).sumOf { it.maximumJoltage(batteryCount = 2) }
    override fun partTwo(input: String) = parseInput(input).sumOf { it.maximumJoltage(batteryCount = 12) }
}
