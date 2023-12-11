package aockt.util.spacial

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

@DisplayName("Util > Spacial: Point")
class PointTest : FunSpec({

    test("Equality holds regardless of initialization method.") {
        val longPoint = Point(0L, 1L)
        val intPoint = Point(0, 1)
        longPoint shouldBe intPoint
        intPoint shouldBe longPoint
    }

    @Suppress("LocalVariableName")
    test("Distance is calculated correctly and is always positive.") {
        val A = Point(-5, -10)
        val B = Point(20, 17)

        val AB = A distanceTo B
        val BA = B distanceTo A
        AB shouldBe BA

        A distanceTo A shouldBe 0L
        BA shouldBeGreaterThan 0L
    }
})
