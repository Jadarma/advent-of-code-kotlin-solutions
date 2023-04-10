package aockt.y2022

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2022, 6, "Tuning Trouble")
class Y2022D06Test : AdventSpec<Y2022D06>({

    partOne {
        "mjqjpqmgbljsphdztnvjfqwrcgsmlb" shouldOutput 7
        "bvwbjplbgvbhsrlpgdmjqwftvncz" shouldOutput 5
        "nppdvjthqldpwncqszvftbrmjlhg" shouldOutput 6
        "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg" shouldOutput 10
        "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw" shouldOutput 11
    }

    partTwo {
        "mjqjpqmgbljsphdztnvjfqwrcgsmlb" shouldOutput 19
        "bvwbjplbgvbhsrlpgdmjqwftvncz" shouldOutput 23
        "nppdvjthqldpwncqszvftbrmjlhg" shouldOutput 23
        "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg" shouldOutput 29
        "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw" shouldOutput 26
    }
})
