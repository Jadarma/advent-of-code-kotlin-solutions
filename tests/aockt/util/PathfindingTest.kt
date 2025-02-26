package aockt.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.math.abs

class PathfindingTest : FunSpec({

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
    val edges = mapOf(
        //@formatter:off
        'A' to "F",   'B' to "CG",   'C' to "BD", 'D' to "CI",  'E' to "",
        'F' to "AK",  'G' to "BL",   'H' to "I",  'I' to "DHN", 'J' to "O",
        'K' to "FP",  'L' to "GQ",   'M' to "N",  'N' to "IMO", 'O' to "JNT",
        'P' to "KQU", 'Q' to "LPRV", 'R' to "Q",  'S' to "T",   'T' to "OSY",
        'U' to "P",   'V' to "QW",   'W' to "VX", 'X' to "WY",  'Y' to "TX",
        //@formatter:on
    )
    val neighbours: (Char) -> Iterable<Pair<Char, Int>> = { node: Char -> edges[node].orEmpty().map { it to 1 } }

    context("A maze search") {
        test("finds the shortest path") {
            val visited = mutableSetOf<Char>()
            val result = Pathfinding.search(
                start = 'A',
                neighbours = neighbours,
                onVisit = { visited += it },
                goalFunction = { it == 'Y' },
                trackPath = true,
            )

            with(result) {
                shouldNotBeNull()
                start shouldBe 'A'
                end shouldBe 'Y'
                cost shouldBe 8
                path.joinToString("") { it.first.toString() } shouldBe "AFKPQVWXY"
            }

            // Since no heuristic is used, the G node should have been visited.
            visited shouldContain 'G'
        }

        test("does not compute path unless specified") {
            shouldThrow<IllegalStateException> {
                Pathfinding
                    .search(
                        start = 'A',
                        neighbours = neighbours,
                        goalFunction = { it == 'Y' },
                        trackPath = false,
                    )
                    .shouldNotBeNull()
                    .path
            }
        }

        test("can tell when there is no path") {
            val result = Pathfinding.search(
                start = 'A',
                neighbours = neighbours,
                goalFunction = { it == 'E' },
            )
            result.shouldBeNull()
        }

        test("respects maximum cost") {
            val result = Pathfinding.search(
                start = 'A',
                neighbours = neighbours,
                maximumCost = 4,
                goalFunction = { it == 'Y' },
            )
            result.shouldBeNull()
        }

        test("does not visit inefficient nodes when using a good heuristic") {
            // Manhattan distance between points.
            val heuristic = { node: Char ->
                fun coordsOf(char: Char): Pair<Int, Int> {
                    require(char in 'A'..'Z')
                    val value = char - 'A'
                    return value / 5 to value % 5
                }

                val aa = coordsOf(node)
                val bb = coordsOf('Y')
                abs(aa.first - bb.first) + abs(aa.second - bb.second)
            }

            val visited = mutableSetOf<Char>()
            val result = Pathfinding.search(
                start = 'A',
                neighbours = neighbours,
                onVisit = { visited += it },
                goalFunction = { it == 'Y' },
                heuristic = heuristic,
            )

            with(result) {
                shouldNotBeNull()
                start shouldBe 'A'
                end shouldBe 'Y'
            }
            // The G node should be optimised out because of the heuristic.
            visited shouldNotContain 'G'
        }
    }
})
