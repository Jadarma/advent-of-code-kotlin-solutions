package aockt.y2025

import aockt.util.parse
import aockt.util.spacial3d.Point3D
import aockt.util.spacial3d.distanceTo
import io.github.jadarma.aockt.core.Solution

object Y2025D08 : Solution {

    /**
     * A junction box that can connect decoration wires with other junction boxes.
     * @property id       A number to identify this junction with.
     * @property position The location of the box in 3D space.
     */
    private data class JunctionBox(val id: Int, val position: Point3D) {
        infix fun distanceTo(other: JunctionBox): Long = position.distanceTo(other.position)
    }

    /**
     * Keeps track of multiple [JunctionBox]es and attempts to intelligently connect them together in circuits.
     * @param boxPositions The coordinates at which the junction boxes are located.
     */
    private class CircuitManager(boxPositions: Iterable<Point3D>) {

        /** Lookup table for junction boxes. */
        val boxes: List<JunctionBox> = boxPositions.mapIndexed(::JunctionBox)

        /** Lookup table from junction box IDs to the currently assigned circuit group. */
        private val junctionToCircuits: MutableMap<Int, Int> = mutableMapOf()

        /** Groups together junction IDs in interconnected circuit groups. */
        private val circuits: List<MutableSet<Int>> = List(boxes.size) { index ->
            junctionToCircuits[index] = index
            mutableSetOf(index)
        }

        /** Return the circuit groups of connected [JunctionBox]es, from largest to smallest. */
        val circuitGroups: List<Set<JunctionBox>>
            get() =
                circuits.asSequence()
                    .filterNot { it.isEmpty() }
                    .sortedByDescending { it.size }
                    .map { ids -> ids.map(boxes::get).toSet() }
                    .toList()

        /**
         * Determines the next pair of junction boxes that can be connected.
         * To qualify, a pair is selected such that:
         * - The two boxes are closest together.
         * - The first box has a lower ID.
         * - The boxes are not already part of the same circuit group.
         */
        private val connectionCandidates: Iterator<Pair<JunctionBox, JunctionBox>> = iterator {
            while (true) {
                boxes.asSequence()
                    .flatMap { boxes.map { other -> it to other } }
                    .filterNot { it.first.id >= it.second.id }
                    .filterNot { junctionToCircuits[it.first.id] == junctionToCircuits[it.second.id] }
                    .sortedBy { (a, b) -> a distanceTo b }
                    .toList()
                    .ifEmpty { break }
                    .let { pairs -> yieldAll(pairs) }
            }
        }

        /** Connect the next closest junction boxes together and return them, or `null` if no boxes can be connected. */
        fun connectNextClosest(): Pair<JunctionBox, JunctionBox>? {
            if (!connectionCandidates.hasNext()) return null
            return connectionCandidates.next().also { (a, b) -> connect(a, b) }
        }

        /**
         * Connect two junction boxes [a] and [b] together with wire, and merge their circuit groups together.
         * By convention, the smaller circuit group contents moves to the larger one.
         * Does nothing if they are already part of the same group.
         */
        private fun connect(a: JunctionBox, b: JunctionBox) {
            check(a.id < b.id) { "Internal convention broken! Junction box ids should be ordered." }

            val (circuitToGrow, circuitToShrink) = run {
                val circuitOfA = junctionToCircuits.getValue(a.id)
                val circuitOfB = junctionToCircuits.getValue(b.id)

                if (circuitOfA == circuitOfB) return

                if (circuits[circuitOfA].size >= circuits[circuitOfB].size) circuitOfA to circuitOfB
                else circuitOfB to circuitOfA
            }

            circuits[circuitToGrow].addAll(circuits[circuitToShrink])
            circuits[circuitToShrink].forEach { box -> junctionToCircuits[box] = circuitToGrow }
            circuits[circuitToShrink].clear()
        }
    }

    /** Parse the [input] and return the [CircuitManager] that organizes all connections between them. */
    private fun parseInput(input: String): CircuitManager = parse {
        input
            .lineSequence()
            .map { it.split(',', limit = 3).map(String::toLong) }
            .map { (x, y, z) -> Point3D(x, y, z) }
            .asIterable()
            .let(::CircuitManager)
    }

    override fun partOne(input: String): Long = parseInput(input).run {
        repeat(times = if (boxes.size > 20) 1000 else 10) { connectNextClosest() }
        return circuitGroups.take(3).map { it.size.toLong() }.reduce(Long::times)
    }

    override fun partTwo(input: String): Long = parseInput(input).run {
        var lastConnected: Pair<JunctionBox, JunctionBox>? = null
        var iterations = 0
        while (iterations++ < 10_000 && circuitGroups.size > 1) { lastConnected = connectNextClosest() }
        return lastConnected?.run { first.position.x * second.position.x } ?: error("Could not connect circuits.")
    }
}
