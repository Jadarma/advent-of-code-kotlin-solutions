package aockt.util.math

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DisplayName("Util > Math: Factors")
class FactorsTest: FunSpec({

    context("Greatest Common Denominator") {
        test("Calculates correctly") {
            gcd(0, 2) shouldBe 2
            gcd(0, 0) shouldBe 0
            gcd(17, 19) shouldBe 1
        }
        test("Overloads behave the same") {
            gcd(18, 27) shouldBe gcd(18L, 27L)
        }
        test("Answer is always positive") {
            gcd(-6L, -4L) shouldBe 2
        }
    }

    context("Least Common Multiple") {
        test("Calculates Correctly") {
            lcm(3, 5) shouldBe 15
            lcm(2, 3, 5) shouldBe 30
            listOf(2, 3, 5).lcm() shouldBe 30
        }
        test("Overloads behave the same") {
            lcm(3, 5) shouldBe lcm(3L, 5L)
            lcm(2, 3, 5) shouldBe lcm(2L, 3L, 5L)
            listOf(2, 3, 5).lcm() shouldBe listOf(2L, 3L, 5L).lcm()
        }
        test("Answer is always positive") {
            lcm(2, -4) shouldBe 4
            lcm(0, -3) shouldBe 0
            lcm(-5, -7) shouldBe 35
        }
    }
})
