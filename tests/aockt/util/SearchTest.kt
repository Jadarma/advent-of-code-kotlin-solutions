package aockt.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.math.abs

class SearchTest : FunSpec({

    /**
     * A test graph that encodes the following maze, each move has a constant cost of 1.
     *  ```text
     *  #####################
     *  #(A)# B   C   D # E #
     *  #   #   #####   #####
     *  # F # G # H   I # J #
     *  #   #   #####   #   #
     *  # K # L # M   N   O #
     *  #   #   #########   #
     *  # P   Q   R # S   T #
     *  #   #   #########   #
     *  # U # V   W   X  (Y)#
     *  #####################
     *  ```
     */
    val testGraph = object : Graph<Char> {

        private val edges = mapOf(
            //@formatter:off
            'A' to "F",   'B' to "CG",   'C' to "BD", 'D' to "CI",  'E' to "",
            'F' to "AK",  'G' to "BL",   'H' to "I",  'I' to "DHN", 'J' to "O",
            'K' to "FP",  'L' to "GQ",   'M' to "N",  'N' to "IMO", 'O' to "JNT",
            'P' to "KQU", 'Q' to "LPRV", 'R' to "Q",  'S' to "T",   'T' to "OSY",
            'U' to "P",   'V' to "QW",   'W' to "VX", 'X' to "WY",  'Y' to "TX",
            //@formatter:on
        )

        fun manhattanBetween(a: Char, b: Char): Int {
            fun coordsOf(char: Char): Pair<Int, Int> {
                require(char in 'A'..'Z')
                val value = char - 'A'
                return value / 5 to value % 5
            }

            val aa = coordsOf(a)
            val bb = coordsOf(b)
            return abs(aa.first - bb.first) + abs(aa.second - bb.second)
        }

        override fun neighboursOf(node: Char): List<Pair<Char, Int>> = edges[node].orEmpty().map { it to 1 }
    }

    context("A maze search") {
        test("finds the shortest path") {
            val result = testGraph.search('A') { it == 'Y' }

            result.startedFrom shouldBe 'A'
            result.destination shouldBe 'Y'

            with(result.pathTo('Y')) {
                shouldNotBeNull()
                path.joinToString(separator = "") shouldBe "AFKPQVWXY"
                cost shouldBe 8
            }

            // Since no heuristic is used, the G node should have been visited.
            with(result.pathTo('G')) {
                shouldNotBeNull()
                path.joinToString(separator = "") shouldBe "AFKPQLG"
                cost shouldBe 6
            }
        }

        test("can tell when there is no path") {
            val search = testGraph.search('A') { it == 'E' }
            search.path().shouldBeNull()
            search.pathTo('E').shouldBeNull()
        }

        test("does not visit inefficient nodes when using a good heuristic") {
            val search = testGraph.search(
                start = 'A',
                onVisited = { check(it != 'G') }, // The G node should be optimised out because of the heuristic.
                heuristic = { testGraph.manhattanBetween(it, 'Y') },
                goalFunction = { it == 'Y' },
            )

            with(search.path()) {
                shouldNotBeNull()
                path.joinToString(separator = "") shouldBe "AFKPQVWXY"
                cost shouldBe 8
            }
        }
    }
})
