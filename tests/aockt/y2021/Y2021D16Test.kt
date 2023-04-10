package aockt.y2021

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2021, 16, "Packet Decoder")
class Y2021D16Test : AdventSpec<Y2021D16>({

    partOne {
        "8A004A801A8002F478" shouldOutput 16
        "620080001611562C8802118E34" shouldOutput 12
        "C0015000016115A2E0802F182340" shouldOutput 23
        "A0016C880162017C3686B18A3D4780" shouldOutput 31
    }

    partTwo {
        "C200B40A82" shouldOutput 3
        "04005AC33890" shouldOutput 54
        "880086C3E88112" shouldOutput 7
        "CE00C43D881120" shouldOutput 9
        "D8005AC2A8F0" shouldOutput 1
        "F600BC2D8F" shouldOutput 0
        "9C005AC2F8F0" shouldOutput 0
        "9C0141080250320F1802104A08" shouldOutput 1
    }
})
