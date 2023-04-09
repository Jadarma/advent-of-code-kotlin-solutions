package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 7, "Internet Protocol Version 7")
class Y2016D07Test : AdventSpec<Y2016D07>({

    partOne {
        val exampleInput =
            """
            abba[mnop]qrst
            abcd[bddb]xyyx
            aaaa[qwer]tyui
            ioxxoj[asdfgh]zxcvbn
            """.trimIndent()
        exampleInput shouldOutput 2
    }

    partTwo {
        val exampleInput =
            """
            aba[bab]xyz
            xyx[xyx]xyx
            aaa[kek]eke
            zazbz[bzb]cdb
            """.trimIndent()
        exampleInput shouldOutput 3
    }
})
