package aockt.y2023

import aockt.util.parse
import aockt.util.spacial.Area
import aockt.util.spacial.overlaps
import aockt.util.spacial3d.Point3D
import io.github.jadarma.aockt.core.Solution

object Y2023D22 : Solution {

    /** A line of sand cubes, defined by the [start] and [end] coordinates. */
    private data class SandBrick(val start: Point3D, val end: Point3D) {

        init {
            require(start.z <= end.z) { "Start and end given out of order." }
            require(start.z >= 1) { "Sand brick collides with ground." }
            val isHorizontal = start.z == end.z && (start.x == end.x || start.y == end.y)
            val isVertical = start.x == end.x && start.y == end.y
            require(isHorizontal || isVertical) { "The sand brick must be a straight line." }
        }

        /** The area this line occupies on a flat plane, which can cause collisions when falling. */
        val fallingArea: Area = Area(
            xRange = minOf(start.x, end.x)..maxOf(start.x, end.x),
            yRange = minOf(start.y, end.y)..maxOf(start.y, end.y),
        )

        /** Returns the state of the sand brick if it were to fall until the [start] rests at the given [restHeight]. */
        fun fallTo(restHeight: Long): SandBrick = SandBrick(
            start = start.copy(z = restHeight),
            end = end.copy(z = end.z - start.z + restHeight),
        )

        /** Checks if this and the [other] brick will stack on each other after falling to the ground. */
        fun fallingAreaOverlaps(other: SandBrick): Boolean = fallingArea overlaps other.fallingArea
    }

    /** A physics simulator for magical sand brick. */
    private class SandBrickSimulator(bricks: Iterable<SandBrick>) {

        /** The resting position of all bricks. */
        val settledBricks: List<SandBrick> =
            bricks
                .toMutableList()
                .apply {
                    sortBy { it.start.z }
                    forEachIndexed { index, brick ->
                        this[index] = slice(0..<index)
                            .filter { brick.fallingAreaOverlaps(it) }
                            .maxOfOrNull { it.end.z + 1 }
                            .let { restHeight -> brick.fallTo(restHeight ?: 1L) }
                    }
                }
                .sortedBy { it.start.z }

        /** A mapping from a brick to all bricks that it rests directly on top of. */
        val supportedBy: Map<SandBrick, Set<SandBrick>>

        /** Syntactical sugar for getting all bricks resting directly on top of this one. */
        private val SandBrick.supportedBricks: Set<SandBrick> get() = supportedBy.getValue(this)

        /** A mapping from a brick to all bricks that rest directly on top of it. */
        val supporting: Map<SandBrick, Set<SandBrick>>

        /** Syntactical sugar for getting all bricks that this brick rests directly on top of. */
        private val SandBrick.standingOn: Set<SandBrick> get() = supporting.getValue(this)

        init {
            val supportedBy: Map<SandBrick, MutableSet<SandBrick>> = settledBricks.associateWith { mutableSetOf() }
            val supporting: Map<SandBrick, MutableSet<SandBrick>> = settledBricks.associateWith { mutableSetOf() }

            settledBricks.forEachIndexed { index, above ->
                settledBricks.slice(0..<index).forEach { below ->
                    if (below.fallingAreaOverlaps(above) && above.start.z == below.end.z + 1) {
                        supportedBy.getValue(below).add(above)
                        supporting.getValue(above).add(below)
                    }
                }
            }

            this.supportedBy = supportedBy
            this.supporting = supporting
        }

        /**
         * The bricks that do not contribute to the structural integrity of the sand formation.
         * They can be disintegrated without causing other bricks to fall.
         */
        val redundantBricks: Set<SandBrick> =
            settledBricks
                .filter { it.supportedBricks.all { supported -> supported.standingOn.count() >= 2 } }
                .toSet()

        /** Simulate disintegrating the [brick] and return all bricks which would fall as a result. */
        fun fallingBricksIfDisintegrating(brick: SandBrick): Set<SandBrick> = buildSet {
            require(brick in supporting) { "The brick $brick is not part of the simulation." }
            val fallingBricks = this

            brick.supportedBricks
                .filter { supported -> supported.standingOn.size == 1 }
                .let(fallingBricks::addAll)

            val queue = ArrayDeque(elements = fallingBricks)

            while (queue.isNotEmpty()) {
                queue.removeFirst()
                    .supportedBricks
                    .minus(fallingBricks)
                    .filter { supportedByFalling -> fallingBricks.containsAll(supportedByFalling.standingOn) }
                    .onEach(fallingBricks::add)
                    .forEach(queue::add)
            }
        }
    }

    /** Parses the [input] and returns the list of [SandBrick]s. */
    private fun parseInput(input: String): List<SandBrick> = parse {
        input
            .lineSequence()
            .map { line -> line.replace('~', ',') }
            .map { line -> line.split(',', limit = 6).map(String::toInt) }
            .onEach { require(it.size == 6) }
            .map {
                SandBrick(
                    start = it.take(3).let { (x, y, z) -> Point3D(x, y, z) },
                    end = it.takeLast(3).let { (x, y, z) -> Point3D(x, y, z) },
                )
            }
            .toList()
    }

    override fun partOne(input: String) = parseInput(input).let(::SandBrickSimulator).redundantBricks.count()
    override fun partTwo(input: String) = parseInput(input).let(::SandBrickSimulator).run {
        settledBricks
            .map(::fallingBricksIfDisintegrating)
            .sumOf { it.count() }
    }
}
