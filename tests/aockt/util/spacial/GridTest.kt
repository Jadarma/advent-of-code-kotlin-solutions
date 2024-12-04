package aockt.util.spacial

import aockt.util.spacial.Direction.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class GridTest : FunSpec({

    val exampleInput = """
        abcdefg
        hijklmn
        opqrstu
        vwxyz01
    """.trimIndent()

    context("Constructors") {
        test("can parse string") {
            val gridString = Grid(exampleInput)
            val gridCustomString = Grid(exampleInput) { it }
            gridString
                .shouldBeEqual(gridCustomString)
                .shouldNotBeSameInstanceAs(gridCustomString)
        }

        test("can create manually") {
            val gridA = Grid(3, 3) { x, y -> x + y }
            val gridB = Grid(3, 3) { x, y -> x + y }
            gridA
                .shouldBeEqual(gridB)
                .shouldNotBeSameInstanceAs(gridB)
        }
    }

    context("Indexing") {
        val grid = Grid(exampleInput)

        test("by coordinates") {
            grid[0, 0] shouldBe 'v'
            grid[3, 0] shouldBe 'y'
            grid[0, 2] shouldBe 'h'
            grid[1, 2] shouldBe 'i'
        }

        test("by point") {
            grid[Point(0, 0)] shouldBe 'v'
            grid[Point(3, 0)] shouldBe 'y'
            grid[Point(0, 2)] shouldBe 'h'
            grid[Point(1, 2)] shouldBe 'i'
        }
    }

    context("Iterating") {
        val grid = Grid(exampleInput)

        test("in a direction") {
            listOf(
                Triple(Point(0, 0), Left, "v"),
                Triple(Point(0, 0), Right, "vwxyz01"),
                Triple(Point(0, 0), Up, "voha"),
                Triple(Point(0, 0), Down, "v"),
                Triple(Point(5, 2), Left, "mlkjih"),
                Triple(Point(5, 2), Right, "mn"),
                Triple(Point(5, 2), Up, "mf"),
                Triple(Point(5, 2), Down, "mt0"),
            ).forEach { (start, direction, expected) ->
                grid.move(start, direction)
                    .joinToString("") { it.value.toString() }
                    .shouldBe(expected)
            }
        }
    }

    context("Rendering") {
        val grid = Grid(exampleInput)

        test("fully") {
            grid.render { it } shouldBe exampleInput
        }

        test("partially") {
            grid.render(3, 2) { it } shouldBe """
                ⋮⋮⋮⋮⋰
                hijk…
                opqr…
                vwxy…
            """.trimIndent()
        }
    }
})
