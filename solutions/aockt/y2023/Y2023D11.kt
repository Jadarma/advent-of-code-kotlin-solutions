package aockt.y2023

import aockt.util.math.distinctPairs
import aockt.util.parse
import aockt.util.spacial.Point
import aockt.util.spacial.distanceTo
import io.github.jadarma.aockt.core.Solution

object Y2023D11 : Solution {

    /**
     * A galaxy expansion simulator.
     * @param galaxies The apparent locations of the tracked galaxies.
     */
    private class GalaxySimulator(private val galaxies: Set<Point>) {

        private val emptyRows: List<Long>
        private val emptyColumns: List<Long>

        init {
            require(galaxies.isNotEmpty()) { "Nothing to simulate." }
            val populatedRows = galaxies.map { it.y }.toSet()
            val populatedColumns = galaxies.map { it.x }.toSet()
            emptyRows = (0L..populatedRows.max()).filterNot { it in populatedRows }.toList()
            emptyColumns = (0L..populatedColumns.max()).filterNot { it in populatedColumns }.toList()
        }

        /** Expand the galaxy, increasing empty space by a [factor], returning the resulting locations of galaxies. */
        fun expand(factor: Int): Set<Point> {
            val delta = factor - 1
            var expansion = galaxies.toList()
            var expansionDelta = 0
            for(row in emptyRows) {
                expansion = expansion.map { (x, y) ->
                    if (y < row + expansionDelta) Point(x, y) else Point(x, y + delta)
                }
                expansionDelta += delta
            }
            expansionDelta = 0
            for(column in emptyColumns) {
                expansion = expansion.map { (x, y) ->
                    if (x < column + expansionDelta) Point(x, y) else Point(x + delta, y)
                }
                expansionDelta += delta
            }
            return expansion.toSet()
        }
    }

    /** Parse the [input] and return a [GalaxySimulator] initialized with current galactic readings. */
    private fun parseInput(input: String): GalaxySimulator = parse {
        input
            .lines()
            .asReversed()
            .flatMapIndexed { row, line -> line.mapIndexed { column, c -> Triple(row, column, c) } }
            .filter { it.third == '#' }
            .map { (y, x, _) -> Point(x.toLong(),y.toLong()) }
            .toSet()
            .let(::GalaxySimulator)
    }

    /**
     * Expand the galaxy by the given [expansionFactor], and return the sum of the shortest distances between all
     * unique galaxy pairs.
     */
    private fun GalaxySimulator.solve(expansionFactor: Int): Long =
        expand(expansionFactor)
            .distinctPairs()
            .sumOf { it.first distanceTo it.second }

    override fun partOne(input: String) = parseInput(input).solve(expansionFactor = 2)
    override fun partTwo(input: String) = parseInput(input).solve(expansionFactor = 1_000_000)
}
