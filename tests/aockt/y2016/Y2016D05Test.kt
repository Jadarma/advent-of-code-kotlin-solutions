package aockt.y2016

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2016, 5, "How About a Nice Game of Chess?")
class Y2016D05Test : AdventSpec<Y2016D05>({
    partOne { "abc" shouldOutput "18f47a30" }
    partTwo { "abc" shouldOutput "05ace8e3" }
})
