package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 25, "Let it Snow")
class Y2015D25Test : AdventSpec<Y2015D25>({
    val exampleInput = "To continue, please consult the code grid in the manual. Enter the code at row 123, column 456."
    partOne { exampleInput shouldOutput 32_582_479 }
})
