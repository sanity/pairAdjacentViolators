package com.github.sanity.pav

import com.github.sanity.pav.BinarySearchResult.Between
import com.github.sanity.pav.BinarySearchResult.Exact
import io.kotlintest.specs.FreeSpec
import java.util.*

/**
 * Created by ian on 12/8/16.
 */
class utilitiesSpec : FreeSpec() {
    init {
        "betterBinarySearch" - {
            val a = doubleArrayOf(0.3, 0.7, 0.8, 1.0, 1.2)
            "should find an exact value" {
                val exact = a.betterBinarySearch(0.8)
                exact shouldBe Exact(2)
                exact.toString() shouldBe "Exact(2)"
            }
            "should find an approximate match" {
                val between = a.betterBinarySearch(0.9)
                between shouldBe Between(2, 3)
                between.toString() shouldBe "Between(2, 3)"
            }
            "should ensure that the sought value is in-range" {
                shouldThrow<IllegalArgumentException> {
                    a.betterBinarySearch(0.2)
                }
                shouldThrow<IllegalArgumentException> {
                    a.betterBinarySearch(1.3)
                }
            }
        }

        "toArrayList" - {
            "should convert non-ArrayLists to ArrayList" {
                val origList = LinkedList<Int>()
                origList.add(1)
                origList.add(2)
                (origList is ArrayList<*>) shouldBe false
                val raList = origList.toArrayList()
                (raList is RandomAccess) shouldBe true
                raList shouldEqual origList
            }
            "should not modify a list that is already an ArrayList" {
                val origList = arrayListOf(1, 2)
                (origList.toArrayList() === origList) shouldBe true
            }
        }
    }

}