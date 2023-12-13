package aockt.y2023

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D13 : Solution {

    /**
     * Notes on the terrain of Lava Island, but without any lava...
     * @param data The terrain reading, first element is first row, then going upwards. Should only contain `[#.]`.
     */
    private class LavaIslandMap(data: List<String>) {

        // The terrain readings, transposed to make symmetry scanning, well... symmetrical!
        private val byRow: List<BooleanArray>
        private val byColumn: List<BooleanArray>

        init {
            require(data.isNotEmpty()) { "No data to process." }
            val height = data.size
            val width = data.first().length
            require(data.all { it.length == width }) { "Map is not perfect grid." }

            byRow = List(height) { BooleanArray(width) }
            byColumn = List(width) { BooleanArray(height) }

            for (y in 0..<height) {
                for (x in 0..<width) {
                    val state = when (data[y][x]) {
                        '.' -> false
                        '#' -> true
                        else -> error("Invalid map state.")
                    }
                    byRow[height - 1 - y][x] = state
                    byColumn[x][height - 1 - y] = state
                }
            }
        }

        /** Returns the total amount of differences between the contents, 0 meaning perfect match. */
        private fun BooleanArray.compareTo(other: BooleanArray): Int {
            require(size == other.size) { "Cannot compare collections of different sizes." }
            return indices.count { index -> this[index] != other[index] }
        }

        /**
         * Find symmetries in the [pattern], allowing for exactly this amount of [smudges].
         * For each symmetry line found, returns the number of lines below it.
         */
        private fun findSymmetries(pattern: List<BooleanArray>, smudges: Int): List<Int> = buildList {
            for (mirror in 1..<pattern.size) {
                var errors = 0
                for (reflectionLine in 1..minOf(mirror, pattern.size - mirror)) {
                    val above = pattern[mirror + reflectionLine - 1]
                    val below = pattern[mirror - reflectionLine]
                    errors += above.compareTo(below)
                    if (errors > smudges) break
                }
                if (errors == smudges) add(mirror)
            }
        }

        /**
         * Summarize your notes on the observed volcanic pattern.
         * @param smudges How many smudges to expect there to be, exactly.
         * @return The sum between the total amount of columns to the left of each vertical reflection and 100 times
         *         the total amount of rows above each horizontal reflection.
         */
        fun summarize(smudges: Int): Int {
            val horizontal = findSymmetries(byRow, smudges)
            val vertical = findSymmetries(byColumn, smudges)
            return horizontal.sum() * 100 + vertical.sum()
        }
    }

    /** Parse the [input] and return the list of [LavaIslandMap]s. */
    private fun parseInput(input: String): List<LavaIslandMap> = parse {
        val inputRegex = Regex("""^[.#]+$""")
        input
            .splitToSequence("\n\n")
            .map { map -> map.lines().reversed() }
            .onEach { points -> points.all { it.matches(inputRegex) } }
            .map(::LavaIslandMap)
            .toList()
    }

    override fun partOne(input: String) = parseInput(input).sumOf { it.summarize(smudges = 0) }
    override fun partTwo(input: String) = parseInput(input).sumOf { it.summarize(smudges = 1) }
}
