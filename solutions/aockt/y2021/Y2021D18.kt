package aockt.y2021

import io.github.jadarma.aockt.core.Solution
import kotlin.math.roundToInt

object Y2021D18 : Solution {

    /** Marker interface for possible values to fit inside the left or right side of a [SnailNumber]. */
    private sealed interface SnailValue {
        val magnitude: UInt
    }

    /** Represents a literal integer value. */
    @JvmInline
    private value class Literal(val value: UInt) : SnailValue {
        override val magnitude: UInt get() = value
        operator fun plus(other: Literal) = Literal(value + other.value)
        override fun toString() = value.toString()
    }

    /** Represents a snail number, composed of two potentially recursive [SnailValue]s. Guaranteed in reduced form. */
    @JvmInline
    private value class SnailNumber private constructor(val value: Pair<SnailValue, SnailValue>) : SnailValue {
        override val magnitude: UInt get() = with(value) { 3u * first.magnitude + 2u * second.magnitude }
        operator fun plus(other: SnailNumber) = SnailNumber(this to other).flatten().reduceAndUnFlatten()
        override fun toString() = "[${value.first},${value.second}]"

        companion object {
            /** Parse the [value] and return the reduces [SnailNumber] if it is valid. */
            fun parse(value: String): SnailNumber = runCatching {
                value.map {
                    when (it) {
                        '[' -> OPEN
                        ']' -> CLOSE
                        ',' -> COMMA
                        else -> it.digitToInt()
                    }
                }.reduceAndUnFlatten()
            }.getOrElse { throw NumberFormatException("Invalid snail number: ${it.message}") }

            /** Return a flattened list representation of this number. */
            private fun SnailNumber.flatten(): MutableList<Int> = mutableListOf<Int>().apply {
                fun SnailValue.keepFlattening(): Boolean = when (this) {
                    is Literal -> add(value.toInt())
                    is SnailNumber -> {
                        add(OPEN)
                        value.first.keepFlattening()
                        add(COMMA)
                        value.second.keepFlattening()
                        add(CLOSE)
                    }
                }
                keepFlattening()
            }

            /** Rebuild a [SnailNumber] from its [flatten]ed representation, reducing if necessary. */
            private fun List<Int>.reduceAndUnFlatten(): SnailNumber = runCatching {
                val reduced = this.toMutableList().apply {
                    while (true) {
                        if (couldExplode()) continue
                        if (couldSplit()) continue
                        break
                    }
                }
                val reader = reduced.iterator()
                fun parseValue(): SnailValue = when (val token = reader.next()) {
                    in 0..9 -> Literal(token.toUInt())
                    OPEN -> {
                        val lhs = parseValue()
                        require(reader.next() == COMMA)
                        val rhs = parseValue()
                        require(reader.next() == CLOSE)
                        SnailNumber(lhs to rhs)
                    }

                    else -> error("Invalid input: $token")
                }
                parseValue() as SnailNumber
            }.getOrElse { throw IllegalArgumentException("Invalid input", it) }

            /** Tries to apply the exploding rule on a snail number and returns whether it was applied. */
            private fun MutableList<Int>.couldExplode(): Boolean {
                val indexOfExplode = scan(0) { level, token ->
                    level + when (token) {
                        OPEN -> 1
                        CLOSE -> -1
                        else -> 0
                    }
                }.indexOfFirst { it == 5 } - 1
                if (indexOfExplode < 0) return false

                val explode = slice(indexOfExplode..(indexOfExplode + 4))
                val indexOfLeftLiteral = slice(0..indexOfExplode).indexOfLast { it >= 0 }
                if (indexOfLeftLiteral != -1) this[indexOfLeftLiteral] += explode[1]

                val indexOfRightLiteral =
                    slice((indexOfExplode + 4) until size).indexOfFirst { it >= 0 } + indexOfExplode + 4
                if (indexOfRightLiteral != -1) this[indexOfRightLiteral] += explode[3]

                repeat(5) { removeAt(indexOfExplode) }
                add(indexOfExplode, 0)
                return true
            }

            /** Tries to apply the splitting rule on a snail number and returns whether it was applied. */
            private fun MutableList<Int>.couldSplit(): Boolean {
                val overflowLocation = indexOfFirst { it > 9 }.takeIf { it >= 0 } ?: return false
                val value = removeAt(overflowLocation)
                val split = listOf(OPEN, value.floorDiv(2), COMMA, (value / 2.0).roundToInt(), CLOSE)
                return addAll(overflowLocation, split)
            }

            private const val OPEN = -1
            private const val CLOSE = -2
            private const val COMMA = -3
        }
    }

    /** Parse the [input] and return the [SnailNumber]s contained on each line. */
    private fun parseInput(input: String): Sequence<SnailNumber> =
        input
            .lineSequence()
            .map { runCatching { SnailNumber.parse(it) }.getOrElse { throw IllegalArgumentException("Invalid input") } }

    override fun partOne(input: String) =
        parseInput(input)
            .reduce(SnailNumber::plus)
            .magnitude

    override fun partTwo(input: String) =
        parseInput(input).toSet().let { numbers ->
            numbers
                .flatMap { a -> (numbers - a).map { b -> a to b } }
                .maxOf { (a, b) -> (a + b).magnitude }
        }
}
