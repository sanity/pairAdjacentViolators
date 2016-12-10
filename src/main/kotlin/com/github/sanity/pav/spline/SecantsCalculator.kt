package com.github.sanity.pav.spline

import com.github.sanity.pav.Point
import com.github.sanity.pav.toArrayList
import java.util.*

/**
 * Created by ian on 12/9/16.
 */
class SecantsCalculator {
    companion object {
        fun calculate(points: List<Point>): ArrayList<PointWithSecants> {
            val fastPoints: List<Point> = points.toArrayList()

            val numberOfPoints = fastPoints.size
            val secants = ArrayList<Secant>(numberOfPoints - 1)
            for (x in 0..(numberOfPoints - 2)) {
                secants.add(Secant(fastPoints[x], fastPoints[x + 1]))
            }

            val pointsWithSecants = ArrayList<PointWithSecants>(numberOfPoints)
            for (x in fastPoints.indices) {
                pointsWithSecants
                        .add(PointWithSecants(fastPoints[x], (if (x > 0) secants[x - 1] else null), if (x < secants.size) secants[x] else null))
            }
            return pointsWithSecants
        }
    }
}