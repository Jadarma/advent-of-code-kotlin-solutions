package aockt.y2022

import aockt.y2022.Y2022D05.CraneDialect.*
import io.github.jadarma.aockt.core.Solution

object Y2022D05 : Solution {

    /** Parses the input and returns the initial [CraneStackState] and a list of instructions. */
    private fun parseInput(input: String): Pair<CraneStackState, List<CraneMove>> {
        val inputStream = input.lineSequence().iterator()
        lateinit var line: String
        val craneStacks = buildList {
            while (inputStream.hasNext()) {
                line = inputStream.next()
                if (line.trimStart().firstOrNull() != '[') return@buildList
                line
                    .replace(Regex("""\[([A-Z])]\s?""")) { it.groupValues[1] }
                    .replace(Regex("""\s{3,4}"""), "_")
                    .toList()
                    .let(::add)
            }
        }
        val labels = line.trim().split(Regex("\\s+"))

        val craneState = buildMap(capacity = labels.size) {
            labels.forEachIndexed { index, stackName ->
                val stack = craneStacks
                    .mapNotNull { it.getOrNull(index) }
                    .filter { it != '_' }
                    .asReversed()
                    .let { ArrayDeque(it) }
                put(stackName, stack)
            }
        }.let(::CraneStackState)

        inputStream.next()
        val instructions = buildList {
            while (inputStream.hasNext()) {
                add(inputStream.next().let(CraneMove::parse))
            }
        }

        return (craneState to instructions)
    }

    /** Known dialects for different CraneMover models.*/
    private enum class CraneDialect { CrateMover9000, CrateMover9001 }

    /** A single instruction to move crates between stacks. Behavior depends on [CraneDialect]. */
    private data class CraneMove(val takeFrom: String, val moveTo: String, val amount: Int) {
        override fun toString() = "move $amount from $takeFrom to $moveTo"

        companion object {
            private val craneInstructionRegex = Regex("""^move (\d+) from (\S+) to (\S+)$""")

            /** Parse an [input] into a [CraneMove] or throw an [IllegalArgumentException] if it is invalid. */
            fun parse(input: String): CraneMove {
                val match = craneInstructionRegex.matchEntire(input)
                requireNotNull(match) { "Input '$input' is not a valid CraneMove." }
                return CraneMove(
                    takeFrom = match.groupValues[2],
                    moveTo = match.groupValues[3],
                    amount = match.groupValues[1].toInt(),
                )
            }
        }
    }

    @JvmInline
    private value class CraneStackState(private val state: Map<String, ArrayDeque<Char>>) {
        /** Executes the [instruction], using a specific [craneDialect]. Mutates inner state. */
        fun execute(instruction: CraneMove, craneDialect: CraneDialect) {
            val fromStack = state[instruction.takeFrom] ?: return
            val toStack = state[instruction.moveTo] ?: return
            when (craneDialect) {
                CrateMover9000 -> repeat(instruction.amount) {
                    toStack.addLast(fromStack.removeLast())
                }

                CrateMover9001 -> {
                    (1..instruction.amount)
                        .map { fromStack.removeLast() }
                        .asReversed()
                        .forEach(toStack::addLast)
                }
            }
        }

        /** Returns a string of all characters of each crate stack's top crate label, ignoring empty stacks. */
        fun topOfStack(): String = state.values.map { it.lastOrNull() ?: "" }.joinToString("")
    }

    /**
     * Given an [input] and a crane [dialect], simulates the crate moving and returns the string made out of all
     * the letters of each top of the stack.
     */
    private fun solve(input: String, dialect: CraneDialect): String = parseInput(input).let { (state, moves) ->
        moves.forEach { state.execute(it, dialect) }
        state.topOfStack()
    }

    override fun partOne(input: String) = solve(input, CrateMover9000)
    override fun partTwo(input: String) = solve(input, CrateMover9001)
}
