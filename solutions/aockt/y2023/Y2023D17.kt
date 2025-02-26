package aockt.y2023

import aockt.util.*
import aockt.util.spacial.Area
import aockt.util.spacial.Direction
import aockt.util.spacial.Direction.*
import aockt.util.spacial.Point
import aockt.util.spacial.move
import aockt.util.spacial.opposite
import io.github.jadarma.aockt.core.Solution

object Y2023D17 : Solution {

    /**
     * A map of the factory in Gear Island.
     * @param initial The points in the map, associated to the amount of lost heat whenever travelling through it.
     */
    private class GearIslandMap(initial: Iterable<Pair<Point, Int>>) {

        private val grid: Map<Point, Int> = initial.associate { it.first to it.second }

        val area: Area = Area(grid.keys)

        /** A search state when navigating with a crucible. */
        private data class CrucibleSearchState(
            val position: Point,
            val direction: Direction,
            val straightLineStreak: Int,
        )

        /**
         * Get the valid neighbouring nodes of a search state, by making sure to prevent the crucible:
         * - from going out of bounds.
         * - from going too much in the same direction.
         * - from turning 180 degrees.
         * - from turning if it is an ultra crucible and hasn't walked enough in a straight line.
         *
         * @param node          The current search state.
         * @param ultraCrucible Whether to navigate as an ultra crucible, or a regular one.
         */
        private fun neighboursOf(
            node: CrucibleSearchState,
            ultraCrucible: Boolean,
        ): List<Pair<CrucibleSearchState, Int>> {
            val validDirections = listOf(Up, Down, Left, Right)
                .minus(node.direction.opposite)
                .run { if (node.straightLineStreak == if (ultraCrucible) 10 else 3) minus(node.direction) else this }
                .run { if (ultraCrucible && node.straightLineStreak in 1..<4) listOf(node.direction) else this }
                .filter { direction -> node.position.move(direction) in area }

            return validDirections.map { direction ->
                val nextPosition = node.position.move(direction)
                val nextCost = grid.getValue(nextPosition)
                val nextState = CrucibleSearchState(
                    position = nextPosition,
                    direction = direction,
                    straightLineStreak = if (node.direction == direction) node.straightLineStreak.inc() else 1,
                )
                nextState to nextCost
            }
        }

        /** Navigate a crucible from [start] to [end], returning the path taken and the running cost along it. */
        fun navigate(start: Point, end: Point, withUltraCrucible: Boolean): List<Pair<Point, Int>> {
            return Pathfinding
                .search(
                    start = CrucibleSearchState(start, Right, 0),
                    neighbours = { neighboursOf(it, withUltraCrucible) },
                    goalFunction = { it.position == end && (!withUltraCrucible || it.straightLineStreak >= 4) },
                    trackPath = true,
                )!!
                .path
                .map { (node, cost) -> node.position to cost }
        }
    }

    /** Parse the [input] and return the [GearIslandMap]. */
    private fun parseInput(input: String): GearIslandMap = parse {
        input
            .lines()
            .asReversed()
            .flatMapIndexed { y, line -> line.mapIndexed { x, n -> Point(x, y) to n.digitToInt() } }
            .let(::GearIslandMap)
    }

    /** Calculate the cost of travelling with a crucible from the top-left to the bottom-right corners. */
    private fun GearIslandMap.solve(withUltraCrucible: Boolean): Int {
        val start = Point(area.xRange.first, area.yRange.last)
        val end = Point(area.xRange.last, area.yRange.first)
        return navigate(start, end, withUltraCrucible).last().second
    }

    override fun partOne(input: String) = parseInput(input).solve(withUltraCrucible = false)
    override fun partTwo(input: String) = parseInput(input).solve(withUltraCrucible = true)
}
