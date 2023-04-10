package aockt.y2021

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2021D22 : Solution {

    /** Represents a cuboid in 3D space, arranged orthogonally with respect to the axes. */
    private data class Cuboid(val xRange: LongRange, val yRange: LongRange, val zRange: LongRange) {
        init {
            require(listOf(xRange, yRange, zRange).none { it.isEmpty() }) { "Empty range, this step does nothing." }
        }

        companion object {
            /** Ensures the ranger are valid and returns a [Cuboid] bound by them, or `null` otherwise. */
            fun of(xRange: LongRange, yRange: LongRange, zRange: LongRange): Cuboid? {
                if (xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()) return null
                return Cuboid(xRange, yRange, zRange)
            }
        }
    }

    /** Computes the volume of a [Cuboid]. */
    private val Cuboid.volume: Long
        get() = listOf(xRange, yRange, zRange).map { it.last + 1 - it.first }.reduce(Long::times).absoluteValue

    /** Return the range of values for the given [axis]: `X`, `Y`, or `Z`. */
    private operator fun Cuboid.get(axis: Char): LongRange = when (axis) {
        'X' -> xRange
        'Y' -> yRange
        'Z' -> zRange
        else -> error("Invalid axis.")
    }

    /**
     * Split a [Cuboid] into a maximum of three parts, by splitting an [axis] at a given [range], which must not be
     * necessarily contained within this cuboid range.
     * The first value is whatever is _left_ of the range _(exclusive)_.
     * The second value is whatever is _within_ the range _(inclusive)_.
     * The third value is whatever is _right_ of the range _(exclusive)_.
     */
    private fun Cuboid.split(axis: Char, range: LongRange): Triple<Cuboid?, Cuboid?, Cuboid?> {
        val axisRange = this[axis]
        val l = axisRange.first until range.first
        val m = maxOf(axisRange.first, range.first)..minOf(axisRange.last, range.last)
        val r = (range.last + 1)..axisRange.last

        return when (axis) {
            'X' -> Triple(Cuboid.of(l, yRange, zRange), Cuboid.of(m, yRange, zRange), Cuboid.of(r, yRange, zRange))
            'Y' -> Triple(Cuboid.of(xRange, l, zRange), Cuboid.of(xRange, m, zRange), Cuboid.of(xRange, r, zRange))
            'Z' -> Triple(Cuboid.of(xRange, yRange, l), Cuboid.of(xRange, yRange, m), Cuboid.of(xRange, yRange, r))
            else -> error("Invalid axis.")
        }
    }

    /** Returns the set of [Cuboid]s that sum up to the result of removing intersecting regions with the [other] one. */
    private operator fun Cuboid.minus(other: Cuboid): Set<Cuboid> = when {
        this intersect other == null -> setOf(this)
        else -> buildSet {
            "XYZ".fold(this@minus) { remaining, axis ->
                val (left, middle, right) = remaining.split(axis, other[axis])
                if (left != null) add(left)
                if (right != null) add(right)
                middle ?: return@buildSet
            }
        }
    }

    /** Returns the [Cuboid] that is defined by the intersection of this and the [other], or `null` if they do not. */
    private infix fun Cuboid.intersect(other: Cuboid): Cuboid? = Cuboid.of(
        xRange = maxOf(xRange.first, other.xRange.first)..minOf(xRange.last, other.xRange.last),
        yRange = maxOf(yRange.first, other.yRange.first)..minOf(yRange.last, other.yRange.last),
        zRange = maxOf(zRange.first, other.zRange.first)..minOf(zRange.last, other.zRange.last),
    )

    /** A submarine core boot-up sequence. */
    private data class Step(val turnOn: Boolean, val region: Cuboid) {
        /** Returns whether this step executes entirely within the initialization region. */
        val isInsideInitializationRegion: Boolean
            get() = with(region) {
                xRange.first >= -50 && xRange.last <= 50
                    && yRange.first >= -50 && yRange.last <= 50
                    && zRange.first >= -50 && zRange.last <= 50
            }
    }

    /**
     * Execute the submarine core boot up sequence following the [steps] and returns all [Cuboid] regions where the
     * reactor cubes are turned on.
     */
    private fun coreBootUpSequence(steps: Iterable<Step>): Set<Cuboid> = steps.fold(emptySet()) { cuboids, step ->
        val (turnOn, region) = step
        buildSet {
            cuboids.forEach { cuboid -> addAll(cuboid - region) }
            if (turnOn) add(region)
        }
    }

    /** Parse the [input] and return the sequence of boot-up [Step]s for the submarine's core. */
    private fun parseInput(input: String): Sequence<Step> {
        val stepRegex = Regex("""^(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)$""")
        return input
            .lineSequence()
            .map { stepRegex.matchEntire(it)?.destructured ?: throw IllegalArgumentException("Invalid input.") }
            .map { (turn, xl, xh, yl, yh, zl, zh) ->
                Step(
                    turnOn = turn == "on",
                    region = Cuboid(xl.toLong()..xh.toLong(), yl.toLong()..yh.toLong(), zl.toLong()..zh.toLong())
                )
            }
    }

    override fun partOne(input: String) = parseInput(input)
        .filter { it.isInsideInitializationRegion }
        .let { coreBootUpSequence(it.asIterable()) }
        .sumOf { it.volume }

    override fun partTwo(input: String) = coreBootUpSequence(parseInput(input).asIterable()).sumOf { it.volume }
}
