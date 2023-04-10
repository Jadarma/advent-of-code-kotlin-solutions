package aockt.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

@DisplayName("Utils: Collections")
class CollectionsUtilTest : FunSpec({

    context("Permutations") {
        test("generate simple permutations correctly") {
            val list = listOf(1, 2, 3)
            val permutations = listOf(
                listOf(1, 2, 3),
                listOf(1, 3, 2),
                listOf(2, 1, 3),
                listOf(2, 3, 1),
                listOf(3, 1, 2),
                listOf(3, 2, 1),
            )

            list.generatePermutations().toList() shouldContainExactlyInAnyOrder permutations
        }

        test("only work for small collections") {
            val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
            shouldThrow<Exception> { list.generatePermutations() }
        }
    }

    context("Power Sets") {
        test("generate simple power sets") {
            val items = listOf(1, 2, 3)
            val powerSet = listOf(
                emptyList(),
                listOf(1),
                listOf(2),
                listOf(3),
                listOf(1, 2),
                listOf(1, 3),
                listOf(2, 3),
                listOf(1, 2, 3),
            )
            items.powerSet() shouldContainExactlyInAnyOrder powerSet
        }
        test("work with duplicate items") {
            val items = listOf(1, 1)
            val powerSet = listOf(
                emptyList(),
                listOf(1),
                listOf(1),
                listOf(1, 1),
            )
            items.powerSet() shouldContainExactlyInAnyOrder powerSet
        }
    }
})
