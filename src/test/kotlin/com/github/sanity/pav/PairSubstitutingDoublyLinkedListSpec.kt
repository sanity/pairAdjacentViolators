package com.github.sanity.pav

import com.github.sanity.pav.PairSubstitutingDoublyLinkedList.Companion.createFromList
import io.kotlintest.specs.FreeSpec
import java.util.*

/**
 * Created by ian on 9/24/16.
 */
class PairSubstitutingDoublyLinkedListSpec : FreeSpec() {
    init {
        "PairSubstitutingDoublyLinkedList" - {
            "createFromList" - {
                "should return the original unmodified list" {
                    val origList = arrayListOf(1, 2, 3)
                    val mc = createFromList(origList)
                    mc.toList() shouldEqual origList
                }

                "should fail on an empty list" {
                    shouldThrow<IllegalArgumentException> {
                        createFromList(ArrayList<Int>())
                    }
                }
            }

            "iterate() should return each element once, and in the correct order" {
                val origList = arrayListOf(1, 2, 3, 4)
                var position = 0
                val mc = createFromList(origList)
                mc.iterate {
                    it.nextValue shouldEqual origList.get(position)
                    if (it.previousValue != null) {
                        it.previousValue shouldEqual origList.get(position - 1)
                    } else {
                        position shouldEqual 0
                    }
                    position++
                    null
                }
                position shouldEqual origList.size
            }

            "Replacement" - {
                "should work correctly in the middle of the list" {
                    val origList = arrayListOf(1, 2, 3, 4)
                    val mc = createFromList(origList)
                    mc.iterate {
                        when {
                            it.previousValue == 2 && it.nextValue == 3 -> 100
                            else -> null
                        }
                    }
                    mc.toList() shouldEqual arrayListOf(1, 100, 4)
                    mc.checkListIntegrity() shouldBe(true)
                }

                "should fail on the first value" {
                    val origList = arrayListOf(1, 2, 3, 4)
                    val mc = createFromList(origList)
                    shouldThrow<IllegalArgumentException> {
                        mc.iterate {
                            when {
                                it.nextValue == 1 -> 100
                                else -> null
                            }
                        }
                    }
                }

                "should work on the first and second value" {
                    val origList = arrayListOf(1, 2, 3, 4)
                    val mc = createFromList(origList)
                    mc.iterate {
                        when {
                            it.previousValue == 1 && it.nextValue == 2 -> 100
                            else -> null
                        }
                    }
                    mc.toList() shouldEqual arrayListOf(100, 3, 4)
                    mc.checkListIntegrity() shouldBe(true)
                }

                "should work on the second and last value" {
                    val origList = arrayListOf(1, 2, 3, 4)
                    val mc = createFromList(origList)
                    mc.iterate {
                        when {
                            it.previousValue == 3 && it.nextValue == 4 -> 100
                            else -> null
                        }
                    }
                    mc.toList() shouldEqual arrayListOf(1, 2, 100)
                    mc.checkListIntegrity() shouldBe(true)
                }

                "should work on two overlapping values" {
                    val origList = arrayListOf(1, 2, 3, 4)
                    val mc = createFromList(origList)
                    mc.iterate {
                        when {
                            it.previousValue == 3 && it.nextValue == 4 -> 100
                            it.previousValue == 2 && it.nextValue == 100 -> 200
                            else -> null
                        }
                    }
                    mc.toList() shouldEqual arrayListOf(1, 200)
                    mc.checkListIntegrity() shouldBe(true)
                }
            }
        }
    }
}