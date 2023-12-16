package aockt.y2023

import aockt.util.parse
import aockt.util.spacial.Area
import aockt.util.spacial.Direction
import aockt.util.spacial.Direction.*
import aockt.util.spacial.Point
import aockt.util.spacial.move
import aockt.y2023.Y2023D16.Element.*
import io.github.jadarma.aockt.core.Solution

object Y2023D16 : Solution {

    /** The type of element a beam of light can encounter. */
    private enum class Element { LeftMirror, RightMirror, HorizontalSplitter, VerticalSplitter }

    /**
     * A mirror maze that can focus lasers.
     * @param content The elements in the maze and their location.
     */
    private class MirrorMaze(content: Iterable<Pair<Point, Element>>) {

        private val grid: MutableMap<Point, Element> = content
            .associate { it.first to it.second }
            .toMutableMap()

        /** The area of the maze. */
        val area: Area = Area(grid.keys)

        /** Get the element at the given [point], if it exists. */
        operator fun get(point: Point): Element? = grid[point]

        /**
         * Propagate a laser beam starting from a [source] location and [heading] into a direction.
         *
         * @param source  Where this beam starts from.
         * @param heading The direction of the beam.
         * @return        All points in the maze that the beam and all its splits energize at least once.
         *                The points are not given in order, as the beam can split multiple times.
         */
        fun beam(source: Point, heading: Direction): Set<Point> = buildSet {

            // The laser might be split into a loop, this acts like a circuit breaker.
            // Direction also is a factor, since it's valid for beams to cross each other.
            val seen = mutableSetOf<Pair<Point, Direction>>()

            fun beamBranch(source: Point, heading: Direction) {
                var point = source
                var direction = heading

                while (point in area) {
                    if (point to direction in seen) return
                    seen.add(point to direction)
                    add(point)

                    direction = when (get(point)) {
                        null -> direction
                        LeftMirror -> when (direction) {
                            Left -> Up
                            Right -> Down
                            Down -> Right
                            Up -> Left
                        }

                        RightMirror -> when (direction) {
                            Left -> Down
                            Right -> Up
                            Up -> Right
                            Down -> Left
                        }

                        HorizontalSplitter -> when (direction) {
                            Left, Right -> direction
                            Up, Down -> Left.also { beamBranch(point.move(Right), Right) }
                        }

                        VerticalSplitter -> when (direction) {
                            Up, Down -> direction
                            Left, Right -> Up.also { beamBranch(point.move(Down), Down) }
                        }
                    }

                    point = point.move(direction)
                }
            }

            beamBranch(source, heading)
        }
    }

    /** Parse the [input] and return the [MirrorMaze]. */
    private fun parseInput(input: String): MirrorMaze = parse {
        fun parseElement(char: Char): Element? = when (char) {
            '\\' -> LeftMirror
            '/' -> RightMirror
            '|' -> VerticalSplitter
            '-' -> HorizontalSplitter
            '.' -> null
            else -> error("Unknown char: $char.")
        }
        input
            .lines()
            .asReversed()
            .flatMapIndexed { y, line -> line.mapIndexed { x, c -> parseElement(c)?.let { Point(x, y) to it } } }
            .filterNotNull()
            .let(::MirrorMaze)
    }

    override fun partOne(input: String): Any {
        val maze = parseInput(input)
        return with(maze.area) { Point(xRange.first, yRange.last) }
            .let { source -> maze.beam(source, Right) }
            .count()
    }

    override fun partTwo(input: String): Any {
        val maze = parseInput(input)
        return buildList {
            with(maze.area) {
                for (y in yRange) add(maze.beam(Point(xRange.first, y), Right))
                for (x in xRange) add(maze.beam(Point(x, yRange.last), Down))
                for (y in yRange) add(maze.beam(Point(xRange.last, y), Left))
                for (x in xRange) add(maze.beam(Point(x, yRange.first), Up))
            }
        }.maxOf { beam -> beam.count() }
    }
}
