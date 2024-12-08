package aockt.y2024

import aockt.util.math.distinctPairs
import aockt.util.parse
import aockt.util.spacial.*
import io.github.jadarma.aockt.core.Solution

object Y2024D08 : Solution {

    /**
     * The map of antennas on the Easter Bunny roof.
     * @property area The bounds to check nodes in.
     * @property antennas All the antenna positions, group by the frequencies they emit.
     */
    private class AntennaMap(val area: Area, val antennas: Map<Char, Set<Point>>) {

        /**
         * Return all the unique points within the [area] that form at least one frequency anti-node.
         * If [repeating] is enabled, an anti-node will spawn at every collinear interval between two matching
         * antennas _(including themselves)_.
         */
        fun antiNodes(repeating: Boolean = false): Set<Point> = buildSet {
            antennas.values
                .asSequence()
                .flatMap(Set<Point>::distinctPairs)
                .forEach { (a, b) ->
                    val dx = a.x - b.x
                    val dy = a.y - b.y
                    var step = if (repeating) 0 else 1
                    while (repeating || step == 1) {
                        val p1 = Point(x = a.x + dx * step, y = a.y + dy * step).takeIf { it in area }?.let(::add)
                        val p2 = Point(x = b.x - dx * step, y = b.y - dy * step).takeIf { it in area }?.let(::add)
                        if (p1 == null && p2 == null) break
                        step++
                    }
                }
        }
    }

    /** Parse the [input] and return the [AntennaMap]. */
    private fun parseInput(input: String): AntennaMap = parse {
        with(Grid(input)) {
            AntennaMap(
                area = Area(width, height),
                antennas = points()
                    .filter { it.value != '.' }
                    .groupBy({ it.value }, { it.position })
                    .mapValues { (_, points) -> points.toSet() },
            )
        }
    }

    override fun partOne(input: String): Int = parseInput(input).antiNodes(repeating = false).count()
    override fun partTwo(input: String): Int = parseInput(input).antiNodes(repeating = true).count()
}
