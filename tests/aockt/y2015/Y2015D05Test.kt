package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 5, "Doesn't He Have Intern-Elves For This?")
class Y2015D05Test : AdventSpec<Y2015D05>({

    partOne {
        listOf("ugknbfddgicrmopn", "aaa") shouldAllOutput 1
        listOf("jchzalrnumimnmhp", "haegwjzuvuyypxyu", "dvszwmarrgswjxmb") shouldAllOutput 0
    }

    partTwo {
        listOf("qjhvhtzxzqqjkmpb", "xxyxx") shouldAllOutput 1
        listOf("uurcxstgmygtbstg", "ieodomkazucvgmuy") shouldAllOutput 0
    }
})
