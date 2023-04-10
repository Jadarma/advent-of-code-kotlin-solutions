package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 12, "Passage Pathing")
class Y2021D12Test : AdventSpec<Y2021D12>({

    val smallExample = """
        start-A
        start-b
        A-c
        A-b
        b-d
        A-end
        b-end
        """.trimIndent()

    val largerExample = """
        dc-end
        HN-start
        start-kj
        dc-start
        dc-HN
        LN-dc
        HN-end
        kj-sa
        kj-HN
        kj-dc
    """.trimIndent()

    val largestExample = """
        fs-end
        he-DX
        fs-he
        start-DX
        pj-DX
        end-zg
        zg-sl
        zg-pj
        pj-he
        RW-he
        fs-DX
        pj-RW
        zg-RW
        start-pj
        he-WI
        zg-he
        pj-fs
        start-RW
    """.trimIndent()

    partOne {
        smallExample shouldOutput 10
        largerExample shouldOutput 19
        largestExample shouldOutput 226
    }

    partTwo {
        smallExample shouldOutput 36
        largerExample shouldOutput 103
        largestExample shouldOutput 3509
    }
})
