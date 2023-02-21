package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.trading.domain.Item

class ItemBag private constructor() : HashMap<Item, Int>() {

    init {
        Item.values().forEach { this[it] = 0 }
    }

    companion object {
        fun fromAmount(amounts: Map<Item, Int>): ItemBag {
            val itemBag = ItemBag()
            for (item in amounts) {
                itemBag[item.key] = item.value
            }
            return itemBag
        }

        fun fromNothing(): ItemBag {
            return ItemBag()
        }
    }

    fun useItem(item: Item, amount: Int): ItemBag {
        val itemBag = ItemBag()
        itemBag[item] = itemBag[item]!! - amount
        return itemBag
    }
}