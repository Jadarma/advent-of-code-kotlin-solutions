package aockt.util

/** An util for translating the visual answers on some puzzles to its text value. */
object OcrDecoder {

    /**
     * Translation from the ASCII art (including the padding) to the letter.
     * Unknown values for: D, M, N, Q, T, V, W, and X.
     */
    private val fontMap: Map<String, Char> = mapOf(
        ".##..\n#..#.\n#..#.\n####.\n#..#.\n#..#." to 'A',
        "###..\n#..#.\n###..\n#..#.\n#..#.\n###.." to 'B',
        ".##..\n#..#.\n#....\n#....\n#..#.\n.##.." to 'C',
        "####.\n#....\n###..\n#....\n#....\n####." to 'E',
        "####.\n#....\n###..\n#....\n#....\n#...." to 'F',
        ".##..\n#..#.\n#....\n#.##.\n#..#.\n.###." to 'G',
        "#..#.\n#..#.\n####.\n#..#.\n#..#.\n#..#." to 'H',
        ".###.\n..#..\n..#..\n..#..\n..#..\n.###." to 'I',
        "..##.\n...#.\n...#.\n...#.\n#..#.\n.##.." to 'J',
        "#..#.\n#.#..\n##...\n#.#..\n#.#..\n#..#." to 'K',
        "#....\n#....\n#....\n#....\n#....\n####." to 'L',
        ".##..\n#..#.\n#..#.\n#..#.\n#..#.\n.##.." to 'O',
        "###..\n#..#.\n#..#.\n###..\n#....\n#...." to 'P',
        "###..\n#..#.\n#..#.\n###..\n#.#..\n#..#." to 'R',
        ".###.\n#....\n#....\n.##..\n...#.\n###.." to 'S',
        "#..#.\n#..#.\n#..#.\n#..#.\n#..#.\n.##.." to 'U',
        "#...#\n#...#\n.#.#.\n..#..\n..#..\n..#.." to 'Y',
        "####.\n...#.\n..#..\n.#...\n#....\n####." to 'Z',
    )

    /**
     * Attempts to decode the [text] from an ASCII art format of 6x5 glyphs into their string representation.
     * If a glyph is not recognized, it will be replaced by a '?' instead.
     * The answer is always in uppercase.
     */
    fun decode(text: String): String {
        val lines = text.removeSuffix("\n").lines()
        require(lines.size == 6) { "AoC OCR only works on characters that are six pixels tall." }
        require(lines.all { it.length % 5 == 0 }) { "AoC OCR only works on characters that are 5 wide." }

        fun readCharAt(index: Int) = lines
            .joinToString(separator = "\n") { it.slice(index * 5 until ((index + 1) * 5)) }
            .let { fontMap[it] ?: '?' }

        val length = lines.first().length / 5

        return buildString(length) {
            (0 until length)
                .map(::readCharAt)
                .forEach(::append)
        }
    }
}
