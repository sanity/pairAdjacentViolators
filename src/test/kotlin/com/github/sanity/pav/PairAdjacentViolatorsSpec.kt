package com.github.sanity.pav

import com.github.sanity.pav.PairAdjacentViolators.PAVMode.DECREASING
import com.github.sanity.pav.spline.MonotoneSpline
import io.kotlintest.specs.FreeSpec

/**
 * Created by ian on 9/23/16.
 */
class PairAdjacentViolatorsSpec : FreeSpec() {
    init {
        "Points" - {
            "should merge correctly" {
                val a = Point(1.0, 2.0, 3.0);
                val b = Point(4.0, 5.0, 6.0)
                val mergedX = (1.0 * 3.0 + 4.0 * 6.0) / (3.0 + 6.0)
                val mergedY = (2.0 * 3.0 + 5.0 * 6.0) / (3.0 + 6.0)
                val mergedWeight = 3.0 + 6.0
                a.merge(b) shouldEqual Point(mergedX, mergedY, mergedWeight)
            }

            "should default to a weight of 1.0" {
                Point(0.0, 0.0).weight shouldEqual 1.0
            }

            "should convert to strings correctly" {
                Point(1.0, 2.0).toString() shouldEqual "(1.0, 2.0)"
                Point(1.0, 2.0, 3.0).toString() shouldEqual "(1.0, 2.0 :3.0)"
            }
        }

        "PairAdjacentViolators" - {
            "a PAV given a single point" - {
                val point = listOf(Point(2.3, 4.2))
                val pav = PairAdjacentViolators(point)
                "should provide an interpolator that returns the y value for any x" {
                    pav.interpolator().invoke(7623.2) shouldBe (4.2 plusOrMinus 0.0000001)
                }
                "should provide an inverse-interpolator that returns the x value for any y" {
                    pav.inverseInterpolator().invoke(361.2) shouldBe (2.3 plusOrMinus 0.0000001)
                }
            }
            "should sort points by x value" {
                val increasingPointsSorted = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0))
                val increasingPointsUnSorted = listOf(Point(5.0, 3.0), Point(3.0, 1.0), Point(4.0, 2.0))
                PairAdjacentViolators(increasingPointsUnSorted).isotonicPoints shouldEqual increasingPointsSorted
            }

            "should merge two points with the same x value" {
                val points = listOf(Point(1.0, 5.0), Point(1.0, 6.0), Point(3.0, 10.0))
                val mergedPoints = listOf(Point(1.0, 5.5, 2.0), Point(3.0, 10.0))
                PairAdjacentViolators(points).isotonicPoints shouldEqual mergedPoints
            }

            "should 'backtrack' to merge previous points where necessary" {
                val points = listOf(Point(1.0, 5.0), Point(2.0, 6.0), Point(3.0, 1.0))
                PairAdjacentViolators(points).isotonicPoints shouldEqual listOf(Point(2.0, (5.0 + 6.0 + 1.0) / 3.0, 3.0))
            }

            "in ascending mode" - {
                "should not change an already increasing set of points" {
                    val increasingPoints = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0))
                    PairAdjacentViolators(increasingPoints).isotonicPoints shouldEqual increasingPoints
                }

                "should merge a single pair of non-increasing points" {
                    val nonIncreasingPoints = listOf(Point(1.0, 2.0), Point(2.0, 1.0), Point(3.0, 5.0))
                    PairAdjacentViolators(nonIncreasingPoints).isotonicPoints shouldEqual listOf(Point(1.5, 1.5, 2.0), Point(3.0, 5.0))
                }

                "should merge two points with the same y value" {
                    val points = listOf(Point(1.0, 2.0), Point(2.0, 2.0), Point(3.0, 5.0))
                    PairAdjacentViolators(points).isotonicPoints shouldEqual listOf(Point(1.5, 2.0, 2.0), Point(3.0, 5.0))
                }

                "should merge multiple non-increasing points" {
                    val nonIncreasingPoints = listOf(Point(1.0, 5.0), Point(2.0, 4.0), Point(3.0, 3.0))
                    PairAdjacentViolators(nonIncreasingPoints).isotonicPoints shouldEqual listOf(Point(2.0, 4.0, 3.0))
                }
            }

            "in descending mode" - {
                "should not merge decreasing points" {
                    val decreasingPoints = listOf(Point(1.0, 5.0), Point(2.0, 4.0), Point(3.0, 3.0))
                    PairAdjacentViolators(decreasingPoints, DECREASING).isotonicPoints shouldEqual decreasingPoints
                }

                "should merge two points with the same y value" {
                    val points = listOf(Point(1.0, 5.0), Point(2.0, 2.0), Point(3.0, 2.0))
                    PairAdjacentViolators(points, DECREASING).isotonicPoints shouldEqual listOf(Point(1.0, 5.0),Point(2.5, 2.0, 2.0))
                }
            }

            "PAV interpolation" - {
                "should interpolate as expected" - {
                    "for increasing points" {
                        val increasingPoints = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0), Point(8.0, 4.0))
                        val pav = PairAdjacentViolators(increasingPoints)
                        pav.isotonicPoints shouldEqual increasingPoints
                        val splineInterpolator = pav.interpolator(PairAdjacentViolators.InterpolationStrategy.SPLINE, MonotoneSpline.ExtrapolationStrategy.TANGENT)
                        var lastPointVar: Point? = null
                        for (point in increasingPoints) {
                            splineInterpolator(point.x) shouldEqual point.y
                            val lastPoint = lastPointVar
                            if (lastPoint != null) {
                                val midPointValue = splineInterpolator((point.x + lastPoint.x) / 2.0)
                                midPointValue should { it >= lastPoint.y && it <= point.y }
                            }
                            lastPointVar = point
                        }
                    }
                    "for decreasing points" {
                        val decreasingPoints = listOf(Point(3.0, 4.0), Point(4.0, 3.0), Point(5.0, 2.0), Point(8.0, 1.0))
                        val pav = PairAdjacentViolators(decreasingPoints, DECREASING)
                        pav.isotonicPoints shouldEqual decreasingPoints
                        val splineInterpolator = pav.interpolator(PairAdjacentViolators.InterpolationStrategy.SPLINE, MonotoneSpline.ExtrapolationStrategy.TANGENT)
                        var lastPointVar: Point? = null
                        for (point in decreasingPoints) {
                            splineInterpolator(point.x) shouldEqual point.y
                            val lastPoint = lastPointVar
                            if (lastPoint != null) {
                                val midPointValue = splineInterpolator((point.x + lastPoint.x) / 2.0)
                                midPointValue should { it >= lastPoint.y && it <= point.y }
                            }
                            lastPointVar = point
                        }
                    }
                }

                "should reverse-interpolate as expected" {
                    val points = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0), Point(8.0, 4.0))
                    val pav = PairAdjacentViolators(points)
                    pav.isotonicPoints shouldEqual points
                    val splineInterpolator = pav.inverseInterpolator(PairAdjacentViolators.InterpolationStrategy.SPLINE)
                    var lastPointVar: Point? = null
                    for (point in points) {
                        splineInterpolator(point.y) shouldEqual point.x
                        val lastPoint = lastPointVar
                        if (lastPoint != null) {
                            val midPointValue = splineInterpolator((point.y + lastPoint.y) / 2.0)
                            midPointValue should { it >= lastPoint.x && it <= point.x }
                        }
                        lastPointVar = point
                    }
                }
            }
        }
    }
}
