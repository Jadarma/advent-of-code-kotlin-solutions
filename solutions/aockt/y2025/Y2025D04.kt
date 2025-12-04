package aockt.y2025

import aockt.util.parse
import aockt.util.spacial.*
import io.github.jadarma.aockt.core.Solution

object Y2025D04 : Solution {

    /** Digital representation of the paper deposit of the printing department. */
    private class PaperStore(floorPlan: String) {

        private val grid: MutableGrid<Boolean> = MutableGrid(floorPlan) {
            require(it in "@.") { "Invalid floor plan: Unknown cell '$it'" }
            it == '@'
        }

        /** Returns the positions of stored paper rolls accessible via forklift. */
        fun accessiblePapers(): List<Point> =
            grid.points()
                .filter { it.value }
                .map { it.position }
                .filter { point ->
                    var total = 0
                    for (x in point.x - 1..point.x + 1) {
                        for (y in point.y - 1..point.y + 1) {
                            val neighbor = Point(x, y)
                            if (neighbor == point) continue
                            if (grid.getOrNull(neighbor) == true) total++
                            if (total >= 4) return@filter false
                        }
                    }
                    true
                }
                .toList()

        /** Removes all accessible paper rolls and returns how many, if any, were removed. */
        fun removeAccessiblePapers(): Int =
            accessiblePapers()
                .onEach { grid[it] = false }
                .count()
    }

    /** Parse the [input] and return the [PaperStore] layout. */
    private fun parseInput(input: String): PaperStore = parse { PaperStore(input) }

    override fun partOne(input: String) = parseInput(input).accessiblePapers().count()

    override fun partTwo(input: String) = parseInput(input).run {
        var total = 0
        while (true) {
            val removed = removeAccessiblePapers()
            if (removed == 0) break
            total += removed
        }
        total
    }
}
