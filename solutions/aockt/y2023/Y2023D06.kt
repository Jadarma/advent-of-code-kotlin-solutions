package aockt.y2023

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D06 : Solution {

    /** A race record, detailing the [time] it took and the total [distance] travelled. */
    private data class RaceRecord(val time: Long, val distance: Long)

    /**
     * A race simulation result.
     * @property time The total time spent on the race.
     * @property waitTime How much time was spent holding the boat's button.
     * @property distance The total distance traveled by the boat in the [time] frame.
     */
    private data class RaceResult(val time: Long, val waitTime: Long, val distance: Long)

    /** Simulate all possible race outcomes that would beat this record, depending on how much to hold the button. */
    private fun RaceRecord.simulateBetterStrategies(): Sequence<RaceResult> = sequence {
        for (waitTime in 0..time) {
            val raceTime = time - waitTime
            val raceDistance = raceTime * waitTime
            if (raceDistance > distance) yield(RaceResult(time, waitTime, raceDistance))
        }
    }

    /**
     * Parses the [input], returning the list containing boat race records, depending on how you squint.
     * @param input The puzzle input.
     * @param noticeKerning Whether to treat the input as one big number or not.
     */
    private fun parseInput(input: String, noticeKerning: Boolean): List<RaceRecord> = parse {
        fun String.parseMany() = split(' ').map(String::trim).filter(String::isNotBlank).map(String::toLong)
        fun String.parseOne() = split(' ').joinToString(separator = "", transform = String::trim).toLong().let(::listOf)

        val parser = if (noticeKerning) String::parseOne else String::parseMany

        val (timeLine, distanceLine) = input.split('\n', limit = 2)
        val times = timeLine.substringAfter("Time:").run(parser)
        val distances = distanceLine.substringAfter("Distance:").run(parser)
        require(times.size == distances.size) { "Some records are incomplete." }

        times.zip(distances, ::RaceRecord)
    }

    /** Given some records, finds in how many ways can each of it be beaten, and returns the product of those values.*/
    private fun List<RaceRecord>.solve() = map { it.simulateBetterStrategies().count() }.fold(1L, Long::times)

    override fun partOne(input: String) = parseInput(input, noticeKerning = false).solve()
    override fun partTwo(input: String) = parseInput(input, noticeKerning = true).solve()
}
