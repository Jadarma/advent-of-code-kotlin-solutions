package aockt.y2016

import io.github.jadarma.aockt.core.Solution
import java.security.MessageDigest
import kotlin.experimental.and

object Y2016D05 : Solution {

    /** Returns the hexadecimal value of this string's MD5 hash. */
    private val String.md5
        get() = MessageDigest
            .getInstance("MD5")
            .digest(this.toByteArray())

    /** Brute forces MD5 hashes that start with five zeroes, in ascending order. */
    private fun bruteForce(input: String) =
        generateSequence(0, Int::inc)
            .map { "$input$it".md5 }
            .filter { it[0] == 0.toByte() && it[1] == 0.toByte() && it[2].and(240.toByte()) == 0.toByte() }
            .map { digest -> digest.joinToString("") { "%02x".format(it) } }

    override fun partOne(input: String) =
        bruteForce(input)
            .take(8)
            .map { it[5] }
            .joinToString("")

    override fun partTwo(input: String): String {

        val password = CharArray(8) { '_' }

        bruteForce(input)
            .map { (it[5] - '0') to it[6] }
            .filter { it.first in 0..7 }
            .filter { password[it.first] == '_' }
            .onEach { password[it.first] = it.second }
            .takeWhile { password.any { it == '_' } }
            .count()

        return String(password)
    }
}
