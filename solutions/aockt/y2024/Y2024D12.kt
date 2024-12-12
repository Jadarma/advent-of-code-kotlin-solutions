package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Direction
import aockt.util.spacial.Grid
import aockt.util.spacial.Point
import aockt.util.spacial.getOrNull
import aockt.util.spacial.move
import aockt.util.spacial.points
import aockt.util.spacial.turnedClockwise
import io.github.jadarma.aockt.core.Solution

object Y2024D12 : Solution {

    /** The layout of an elven garden. */
    private class GardenMap(private val data: Grid<Char>) : Grid<Char> by data {

        /**
         * Calculate how much would it cost to fence-off every garden subplot.
         * If [withBulkDiscount], any plot side will cost one unit, regardless of its length.
         */
        fun estimateFencePrice(withBulkDiscount: Boolean): Int {
            val getPerimeterContribution = if (withBulkDiscount) ::countCorners else ::countSides
            val seen = mutableSetOf<Point>()

            return points().sumOf { (point, plant) ->
                val queue = ArrayDeque<Point>().apply { add(point) }
                var area = 0
                var perimeter = 0

                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                        .takeUnless { it in seen }
                        ?.also(seen::add)
                        ?.also { area++ }
                        ?: continue

                    perimeter += getPerimeterContribution(current, plant)

                    Direction.all
                        .asSequence()
                        .map(current::move)
                        .filter { getOrNull(it) == plant }
                        .forEach(queue::add)
                }

                area * perimeter
            }
        }

        /** Count how many of this [point]'s sides neighbor different plots or garden edges. */
        private fun countSides(point: Point, plant: Char): Int =
            Direction.all.count { getOrNull(point.move(it)) != plant }

        /** Count how many times this [point] corners different plots or garden edges. */
        private fun countCorners(point: Point, plant: Char): Int =
            Direction.all.count {
                val n1 = getOrNull(point.move(it))
                val n2 = getOrNull(point.move(it).move(it.turnedClockwise))
                val n3 = getOrNull(point.move(it.turnedClockwise))
                (n1 != plant && n3 != plant) || (n1 == plant && n2 != plant && n3 == plant)
            }
    }

    /** Parse the [input] and return the [GardenMap]. */
    private fun parseInput(input: String): GardenMap = parse { Grid(input).let(::GardenMap) }

    override fun partOne(input: String): Int = parseInput(input).estimateFencePrice(withBulkDiscount = false)
    override fun partTwo(input: String): Int = parseInput(input).estimateFencePrice(withBulkDiscount = true)
}
