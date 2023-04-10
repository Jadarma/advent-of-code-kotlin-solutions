package aockt.y2021

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2021D19 : Solution {

    /** Represents a discrete point in 3D space. */
    private data class Point(val x: Int, val y: Int, val z: Int)

    private operator fun Point.minus(other: Point): Point = Point(x - other.x, y - other.y, z - other.z)
    private operator fun Point.plus(other: Point): Point = Point(x + other.x, y + other.y, z + other.z)
    private infix fun Point.manhattanDistanceTo(other: Point): Int =
        (other - this).run { listOf(x.absoluteValue, y.absoluteValue, z.absoluteValue).sum() }

    /** Represents a rotation relative to the local origin in 90 degree increments. */
    private class Orientation private constructor(val upVector: Point, val rotation: Int) {

        fun remap(point: Point): Point {
            val reoriented = reorient.getValue(upVector).invoke(point)
            return rotate.getValue(rotation).invoke(reoriented)
        }

        companion object {
            private val reorient: Map<Point, Point.() -> Point> = mapOf(
                Point(0, 1, 0) to { this },
                Point(0, -1, 0) to { Point(x, -y, -z) },
                Point(1, 0, 0) to { Point(y, x, -z) },
                Point(-1, 0, 0) to { Point(y, -x, z) },
                Point(0, 0, 1) to { Point(y, z, x) },
                Point(0, 0, -1) to { Point(y, -z, -x) },
            )
            private val rotate: Map<Int, Point.() -> Point> = mapOf(
                0 to { this },
                1 to { Point(z, y, -x) },
                2 to { Point(-x, y, -z) },
                3 to { Point(-z, y, x) },
            )

            /** Precompiled list of all the valid orientations. */
            val all: List<Orientation> = buildList {
                for (upVector in reorient.keys) {
                    for (rotation in rotate.keys) {
                        add(Orientation(upVector, rotation))
                    }
                }
            }
        }
    }

    /** Holds readings of a beacon scanner. */
    private data class Scanner(val id: Int, val beaconDeltas: List<Point>) : List<Point> by beaconDeltas {
        fun withOrientation(orientation: Orientation): Scanner =
            copy(beaconDeltas = beaconDeltas.map { orientation.remap(it) })
    }

    /**
     * Tries to match a [target] scanner with a [reference] one. If successful, returns the transformed [target] such
     * that the two scanners have the same orientation, and the delta between them.
     */
    private fun calibrateScannersOrNull(reference: Scanner, target: Scanner): Pair<Scanner, Point>? {
        if (reference.size < 12 || target.size < 12) return null
        Orientation.all.forEach { orientation ->
            val reorientedScanner = target.withOrientation(orientation)
            reference.forEach { p1 ->
                reorientedScanner.forEach { p2 ->
                    val delta = p2 - p1
                    val transformed = reorientedScanner.map { it - delta }.toSet()
                    if (reference.intersect(transformed).size >= 12) return reorientedScanner to delta
                }
            }
        }
        return null
    }

    /**
     * Given a set of [scanners], tries to calibrate them, remapping them to the same [Orientation] and returning a map
     * of their positions relative to the first. Any scanners that do not share at least 12 beacons with the rest are
     * ignored.
     */
    private fun calibrateScanners(scanners: Set<Scanner>): Map<Scanner, Point> {
        if (scanners.isEmpty()) return emptyMap()
        return buildMap {
            val pendingScanners = scanners.toMutableList()
            put(pendingScanners.removeFirst(), Point(0, 0, 0))
            while (pendingScanners.isNotEmpty()) {
                var adjustedScannerId = -1
                for (scanner in pendingScanners.shuffled()) {
                    for (reference in keys.shuffled()) {
                        val (adjustedScanner, deltaToRef) = calibrateScannersOrNull(reference, scanner) ?: continue
                        put(adjustedScanner, getValue(reference) - deltaToRef)
                        adjustedScannerId = scanner.id
                        break
                    }
                    if (adjustedScannerId >= 0) break
                }
                if (adjustedScannerId == -1) return@buildMap
                pendingScanners.removeIf { it.id == adjustedScannerId }
            }
        }
    }

    /** Parse the [input] and return the [Scanner] readings. */
    private fun parseInput(input: String): Set<Scanner> = buildSet {
        val points = mutableListOf<Point>()
        var id = -1
        input
            .lineSequence()
            .map { it.removeSurrounding("--- ", " ---") }
            .forEach { line ->
                when {
                    line.startsWith("scanner") -> id = line.substringAfter(' ').toInt()
                    line.isEmpty() -> add(Scanner(id, points.toList())).also { points.clear() }
                    else -> line.split(",").map { it.toInt() }.let { (x, y, z) -> points.add(Point(x, y, z)) }
                }
            }
        add(Scanner(id, points.toList()))
    }

    override fun partOne(input: String) =
        parseInput(input)
            .let(this::calibrateScanners)
            .flatMap { (scanner, delta) -> scanner.map { it + delta } }
            .toSet()
            .count()

    override fun partTwo(input: String) =
        parseInput(input)
            .let(this::calibrateScanners)
            .run {
                keys
                    .flatMap { s1 -> (keys - s1).map { s2 -> getValue(s1) to getValue(s2 as Scanner) } }
                    .distinct()
                    .maxOf { (p1, p2) -> p1 manhattanDistanceTo p2 }
            }
}
