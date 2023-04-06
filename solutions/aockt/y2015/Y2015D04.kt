package aockt.y2015

import io.github.jadarma.aockt.core.Solution
import java.security.MessageDigest

object Y2015D04 : Solution {

    /** Returns the hexadecimal value of this string's MD5 hash. */
    private val String.md5
        get() = MessageDigest
            .getInstance("MD5")
            .digest(this.toByteArray())
            .joinToString("") { "%02x".format(it) }

    /** Finds the lowest number than appended to the [secretKey] produces an MD5 hash with [zeroes] leading zeroes. */
    private fun bruteForce(secretKey: String, zeroes: Int): Long =
        generateSequence(1L) { it + 1 }
            .first { nonce -> secretKey.plus(nonce).md5.take(zeroes).all { it == '0' } }

    override fun partOne(input: String) = bruteForce(input, 5)
    override fun partTwo(input: String) = bruteForce(input, 6)
}
