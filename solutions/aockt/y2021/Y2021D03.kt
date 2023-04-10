package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D03 : Solution {

    /**
     * Given a list of numbers, generates a mask number based on the [predicate].
     * @param mask The mask of relevant bits to keep. Should always be 2^n - 1, where n is the numbers of bits in use.
     * @param predicate The function that decides what the bit value for a position would be. It takes it the ratio of
     *                  set bits for any particular column.
     */
    private fun List<ULong>.generatePopCountFilter(
        mask: ULong,
        predicate: (popCountRatio: Double) -> Boolean,
    ): ULong =
        fold(MutableList(ULong.SIZE_BITS) { 0 }) { acc, number ->
            acc.apply {
                indices.forEach { i -> acc[i] += number.shr(ULong.SIZE_BITS - i - 1).and(1uL).toInt() }
            }
        }
            .let { count -> BooleanArray(ULong.SIZE_BITS) { predicate(count[it] / size.toDouble()) } }
            .joinToString(separator = "") { if (it) "1" else "0" }
            .toULong(2)
            .and(mask)

    /**
     * Given a list of numbers, generates a mask number based on the [predicate] similar to [generatePopCountFilter],
     * but instead of taking all columns at once, each column filters the candidates of the others, starting from most
     * significant bit to least significant.
     * @param mask The mask of relevant bits to keep. Should always be 2^n - 1, where n is the numbers of bits in use.
     * @param predicate The function that decides what the bit value for a position would be. It takes it the ratio of
     *                  set bits for any particular column.
     */
    private fun List<ULong>.generateIterativePopCountFilter(
        mask: ULong,
        predicate: (popCountRatio: Double) -> Boolean,
    ): ULong {
        require(isNotEmpty()) { "Nothing to filter." }
        require(mask.countOneBits() + mask.countLeadingZeroBits() == ULong.SIZE_BITS) { "Invalid mask." }
        val binaryColumns = mask.countOneBits()
        val candidates = toMutableList()

        for (index in 0 until binaryColumns) {
            val indexMask = 1uL shl (ULong.SIZE_BITS - mask.countLeadingZeroBits() - index - 1)
            if (candidates.size <= 1) break
            val filter = candidates.generatePopCountFilter(mask, predicate)
            candidates.removeAll { (it xor filter and indexMask) != 0uL }
        }

        require(candidates.isNotEmpty()) { "No candidate satisfies the filter." }
        return candidates.first()
    }

    /**
     * Returns the bit mask (relevant binary columns) and the list of numbers read from the binary diagnostics report.
     * The mask is important because depending on what [input] you use, you might use different sized binary numbers.
     */
    private fun parseInput(input: String): Pair<ULong, List<ULong>> {
        var numberLength = -1
        val numbers = input
            .lineSequence()
            .onEach { if (numberLength == -1) numberLength = it.length else require(it.length == numberLength) }
            .map { it.toULongOrNull(2) ?: throw IllegalArgumentException() }
            .toList()
        require(numberLength <= ULong.SIZE_BITS) { "This machine is not compatible with extra wide diagnostics." }
        return ULong.MAX_VALUE.shr(ULong.SIZE_BITS - numberLength) to numbers
    }

    override fun partOne(input: String): Any {
        val (mask, numbers) = parseInput(input)
        val epsilon = numbers.generatePopCountFilter(mask) { it >= 0.5 }
        val gamma = numbers.generatePopCountFilter(mask) { it < 0.5 }
        return epsilon * gamma
    }

    override fun partTwo(input: String): Any {
        val (mask, numbers) = parseInput(input)
        val o2gen = numbers.generateIterativePopCountFilter(mask) { it >= 0.5 }
        val co2Scrub = numbers.generateIterativePopCountFilter(mask) { it < 0.5 }
        return o2gen * co2Scrub
    }
}
