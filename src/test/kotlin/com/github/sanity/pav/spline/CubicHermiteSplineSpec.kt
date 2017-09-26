package com.github.sanity.pav.spline

import io.kotlintest.matchers.*
import io.kotlintest.specs.FreeSpec

/**
 * Created by ian on 12/7/16.
 */
class CubicHermiteSplineSpec : FreeSpec() {
    init {
        val x1 = 1.5
        val y1 = 1.3
        val m1 = 0.2
        val x2 = 3.2
        val y2 = 3.4
        val m2 = 0.3
        val chs = CubicHermiteSpline(x1, y1, m1, x2, y2, m2)
        "Spline should intersect (x1,y1)" {
            chs.interpolate(x1) shouldBe exactly(y1)
        }
        "Spline should intersect (x2,y2)" {
            chs.interpolate(x2) shouldBe exactly(y2)
        }
        val delta = 0.01
        "Spline slope at x1,y1 should be approximately m1" {
            val slope = (chs.interpolate(x1 + delta)-chs.interpolate(x1)) / delta
            slope shouldBe (m1 plusOrMinus 0.05)
        }
        "Spline slope at x2,y2 should be approximately m2" {
            val slope = (chs.interpolate(x2 + delta)-chs.interpolate(x2)) / delta
            slope shouldBe (m2 plusOrMinus 0.05)
        }
    }
}
