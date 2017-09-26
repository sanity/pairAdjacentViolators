package com.github.sanity.pav.spline

import com.github.sanity.pav.Point
import io.kotlintest.matchers.*
import io.kotlintest.specs.FreeSpec

/**
 * Created by ian on 12/9/16.
 */
class SecantsCalculatorSpec : FreeSpec() {
    init {
        "Compute a single secant correctly" {
            val points = arrayListOf(Point(1.0, 1.0), Point(2.0, 1.5))
            val secantPoints = SecantsCalculator.calculate(points)
            secantPoints.size shouldBe 2
            val first = secantPoints[0]
            first.point shouldEqual Point(1.0, 1.0)
            first.secantBefore shouldEqual null
            first.secantAfter shouldEqual Secant(0.5)
            val second = secantPoints[1]
            second.point shouldEqual Point(2.0, 1.5)
            second.secantBefore shouldEqual Secant(0.5)
            second.secantAfter shouldEqual null
        }
    }
}