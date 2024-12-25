package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Direction.*
import aockt.util.spacial.*
import aockt.y2024.Y2024D25.DoorSecurityDevice.*
import io.github.jadarma.aockt.core.Solution

object Y2024D25 : Solution {

    /** Door security device used on North Pole doors. */
    private sealed class DoorSecurityDevice {

        /** Height of lock pins or key grooves. */
        abstract val pinOut: List<Int>

        data class Lock(override val pinOut: List<Int>) : DoorSecurityDevice()
        data class Key(override val pinOut: List<Int>) : DoorSecurityDevice()

        companion object {
            /** Scans the [schematic] and returns the appropriate device. */
            fun fromSchematic(schematic: String): DoorSecurityDevice {
                val grid = Grid(schematic)
                require(grid.height == 7 && grid.width == 5) { "Unknown make or model." }

                val isLock = grid[0, 0] == '.'
                val direction = if (isLock) Down else Up
                val startHeight = if (isLock) grid.height - 2 else 1

                val pinOut = List(grid.width) { index ->
                    grid
                        .move(Point(index, startHeight), direction)
                        .takeWhile { it.value == '#' }
                        .count()
                }

                return if (isLock) Lock(pinOut) else Key(pinOut)
            }
        }
    }

    /** Returns whether this key _(loosely)_ fits the [lock], meaning their pin-outs do dot overlap. */
    private infix fun Key.fits(lock: Lock): Boolean = pinOut.zip(lock.pinOut).all { (k, l) -> k + l <= 5 }

    /** Parse the [input] and return the [Lock]s and [Key]s described by the schematics. */
    private fun parseInput(input: String): Pair<Set<Lock>, Set<Key>> = parse {
        val (locks, keys) = input
            .splitToSequence("\n\n")
            .map { DoorSecurityDevice.fromSchematic(it) }
            .partition { it is Lock }

        @Suppress("UNCHECKED_CAST")
        locks.toSet() as Set<Lock> to keys.toSet() as Set<Key>
    }

    override fun partOne(input: String): Int {
        val (locks, keys) = parseInput(input)
        return keys.sumOf { key -> locks.count { key fits it } }
    }
}
