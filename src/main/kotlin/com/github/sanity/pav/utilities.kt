package com.github.sanity.pav

/**
 * Created by ian on 12/8/16.
 */

fun DoubleArray.betterBinarySearch(v: Double): BinarySearchResult {
    if (v < this.first() || v > this.last()) {
        throw IllegalArgumentException("v ($v) is outside range of DoubleArray (${this.first()}0${this.last()})")
    }
    val result = this.binarySearch(v)
    return if (result >= 0) {
        BinarySearchResult.Exact(result)
    } else {
        val insertionPoint = -result - 1
        BinarySearchResult.Between(insertionPoint - 1, insertionPoint)
    }
}

sealed class BinarySearchResult {
    class Exact(val index: Int) : BinarySearchResult() {
        override fun equals(other: Any?): Boolean {
            return if (other is Exact) {
                index == other.index
            } else {
                false
            }
        }

        override fun toString(): String {
            return "Exact($index)"
        }
    }
    class Between(val lowIndex: Int, val highIndex: Int) : BinarySearchResult() {
        override fun equals(other: Any?): Boolean {
            return if (other is Between) {
                lowIndex == other.lowIndex && highIndex == other.highIndex
            } else {
                false
            }
        }

        override fun toString(): String {
            return "Between($lowIndex, $highIndex)"
        }
    }
}