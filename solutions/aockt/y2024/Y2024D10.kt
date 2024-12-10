package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.*
import io.github.jadarma.aockt.core.Solution

object Y2024D10 : Solution {

    /** A hiking trail topographical map. */
    private class TopographicMap(private val data: Grid<Int>) : Grid<Int> by data {

        /** The locations of trailheads, or points with zero altitude. */
        val trailHeads: Set<Point> = data.points().mapNotNull { (p, v) -> p.takeIf { v == 0 } }.toSet()

        /**
         * Calculates the score of this [trailHead], or how many peaks can be reached from here.
         * If the point is not a trailhead, the score is 0.
         */
        fun scoreOf(trailHead: Point): Int = trailPathsFrom(trailHead, distinct = true).size

        /**
         * Calculates the rating of this [trailHead], or how many different routes you can take to reach a peak.
         * If the point is not a trailhead, the rating is 0.
         */
        fun ratingOf(trailHead: Point): Int = trailPathsFrom(trailHead, distinct = false).size

        /**
         * Finds all the trail paths from the [start] point, and returns the location of their peaks.
         * If the point is not a trailhead, returns an empty list.
         * If [distinct], duplicate paths to the same peak are discarded.
         */
        private fun trailPathsFrom(start: Point, distinct: Boolean): List<Point> = buildList {
            if (start !in trailHeads) return@buildList
            val area = Area(width, height)
            val visited = mutableSetOf<Point>()

            fun recurse(point: Point) {
                val altitude = data[point]

                if (altitude == 9) {
                    if (!distinct || point !in visited) add(point)
                    visited.add(point)
                }

                Direction.all
                    .asSequence()
                    .map(point::move)
                    .filter { it in area }
                    .filter { data[it] == altitude + 1 }
                    .forEach(::recurse)
            }

            recurse(start)
        }
    }

    /** Parse the [input] and return the [TopographicMap] of the hiking region. */
    private fun parseInput(input: String): TopographicMap = parse {
        val data = Grid(input) { it.digitToInt() }
        TopographicMap(data)
    }

    override fun partOne(input: String): Int = parseInput(input).run { trailHeads.sumOf(::scoreOf) }
    override fun partTwo(input: String): Int = parseInput(input).run { trailHeads.sumOf(::ratingOf) }
}
