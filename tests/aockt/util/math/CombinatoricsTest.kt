package aockt.util.math

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll

@DisplayName("Util > Math: Combinatorics")
class CombinatoricsTest : FunSpec({

    context("Distinct Pairs") {
        test("Produces correct output") {
            val expected = listOf(1 to 2, 1 to 3, 2 to 3, 1 to 4, 2 to 4, 3 to 4)
            val actual = listOf(1,2,3,4).distinctPairs().toList()
            actual shouldContainAll expected
        }
    }
})
