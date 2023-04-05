package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 1, "Not Quite Lisp")
class Y2015D01Test : AdventSpec<Y2015D01>({

    partOne {
        listOf("(())", "()()") shouldAllOutput 0
        listOf("(((", "(()(()(") shouldAllOutput 3
        listOf("())", "))(") shouldAllOutput -1
        listOf(")))", ")())())") shouldAllOutput -3
    }

    partTwo {
        ")" shouldOutput 1
        "()())" shouldOutput 5
    }
})
