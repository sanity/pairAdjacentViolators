package com.trystacks.pav.benchmarks

import com.trystacks.pav.PairAdjacentViolators
import com.trystacks.pav.PairAdjacentViolators.Point
import java.time.Duration
import java.util.*

/**
 * Created by ian on 9/25/16.
 */

fun main(args: Array<String>) {
    val rand = Random()
    val points = ArrayList<Point>()
    for (x in 0 .. 1000) {
        points.add(Point(x.toDouble(), funX(x.toDouble()) + ((rand.nextDouble()-0.5) * 50000.0)))
    }
    val startTime = System.nanoTime()
    val pav = PairAdjacentViolators(points)
    val interpolator = pav.interpolator()
    val mergedPoints = pav.get()
    println("Took ${Duration.ofNanos(System.nanoTime()-startTime)} to build PAV for ${points.size} input points resulting in ${mergedPoints.size} merged points")
    for (p in points) {
        println("${p.x}\t${p.y}\t${interpolator(p.x)}")
    }
}

fun funX(x: Double): Double = x + 0.1 * x * x
