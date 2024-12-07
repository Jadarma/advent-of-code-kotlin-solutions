package aockt.y2024

import aockt.util.parse
import aockt.y2024.Y2024D07.Operator.*
import io.github.jadarma.aockt.core.Solution

object Y2024D07 : Solution {

    /** Possible operators for the calibration equations. */
    private enum class Operator { Add, Multiply, Concatenate }

    /** Undoes an operator by applying the opposite. Unsafe calls throw. */
    private fun Operator.undo(a: Long, b: Long): Long = when(this) {
        Add -> a - b
        Multiply -> a / b
        Concatenate -> a.toString().removeSuffix(b.toString()).toLong()
    }

    /** The numbers of a bridge repair calibration equation, with missing operators. */
    private data class Equation(val result: Long, val operands: List<Long>) {

        init {
            require(operands.size >= 2) { "Equation must have at least two operands." }
        }

        /** Determines if this equation has at least one solution using the given [operators]. */
        fun isSolvable(operators: Set<Operator>): Boolean {

            // DFS + pruning: Solve equation backwards, removing candidate operators that can't apply:
            // - Can't be Add if subtracting would lead to negative numbers.
            // - Can't be Multiply if division is not exact, or divisor is zero.
            // - Can't be Concatenate if the number isn't a (strictly shorter) suffix of the other.
            // When reaching the last number, the equation succeeds if it is equal to the accumulator.
            fun recurse(acc: Long, index: Int): Boolean {
                if (index == 0) return acc == operands.first()
                val number = operands[index]
                return operators
                    .asSequence()
                    .filterNot { it == Add && acc < number }
                    .filterNot { it == Multiply && number == 0L }
                    .filterNot { it == Multiply && acc % number != 0L }
                    .filterNot { it == Concatenate && acc == number }
                    .filterNot { it == Concatenate && !acc.toString().endsWith(number.toString())}
                    .any { recurse(it.undo(acc, number), index - 1) }
            }

            return recurse(result, operands.lastIndex)
        }
    }

    /** Parse the [input] and return the list of equations. */
    private fun parseInput(input: String): List<Equation> = parse {
        input
            .lines()
            .map { line ->
                val (result, operands) = line.split(": ", limit = 2)
                Equation(result.toLong(), operands.split(' ').map(String::toLong))
            }
    }

    /** Common solution to both parts. */
    private fun solve(input: String, vararg operators: Operator): Long =
        parseInput(input)
            .filter { it.isSolvable(operators.toSet()) }
            .sumOf { it.result }

    override fun partOne(input: String) = solve(input, Add, Multiply)
    override fun partTwo(input: String) = solve(input, Add, Multiply, Concatenate)
}
