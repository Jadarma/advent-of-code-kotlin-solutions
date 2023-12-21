package aockt.y2023

import aockt.util.parse
import aockt.util.spacial.Area
import aockt.util.spacial.Direction
import aockt.util.spacial.Point
import aockt.util.spacial.move
import aockt.util.validation.assume
import io.github.jadarma.aockt.core.Solution

object Y2023D21 : Solution {

    /**
     * The map to an elven garden, showing only the finite version of the repeating tile.
     * @property area  The bounds of the tile.
     * @property walls The coordinates of walls, which cannot be travelled on.
     */
    private class Garden(val area: Area, val walls: Set<Point>) {

        init {
            require(walls.all { it in area }) { "Some walls are not within the area." }
        }

        /** Get the neighbours of the [point], within the same tile, and excluding walls. */
        private fun neighborsOf(point: Point): List<Point> =
            Direction.all
                .map(point::move)
                .filter { it in area && it !in walls }

        /**
         * From the [start]ing point, simulates walking an exact number of [steps] and returns all the points where
         * you could end up.
         */
        fun explore(start: Point, steps: Int): Set<Point> = buildSet {
            val queue = ArrayDeque<Pair<Point, Int>>()
            val alreadyQueued = mutableSetOf<Point>()
            queue.add(start to steps)

            while (queue.isNotEmpty()) {
                val (point, stepsLeft) = queue.removeFirst()
                if (stepsLeft % 2 == 0) add(point)
                if (stepsLeft > 0) {
                    neighborsOf(point)
                        .filterNot { it in alreadyQueued }
                        .forEach {
                            queue.add(it to stepsLeft - 1)
                            alreadyQueued.add(it)
                        }
                }
            }
        }
    }

    /** Parse the [input] and return the garden shape and the elf's starting point. */
    private fun parseInput(input: String): Pair<Garden, Point> = parse {
        lateinit var start: Point
        lateinit var area: Area
        val walls = input
            .lines()
            .asReversed()
            .also { area = Area(it.first().length, it.size) }
            .asSequence()
            .flatMapIndexed { y, line -> line.mapIndexed { x, c -> Point(x, y) to c } }
            .onEach { (point, element) -> if (element == 'S') start = point }
            .filter { it.second == '#' }
            .map { it.first }
            .toSet()
        Garden(area, walls) to start
    }

    override fun partOne(input: String) = parseInput(input).let { (garden, start) -> garden.explore(start, 64).count() }

    override fun partTwo(input: String): Any {
        val (garden, start) = parseInput(input)
        val steps = 26501365

        val tileSize: Int = with(garden.area) {
            assume(width == height) { "The garden should be a square." }
            assume(width % 2 == 1L) { "The garden should have an odd-size, in order to have true middles." }
            assume(start.x == width / 2 && start.y == height / 2) { "The starting point should be in the middle." }
            assume(steps.rem(width) == width / 2) { "Walking straight will always end in the center of a garden." }
            val clearLines = (0L..start.x)
                .asSequence()
                .flatMap { i ->
                    listOf(
                        // Horizontal
                        Point(i, start.y),
                        Point(start.x + i, start.y),
                        // Vertical
                        Point(start.x, i),
                        Point(start.x, start.y + i),
                        // Rhombus
                        Point(i, start.y + i),
                        Point(start.x + i, height - 1 - i),
                        Point(width - 1 - i, start.y - i),
                        Point(start.x - i, i),
                    )
                }
                .none { it in garden.walls }
            assume(clearLines) { "The horizontal and vertical columns, and their 'bounding rhombus' should be empty." }
            width.toInt()
        }
        val gridSize: Int = steps / tileSize - 1

        // Different starting points, from the edges of the garden tile.
        val startTop = Point(start.x, tileSize - 1L)
        val startRight = Point(tileSize - 1L, start.y)
        val startBottom = Point(start.x, 0L)
        val startLeft = Point(0L, start.y)
        val startTopRight = Point(tileSize - 1L, tileSize - 1L)
        val startTopLeft = Point(0L, tileSize - 1L)
        val startBottomRight = Point(tileSize - 1L, 0L)
        val startBottomLeft = Point(0L, 0L)

        // Number of locations in tiles fully contained in the grid.
        val oddTiles = garden.explore(start, tileSize * 2 + 1).count().toLong()
        val evenTiles = garden.explore(start, tileSize * 2).count().toLong()
        val fullyContained: Long =
            oddTiles * (gridSize / 2 * 2 + 1L).let { it * it } +
                evenTiles * (gridSize.inc() / 2 * 2L).let { it * it }

        // Number of locations in the small corners of the grid.
        val inCorners: Long = listOf(startTop, startRight, startBottom, startLeft)
            .sumOf { garden.explore(it, tileSize - 1).count().toLong() }

        // Number of locations in the smaller triangles along the grid edges.
        val inSmallerTriangleEdges: Long = listOf(startTopRight, startBottomRight, startBottomLeft, startTopLeft)
            .sumOf { garden.explore(it, tileSize / 2 - 1).count().toLong() }
            .times(gridSize + 1)

        // Number of locations in the larger triangles along the grid edges.
        val inLargerTriangleEdges: Long = listOf(startTopRight, startBottomRight, startBottomLeft, startTopLeft)
            .sumOf { garden.explore(it, tileSize * 3 / 2 - 1).count().toLong() }
            .times(gridSize)

        return fullyContained + inCorners + inSmallerTriangleEdges + inLargerTriangleEdges
    }
}
