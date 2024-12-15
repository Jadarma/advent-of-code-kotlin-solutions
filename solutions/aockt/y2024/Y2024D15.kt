package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Direction
import aockt.util.spacial.Direction.*
import aockt.util.spacial.*
import aockt.y2024.Y2024D15.Warehouse.Tile
import aockt.y2024.Y2024D15.Warehouse.Tile.*
import io.github.jadarma.aockt.core.Solution

object Y2024D15 : Solution {

    /** Model of a lanternfish food warehouse. */
    private class Warehouse(val data: MutableGrid<Tile>) : Grid<Tile> by data {

        /** The types of tile a singular grid cell can have. */
        enum class Tile { Wall, Empty, Robot, Crate, WideCrateLeft, WideCrateRight }

        /** The current robot position. */
        private lateinit var robot: Point

        /**
         * Thorough input validation to ensure later assumptions:
         * - All edges are walls.
         * - Only one robot.
         * - Wide crate halves are properly paired.
         */
        init {
            var foundRobot = false
            data.points().forEach { (p, v) ->
                if (p.x == 0L || p.y == 0L || p.x == width - 1L || p.y == height - 1L) {
                    require(v == Wall) { "Invalid warehouse map. Edges should be walls." }
                }

                if (v == Robot) {
                    require(!foundRobot) { "There can only be one robot." }
                    foundRobot = true
                    robot = p
                }

                if (v == WideCrateLeft) {
                    require(getOrNull(p.move(Right)) == WideCrateRight) { "Wide crate cannot have split halves." }
                }

                if (v == WideCrateRight) {
                    require(getOrNull(p.move(Left)) == WideCrateLeft) { "Wide crate cannot have split halves." }
                }
            }

            require(foundRobot) { "Warehouse must have exactly one robot, but none was found." }
        }

        /** Calculate the sum of crates' GPS signals. */
        fun boxGpsSignal(): Long = points().sumOf { (p, v) ->
            when (v) {
                Crate, WideCrateLeft -> (height - 1 - p.y) * 100 + p.x
                else -> 0L
            }
        }

        /**
         * Attempt to move the robot in a [direction] and move crates accordingly.
         * This mutates the warehouse and returns a reference to the same instance.
         */
        fun move(direction: Direction): Warehouse = apply { shift(direction) }

        /** Checks if the robot can move in a [direction]. It can do so if it is an empty space, or a movable crate. */
        private fun canMove(direction: Direction): Boolean {

            fun recurse(point: Point): Boolean {
                val next = point.move(direction)
                return when (get(point)) {
                    Empty -> true
                    Wall -> false
                    WideCrateLeft -> recurse(next) && (direction is Horizontal || recurse(next.move(Right)))
                    WideCrateRight -> recurse(next) && (direction is Horizontal || recurse(next.move(Left)))
                    Crate, Robot -> recurse(next)
                }
            }

            return recurse(robot)
        }

        /** Moves the robot in a [direction] and moves crates if necessary. Only performs the action if it is valid. */
        private fun shift(direction: Direction) {

            if (!canMove(direction)) return

            fun recurse(point: Point) {
                val current = get(point)
                val next = point.move(direction)

                if (current == Empty) return
                check(current != Wall) { "Tried to shift a wall." }

                recurse(next)
                data[next] = current
                data[point] = Empty
                if (current == Robot) robot = next

                if (direction is Horizontal) return

                if (current == WideCrateLeft) {
                    recurse(next.move(Right))
                    data[next.move(Right)] = WideCrateRight
                    data[point.move(Right)] = Empty
                }

                if (current == WideCrateRight) {
                    recurse(next.move(Left))
                    data[next.move(Left)] = WideCrateLeft
                    data[point.move(Left)] = Empty
                }
            }

            recurse(robot)
        }
    }

    /**
     * Parse the [input] and return the [Warehouse] layout and the robot movements.
     * If [wide], returns the part two warehouse.
     */
    private fun parseInput(input: String, wide: Boolean): Pair<Warehouse, List<Direction>> = parse {
        val (originalLayout, movements) = input.split("\n\n", limit = 2)
        val layout = if (!wide) originalLayout else originalLayout
            .replace("#", "##")
            .replace(".", "..")
            .replace("O", "[]")
            .replace("@", "@.")

        val directions = movements.mapNotNull {
            when (it) {
                '<' -> Left
                '>' -> Right
                '^' -> Up
                'v' -> Down
                '\n' -> null
                else -> error("Invalid direction: $it")
            }
        }

        val tiles = MutableGrid(layout) {
            when (it) {
                '#' -> Wall
                '.' -> Empty
                'O' -> Crate
                '[' -> WideCrateLeft
                ']' -> WideCrateRight
                '@' -> Robot
                else -> error("Invalid map tile: $it")
            }
        }

        Warehouse(tiles) to directions
    }

    /** Common solution. */
    private fun solve(input: String, wide: Boolean): Long =
        parseInput(input, wide)
            .let { (warehouse, movements) -> movements.fold(warehouse, Warehouse::move) }
            .boxGpsSignal()

    override fun partOne(input: String): Long = solve(input, wide = false)
    override fun partTwo(input: String): Long = solve(input, wide = true)
}
