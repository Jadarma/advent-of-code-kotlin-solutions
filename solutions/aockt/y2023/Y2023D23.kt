package aockt.y2023

import aockt.util.parse
import aockt.util.spacial.Area
import aockt.util.spacial.Direction
import aockt.util.spacial.Direction.*
import aockt.util.spacial.Point
import aockt.util.spacial.move
import aockt.util.spacial.opposite
import aockt.util.validation.assume
import io.github.jadarma.aockt.core.Solution

object Y2023D23 : Solution {

    /**
     * Information about a single node in the actual Snow Island map.
     * @property location The node coordinates.
     * @property slope    The direction the terrain is slanted in, if at all.
     */
    private data class Node(val location: Point, val slope: Direction?)

    /**
     * A node in the island map which is either an entry point or a junction of 3 or more paths.
     * @property location The node coordinates.
     * @property neighbors A map from the location of neighboring points of interest to the distance to reach them.
     */
    private data class PointOfInterest(val location: Point, val neighbors: Map<Point, Int>)

    /**
     * A navigation system for trails on an island.
     * @param nodes The raw data of the island map.
     * @property start The entry point at the top of the map.
     * @property end   The exit point at the bottom of the map.
     */
    private class SnowIslandNavigator(nodes: Iterable<Node>) {

        val start: Point
        val end: Point

        /** The points of interest, indexed by their location. */
        private val navigationMap: Map<Point, PointOfInterest>

        init {
            val originalNodes: Map<Point, Node> = nodes.associateBy { it.location }
            val area = Area(originalNodes.keys)

            start = Point(area.xRange.first, area.yRange.last)
            end = Point(area.xRange.last, area.yRange.first)

            check(start in originalNodes) { "Could not determine entry point." }
            check(end in originalNodes) { "Could not determine exit point." }

            val pointsOfInterest: Set<Point> = buildSet {
                add(start)
                for (node in originalNodes.values) {
                    val neighbors = Direction.all.map { node.location.move(it) }.filter { it in originalNodes }
                    if (neighbors.size >= 3) {
                        assume(node.slope == null) { "A junction should not have a slope." }
                        add(node.location)
                    }
                }
                add(end)
            }

            /**
             * Walks a trail [from] a point of interest [towards] a direction, returning the location of the next
             * point of interest and the amount walked to get there.
             * Returns null if a point of interest cannot be reached, such as if trying to walk up a slope, or the
             * trail is a dead end.
             */
            fun walkTrail(from: Point, towards: Direction): Pair<Point, Int>? {
                var trailLength = 0
                var trailDirection = towards
                var trailLocation = from

                while (true) {
                    trailLength += 1
                    trailLocation = trailLocation.move(trailDirection)

                    when (trailLocation) {
                        !in originalNodes -> return null
                        in pointsOfInterest -> return trailLocation to trailLength
                    }

                    trailDirection = when (val slope = originalNodes.getValue(trailLocation).slope) {
                        null -> Direction.all.minus(trailDirection.opposite)
                            .firstOrNull { trailLocation.move(it) in originalNodes }
                            ?: return null

                        else -> {
                            if (slope != trailDirection) return null
                            slope
                        }
                    }
                }
            }

            navigationMap = pointsOfInterest.associateWith { poi ->
                Direction.all
                    .mapNotNull { direction -> walkTrail(from = poi, towards = direction) }.toMap()
                    .let { neighbors -> PointOfInterest(poi, neighbors) }
            }

            check(pointsOfInterest.all { it in navigationMap }) { "Failed to correctly build navigation map." }
        }

        private fun dfsMaximize(point: Point, goal: Point, seen: MutableSet<Point>): Int {
            if (point == goal) return 0
            var maxCost = Int.MIN_VALUE
            val node = navigationMap.getValue(point)
            seen.add(point)
            for (next in node.neighbors) {
                if (next.key in seen) continue
                maxCost = maxOf(maxCost, dfsMaximize(next.key, goal, seen) + node.neighbors.getValue(next.key))
            }
            seen.remove(point)
            return maxCost
        }

        /**
         * Calculates the length of the longest path from [start] to [finish] that does not pass through the same
         * point twice. A negative value means such a path is not possible.
         */
        fun scenicRouteLength(start: Point, finish: Point): Int = dfsMaximize(start, finish, mutableSetOf())
    }

    /**
     * Parse the input and return the map of Snow Island.
     * @param input      The puzzle input.
     * @param withSlopes Whether to take in account the slopes or to ignore them.
     */
    private fun parseInput(input: String, withSlopes: Boolean): List<Node> = parse {
        input
            .lines()
            .asReversed()
            .flatMapIndexed { y, line -> line.mapIndexed { x, c -> Point(x, y) to c } }
            .mapNotNull { (point, c) ->
                val slope = when (c) {
                    '>' -> Right
                    '<' -> Left
                    'v' -> Down
                    '^' -> Up
                    else -> null
                }.takeIf { withSlopes }
                Node(point, slope).takeUnless { c == '#' }
            }
    }

    /** Create a navigation map from the nodes and return the longest path available between the start and end. */
    private fun List<Node>.solve(): Int = SnowIslandNavigator(this).run { scenicRouteLength(start, end) }

    override fun partOne(input: String) = parseInput(input, withSlopes = true).solve()
    override fun partTwo(input: String) = parseInput(input, withSlopes = false).solve()
}
