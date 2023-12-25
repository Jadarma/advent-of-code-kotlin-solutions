package aockt.y2023

import aockt.util.math.distinctPairs
import aockt.util.parse
import aockt.util.spacial3d.Point3D
import io.github.jadarma.aockt.core.Solution

/**
 * This complicated day was only possible due to amazing insights and other solutions shared by the community:
 * - Collision formulae for part 1: [HyperNeutrino](https://www.youtube.com/watch?v=guOyA7Ijqgk)
 * - Reducing velocity search space: [u/abnew123](https://old.reddit.com/r/adventofcode/comments/18pptor/2023_day_24_part_2java_is_there_a_trick_for_this/kepxbew/)
 * - Use rock's reference frame: [u/xiaowuc1](https://old.reddit.com/r/adventofcode/comments/18pptor/2023_day_24_part_2java_is_there_a_trick_for_this/keps780/)
 * - Validating velocities by brute force: [u/Smooth-Aide-1751](https://old.reddit.com/r/adventofcode/comments/18pnycy/2023_day_24_solutions/ker8l05/)
 */
object Y2023D24 : Solution {

    /** Snapshot in time of the location and velocity of a hailstone. */
    private data class HailStone(val x: Long, val y: Long, val z: Long, val vx: Long, val vy: Long, val vz: Long) {
        constructor(location: Point3D, velocity: Point3D) : this(
            location.x, location.y, location.z,
            velocity.x, velocity.y, velocity.z
        )
    }

    /** Determine where the hailstones [h1] and [h2] will collide _(if ignoring the Z axis)_, if at all. */
    private fun intersectionInXYPlane(h1: HailStone, h2: HailStone): Pair<Double, Double>? {
        val c1: Double = (h1.vy * h1.x - h1.vx * h1.y).toDouble()
        val c2: Double = (h2.vy * h2.x - h2.vx * h2.y).toDouble()

        val slopeDiff = h1.vy * -h2.vx - h2.vy * -h1.vx
        if (slopeDiff == 0L) return null

        val x = (c1 * -h2.vx - c2 * -h1.vx) / slopeDiff
        val y = (c2 * h1.vy - c1 * h2.vy) / slopeDiff

        val intersectsInFuture = listOf(
            (x - h1.x < 0) == (h1.vx < 0),
            (y - h1.y < 0) == (h1.vy < 0),
            (x - h2.x < 0) == (h2.vx < 0),
            (y - h2.y < 0) == (h2.vy < 0),
        ).all { it }

        return (x to y).takeIf { intersectsInFuture }
    }

    /** Returns the position of this hailstone after it moved with its velocity for a given amount of [time]. */
    private fun HailStone.positionAfterTime(time: Double): Triple<Double, Double, Double> = Triple(
        first = x + vx * time,
        second = y + vy * time,
        third = z + vz * time,
    )

    /**
     * Checks if the two hailstones will collide together in the future.
     * Returns false if the stones are parallel, the collision would have occurred in the past, or it isn't possible
     */
    private fun HailStone.willCollideWith(other: HailStone): Boolean {
        val t = when {
            vx != other.vx -> (other.x - x).toDouble() / (vx - other.vx)
            vy != other.vy -> (other.y - y).toDouble() / (vy - other.vy)
            vz != other.vz -> (other.z - z).toDouble() / (vz - other.vz)
            else -> return false
        }
        if (t < 0) return false
        return positionAfterTime(t) == other.positionAfterTime(t)
    }


