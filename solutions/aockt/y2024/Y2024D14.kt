package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Area
import aockt.util.spacial.Direction
import aockt.util.spacial.Point
import aockt.util.spacial.move
import io.github.jadarma.aockt.core.Solution

object Y2024D14 : Solution {

    /** Info about the [position] and [velocity] of a restroom security robot. */
    private data class Robot(val position: Point, val velocity: Point) {

        /** Compute the robot state after moving for one second. */
        fun move(): Robot = copy(position = Point(position.x + velocity.x, position.y + velocity.y))

        /** Teleports the Robot back inside the bounds of an [area]. */
        fun teleport(area: Area): Robot {
            val x = when (position.x) {
                in area.xRange -> position.x
                in Int.MIN_VALUE..<area.xRange.first -> position.x + area.width
                else -> position.x - area.width
            }
            val y = when (position.y) {
                in area.yRange -> position.y
                in Int.MIN_VALUE..<area.yRange.first -> position.y + area.height
                else -> position.y - area.height
            }
            return copy(position = Point(x, y))
        }
    }

    /** Simulate robot movements in one second increments. First element is the [initial] state. */
    private fun simulate(initial: List<Robot>): Sequence<List<Robot>> = sequence {
        val totalRobots = initial.size
        val area = if (totalRobots < 100) Area(11, 7) else Area(101, 103)

        val queue = ArrayDeque(initial)

        while (true) {
            yield(queue)
            repeat(totalRobots) {
                queue
                    .removeFirst()
                    .move()
                    .teleport(area)
                    .let(queue::addLast)
            }
        }
    }

    /**
     * **Bonus Tree Finder Heuristic!** - Slower but more generally correct.
     * The Easter egg occurs when _"most of the robots"_ arrange themselves to an image.
     * We use the same technique as in [Y2024D12] to calculate size of contiguous groups based on robot locations.
     * The tree image consists of a tree, and a border, that do not touch each-other.
     * Therefore, we have found it when the largest two distinct groups' size exceed half the total robots.
     */
    @Suppress("UNUSED")
    private fun checkForTree(robots: List<Robot>): Boolean {
        if (robots.isEmpty()) return false

        val byLocation = robots
            .groupBy { it.position }
            .mapValues { it.value.isNotEmpty() }

        val groups = mutableListOf<Int>()
        val seen = mutableSetOf<Point>()

        for (robot in robots) {
            var count = 0
            val queue = ArrayDeque<Point>().apply { add(robot.position) }
            while (queue.isNotEmpty()) {
                val current = queue
                    .removeFirst()
                    .takeUnless { it in seen }
                    ?.also(seen::add)
                    ?.also { count++ }
                    ?: continue

                Direction.all
                    .asSequence()
                    .map(current::move)
                    .filter { it in byLocation }
                    .forEach(queue::add)

                groups.add(count)
            }
        }

        return groups.sortedDescending().take(2).sum() >= robots.size / 2
    }

    /** Parse the [input] and return the list of [Robot]s. */
    private fun parseInput(input: String): List<Robot> = parse {
        val regex = Regex("""^p=(\d+),(\d+) v=(-?\d+),(-?\d+)$""")
        input
            .lineSequence()
            .map(regex::matchEntire)
            .map { it!!.destructured }
            .map { (px, py, vx, vy) ->
                Robot(
                    position = Point(px.toInt(), py.toInt()),
                    velocity = Point(vx.toInt(), vy.toInt()),
                )
            }
            .toList()
    }

    override fun partOne(input: String): Int {
        val robots = parseInput(input)

        val area = if (robots.size < 100) Area(11, 7) else Area(101, 103)
        val halfX = area.width / 2
        val halfY = area.height / 2

        val quads = setOf(
            Area(xRange = 0..<halfX, yRange = 0..<halfY),
            Area(xRange = halfX + 1..<area.width, yRange = 0..<halfY),
            Area(xRange = halfX + 1..<area.width, yRange = halfY + 1..<area.height),
            Area(xRange = 0..<halfX, yRange = halfY + 1..<area.height),
        )

        return simulate(robots)
            .drop(100).first()
            .groupingBy { robot -> quads.firstOrNull { robot.position in it } }
            .eachCount()
            .filterKeys { it != null }
            .values.reduce(Int::times)
    }

    override fun partTwo(input: String): Int =
        parseInput(input)
            .let(::simulate)
            .take(100_000) // Sanity stop condition to prevent endless loop.
            //.indexOfFirst(::checkForTree) // <- More thorough alternative for detecting.
            .indexOfFirst { it.distinctBy(Robot::position).size == it.size }
}
