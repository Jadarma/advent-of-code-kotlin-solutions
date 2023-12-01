package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D01 : Solution {

    /** A map from digit spellings to their numerical values. */
    private val textToDigit: Map<String, Int> =
        listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
            .withIndex()
            .associate { it.value to it.index }

    /**
     * Parse the string and return a list of digits contained within it, with the following properties:
     * - The list is in order of appearance.
     * - The digits can also be spelled.
     * - A character in the input may be used to spell two different digits.
     * - If multiple digit spellings would have the same prefix, the longest wins (N/A in English).
     *
     * @param acceptSpelling Whether to accept spelling of digits. If false, only numerical values will be considered.
     */
    private fun String.digitize(acceptSpelling: Boolean): List<Int> = buildList {
        val windowSize = textToDigit.maxOfOrNull { it.key.length } ?: 1
        this@digitize
            .windowed(windowSize, partialWindows = true)
            .asSequence()
            .mapNotNull { window ->
                when {
                    window.first().isDigit() -> window.first().digitToInt()
                    !acceptSpelling -> null
                    else -> {
                        // Try all prefixes of the search window, longest to shortest.
                        window
                            .indices
                            .reversed()
                            .map { window.slice(0..it) }
                            .firstNotNullOfOrNull(textToDigit::get)
                    }
                }
            }
            .forEach(::add)
        require(isNotEmpty()) { "Invalid calibration input." }
    }

    /** Parses the [input] and returns a sequence of calibration numbers, one for each input row. */
    private fun calibrate(input: String, acceptSpelling: Boolean): Sequence<Int> =
        input
            .lineSequence()
            .map { it.digitize(acceptSpelling) }
            .map { it.first() * 10 + it.last() }

    override fun partOne(input: String) = calibrate(input, acceptSpelling = false).sum()
    override fun partTwo(input: String) = calibrate(input, acceptSpelling = true).sum()
}
