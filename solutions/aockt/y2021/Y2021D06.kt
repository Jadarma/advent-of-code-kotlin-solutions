package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D06 : Solution {

    /**
     * A simulation model of a population of lantern fish.
     * @constructor Initializes this population based on the days left of each fish's reproductive cycle.
     */
    private class LanternFishPopulation(seed: List<Int>) {
        private val cycles = LongArray(9) { 0L }

        init {
            seed.forEach { cycle ->
                require(cycle in 0..8) { "Invalid reproduction cycle for a LanternFish." }
                cycles[cycle]++
            }
        }

        /** Pass a day in the simulation, and give birth to more fish. */
        fun simulateDay() {
            val fishThatWillGiveBirth = cycles[0]
            for (age in 0..7) cycles[age] = cycles[age + 1]
            cycles[8] = fishThatWillGiveBirth
            cycles[6] += cycles[8]
        }

        /** Returns the total number of live fish at this point in the simulation. */
        fun populationCount(): Long = cycles.sum()
    }

    /** Parse the input and return the [LanternFishPopulation] described in the input. */
    private fun parseInput(input: String): LanternFishPopulation =
        input
            .splitToSequence(',')
            .map { cycle -> cycle.toIntOrNull() ?: throw IllegalArgumentException() }
            .let { LanternFishPopulation(it.toList()) }

    override fun partOne(input: String) =
        parseInput(input)
            .apply { repeat(80) { simulateDay() } }
            .populationCount()

    override fun partTwo(input: String) =
        parseInput(input)
            .apply { repeat(256) { simulateDay() } }
            .populationCount()
}
