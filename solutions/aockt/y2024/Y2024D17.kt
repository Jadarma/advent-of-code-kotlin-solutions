package aockt.y2024

import aockt.util.parse
import aockt.y2024.Y2024D17.Computer.Instruction.*
import io.github.jadarma.aockt.core.Solution

object Y2024D17 : Solution {

    /**
     * A three-bit computer virtual machine
     * @property regA The value of the A register.
     * @property regB The value of the B register.
     * @property regC The value of the C register.
     * @property ip   The instruction pointer.
     */
    private class Computer {

        /** Computer instruction set. */
        enum class Instruction { ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV }

        var regA: Long = 0L; private set
        var regB: Long = 0L; private set
        var regC: Long = 0L; private set
        var ip: Int = 0; private set

        /** Update the register memory with these overrides. Returns a reference to the same computer. */
        fun memSet(regA: Long = this.regA, regB: Long = this.regB, regC: Long = this.regC): Computer = apply {
            this.regA = regA
            this.regB = regB
            this.regC = regC
        }

        /**
         * Resets the [ip] to zero, then executes the [program] and returns the output values as a lazy sequence.
         * The [haltLimit] is used to allow graceful shutdown if the program executed too many instructions between
         * printing values. In that case, the last element will be a -1, which cannot occur naturally.
         */
        fun execute(program: List<Int>, haltLimit: Int = program.size): Sequence<Int> = sequence {
            require(program.all { it in 0..7 }) { "Invalid program. All inputs must be in 0..7." }

            var halt = 0
            ip = 0

            while (ip < program.size) {
                if (ip == program.size - 1) break
                if (halt++ > haltLimit) {
                    yield(-1)
                    break
                }

                val instruction = Instruction.entries[program[ip++]]
                val operand = program[ip++]

                @Suppress("ReplaceWithOperatorAssignment")
                when (instruction) {
                    ADV -> regA = regA / (1 shl (if (operand == 5) regB else operand).toInt())
                    BDV -> regB = regA / (1 shl (if (operand == 5) regB else operand).toInt())
                    CDV -> regC = regA / (1 shl (if (operand == 5) regB else operand).toInt())
                    BXL -> regB = regB xor operand.toLong()
                    BXC -> regB = regB xor regC
                    BST -> regB = comboOperand(operand)
                    JNZ -> if (regA != 0L) ip = operand
                    OUT -> yield(comboOperand(operand).toInt()).also { halt = 0 }
                }
            }
        }

        /** Get the value of a combo [operand]. This value will always be a three bit number. */
        private fun comboOperand(operand: Int): Long = when (operand) {
            in 0..3 -> operand.toLong()
            4 -> regA and 0b111
            5 -> regB and 0b111
            6 -> regC and 0b111
            7 -> error("Combo operand 7 is reserved for future releases.")
            else -> error("Impossible state")
        }
    }

    /** Parse the [input] and return the initial register values and the program instructions. */
    private fun parseInput(input: String): Pair<Triple<Long, Long, Long>, List<Int>> = parse {
        val regex = Regex("""Register A: (\d+)\nRegister B: (\d+)\nRegister C: (\d+)\n\nProgram: (\d(?:,\d)*)""")
        val (a, b, c, program) = regex.matchEntire(input)!!.destructured
        Triple(a.toLong(), b.toLong(), c.toLong()) to program.split(',').map { it.single().digitToInt(radix = 8) }
    }

    override fun partOne(input: String): String =
        parseInput(input)
            .let { (reg, program) -> Computer().memSet(reg.first, reg.second, reg.third).execute(program) }
            .joinToString(separator = ",")

    override fun partTwo(input: String): Long {
        val (reg, program) = parseInput(input)
        val computer = Computer()

        /**
         * Find three bits that, when appended to [regA], cause the original program to print the desired [output] as
         * its first output.
         * Return a list of all such values appended to regA, or an empty list if no candidates found.
         */
        fun findCandidateBits(regA: Long, output: Int): List<Long> = buildList {
            for (bits in 0b000L..0b111L) {
                val candidate = (regA shl 3) or bits
                val isMatch = computer.memSet(candidate, reg.second, reg.third).execute(program).firstOrNull() == output
                if (isMatch) add(candidate)
            }
        }

        // Reverse engineering the program, it turns out that the output is dictated by the A register.
        // Based on the A register, a value is computed and printed, then A is shifted to the right three places, and
        // the process repeats; the last printed value occurs when A has only 3 bits left.
        // Therefore, we can find candidate A registers by taking the program in reverse order, and appending
        // brute-forced three bit values such that when executing it, the first output is the desired instruction.
        // At the end, we might have multiple solutions, we care for the smallest one.
        return program
            .asReversed()
            .fold(listOf(0L)) { candidates, instruction -> candidates.flatMap { findCandidateBits(it, instruction) } }
            .minOrNull() ?: -1
    }
}
