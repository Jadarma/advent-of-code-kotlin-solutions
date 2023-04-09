package aockt.util

import io.github.jadarma.aockt.test.AdventSpec
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DisplayName("Util: OCR")
class OcrDecoderTest : FunSpec({

    test("Able to decode known letters") {

        val rawLetters = """
            .##..###...##..####.####..##..#..#..###...##.#..#.#.....##..###..###...###.#..#.#...#####.
            #..#.#..#.#..#.#....#....#..#.#..#...#.....#.#.#..#....#..#.#..#.#..#.#....#..#.#...#...#.
            #..#.###..#....###..###..#....####...#.....#.##...#....#..#.#..#.#..#.#....#..#..#.#...#..
            ####.#..#.#....#....#....#.##.#..#...#.....#.#.#..#....#..#.###..###...##..#..#...#...#...
            #..#.#..#.#..#.#....#....#..#.#..#...#..#..#.#.#..#....#..#.#....#.#.....#.#..#...#..#....
            #..#.###...##..####.#.....###.#..#..###..##..#..#.####..##..#....#..#.###...##....#..####.
        """.trimIndent().also { println(it.lineSequence().first().length) }

        OcrDecoder.decode(rawLetters) shouldBe "ABCEFGHIJKLOPRSUYZ"
    }

})
