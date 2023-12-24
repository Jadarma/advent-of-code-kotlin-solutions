package aockt.y2023

import aockt.util.math.distinctPairs
import aockt.util.parse
import aockt.util.spacial3d.Point3D
import io.github.jadarma.aockt.core.Solution

/**
 * _NOTES:_
 * - Part 1 solution adapted from [HyperNeutrino](https://www.youtube.com/watch?v=guOyA7Ijqgk)'s video.
 */
object Y2023D24 : Solution {

    private data class Point2DFP(val x: Double, val y: Double)
    private data class Point3DFP(val x: Double, val y: Double, val z: Double)

    private data class Hailstone(val location: Point3DFP, val velocity: Point3D) {

        val a: Double = velocity.y.toDouble()
        val b: Double = -velocity.x.toDouble()
        val c: Double = velocity.y * location.x - velocity.x * location.y

        infix fun isParallelWith(other: Hailstone): Boolean = a * other.b == other.a * b

        fun intersectionPointWith(other: Hailstone): Point2DFP? {
            if (this isParallelWith other) return null
            val x: Double = (c * other.b - other.c * b) / (a * other.b - other.a * b)
            val y: Double = (other.c * a - c * other.a) / (a * other.b - other.a * b)

            val intersectsInFuture = listOf(
                (x - location.x < 0) == (velocity.x < 0),
                (y - location.y < 0) == (velocity.y < 0),
                (x - other.location.x < 0) == (other.velocity.x < 0),
                (y - other.location.y < 0) == (other.velocity.y < 0),
            ).all { it }

            return Point2DFP(x, y).takeIf { intersectsInFuture }
        }
    }

    /** Parse the [input] and return the states of all hailstones at time zero. */
    private fun parseInput(input: String): List<Hailstone> = parse {
        val lineRegex = Regex("""^(-?\d+), (-?\d+), (-?\d+) @ (-?\d+), (-?\d+), (-?\d+)$""")
        input
            .lineSequence()
            .map { line -> lineRegex.matchEntire(line)!!.destructured }
            .map { (x, y, z, vx, vy, vz) ->
                Hailstone(
                    location = Point3DFP(x.toDouble(), y.toDouble(), z.toDouble()),
                    velocity = Point3D(vx.toLong(), vy.toLong(), vz.toLong()),
                )
            }
            .toList()
    }

    override fun partOne(input: String): Any {
        val hailstones = parseInput(input)
        val area = if(hailstones.size < 10) 7.0..27.0 else 200000000000000.0..400000000000000.0

        return hailstones.distinctPairs()
            .mapNotNull { (h1, h2) -> h1.intersectionPointWith(h2) }
            .count { it.x in area && it.y in area }
    }
}
