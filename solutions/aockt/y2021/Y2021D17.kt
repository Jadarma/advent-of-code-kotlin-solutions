package aockt.y2021

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue
import kotlin.math.sign

object Y2021D17 : Solution {

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: Int, val y: Int)

    /** Generates the sequence of all the triangular numbers that fit in an [Int] */
    private fun triangularNumbers(): Sequence<Int> = (0 until 46341).asSequence().map { n -> n * (n + 1) / 2 }

    /** Simulates launching a projectile from the origin of (0,0) with a [launchVelocity] vector. */
    private fun simulateTrajectory(launchVelocity: Point): Sequence<Point> = sequence {
        var (dx, dy) = launchVelocity
        var point = Point(0, 0).also { yield(it) }
        while (true) {
            point = Point(point.x + dx, point.y + dy).also { yield(it) }
            dx += -1 * dx.sign
            dy -= 1
        }
    }

    /**
     * Calculates all the possible launch velocities such that, if shot from the origin, a projectile would pass through
     * the [target]. For every possibility, returns the initial velocity and the path the projectile would take.
     */
    private fun generateTrajectories(target: Pair<Point, Point>): Sequence<Pair<Point, List<Point>>> = sequence {
        val minimumXMagnitude = triangularNumbers().indexOfFirst { it >= target.first.x }
        val maximumYMagnitude = target.second.y.absoluteValue - 1

        val xRange = minimumXMagnitude..target.second.x
        val yRange = target.second.y..maximumYMagnitude
        val sanityIterationLimit = xRange.last + yRange.last

        fun Point.isInBounds() = x in target.first.x..target.second.x && y in target.second.y..target.first.y

        for (x in xRange) {
            for (y in yRange) {
                val velocity = Point(x, y)
                val path = simulateTrajectory(velocity)
                    .take(sanityIterationLimit)
                    .takeWhile { point -> point.x <= target.second.x && point.y >= target.second.y }
                    .toList()
                if (path.last().isInBounds()) yield(velocity to path)
            }
        }
    }

    /** Parse the input and return the target area for the probe to visit. */
    private fun parseInput(input: String): Pair<Point, Point> = runCatching {
        val regex = Regex("""^target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)$""")
        val (xl, xh, yl, yh) = regex.matchEntire(input)!!.groupValues.drop(1).map { it.toInt() }
        require(xl < xh && yl < yh)
        require(xl >= 0) { "We only assume positive values of X. Transform your cartesian and try again." }
        require(yh <= 0) { "We only assume negative values of Y. Is your trench above sea level?" }
        return Point(xl, yh) to Point(xh, yl)
    }.getOrElse { throw IllegalArgumentException("Invalid input", it) }

    override fun partOne(input: String) =
        parseInput(input).let {
            val maxYValue = it.second.y.absoluteValue - 1
            triangularNumbers().drop(maxYValue).first()
        }

    override fun partTwo(input: String) =
        parseInput(input)
            .let(this::generateTrajectories)
            .count()
}
