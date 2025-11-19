package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 12, "JSAbacusFramework(dot)io")
class Y2015D12Test : AdventSpec<Y2015D12>({

    partOne {
        listOf("""[1,2,3]""", """{"a":2,"b":4}""") shouldAllOutput 6
        listOf("""[[[3]]]""", """{"a":{"b":4},"c":-1}""") shouldAllOutput 3
        listOf("""{"a":[-1,1]}""", """[-1,{"a":1}]""", "[]", "{}") shouldAllOutput 0
    }

    partTwo {
        """{"d":"red","e":[1,2,3,4],"f":5}""" shouldOutput 0
        """[1,{"c":"red","b":2},3]""" shouldOutput 4
        listOf("""[1,2,3]""", """[1,"red",5]""") shouldAllOutput 6
    }
})
