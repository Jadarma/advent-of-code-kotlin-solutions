package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Direction.*
import aockt.util.spacial.*
import aockt.y2024.Y2024D16.ReindeerMaze.Tile.*
import io.github.jadarma.aockt.core.Solution
import java.util.*

object Y2024D16 : Solution {

    /**
     * The map of the Reindeer maze.
     *
     * @param data The maze data. Must be walled-off, and have exactly one start and end.
     *
     * @property start The maze start. Reindeer start from here facing right.
     * @property end The maze end. Reindeer must find this point.
     * @property costOfShortestPath The minimum cost to reach the [end], or -1 if the maze is unsolvable.
     * @property bestSeatCount How many seats are on at least one of the best routes, or -1 if the maze is unsolvable.
     */
    private class ReindeerMaze(private val data: Grid<Tile>) : Grid<ReindeerMaze.Tile> by data {

        /** Possible values of the maze cells. */
        enum class Tile { Wall, Empty, Start, End }

        val start: Point = points().single { it.value == Start }.position
        val end: Point = points().single { it.value == End }.position

        val costOfShortestPath: Int get() = solution.first
        val bestSeatCount: Int get() = solution.second

        /** The solutions to [costOfShortestPath] and [bestSeatCount], computed by solving the maze with Dijkstra. */
        private val solution: Pair<Int, Int> by lazy {

            var minCost = Int.MAX_VALUE
            val bestSeats = mutableSetOf<Point>()
            val seen = mutableMapOf<Pair<Point, Direction>, Int>()

            val queue = PriorityQueue(compareBy(Reindeer::cost))
            queue.add(Reindeer(listOf(start), Right, 0))

            while (queue.isNotEmpty()) {
                queue
                    .poll()
                    .takeIf { (path, orientation, cost) ->
                        // Update known costs.
                        val node = path.last() to orientation
                        val bestKnownCost = seen.getOrDefault(node, Int.MAX_VALUE)
                        if (cost < bestKnownCost) seen[node] = cost

                        // Solutions emerge in sorted cost order. If we found a longer solution, we can stop.
                        if (path.last() == end) {
                            if (cost > minCost) return@lazy minCost to bestSeats.size
                            minCost = cost
                            bestSeats += path
                        }

                        // Prune suboptimal solutions.
                        cost <= bestKnownCost
                    }
                    ?.possibleNextNodes()
                    ?.forEach(queue::add)
            }

            -1 to -1
        }

        /** Reindeer pathfinding search state, with the [path] taken so far and current [orientation]. */
        private data class Reindeer(val path: List<Point>, val orientation: Direction, val cost: Int)

        /** Return the next possible search states. */
        private fun Reindeer.possibleNextNodes(): Sequence<Reindeer> =
            Direction.all.asSequence()
                .filterNot { it == orientation.opposite }
                .filterNot { get(path.last().move(it)) == Wall }
                .map {
                    Reindeer(
                        path = path + path.last().move(it),
                        orientation = it,
                        cost = cost + if (it == orientation) 1 else 1001
                    )
                }
    }

    /** Parse the [input] and return the [ReindeerMaze]. */
    private fun parseInput(input: String): ReindeerMaze = parse {
        Grid(input) {
            when (it) {
                '#' -> Wall
                '.' -> Empty
                'S' -> Start
                'E' -> End
                else -> error("Invalid input node: $it")
            }
        }.let(::ReindeerMaze)
    }

    override fun partOne(input: String) = parseInput(input).costOfShortestPath
    override fun partTwo(input: String) = parseInput(input).bestSeatCount
}
