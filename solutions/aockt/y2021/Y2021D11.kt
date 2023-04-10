package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D11 : Solution {

    /** Represents a 10x10 2D map of octopus energy levels, as scanned by the submarine. */
    private class OctopusMap(initial: List<Int>) {

        /** Represents a discrete point in 2D space. */
        private data class Point(val x: Int, val y: Int)

        /** The state of an octopus, keeps track of location, energy level, and whether it flashed this turn. */
        private data class Octopus(val coordinates: Point, var energy: Int = 0, var flashed: Boolean = false) {
            /** Energize this octopus and return whether it flashed as a result of gaining new energy. */
            fun energize(): Boolean {
                if (flashed) return false
                energy = if (energy < 9) energy + 1 else 0
                flashed = energy == 0
                return flashed
            }
        }

        private val data: Array<Octopus>

        init {
            require(initial.size == 100) { "Not enough data points." }
            data = Array(100) { Octopus(coordinates = Point(it / 10, it % 10), energy = initial[it]) }
        }

        /** Returns all the [Point]s that are orthogonally or diagonally adjacent to this one. */
        private fun Point.adjacent(): Set<Point> = buildSet {
            for (i in (x - 1)..(x + 1)) {
                if (i !in 0..9) continue
                for (j in (y - 1)..(y + 1)) {
                    if (j !in 0..9) continue
                    add(Point(i, j))
                }
            }
            remove(this@adjacent)
        }

        /** Simulate the next step of the energy cycle and return all points at which an octopus flashed. */
        fun simulateStep(): Set<Point> = buildSet {
            data.forEach {
                it.flashed = false
                it.energize()
                if (it.flashed) add(it.coordinates)
            }

            val flashQueue = ArrayDeque(this)
            while (flashQueue.isNotEmpty()) {
                flashQueue.removeFirst().adjacent().forEach { point ->
                    data[point.x * 10 + point.y].takeIf { it.energize() }?.coordinates?.let {
                        add(it)
                        flashQueue.addLast(it)
                    }
                }
            }
        }
    }

    /** Parse the [input] and return the [OctopusMap] as scanned by the submarine. */
    private fun parseInput(input: String): OctopusMap =
        input
            .lines().joinToString("")
            .map { it.digitToInt() }
            .let(::OctopusMap)

    override fun partOne(input: String) =
        parseInput(input).run {
            (1..100).fold(0) { acc, _ -> acc + simulateStep().size }
        }

    override fun partTwo(input: String) =
        parseInput(input).run {
            var step = 1
            while (simulateStep().size != 100) step++
            step
        }
}
