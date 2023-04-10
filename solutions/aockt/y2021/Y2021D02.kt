package aockt.y2021

import aockt.y2021.Y2021D02.SubmarineCommand.*
import io.github.jadarma.aockt.core.Solution

object Y2021D02 : Solution {

    /** The valid submarine commands, they might do different things depending on how you read the manual. */
    private sealed interface SubmarineCommand {
        val amount: UInt

        data class Up(override val amount: UInt) : SubmarineCommand
        data class Down(override val amount: UInt) : SubmarineCommand
        data class Forward(override val amount: UInt) : SubmarineCommand

        companion object {
            fun parse(value: String): SubmarineCommand {
                val (command, amount) = value
                    .split(" ")
                    .let { (a, b) -> a to (b.toUIntOrNull() ?: throw IllegalArgumentException("Invalid value: $b")) }
                return when (command) {
                    "up" -> ::Up
                    "down" -> ::Down
                    "forward" -> ::Forward
                    else -> throw IllegalArgumentException("Invalid command: $command.")
                }(amount)
            }
        }
    }

    /** Holds the coordinates of a submarine's position at a given time. */
    private data class Position(val depth: Int = 0, val horizontal: Int = 0, val aim: Int = 0) {
        init {
            require(depth >= 0) { "Submarines can't fly." }
        }
    }

    /** Parse the [input] and return the [SubmarineCommand]s to steer the submarine with. */
    private fun parseInput(input: String): Sequence<SubmarineCommand> =
        input
            .lineSequence()
            .map(SubmarineCommand::parse)

    override fun partOne(input: String) =
        parseInput(input)
            .fold(Position()) { position, command ->
                when (command) {
                    is Up -> position.copy(depth = maxOf(0, position.depth - command.amount.toInt()))
                    is Down -> position.copy(depth = position.depth + command.amount.toInt())
                    is Forward -> position.copy(horizontal = position.horizontal + command.amount.toInt())
                }
            }
            .let { it.horizontal * it.depth }

    override fun partTwo(input: String) =
        parseInput(input)
            .fold(Position()) { position, command ->
                when (command) {
                    is Up -> position.copy(aim = position.aim - command.amount.toInt())
                    is Down -> position.copy(aim = position.aim + command.amount.toInt())
                    is Forward -> position.copy(
                        horizontal = position.horizontal + command.amount.toInt(),
                        depth = maxOf(0, position.depth + (position.aim * command.amount.toInt())),
                    )
                }
            }
            .let { it.horizontal * it.depth }
}
