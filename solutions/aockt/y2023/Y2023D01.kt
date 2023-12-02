package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D01 : Solution {

    /** A map from digit symbols and spellings to their numerical values. */
    private val textToDigit: Map<String, Int> = buildMap {
        listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
            .withIndex()
            .forEach { (value, text) ->
                put(value.toString(), value)
                put(text, value)
            }
    }

    /**
     * Processes a modified calibration string and extracts the original calibration number.
     * @param acceptSpelling Whether to accept spelled-out digits. If false, only numerical notation is considered.
     */
    private fun String.normalizeCalibrationNumber(acceptSpelling: Boolean): Int = runCatching {
        val acceptable =
            if(acceptSpelling) textToDigit.keys
            else textToDigit.keys.filter { it.length == 1 && it.first().isDigit() }

        val first = findAnyOf(acceptable)!!.second.let(textToDigit::getValue)
        val last = findLastAnyOf(acceptable)!!.second.let(textToDigit::getValue)
        first * 10 + last
    }.getOrElse { throw IllegalArgumentException("Invalid input.", it) }

    /** Parses the [input], computes the calibration numbers and returns their sum. */
    private fun calibrate(input: String, acceptSpelling: Boolean): Int =
        input
            .lineSequence()
            .sumOf { it.normalizeCalibrationNumber(acceptSpelling) }

    override fun partOne(input: String) = calibrate(input, acceptSpelling = false)
    override fun partTwo(input: String) = calibrate(input, acceptSpelling = true)
}
