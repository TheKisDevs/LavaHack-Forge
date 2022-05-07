package com.kisman.cc.util.enums

enum class HandRewriteSlotMappings(
        val displaySlot : Int,
        val windowClickSlot : Int
) {
    Slot1(1, 36),
    Slot2(2, 37),
    Slot3(3, 38),
    Slot4(4, 39),
    Slot5(5, 40),
    Slot6(6, 41),
    Slot7(7, 42),
    Slot8(8, 43),
    Slot9(9, 44);

    companion object {
        val defaultSlot = Slot1

        fun get(slot : Int) : HandRewriteSlotMappings {
            for(slot_ in values()) {
                if(slot_.displaySlot == slot) return slot_
            }
            return defaultSlot
        }
    }
}