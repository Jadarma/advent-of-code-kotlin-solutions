package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D20 : Solution {

    /**
     * Represents a finite slice of an infinite image, with a maximum size of 2^15 * 2^15 pixels.
     * @property width The finite width of this slice.
     * @property height The finite height of this slice.
     * @property defaultValue The value of pixels outside the bounds of this slice.
     */
    private class InfiniteImageSlice(
        val width: Int,
        val height: Int,
        val defaultValue: Boolean = false,
        private val imageData: BooleanArray,
    ) {
        init {
            require(width > 0 && height > 0) { "Image dimensions need to be positive." }
            require(width <= Short.MAX_VALUE && height <= Short.MAX_VALUE) { "Image maximum size exceeded." }
            require(imageData.size == width * height) { "Image dimensions and data size do not match." }
        }

        operator fun get(x: Int, y: Int): Boolean =
            if (x in 0 until height && y in 0 until width) imageData[x * width + y] else defaultValue

        operator fun set(x: Int, y: Int, value: Boolean) {
            if (x in 0 until height && y in 0 until width) imageData[x * width + y] = value
        }

        /** Returns the number of set pixels in this slice (ignoring the surrounding infinity). */
        fun countSetPixels(): Int = imageData.count { it }
    }

    /** An advanced image enhancement algorithm able to process slices of infinite images. */
    private class ImageEnhancer(enhancementData: BooleanArray) {
        private val enhancement = enhancementData.copyOf()

        init {
            require(enhancement.size == 512) { "Not enough enhancement values." }
        }

        /** Process the [image] and return a new enhanced copy. */
        fun enhance(image: InfiniteImageSlice): InfiniteImageSlice = InfiniteImageSlice(
            width = image.width + 2,
            height = image.height + 2,
            defaultValue = if (enhancement[0]) !image.defaultValue else image.defaultValue,
            imageData = BooleanArray((image.width + 2) * (image.height + 2)),
        ).apply {
            for (x in 0 until height) {
                for (y in 0 until width) {
                    val kernelValue = buildString(9) {
                        for (i in -1..1) {
                            for (j in -1..1) {
                                append(if (image[x + i - 1, y + j - 1]) '1' else '0')
                            }
                        }
                    }.toInt(2)
                    this[x, y] = enhancement[kernelValue]
                }
            }
        }
    }

    /** Parse the [input] and return the [ImageEnhancer] algorithm instance and the initial [InfiniteImageSlice]. */
    private fun parseInput(input: String): Pair<ImageEnhancer, InfiniteImageSlice> = runCatching {
        val lines = input.lineSequence().iterator()
        val enhancementData = lines.next().map { it == '#' }.toBooleanArray()
        lines.next()
        var width = -1
        var height = 0
        val imageData = lines.asSequence()
            .onEach { if (width == -1) width = it.length else require(it.length == width) }
            .onEach { height++ }
            .flatMap { line -> line.map { it == '#' } }
            .toList()
            .toBooleanArray()
        ImageEnhancer(enhancementData) to InfiniteImageSlice(width, height, false, imageData)
    }.getOrElse { throw IllegalArgumentException("Invalid input.", it) }

    override fun partOne(input: String): Any {
        val (enhancement, image) = parseInput(input)
        val enhanced = (1..2).fold(image) { acc, _ -> enhancement.enhance(acc) }
        return enhanced.countSetPixels()
    }

    override fun partTwo(input: String): Any {
        val (enhancement, image) = parseInput(input)
        val enhanced = (1..50).fold(image) { acc, _ -> enhancement.enhance(acc) }
        return enhanced.countSetPixels()
    }
}
