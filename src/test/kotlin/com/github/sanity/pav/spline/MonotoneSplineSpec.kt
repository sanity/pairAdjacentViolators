package com.github.sanity.pav.spline

import com.github.sanity.pav.Point
import com.github.sanity.pav.spline.MonotoneSpline.ExtrapolationStrategy.TANGENT
import io.kotlintest.specs.FreeSpec

/**
 * Created by ian on 12/6/16.
 */
class MonotoneSplineSpec : FreeSpec() {
    init {
        "A simple increasing spline" - {
            val spline = MonotoneSpline(listOf(Point(0.0, 0.0), Point(1.0, 2.0), Point(2.0, 2.5)))
            "with flat extrapolation strategy" - {
                "should provide the first y value if x is less than the first point" {
                    spline.interpolate(-1.0, MonotoneSpline.ExtrapolationStrategy.FLAT) shouldBe exactly(0.0)
                }
                "should provide the last y value if x is greater than the last point" {
                    spline.interpolate(3.0, MonotoneSpline.ExtrapolationStrategy.FLAT) shouldBe exactly(2.5)
                }
            }
            "should provide correct outputs for the actual points used as input" {
                spline.interpolate(0.0) shouldBe exactly(0.0)
                spline.interpolate(1.0) shouldBe exactly(2.0)
                spline.interpolate(2.0) shouldBe exactly(2.5)
            }
            "should be continuous and increasing" {
                var x = 0.0
                var prevY : Double? = null
                while (x < 2.0) {
                    val y = spline.interpolate(x)
                    if (prevY != null) {
                        val delta = y - prevY
                        (delta > 0.0) shouldBe true
                        (delta < 0.1) shouldBe true
                    }
                    prevY = y
                    x += 0.01
                }
            }
        }
        "A flat increasing spline with slope 2" - {
            val spline = MonotoneSpline(listOf(Point(0.0, 0.0), Point(1.0, 2.0), Point(2.0, 4.0)))
            "should have the expected slope within the range of its points" {
                spline.interpolate(0.5) shouldBe (1.0 plusOrMinus 0.001)
                spline.interpolate(1.1) shouldBe (2.2 plusOrMinus 0.001)
                spline.interpolate(1.5) shouldBe (3.0 plusOrMinus 0.001)
                spline.interpolate(1.7) shouldBe (3.4 plusOrMinus 0.001)
            }
            "with tangent extrapolation strategy" - {
                "should extrapolate with the appropriate slope of its range of points" {
                    spline.interpolate(-0.5, TANGENT) shouldBe (-1.0 plusOrMinus 0.001)
                    spline.interpolate(3.0, TANGENT) shouldBe (6.0 plusOrMinus 0.001)
                }
            }
        }
        "A simple decreasing spline" - {
            val spline = MonotoneSpline(listOf(Point(0.0, 2.5), Point(1.0, 2.0), Point(2.0, 1.0)))
            "should provide correct outputs for the actual points used as input" {
                spline.interpolate(0.0) shouldBe exactly(2.5)
                spline.interpolate(1.0) shouldBe exactly(2.0)
                spline.interpolate(2.0) shouldBe exactly(1.0)
            }
            "should be continuous and decreasing" {
                var x = 0.0
                var prevY : Double? = null
                while (x < 2.0) {
                    val y = spline.interpolate(x)
                    if (prevY != null) {
                        val delta = y - prevY!!
                        (delta < 0.0) shouldBe true
                        (delta > -0.1) shouldBe true
                    }
                    prevY = y
                    x += 0.01
                }
            }
        }

        "should not accept non-monotonic input points" {
            shouldThrow<IllegalArgumentException> {
                MonotoneSpline(listOf(Point(0.0, 2.0), Point(1.0, 1.0), Point(2.0, 2.5)))
            }
        }
    }
}