package aockt.y2022

import io.github.jadarma.aockt.core.Solution

object Y2022D06 : Solution {

    /**
     * Given a communication's device stream as [input] and a target [bufferSize], returns the index (1-based) of the
     * start-of-packet market, or 0 if no such marker was found.
     */
    private fun lockOnSignal(input: String, bufferSize: Int): Int {
        require(bufferSize > 0) { "Buffer size must be positive." }
        if (input.length < bufferSize) return 0
        if (bufferSize == 1) return 1
        var index = bufferSize
        input.asSequence().windowed(bufferSize).forEach { buffer ->
            if (buffer.toSet().size == bufferSize) return index
            index++
        }
        return -1
    }

    override fun partOne(input: String) = lockOnSignal(input, 4)
    override fun partTwo(input: String) = lockOnSignal(input, 14)
}
