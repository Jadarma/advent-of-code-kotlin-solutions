package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 20, "Pulse Propagation")
class Y2023D20Test : AdventSpec<Y2023D20>({

    val exampleOne = """
        broadcaster -> a, b, c
        %a -> b
        %b -> c
        %c -> inv
        &inv -> a
    """.trimIndent()

    val exampleTwo = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
    """.trimIndent()

    partOne {
        exampleOne shouldOutput 32_000_000
        exampleTwo shouldOutput 11_687_500
    }

    partTwo()
})
