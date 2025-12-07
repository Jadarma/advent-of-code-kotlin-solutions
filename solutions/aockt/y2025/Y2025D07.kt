package aockt.y2025

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2025D07 : Solution {

    /** The possible values of the laboratory layout. */
    private enum class Cell(private val symbol: Char) {
        Start('S'),
        Splitter('^'),
        Empty('.');

        companion object {
            fun of(symbol: Char): Cell =
                entries.find { it.symbol == symbol }
                    ?: throw IllegalArgumentException("Unknown symbol: $symbol")
        }
    }

    /** Parse the [input] and return the grid of [Cell]s. */
    private fun parseInput(input: String): List<List<Cell>> = parse {
        input
            .lineSequence()
            .map { it.map(Cell::of) }
            .filterNot { line -> line.all { it == Cell.Empty } }
            .toList()
            .apply {
                require(isNotEmpty()) { "Empty input!" }
                require(first().count { it == Cell.Start } == 1) { "Expected exactly one Start cell." }
                require(drop(1).none { it.contains(Cell.Start) }) { "Start cell cannot appear on lower rows." }
                require(all { it.first() == Cell.Empty && it.last() == Cell.Empty }) { "Missing space padding." }
            }
    }

    /**
     * Solve the quantum tachyon manifolds simulation.
     * @param grid The laboratory schematic.
     * @return The number times beams split and the total number of paths they can take in the multiverse.
     */
    private fun solve(grid: List<List<Cell>>): Pair<Long, Long> {
        val start = grid.first().indexOf(Cell.Start)
        val beams = MutableList(grid.first().size) { if (it == start) 1L else 0L }
        var splits = 0L

        grid.forEach { line ->
            beams.withIndex()
                .filter { line[it.index] == Cell.Splitter }
                .forEach { (index, paths) ->
                    if(paths > 0) splits++
                    beams[index - 1] += paths
                    beams[index + 1] += paths
                    beams[index] = 0
                }
        }

        return splits to beams.sum()
    }

    override fun partOne(input: String) = parseInput(input).let(::solve).first
    override fun partTwo(input: String) = parseInput(input).let(::solve).second
}
