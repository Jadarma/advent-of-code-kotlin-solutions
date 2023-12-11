package aockt.y2023

import aockt.util.Direction
import aockt.util.Direction.*
import aockt.util.Point2D
import aockt.util.move
import aockt.util.opposite
import aockt.util.parse
import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2023D10 : Solution {

    /**
     * A pipe segment.
     * @property location The coordinates where this pipe segment is placed.
     * @property flow     The directions in which this pipe redirects fluids.
     */
    private data class PipeSegment(val location: Point2D, val flow: Pair<Direction, Direction>) {

        init {
            require(flow.first != flow.second) { "A pipe cannot have the same output direction as the input." }
        }

        /**
         * Returns which direction would the contents of the pipe go depending on the [incoming] direction.
         * If the pipe cannot accept input from that direction, returns `null` instead.
         */
        fun redirectFlow(incoming: Direction): Direction? = when (incoming) {
            flow.first -> flow.second
            flow.second -> flow.first
            else -> null
        }
    }

    /**
     * A map of the pipe system observed on the floating metal island.
     * @property nodes The pipe segments in the maze, indexed by their coordinates.
     * @param    start The starting location from which to find the loop.
     */
    private class PipeMaze(
        private val nodes: Map<Point2D, PipeSegment>,
        start: Point2D,
    ) {

        /** The loop contained in this pipe maze, obtained by following the pipes from the start location. */
        val loop: List<PipeSegment> = buildList {

            /** From a pipe segment and the current flow direction, find the next segment and its respective output. */
            fun Pair<PipeSegment, Direction>.flow(): Pair<PipeSegment, Direction> {
                val (node, flow) = this
                val nextNode = nodes[node.location.move(flow)]
                checkNotNull(nextNode) { "Invalid loop. Pipe diverted outside of bounds." }
                val nextFlow = nextNode.redirectFlow(flow.opposite)
                checkNotNull(nextFlow) { "Invalid loop. Pipe not connected to valid sink." }
                return nextNode to nextFlow
            }

            var pipe: Pair<PipeSegment, Direction> = nodes.getValue(start).let { it to it.flow.first }

            do {
                add(pipe.first)
                pipe = pipe.flow()
                if (size > nodes.size) error("Loop not detected.")
            } while (pipe.first.location != start)
        }

        /** How many points in the maze are fully contained inside the [loop]. Uses the shoelace formula. */
        val loopVolume: Int =
            loop
                .asSequence()
                .plus(loop.first())
                .map(PipeSegment::location)
                .windowed(2)
                .sumOf { (a, b) -> a.x * b.y - a.y * b.x }
                .absoluteValue
                .minus(loop.size)
                .div(2)
                .plus(1)
    }

    /** Parse the [input] and recreate the [PipeMaze]. */
    private fun parseInput(input: String): PipeMaze = parse {
        lateinit var startPoint: Point2D

        fun parseNode(x: Int, y: Int, value: Char) = PipeSegment(
            location = Point2D(x, y),
            flow = when (value) { // It is relevant that the Up direction is first, where applicable.
                '|' -> Up to Down
                '-' -> Left to Right
                'L' -> Up to Right
                'J' -> Up to Left
                '7' -> Down to Left
                'F' -> Down to Right
                else -> error("Unknown pipe type: $value.")
            },
        )

        val rows = input.lines()
        require(rows.all { it.length == rows.first().length }) { "Map not a perfect rectangle." }

        val pipes = rows
            .asReversed()
            .asSequence()
            .flatMapIndexed { y: Int, line: String -> line.mapIndexed { x, c -> Triple(x, y, c) } }
            .onEach { (x, y, c) -> if (c == 'S') startPoint = Point2D(x, y) }
            .filterNot { it.third in ".S" }
            .map { (x, y, c) -> parseNode(x, y, c) }
            .associateBy { it.location }

        val startPipe = listOf(Up, Down, Left, Right)
            .filter { direction -> pipes[startPoint.move(direction)]?.redirectFlow(direction.opposite) != null }
            .also { require(it.size == 2) { "Cannot determine starting pipe type." } }
            .let { (i, o) -> PipeSegment(startPoint, i to o) }

        PipeMaze(
            nodes = pipes.plus(startPoint to startPipe),
            start = startPoint,
        )
    }

    override fun partOne(input: String) = parseInput(input).loop.size / 2
    override fun partTwo(input: String) = parseInput(input).loopVolume
}
