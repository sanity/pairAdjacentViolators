package com.github.sanity.pav.examples

import com.github.sanity.pav.PairAdjacentViolators
import com.github.sanity.pav.PairAdjacentViolators.Point
import java.util.*

/**
 * Generate the example graph used in README.md
 */

fun main(args: Array<String>) {
    val rand = Random()
    val points = ArrayList<Point>()
    for (x in 0 .. 100) {
        points.add(Point(x.toDouble(), funX(x.toDouble()) + ((rand.nextDouble()-0.5) * 5000.0)))
    }
    val startTime = System.nanoTime()
    val pav = PairAdjacentViolators(points)
    val interpolator = pav.interpolator()
    val mergedPoints = pav.isotonicPoints
    println("Took ${System.nanoTime()-startTime}ns to build PAV for ${points.size} input points resulting in ${mergedPoints.size} merged points")
    for (p in points) {
        println("${p.x}\t${p.y}\t${interpolator(p.x)}")
    }
}

fun funX(x: Double): Double = x + 0.1 * x * x
