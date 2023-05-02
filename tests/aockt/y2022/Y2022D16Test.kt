package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec
import kotlin.time.Duration.Companion.minutes

/**
 * Extra test inputs for this day taken from `/u/i_have_no_biscuits`'s post here:
 * [reddit post](https://reddit.com/r/adventofcode/comments/znklnh/2022_day_16_some_extra_test_cases_for_day_16/)
 */
@AdventDay(2022, 16, "Proboscidea Volcanium")
class Y2022D16Test : AdventSpec<Y2022D16>({

    val exampleInput = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent()

    val aLinearLine = """
        Valve LA has flow rate=22; tunnels lead to valves KA, MA
        Valve MA has flow rate=24; tunnels lead to valves LA, NA
        Valve NA has flow rate=26; tunnels lead to valves MA, OA
        Valve OA has flow rate=28; tunnels lead to valves NA, PA
        Valve PA has flow rate=30; tunnels lead to valves OA
        Valve AA has flow rate=0; tunnels lead to valves BA
        Valve BA has flow rate=2; tunnels lead to valves AA, CA
        Valve CA has flow rate=4; tunnels lead to valves BA, DA
        Valve DA has flow rate=6; tunnels lead to valves CA, EA
        Valve EA has flow rate=8; tunnels lead to valves DA, FA
        Valve FA has flow rate=10; tunnels lead to valves EA, GA
        Valve GA has flow rate=12; tunnels lead to valves FA, HA
        Valve HA has flow rate=14; tunnels lead to valves GA, IA
        Valve IA has flow rate=16; tunnels lead to valves HA, JA
        Valve JA has flow rate=18; tunnels lead to valves IA, KA
        Valve KA has flow rate=20; tunnels lead to valves JA, LA
    """.trimIndent()

    val aQuadraticLine = """
        Valve AA has flow rate=0; tunnels lead to valves BA
        Valve BA has flow rate=1; tunnels lead to valves AA, CA
        Valve CA has flow rate=4; tunnels lead to valves BA, DA
        Valve DA has flow rate=9; tunnels lead to valves CA, EA
        Valve EA has flow rate=16; tunnels lead to valves DA, FA
        Valve FA has flow rate=25; tunnels lead to valves EA, GA
        Valve GA has flow rate=36; tunnels lead to valves FA, HA
        Valve HA has flow rate=49; tunnels lead to valves GA, IA
        Valve IA has flow rate=64; tunnels lead to valves HA, JA
        Valve JA has flow rate=81; tunnels lead to valves IA, KA
        Valve KA has flow rate=100; tunnels lead to valves JA, LA
        Valve LA has flow rate=121; tunnels lead to valves KA, MA
        Valve MA has flow rate=144; tunnels lead to valves LA, NA
        Valve NA has flow rate=169; tunnels lead to valves MA, OA
        Valve OA has flow rate=196; tunnels lead to valves NA, PA
        Valve PA has flow rate=225; tunnels lead to valves OA
    """.trimIndent()

    val aCircle = """
        Valve BA has flow rate=2; tunnels lead to valves AA, CA
        Valve CA has flow rate=10; tunnels lead to valves BA, DA
        Valve DA has flow rate=2; tunnels lead to valves CA, EA
        Valve EA has flow rate=10; tunnels lead to valves DA, FA
        Valve FA has flow rate=2; tunnels lead to valves EA, GA
        Valve GA has flow rate=10; tunnels lead to valves FA, HA
        Valve HA has flow rate=2; tunnels lead to valves GA, IA
        Valve IA has flow rate=10; tunnels lead to valves HA, JA
        Valve JA has flow rate=2; tunnels lead to valves IA, KA
        Valve KA has flow rate=10; tunnels lead to valves JA, LA
        Valve LA has flow rate=2; tunnels lead to valves KA, MA
        Valve MA has flow rate=10; tunnels lead to valves LA, NA
        Valve NA has flow rate=2; tunnels lead to valves MA, OA
        Valve OA has flow rate=10; tunnels lead to valves NA, PA
        Valve PA has flow rate=2; tunnels lead to valves OA, AA
        Valve AA has flow rate=0; tunnels lead to valves BA, PA
    """.trimIndent()

    val clusters = """
        Valve AK has flow rate=100; tunnels lead to valves AJ, AW, AX, AY, AZ
        Valve AW has flow rate=10; tunnels lead to valves AK
        Valve AX has flow rate=10; tunnels lead to valves AK
        Valve AY has flow rate=10; tunnels lead to valves AK
        Valve AZ has flow rate=10; tunnels lead to valves AK
        Valve BB has flow rate=0; tunnels lead to valves AA, BC
        Valve BC has flow rate=0; tunnels lead to valves BB, BD
        Valve BD has flow rate=0; tunnels lead to valves BC, BE
        Valve BE has flow rate=0; tunnels lead to valves BD, BF
        Valve BF has flow rate=0; tunnels lead to valves BE, BG
        Valve BG has flow rate=0; tunnels lead to valves BF, BH
        Valve BH has flow rate=0; tunnels lead to valves BG, BI
        Valve BI has flow rate=0; tunnels lead to valves BH, BJ
        Valve BJ has flow rate=0; tunnels lead to valves BI, BK
        Valve BK has flow rate=100; tunnels lead to valves BJ, BW, BX, BY, BZ
        Valve BW has flow rate=10; tunnels lead to valves BK
        Valve BX has flow rate=10; tunnels lead to valves BK
        Valve BY has flow rate=10; tunnels lead to valves BK
        Valve BZ has flow rate=10; tunnels lead to valves BK
        Valve CB has flow rate=0; tunnels lead to valves AA, CC
        Valve CC has flow rate=0; tunnels lead to valves CB, CD
        Valve CD has flow rate=0; tunnels lead to valves CC, CE
        Valve CE has flow rate=0; tunnels lead to valves CD, CF
        Valve CF has flow rate=0; tunnels lead to valves CE, CG
        Valve CG has flow rate=0; tunnels lead to valves CF, CH
        Valve CH has flow rate=0; tunnels lead to valves CG, CI
        Valve CI has flow rate=0; tunnels lead to valves CH, CJ
        Valve CJ has flow rate=0; tunnels lead to valves CI, CK
        Valve CK has flow rate=100; tunnels lead to valves CJ, CW, CX, CY, CZ
        Valve CW has flow rate=10; tunnels lead to valves CK
        Valve CX has flow rate=10; tunnels lead to valves CK
        Valve CY has flow rate=10; tunnels lead to valves CK
        Valve CZ has flow rate=10; tunnels lead to valves CK
        Valve AA has flow rate=0; tunnels lead to valves AB, BB, CB
        Valve AB has flow rate=0; tunnels lead to valves AA, AC
        Valve AC has flow rate=0; tunnels lead to valves AB, AD
        Valve AD has flow rate=0; tunnels lead to valves AC, AE
        Valve AE has flow rate=0; tunnels lead to valves AD, AF
        Valve AF has flow rate=0; tunnels lead to valves AE, AG
        Valve AG has flow rate=0; tunnels lead to valves AF, AH
        Valve AH has flow rate=0; tunnels lead to valves AG, AI
        Valve AI has flow rate=0; tunnels lead to valves AH, AJ
        Valve AJ has flow rate=0; tunnels lead to valves AI, AK
    """.trimIndent()

    partOne {
        exampleInput shouldOutput 1651
        aLinearLine shouldOutput 2640
        aQuadraticLine shouldOutput 13468
        aCircle shouldOutput 1288
        clusters shouldOutput 2400
    }

    partTwo(expensive = true, efficiencyBenchmark = 1.minutes) {
        exampleInput shouldOutput 1707
        aLinearLine shouldOutput 2670
        aQuadraticLine shouldOutput 12887
        aCircle shouldOutput 1484
        clusters shouldOutput 3680
    }
})
