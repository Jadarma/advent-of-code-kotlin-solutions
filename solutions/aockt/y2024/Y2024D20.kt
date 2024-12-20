package aockt.y2024

import aockt.util.math.distinctPairs
import aockt.util.parse
import aockt.util.spacial.*
import aockt.y2024.Y2024D20.Racetrack.Tile.*
import io.github.jadarma.aockt.core.Solution

object Y2024D20 : Solution {

    /**
     * A track for the race condition festival.
     *
     * @property start The starting point.
     * @property end The finish line.
     */
    private class Racetrack(private val data: Grid<Tile>) : Grid<Racetrack.Tile> by data {

        /** Possible values of the map cells. */
        enum class Tile { Empty, Wall, Start, End }

        val start: Point = points().single { it.value == Start }.position
        val end: Point = points().single { it.value == End }.position

        /** A mapping from every non-[Wall] point on the map to the distance from the start. */
        private val distances: Map<Point, Int> by lazy {
            buildMap {
                var current = start
                var direction = Direction.all.single { data[start.move(it)] != Wall }
                while (true) {
                    put(current, size)
                    if (current == end) break

                    direction = Direction.all
                        .asSequence()
                        .minus(direction.opposite)
                        .single { data[current.move(it)] != Wall }

                    current = current.move(direction)
                }
            }
        }

        /**
         * Find all possible cheats as pairs of points from the start to the end of the wall-hack.
         * @param maxDistance The maximum distance a cheat can cover.
         * @param saveAtLeast The minimum advantage over a fair run.
         */
        fun cheats(maxDistance: Int, saveAtLeast: Int): Sequence<Pair<Point, Point>> =
            distances.keys
                .distinctPairs()
                .filter { (a, b) -> a.distanceTo(b) <= maxDistance }
                .filter { (a, b) ->
                    val fromStart = distances.getValue(a)
                    val toEnd = distances.run { size - getValue(b) }
                    val cheat = a.distanceTo(b)
                    val fairPath = distances.size
                    val cheatPath = fromStart + cheat + toEnd
                    fairPath - cheatPath >= saveAtLeast
                }
    }

    /** Parse the [input] and return the [Racetrack] layout. */
    private fun parseInput(input: String): Racetrack = parse {
        Grid(input) {
            when (it) {
                '.' -> Empty
                '#' -> Wall
                'S' -> Start
                'E' -> End
                else -> error("Invalid input.")
            }
        }.let(::Racetrack)
    }

    /** Common solution for both parts. */
    private fun solve(input: String, maximumCheatDuration: Int): Int {
        val raceTrack = parseInput(input)
        val goal = if (raceTrack.width < 50) 64 else 100
        return raceTrack.cheats(maxDistance = maximumCheatDuration, saveAtLeast = goal).count()
    }

    override fun partOne(input: String): Int = solve(input, maximumCheatDuration = 2)
    override fun partTwo(input: String): Int = solve(input, maximumCheatDuration = 20)
}
