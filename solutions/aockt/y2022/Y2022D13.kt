package aockt.y2022

import aockt.y2022.Y2022D13.PacketData.*
import io.github.jadarma.aockt.core.Solution

object Y2022D13 : Solution {

    /** An element in a [Packet]. */
    private sealed interface PacketData : Comparable<PacketData> {

        /** Contains a single positive integer literal.*/
        @JvmInline
        value class NumberData(val value: Int) : PacketData {

            init {
                require(value >= 0) { "Negative values are not allowed." }
            }

            override fun toString() = value.toString()
            override fun compareTo(other: PacketData) = when (other) {
                is NumberData -> value compareTo other.value
                is ListData -> ListData(listOf(this)) compareTo other
            }
        }

        /** Contains a list of zero or more packets. */
        @JvmInline
        value class ListData(private val data: List<PacketData>) : PacketData, List<PacketData> by data {
            override fun toString() = data.joinToString(prefix = "[", postfix = "]", separator = ",")
            override fun compareTo(other: PacketData): Int = when (other) {
                is NumberData -> this compareTo ListData(listOf(other))
                is ListData -> {
                    val p1 = this.data
                    val p2 = other.data

                    (0 until maxOf(p1.size, p2.size)).forEach { index ->
                        val left = p1.getOrNull(index) ?: return -1
                        val right = p2.getOrNull(index) ?: return 1
                        val comparison = left compareTo right
                        if (comparison != 0) return comparison
                    }
                    0
                }
            }
        }
    }

    /** A distress signal packet. */
    @JvmInline
    private value class Packet(val data: ListData) : Comparable<Packet> {

        override fun compareTo(other: Packet): Int = data.compareTo(other.data)
        override fun toString() = data.toString()

        companion object {
            /** Decode the [input] into a [Packet] or throw if invalid. */
            fun parse(input: String): Packet {
                val stream = input.iterator()
                require(stream.nextChar() == '[') { "Expected beginning of list." }

                fun parseList(): ListData {
                    val data = mutableListOf<PacketData>()
                    while (stream.hasNext()) {
                        when (val char = stream.nextChar()) {
                            ',' -> require(data.isNotEmpty()) { "Encountered list separator before first element." }
                            '[' -> parseList().also(data::add)
                            ']' -> return ListData(data)
                            in '0'..'9' -> {
                                var number = char.digitToInt()
                                var nextDigit = stream.nextChar()
                                while (nextDigit.isDigit()) {
                                    number = number * 10 + nextDigit.digitToInt()
                                    nextDigit = stream.nextChar()
                                }
                                data.add(NumberData(number))
                                if (nextDigit == ']') return ListData(data)
                                if (nextDigit != ',') throw IllegalArgumentException("Illegal character '$char' encountered.")
                            }

                            else -> throw IllegalArgumentException("Illegal character '$char' encountered.")
                        }
                    }
                    throw IllegalArgumentException("Reached end of input with an opened list.")
                }

                return Packet(parseList())
            }
        }
    }

    /** Parse the [input] and return the sequence of packets in the input, ignoring whitespace.. */
    private fun parseInput(input: String): Sequence<Packet> =
        input
            .splitToSequence("\n\n")
            .flatMap { pair -> pair.split('\n').also { require(it.size == 2) } }
            .map(Packet::parse)

    override fun partOne(input: String) =
        parseInput(input)
            .chunked(2) { (left, right) -> left < right }
            .withIndex()
            .sumOf { (index, isInRightOrder) -> if (isInRightOrder) index + 1 else 0 }

    override fun partTwo(input: String): Int {
        val dividerPackets = listOf(Packet.parse("[[2]]"), Packet.parse("[[6]]"))
        return parseInput(input)
            .plus(dividerPackets)
            .sorted()
            .run { dividerPackets.map { indexOf(it) + 1 }.reduce(Int::times) }
    }
}
