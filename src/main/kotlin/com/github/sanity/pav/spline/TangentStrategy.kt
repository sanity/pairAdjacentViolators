package com.github.sanity.pav.spline

import com.github.sanity.pav.Point
import java.io.Serializable
import java.util.*

interface TangentStrategy {
    fun compute(points: Iterable<PointWithSecants>): ArrayList<PointWithTangents>
}

data class PointWithTangents(val pointWithSecants: PointWithSecants, val tangent: Double) : Serializable {
    val x = pointWithSecants.point.x
    val y = pointWithSecants.point.y
}

data class PointWithSecants(val point: Point, val secantBefore: Secant?, val secantAfter: Secant?) : Serializable

data class Secant(val slope: Double) : Serializable {
    constructor(start: Point, end: Point) : this((end.y - start.y) / (end.x - start.x))
}