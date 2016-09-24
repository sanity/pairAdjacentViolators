package com.trystacks.pav

import io.kotlintest.specs.StringSpec

/**
 * Created by ian on 9/24/16.
 */
class ChainSpec : StringSpec() {
    init {
        "toMutableChain should retain the converted list" {
            val origList = arrayListOf(1, 2, 3)
            val mc = origList.toMutableChain()
            mc.toList() shouldEqual origList
        }

        "iterate() should return each element once" {
            val origList = arrayListOf(1, 2, 3, 4)
            var position = 0
            val mc = origList.toMutableChain()
            mc.iterate {
                it.nextValue shouldEqual origList.get(position++)
                null
            }
        }

        "toMutableChain should support replacing of prev and next" {
            val origList = arrayListOf(1, 2, 3, 4)
            val mc = origList.toMutableChain()
            mc.iterate {
                when {
                    it.previousValue == 2 && it.nextValue == 3 -> 100
                    else -> null
                }
            }
            mc.toList() shouldEqual arrayListOf(1, 100, 4)
        }

        "Replacement should fail on the first value" {
            val origList = arrayListOf(1, 2, 3, 4)
            val mc = origList.toMutableChain()
            shouldThrow<IllegalArgumentException> {
                mc.iterate {
                    when {
                        it.nextValue == 1 -> 100
                        else -> null
                    }
                }
            }
        }
    }
}