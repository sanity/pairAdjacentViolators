package com.github.sanity.pav.spline

import com.github.sanity.pav.Point
import io.kotlintest.specs.FreeSpec

/**
 * Created by ian on 12/9/16.
 */
class FritschCarlsonTangentStrategySpec : FreeSpec() {
    init {
        val fcts = FritschCarlsonTangentStrategy()

        "Tangents should be initialized correctly" - {
            // Secants here are incorrect relative to the points, this shouldn't matter
            val points = arrayListOf(
                    PointWithSecants(Point(0.0, 0.0), null, Secant(0.1)),
                    PointWithSecants(Point(1.0, 1.0), Secant(0.1), Secant(0.2)),
                    PointWithSecants(Point(2.0, 2.0), Secant(0.2), null))

            val pointsWithTangents = fcts.initTangentsToSecantAverages(points)
            pointsWithTangents.size shouldBe 3

            "First tangent should be the same as the first secant" {
                pointsWithTangents[0].let {
                    it.pointWithSecants shouldBe points[0]
                    it.tangent shouldBe exactly(0.1)
                }
            }

            "Second tangent should be the average of the secants" {
                pointsWithTangents[1].let {
                    it.pointWithSecants shouldBe points[1]
                    it.tangent shouldBe (0.15 plusOrMinus 0.00001)
                }
            }

            "Third tangent should be the same as the last secant" {
                pointsWithTangents[2].let {
                    it.pointWithSecants shouldBe points[2]
                    it.tangent shouldBe (exactly(0.2))
                }
            }
        }
    }
}