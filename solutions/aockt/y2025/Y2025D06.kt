package aockt.y2025

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2025D06 : Solution {

    /**
     * A math operation.
     * @property symbol The string representation of the mathematical symbol.
     * @param identity  The identity element of the mathematical operation.
     * @param operation The implementation lambda of the mathematical operation.
     */
    private enum class Operation(
        val symbol: String,
        private val identity: Long,
        private val operation: (Long, Long) -> Long,
    ) {
        ADD("+", 0, Long::plus),
        MULTIPLY("*", 1, Long::times);

        /** Apply the operation on two operands. */
        operator fun invoke(a: Long, b: Long) = operation(a, b)

        /** Apply the operation on multiple operands. */
        fun fold(inputs: Iterable<Long>): Long = inputs.fold(identity, ::invoke)

        companion object {
            /** Parse the [symbol] into an [Operation]. */
            fun of(symbol: String): Operation = Operation.entries.firstOrNull { it.symbol == symbol }
                ?: throw IllegalArgumentException("Invalid operand symbol: $symbol")
        }
    }

    /**
     * A math homework problem.
     * @property inputs    The list of numbers that should be used in the calculation.
     * @property operation The operation to apply on the inputs.
     */
    private data class Problem(val inputs: List<Long>, val operation: Operation) {

        /** Cheat and use a computer to solve the problem. */
        fun solve(): Long = operation.fold(inputs)
    }

    /**
     * Parse the [input] and return the list of math homework [Problem]s.
     * @param input The input.
     * @param cephalopodNotation If enabled, reads inputs as right-to-left columns instead of normal human intuition.
     */
    private fun parseInput(input: String, cephalopodNotation: Boolean): List<Problem> = parse {
        val lines = input.run {
            // Account for inputs being given with trimmed ends.
            val lines = lines()
            require(lines.size >= 2) { "Invalid problem, not enough input numbers." }
            val maxLength = lines.maxOf { it.length }
            lines.map { it.padEnd(maxLength, ' ') }
        }

        // The input indice slices of the problems.
        val problemSlices = lines.run {
            val spaces = first().indices.filter { index -> all { it[index] == ' ' } }
            listOf(-1) + spaces + listOf(first().length)
        }.zipWithNext { start, end -> start.inc()..end.dec() }

        buildList {
            problemSlices.map { slice ->
                val rawData = lines.map { it.slice(slice) }
                val dataWidth = rawData.first().length
                val rawInputs = rawData.dropLast(1)
                val operation = rawData.last().trim().let(Operation::of)

                val inputs = rawInputs
                    .takeUnless { cephalopodNotation }
                    ?: (dataWidth - 1 downTo 0).map { index -> rawInputs.map { it[index] }.joinToString("") }

                Problem(
                    inputs = inputs.map(String::trim).map(String::toLong),
                    operation = operation,
                ).also(::add)
            }
        }
    }

    override fun partOne(input: String) = parseInput(input, cephalopodNotation = false).sumOf(Problem::solve)
    override fun partTwo(input: String) = parseInput(input, cephalopodNotation = true).sumOf(Problem::solve)
}
