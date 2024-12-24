package aockt.y2024

import aockt.util.parse
import aockt.util.validation.assume
import io.github.jadarma.aockt.core.Solution

object Y2024D24 : Solution {

    /**
     * A logic gate.
     *
     * @property left  Name of the first input wire.
     * @property right Name of the second input wire.
     * @property out   Name of the output wire.
     * @property eval  The operation this gate applies: `out = eval(left, right)`.
     */
    private data class Gate(
        val left: String,
        val right: String,
        val out: String,
        val eval: (Boolean, Boolean) -> Boolean,
    )

    /** Evaluate this circuit using [inputs] values for certain wires. */
    private fun List<Gate>.runCircuit(inputs: Map<String, Boolean>): Long {
        val wires = inputs.toMutableMap()
        val formulas = associateBy { it.out }

        fun evalWire(wire: String): Boolean = wires.getOrPut(wire) {
            with(formulas.getValue(wire)) { eval(evalWire(left), evalWire(right)) }
        }

        repeat(times = outputBinarySize()) {
            evalWire("z${it.toString().padStart(2, '0')}")
        }

        return wires.wireAsLong('z')
    }

    /** Determines the binary size of the circuit's output. */
    private fun List<Gate>.outputBinarySize(): Int =
        asSequence()
            .filter { it.out.startsWith('z') }
            .maxOf { it.out }
            .removePrefix("z")
            .toInt().inc()
            .coerceAtMost(64)

    /**
     * Interpret the wire values of all wires with this [prefix] as a binary number.
     * Stops at first missing bit in the wire, or at 64 bits.
     */
    private fun Map<String, Boolean>.wireAsLong(prefix: Char): Long {
        var result = 0L
        for (i in 0 until 64) {
            val bit = get("$prefix${i.toString().padStart(2, '0')}") ?: break
            if (bit) result = result or (1L shl i)
        }
        return result
    }

    /**
     * Return the first gate that outputs to a Z wire uses the [out] wire as one of its input.
     * If no such gate wired directly, searches recursively.
     */
    private fun List<Gate>.firstZThatUsesOutput(out: String): String {
        val candidates = filter { it.left == out || it.right == out }

        return when (val zGate = candidates.firstOrNull { it.out.startsWith('z') }) {
            null -> candidates.firstNotNullOf { firstZThatUsesOutput(it.out) }
            else -> "z${zGate.out.drop(1).toInt().dec().toString().padStart(2, '0')}"
        }
    }

    /** Parse the [input] and return the circuit diagram as a list of [Gate]s and wire inputs. */
    private fun parseInput(input: String): Pair<List<Gate>, Map<String, Boolean>> = parse {
        val regex = Regex("""^([a-z0-9]{3}) (AND|XOR|OR) ([a-z0-9]{3}) -> ([a-z0-9]{3})$""")
        val (wires, gates) = input.split("\n\n");

        val wireStates = buildMap {
            wires
                .lineSequence()
                .map { it.split(": ", limit = 2) }
                .forEach { (wire, value) -> put(wire, value == "1") }
        }

        val wiringDiagram = gates
            .lineSequence()
            .map { regex.matchEntire(it)!!.destructured }
            .map { (left, op, right, out) ->
                val eval = when (op) {
                    "AND" -> Boolean::and
                    "OR" -> Boolean::or
                    "XOR" -> Boolean::xor
                    else -> error("Invalid op: $op")
                }
                Gate(left, right, out, eval)
            }
            .toList()

        wiringDiagram to wireStates
    }

    override fun partOne(input: String): Long {
        val (gates, inputs) = parseInput(input)
        return gates.runCircuit(inputs)
    }

    override fun partTwo(input: String): String {
        val (gates, inputs) = parseInput(input)

        // Non-XOR gates that output to Z wires (except last one).
        val lastZWire = "z${gates.outputBinarySize() - 1}"
        val nonXorOutGates = gates.asSequence()
            .filterNot { it.eval == Boolean::xor }
            .filter { it.out.startsWith('z') && it.out != lastZWire }
            .toList()

        // XOR intermediary gates that do not output to Z or accept X,Y inputs.
        val xorIntermediaries = gates.asSequence()
            .filter { it.eval == Boolean::xor }
            .filterNot { it.out.startsWith('z') }
            .filterNot { it.left.first() in "xy" || it.right.first() in "xy" }
            .toList()

        // All XOR gates should be connected to the output, but not all swaps should be XOR gates, one is a false carry.
        // Check that this is true for the input, we should be able to swap out exactly 3
        assume(xorIntermediaries.size == 3) { "Assumed that 3 XOR gates are not connected to output." }
        assume(nonXorOutGates.size == xorIntermediaries.size) { "Could not swap all XOR intermediate gates." }

        // The last swap is determined by a mismatched carry bit.
        // Do the swaps between the gates detected earlier, and simulate - It will be correct up until a bit.
        // The position of the error tells us where to look, they must be exactly two gates wired to X and Y followed by
        // the error position, that point to two intermediary gates.
        val falseCarryGates = run {
            val swappedWires = buildList {
                addAll(gates)
                for (a in xorIntermediaries) {
                    val b = nonXorOutGates.first { it.out == gates.firstZThatUsesOutput(a.out) }
                    remove(a); add(a.copy(out = b.out))
                    remove(b); add(b.copy(out = a.out))
                }
            }

            val expectedResult = inputs.wireAsLong('x').plus(inputs.wireAsLong('y'))
            val actualResult = swappedWires.runCircuit(inputs)
            val falseCarryBit = (expectedResult xor actualResult).countTrailingZeroBits().toString()

            swappedWires
                .filter { it.left.endsWith(falseCarryBit) && it.right.endsWith(falseCarryBit) }
                .also { assume(it.size == 2) { "Did not find exactly two false carry gates to swap." } }
        }

        return (nonXorOutGates + xorIntermediaries + falseCarryGates)
            .sortedBy { it.out }
            .joinToString(",") { it.out }
    }
}