    /**
     * Determines the search space for throwing rocks at hailstones by assuming a solution in a given range and then
     * pruning away obviously invalid values.
     *
     * Take any two hailstones: `h1` and `h2`, such that `h2.x > h1.x` and `h2.vx > h1.vx`.
     * Now suppose the rock has a thrown velocity of h2.vx >= r.vx >= h1.vx.
     * A thrown rock must hit both `h1` and `h2` eventually.
     * But once it hits `h1`, we know that `r.x < h2.x`, and also that `r.vx < h2.vx`, therefore it will be impossible
     * for the rock to _"catch up"_ to the second hailstone.
     * As such, we can rule out any rock velocity within the `h1.vx..h2.vx` range.
     * The same logic holds for the Y and Z axes.
     * We can use these impossible ranges to exclude velocities which would miss at least one rock.
     *
     * @param amplitude The throwing velocity range to consider brute-forcing.
     * @return All possible velocity vectors that are not guaranteed to miss.
     */
    private fun List<HailStone>.possibleRockVelocities(amplitude: Int): Sequence<Point3D> = sequence {
        require(amplitude > 0) { "Rock throwing amplitude must be positive." }
        val hailstones = this@possibleRockVelocities
        val velocityRange = -amplitude.toLong()..amplitude.toLong()
        val invalidXRanges = mutableSetOf<LongRange>()
        val invalidYRanges = mutableSetOf<LongRange>()
        val invalidZRanges = mutableSetOf<LongRange>()

        fun MutableSet<LongRange>.testImpossible(p0: Long, v0: Long, p1: Long, v1: Long) {
            if (p0 > p1 && v0 > v1) add(v1..v0)
            if (p1 > p0 && v1 > v0) add(v0..v1)
        }

        for ((h1, h2) in hailstones.distinctPairs()) {
            invalidXRanges.testImpossible(h1.x, h1.vx, h2.x, h2.vx)
            invalidYRanges.testImpossible(h1.y, h1.vy, h2.y, h2.vy)
            invalidZRanges.testImpossible(h1.z, h1.vz, h2.z, h2.vz)
        }

        val possibleX = velocityRange.filter { x -> invalidXRanges.none { x in it } }
        val possibleY = velocityRange.filter { y -> invalidYRanges.none { y in it } }
        val possibleZ = velocityRange.filter { z -> invalidZRanges.none { z in it } }

        for (vx in possibleX) {
            for (vy in possibleY) {
                for (vz in possibleZ) {
                    yield(Point3D(vx, vy, vz))
                }
            }
        }
    }

    /**
     * Given two hailstones [h1] and [h2], together with the assumed rock [velocity], calculate the point from which to
     * throw the rock to collide with both hailstones.
     * Returns null if no such throw is possible.
     */
    private fun deduceThrowingLocation(h1: HailStone, h2: HailStone, velocity: Point3D): Point3D? {
        // Horrible naming scheme, read as: hailstone relative velocity; translated to rock's inertial frame.
        val h1rvx = h1.vx - velocity.x
        val h1rvy = h1.vy - velocity.y
        val h2rvx = h2.vx - velocity.x
        val h2rvy = h2.vy - velocity.y

        val slopeDiff = h1rvx * h2rvy - h1rvy * h2rvx
        if (slopeDiff == 0L) return null

        val t: Long = (h2rvy * (h2.x - h1.x) - h2rvx * (h2.y - h1.y)) / slopeDiff
        if (t < 0) return null

        return Point3D(
            x = h1.x + (h1.vx - velocity.x) * t,
            y = h1.y + (h1.vy - velocity.y) * t,
            z = h1.z + (h1.vz - velocity.z) * t,
        )
    }

    /** Parse the [input] and return the states of all hailstones at time zero. */
    private fun parseInput(input: String): List<HailStone> = parse {
        val lineRegex = Regex("""^(-?\d+), (-?\d+), (-?\d+) @ (-?\d+), (-?\d+), (-?\d+)$""")
        input
            .lineSequence()
            .map { line -> lineRegex.matchEntire(line)!!.destructured }
            .map { (x, y, z, vx, vy, vz) ->
                HailStone(
                    x = x.toLong(), y = y.toLong(), z = z.toLong(),
                    vx = vx.toLong(), vy = vy.toLong(), vz = vz.toLong(),
                )
            }
            .toList()
    }

    override fun partOne(input: String): Any {
        val hailStones = parseInput(input)
        val area = if (hailStones.size < 10) 7.0..27.0 else 200000000000000.0..400000000000000.0

        return hailStones.distinctPairs()
            .mapNotNull { (h1, h2) -> intersectionInXYPlane(h1, h2) }
            .count { (x, y) -> x in area && y in area }
    }

    override fun partTwo(input: String): Any {
        val hailStones = parseInput(input)
        val (h1, h2) = hailStones
        val amplitude = if (hailStones.size < 10) 5 else 250

        return hailStones
            .possibleRockVelocities(amplitude = amplitude)
            .mapNotNull { velocity -> deduceThrowingLocation(h1, h2, velocity)?.let { HailStone(it, velocity) } }
            .first { rock -> hailStones.all { rock.willCollideWith(it) } }
            .let { it.x + it.y + it.z }
    }
}
