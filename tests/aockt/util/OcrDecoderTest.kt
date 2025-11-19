package aockt.util

import io.kotest.core.annotation.DisplayName
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
        """.trimIndent()

        OcrDecoder.decode(rawLetters) shouldBe "ABCEFGHIJKLOPRSUYZ"
    }

})
