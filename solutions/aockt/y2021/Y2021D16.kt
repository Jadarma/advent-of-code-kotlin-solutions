package aockt.y2021

import aockt.y2021.Y2021D16.BitsPacket.TypeID.*
import io.github.jadarma.aockt.core.Solution

object Y2021D16 : Solution {

    /** A BITS packet type. */
    private sealed interface BitsPacket {
        /** The version of this packet. Seemingly unused, unless you find the manual. */
        val version: Int

        /** The type of this packet, describing its behaviour. */
        val type: TypeID

        /** Evaluate the expressing defined by this packet. */
        fun evaluate(): Long

        /** The possible types of [BitsPacket], defining their behaviour. */
        enum class TypeID(val value: Int) {
            Sum(0),
            Product(1),
            Minimum(2),
            Maximum(3),
            Literal(4),
            GreaterThan(5),
            LessThan(6),
            EqualTo(7);

            companion object {
                fun fromValue(value: Int) = values().first { it.value == value }
            }
        }

        /** A literal packet, holding a single [literal] value. */
        data class Literal(override val version: Int, val literal: Long) : BitsPacket {
            override val type = Literal
            override fun evaluate() = literal
        }

        /** An operator packet, holding one or more [subPackets], which can be evaluated based on the [type]. */
        data class Operator(
            override val version: Int,
            override val type: TypeID,
            val subPackets: List<BitsPacket>,
        ) : BitsPacket {
            init {
                when (type) {
                    Literal -> require(false) { "An operator packet cannot be of a literal type." }
                    GreaterThan, LessThan, EqualTo -> require(subPackets.size == 2) { "Comparison operator should always have exactly two operands." }
                    else -> require(subPackets.isNotEmpty()) { "Operator missing operands." }
                }
            }

            override fun evaluate(): Long = when (type) {
                Sum -> subPackets.sumOf { it.evaluate() }
                Product -> subPackets.fold(1L) { acc, packet -> acc * packet.evaluate() }
                Minimum -> subPackets.minOf { it.evaluate() }
                Maximum -> subPackets.maxOf { it.evaluate() }
                GreaterThan -> if (subPackets.first().evaluate() > subPackets.last().evaluate()) 1 else 0
                LessThan -> if (subPackets.first().evaluate() < subPackets.last().evaluate()) 1 else 0
                EqualTo -> if (subPackets.first().evaluate() == subPackets.last().evaluate()) 1 else 0
                Literal -> error("Cannot be a literal")
            }
        }

        companion object {
            fun parse(hexInput: String): BitsPacket {
                val bitStream = hexInput
                    .asSequence()
                    .flatMap { it.digitToInt(16).toString(2).padStart(4, '0').asSequence() }
                    .map { it == '1' }
                    .iterator()
                var bitsRead = 0

                // Consumes the next bit and returns whether it is set.
                fun readBoolean(): Boolean = bitStream.next().also { bitsRead++ }

                // Consumes the next few [bits] and returns them as a binary string.
                fun readString(bits: Int): String = buildString(bits) {
                    repeat(bits) { append(if (readBoolean()) '1' else '0') }
                }

                // Consumes the next few [bits] and returns their integer value.
                fun readInt(bits: Int): Int = readString(bits).toInt(2)

                // Consumes the next few bits in chunks of five, reading the value of a literal.
                fun readLiteralValue(): Long = buildString(32) {
                    while (readBoolean()) append(readString(4))
                    append(readString(4))
                }.toLong(2)

                // Consumes the next few bits and attempts to parse an entire packet.
                fun readPacket(): BitsPacket {
                    val version = readInt(3)
                    val type = TypeID.fromValue(readInt(3))
                    if (type == Literal) return Literal(version, readLiteralValue())
                    val subPackets = buildList {
                        if (readBoolean()) {
                            val numberOfSubPackets = readInt(11)
                            repeat(numberOfSubPackets) {
                                add(readPacket())
                            }
                        } else {
                            val subPacketsLength = readInt(15)
                            val stopReadingAt = bitsRead + subPacketsLength
                            while (bitsRead < stopReadingAt) add(readPacket())
                        }
                    }
                    return Operator(version, type, subPackets)
                }

                val packet = readPacket()
                while (bitStream.hasNext()) require(!readBoolean()) { "Found set bit in end padding." }
                return packet
            }
        }
    }

    override fun partOne(input: String) = BitsPacket.parse(input).run {
        fun BitsPacket.versionSum(): Int = when (this) {
            is BitsPacket.Literal -> version
            is BitsPacket.Operator -> version + subPackets.sumOf { it.versionSum() }
        }
        versionSum()
    }

    override fun partTwo(input: String) = BitsPacket.parse(input).evaluate()
}
