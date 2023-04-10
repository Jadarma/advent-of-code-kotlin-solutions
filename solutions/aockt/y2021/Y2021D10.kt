package aockt.y2021

import aockt.y2021.Y2021D10.SyntaxCheckResult.*
import io.github.jadarma.aockt.core.Solution

object Y2021D10 : Solution {

    /** The possible outcomes of syntax checking the submarine's navigation subsystem. */
    private sealed interface SyntaxCheckResult {
        /** The syntax is valid. */
        object Pass : SyntaxCheckResult

        /** The input contains invalid characters (i.e.: not a brace). */
        object Invalid : SyntaxCheckResult

        /** The input is corrupted, an [illegalChar] was used to close another bracket. */
        data class Corrupted(val illegalChar: Char) : SyntaxCheckResult

        /** The input is incomplete, it ended at a time the [stack] was still not empty. */
        data class Incomplete(val stack: CharSequence) : SyntaxCheckResult
    }

    /** Maps the brackets to their other pair. */
    private val matchingBraceOf: Map<Char, Char> =
        mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
            .flatMap { (a, b) -> listOf(a to b, b to a) }
            .toMap()

    /** Returns the matching brace of this character, a convenience wrapper over [matchingBraceOf]. */
    private val Char.matchingBrace: Char get() = matchingBraceOf[this] ?: throw IllegalArgumentException("Not a brace.")

    /** Analyzes the [input]'s syntax and returns whether it is valid or the cause of failure. */
    private fun checkSyntax(input: String): SyntaxCheckResult =
        input.fold(ArrayDeque<Char>()) { stack, char ->
            stack.apply {
                when (char) {
                    '(', '[', '{', '<' -> stack.addLast(char)
                    ')', ']', '}', '>' -> when (stack.lastOrNull()) {
                        char.matchingBrace -> stack.removeLast()
                        else -> return Corrupted(char)
                    }

                    else -> return Invalid
                }
            }
        }.let { stack -> if (stack.isEmpty()) Pass else Incomplete(stack.joinToString("")) }

    /** Given the [stack] on an incomplete line, determine the remaining characters. */
    private fun completeLine(stack: CharSequence): String = buildString {
        for (i in stack.indices.reversed()) {
            append(stack[i].matchingBrace)
        }
    }

    /** Parse the [input] and return a sequence of the lines and their syntax analyses. */
    private fun parseInput(input: String): Sequence<Pair<String, SyntaxCheckResult>> =
        input.lineSequence().map { it to checkSyntax(it) }

    override fun partOne(input: String): Long =
        parseInput(input)
            .map { it.second }
            .filterIsInstance<Corrupted>()
            .sumOf {
                when (it.illegalChar) {
                    ')' -> 3L
                    ']' -> 57L
                    '}' -> 1197L
                    '>' -> 25137L
                    else -> error("Invalid illegal character.")
                }
            }

    override fun partTwo(input: String): Long =
        parseInput(input)
            .map { it.second }
            .filterIsInstance<Incomplete>()
            .map { completeLine(it.stack).fold(0L) { acc, char -> acc * 5 + ")]}>".indexOf(char) + 1 } }
            .sorted().toList()
            .let { it[it.size / 2] }
}
