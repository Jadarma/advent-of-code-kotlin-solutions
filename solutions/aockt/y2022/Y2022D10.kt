package aockt.y2022

import aockt.util.OcrDecoder
import io.github.jadarma.aockt.core.Solution

object Y2022D10 : Solution {

    /** The possible instructions of an elven handheld device's CPU. */
    private sealed interface Instruction {
        object NOOP : Instruction
        data class AddX(val arg: Int) : Instruction
    }

    /** Parses the [input] and returns the program as a sequence of [Instruction]s. */
    private fun parseInput(input: String): Sequence<Instruction> =
        input
            .lineSequence()
            .map { line ->
                if (line == "noop") Instruction.NOOP else {
                    require(line.startsWith("addx ")) { "Invalid instruction: $line" }
                    Instruction.AddX(line.substringAfter(' ').toInt())
                }
            }

    /** Emulate the execution of a program and return the state of register X for each clock cycle (starting from 0). */
    private fun Sequence<Instruction>.emulateExecution(): Sequence<Int> = sequence {
        var register = 1
        var cycle = 0
        yield(register)
        forEach { instruction ->
            when (instruction) {
                Instruction.NOOP -> {
                    cycle++
                    yield(register)
                }

                is Instruction.AddX -> {
                    cycle += 2
                    yield(register)
                    yield(register)
                    register += instruction.arg
                }
            }
        }
        yield(register)
    }

    /** Given the state progression of the X register, renders a 40x6 image to a string buffer. */
    private fun Sequence<Int>.emulateCrt(): String = buildString(245) {
        var drawingIndex = 1
        this@emulateCrt.take(240).forEach { x: Int ->
            val horizontalPosition = drawingIndex.dec().rem(40)
            if (drawingIndex >= 40 && horizontalPosition == 0) appendLine()
            if (horizontalPosition in x.dec()..x.inc()) append('#') else append('.')
            drawingIndex++
        }
    }

    override fun partOne(input: String) =
        parseInput(input)
            .emulateExecution()
            .mapIndexedNotNull { clock, x -> (clock * x).takeIf { clock.minus(20).rem(40) == 0 } }
            .sum()

    override fun partTwo(input: String) =
        parseInput(input)
            .emulateExecution()
            .drop(1)
            .emulateCrt()
            .let(OcrDecoder::decode)
}
