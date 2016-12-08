package com.github.sanity.pav

import io.kotlintest.specs.FreeSpec

/**
 * Created by ian on 12/7/16.
 */
class CubicHermiteSplineSpec : FreeSpec() {
    init {
        val chs = CubicHermiteSpline(1.5, 1.3, 0.2, 3.2, 3.4, 0.3)
        "Spline should intersect (x1,y1)" {
            chs.interpolate(1.5) shouldBe exactly(1.3)
        }
        "Spline should intersect (x2,y2)" {
            chs.interpolate(3.2) shouldBe exactly(3.4)
        }
        "Spline slope at x1,y1 should be approximately 0.2" {
            val slope = (chs.interpolate(1.51)-chs.interpolate(1.5)) / 0.01
            slope shouldBe (0.2 plusOrMinus 0.05)
        }
        "Spline slope at x2,y2 should be approximately 0.3" {
            val slope = (chs.interpolate(3.21)-chs.interpolate(3.2)) / 0.01
            slope shouldBe (0.3 plusOrMinus 0.05)
        }
    }
}