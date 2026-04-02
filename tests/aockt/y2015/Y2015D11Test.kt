package aockt.y2015

import io.github.jadarma.aockt.AdventDay
import io.github.jadarma.aockt.AdventSpec

@AdventDay(2015, 11, "Corporate Policy")
class Y2015D11Test : AdventSpec<Y2015D11>({

    partOne {
        "abcdefgh" shouldOutput "abcdffaa"
        "ghijklmn" shouldOutput "ghjaabcc"
    }
    partTwo {
        "abcdefgh" shouldOutput "abcdffbb"
        "ghijklmn" shouldOutput "ghjbbcdd"
    }
})
