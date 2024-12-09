package aockt.y2024

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2024D09 : Solution {

    /**
     * A representation of an amphipod's storage drive.
     *
     * @param diskMap The disk representation as digit pairs describing pages, how much is used, and how much is free.
     */
    private class DiskPartitionTable(diskMap: String) {

        /**
         * A fragment of a file. The disk is composed of multiple contiguous pages.
         *
         * @property id The ID of the file.
         * @property used The size occupied by the file fragment.
         * @property free The unused space until the next page begins.
         * @property size The total size on disk of the page.
         */
        private data class Page(val id: Int, val used: Int, val free: Int) {
            val size: Int get() = used + free
        }

        /** The disk layout, represented as a list of variably-sized pages. */
        private val data: ArrayList<Page> = ArrayList<Page>(diskMap.length.let { it / 2 + it % 2 }).apply {
            var fileId = 0
            diskMap
                .asSequence()
                .map(Char::digitToInt)
                .chunked(2) { Page(fileId++, it[0], it.getOrElse(1) { 0 }) }
                .forEach(::addLast)
        }

        /** Calculates the disk partition checksum. */
        fun checksum(): Long {
            var sum = 0L
            var block = 0

            data.forEach { (id, used, free) ->
                repeat(used) { sum += id * block++ }
                block += free
            }

            return sum
        }

        /**
         * Optimise the free space on this drive by moving pages around.
         * This is a mutating operation, the return value is a reference to the same instance.
         *
         * If [allowFragmentation] is enabled, produces a long contiguous empty space at the end of the drive at the
         * expense of degraded reading performance on fragmented files.
         */
        fun optimiseSpace(allowFragmentation: Boolean): DiskPartitionTable = apply {
            if (allowFragmentation) {
                var fullUntil = 0
                while (true) {
                    val freeSpace = freeSpaceOrNull(start = fullUntil) ?: return@apply
                    fragmentLast()
                    move(data.lastIndex, freeSpace)
                    fullUntil = freeSpace
                }
            }

            val processed = mutableSetOf<Int>()
            var pageIndex = data.size
            while (pageIndex > 1) {
                pageIndex--
                val page = data[pageIndex]
                if (page.id in processed) continue
                processed.add(page.id)

                val freeSpace = freeSpaceOrNull(minFree = page.used, end = pageIndex) ?: continue
                move(pageIndex, freeSpace)
                pageIndex++
            }
        }

        /** Find the first page in a given range that has enough free space. */
        private fun freeSpaceOrNull(minFree: Int = 1, start: Int = 0, end: Int = data.lastIndex): Int? =
            data
                .subList(start, end)
                .indexOfFirst { it.free >= minFree }
                .takeIf { it != -1 }
                ?.let { start + it }

        /** If the last page uses more than one block, split the last block in its own page. */
        private fun fragmentLast() = with(data) {
            if (last().used <= 1) return
            val page = removeLast()
            addLast(page.copy(used = page.used - 1, free = 0))
            addLast(page.copy(used = 1, free = page.free))
        }

        /** Moves the [source] page into the free space of the [destination] page. */
        private fun move(source: Int, destination: Int) = with(data) {
            require(source > 0) { "Cannot move first page, it is already first." }
            require(destination < source) { "Pages can only be moved to the left." }
            require(destination >= 0) { "Negative indexes are invalid: $destination." }
            require(this[destination].free >= this[source].used) { "Not enough space to perform move." }

            val page = removeAt(source)

            // Expand the free space of the previous page to cover the space of the removed page.
            val previous = removeAt(source - 1)
            add(source - 1, previous.copy(free = previous.free + page.size))

            // Shrink the page at insertion location, placing the page in its free space.
            val insertedPage = removeAt(destination)
            add(destination, insertedPage.copy(free = 0))
            add(destination + 1, Page(page.id, page.used, insertedPage.free - page.used))
        }
    }

    /** Parse the [input] and return the [DiskPartitionTable]. */
    private fun parseInput(input: String): DiskPartitionTable = parse { DiskPartitionTable(input) }

    override fun partOne(input: String) = parseInput(input).optimiseSpace(allowFragmentation = true).checksum()
    override fun partTwo(input: String) = parseInput(input).optimiseSpace(allowFragmentation = false).checksum()
}
