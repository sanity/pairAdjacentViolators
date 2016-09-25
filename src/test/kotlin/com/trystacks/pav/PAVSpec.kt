package com.trystacks.pav

import com.trystacks.pav.PairAdjacentViolators.PAVMode.DECREASING
import com.trystacks.pav.PairAdjacentViolators.Point
import io.kotlintest.specs.FreeSpec

/**
 * Created by ian on 9/23/16.
 */
class PAVSpec : FreeSpec() {
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
        }

        "PairAdjacentViolators" - {
            "should sort points by x value" {
                val increasingPointsSorted = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0))
                val increasingPointsUnSorted = listOf(Point(5.0, 3.0), Point(3.0, 1.0), Point(4.0, 2.0))
                PairAdjacentViolators(increasingPointsUnSorted).get() shouldEqual increasingPointsSorted
            }

            "should 'backtrack' to merge previous points where necessary" {
                val points = listOf(Point(1.0, 5.0), Point(2.0, 6.0), Point(3.0, 1.0))
                PairAdjacentViolators(points).get() shouldEqual listOf(Point(2.0, (5.0 + 6.0 + 1.0) / 3.0, 3.0))
            }

            "in ascending mode" - {
                "should not change an already increasing set of points" {
                    val increasingPoints = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0))
                    PairAdjacentViolators(increasingPoints).get() shouldEqual increasingPoints
                }

                "should merge a single pair of non-increasing points" {
                    val nonIncreasingPoints = listOf(Point(1.0, 2.0), Point(2.0, 1.0), Point(3.0, 5.0))
                    PairAdjacentViolators(nonIncreasingPoints).get() shouldEqual listOf(Point(1.5, 1.5, 2.0), Point(3.0, 5.0))
                }

                "should merge multiple non-increasing points" {
                    val nonIncreasingPoints = listOf(Point(1.0, 5.0), Point(2.0, 4.0), Point(3.0, 3.0))
                    PairAdjacentViolators(nonIncreasingPoints).get() shouldEqual listOf(Point(2.0, 4.0, 3.0))
                }
            }

            "in descending mode" - {
                "should not merge decreasing points" {
                    val decreasingPoints = listOf(Point(1.0, 5.0), Point(2.0, 4.0), Point(3.0, 3.0))
                    PairAdjacentViolators(decreasingPoints, DECREASING).get() shouldEqual decreasingPoints
                }
            }

            "PAV interpolation" - {
                "should interpolate as expected" {
                    val increasingPoints = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0), Point(8.0, 4.0))
                    val pav = PairAdjacentViolators(increasingPoints)
                    pav.get() shouldEqual increasingPoints
                    val splineInterpolator = pav.interpolator(PairAdjacentViolators.InterpolationStrategy.SPLINE)
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

                "should reverse-interpolate as expected" {
                    val increasingPoints = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0), Point(8.0, 4.0))
                    val pav = PairAdjacentViolators(increasingPoints)
                    pav.get() shouldEqual increasingPoints
                    val splineInterpolator = pav.inverseInterpolator(PairAdjacentViolators.InterpolationStrategy.SPLINE)
                    var lastPointVar: Point? = null
                    for (point in increasingPoints) {
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