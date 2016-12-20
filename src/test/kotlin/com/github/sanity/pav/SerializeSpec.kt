package com.github.sanity.pav

import io.kotlintest.specs.FreeSpec
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by musachev on 20.12.2016.
 */

class SerializeSpec : FreeSpec() {
    init {
        val point = Point(1.0, 42.0)
        val pav = PairAdjacentViolators(listOf(
                point,
                Point(point.y, point.x))
        )

        "Point should be serializable and deserizable" {
            val savedPoint = save(point, "point")
            val loadedPoint: Point = open("point")

            savedPoint.x shouldBe exactly(loadedPoint.x)
            savedPoint.y shouldBe exactly(loadedPoint.y)
            savedPoint.weight shouldBe exactly(loadedPoint.weight)
        }

        "PairAdjacentViolators should be serializable and deserizable" {
            val savedPav = save(pav, "pav")
            val loadedPav: PairAdjacentViolators = open("pav")

            savedPav.isotonicPoints shouldEqual loadedPav.isotonicPoints
        }
    }
}

fun <T> open(path: String): T {
    val fin = FileInputStream(path)
    val obj = ObjectInputStream(fin).readObject()
    fin.close()

    return obj as T
}

fun <T> save(obj: T, path: String) : T {
    val fout = FileOutputStream(path)
    ObjectOutputStream(fout).writeObject(obj)
    fout.close()
    return obj
}