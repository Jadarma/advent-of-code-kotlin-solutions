package aockt.y2023

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D15 : Solution {

    /**
     * A lens boxing operation.
     * @property label The label of the lens to apply the operation on.
     * @property box The ID of the box the operation should be performed in.
     */
    private sealed class Operation(val label: String) {

        val box: Int = hash(label)

        /** An operation that should remove a lens from a box. */
        class Remove(label: String) : Operation(label)

        /** An operation that should replace or add a lens to a box. */
        class Add(label: String, val focalLength: Int) : Operation(label)
    }

    /** A lens with a [focalLength] and identified by a [label]. */
    private data class Lens(val label: String, val focalLength: Int)

    /** An array of 256 boxes with lenses. */
    private class HashBox {

        private val boxes: Map<Int, MutableList<Lens>> = (0..<256).associateWith { mutableListOf() }

        /** Apply the [operation] on the boxes and update their state. */
        fun apply(operation: Operation): HashBox = apply {
            val box = boxes.getValue(operation.box)
            when (operation) {
                is Operation.Add -> {
                    val lens = Lens(operation.label, operation.focalLength)
                    val existingLens = box.indexOfFirst { it.label == operation.label }
                    if (existingLens == -1) box.add(lens)
                    else box[existingLens] = lens
                }

                is Operation.Remove -> box.removeIf { it.label == operation.label }
            }
        }

        /** Returns the total focusing power of all the lenses in all the boxes. */
        val focusingPower: Int
            get() = boxes.entries
                .flatMap { (box, lenses) -> lenses.mapIndexed { slot, lens -> Triple(box, slot, lens) } }
                .sumOf { (box, slot, lens) -> box.inc() * slot.inc() * lens.focalLength }
    }

    /** Calculates the Holiday ASCII String Helper value for the [string]. */
    private fun hash(string: String): Int = string.fold(0) { hash, c -> hash.plus(c.code).times(17).rem(256) }

    /** Parses the [input] and returns the list of [Operation]s described in the manual. */
    private fun parseInput(input: String): List<Operation> = parse {
        val addRegex = Regex("""^([a-z]+)=(\d)$""")
        val removeRegex = Regex("""^([a-z]+)-$""")
        input
            .split(',')
            .map {
                val add = addRegex.matchEntire(it)
                val remove = removeRegex.matchEntire(it)
                when {
                    add != null -> Operation.Add(add.groupValues[1], add.groupValues[2].toInt())
                    remove != null -> Operation.Remove(remove.groupValues[1])
                    else -> error("Input does not match regex.")
                }
            }
    }

    override fun partOne(input: String) = input.split(',').sumOf(::hash)
    override fun partTwo(input: String) = parseInput(input).fold(HashBox(), HashBox::apply).focusingPower
}
