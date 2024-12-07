package aockt.y2024

import aockt.util.parse
import aockt.y2024.Y2024D07.Operator.*
import io.github.jadarma.aockt.core.Solution
import kotlin.math.*

object Y2024D07 : Solution {

    /** Possible operators for the calibration equations. */
    private enum class Operator {
        Add, Multiply, Concatenate;

        fun eval(a: Long, b: Long): Long = when (this) {
            Add -> a + b
            Multiply -> a * b
            Concatenate -> 10.0.pow(floor(log10(b.toDouble()) + 1)).toLong() * a + b // Optimised "$a$b".toLong()
        }
    }

    /** The numbers of a bridge repair calibration equation, with missing operators. */
    private data class Equation(val result: Long, val operands: List<Long>) {

        init {
            require(operands.size >= 2) { "Equation must have at least two operands." }
        }

        /** Determines if this equation has at least one solution using the given [operators]. */
        fun isSolvable(operators: Set<Operator>): Boolean {

            // DFS + pruning: Since all operators result in larger numbers, quit early if overshooting the result.
            fun recurse(acc: Long, index: Int): Boolean = when {
                acc > result -> false
                index + 1 >= operands.size -> acc == result
                else -> operators.any { recurse(it.eval(acc, operands[index + 1]), index + 1) }
            }

            return recurse(operands.first(), 0)
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
