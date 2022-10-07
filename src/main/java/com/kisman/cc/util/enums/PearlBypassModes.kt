package com.kisman.cc.util.enums

//import com.kisman.cc.util.changeEnumEntryName

/**
 * @author _kisman_
 * @since 20:34 of 07.10.2022
 */
enum class PearlBypassModes {
    Normal,
    CrystalPvPcc {
        /*init {
            changeEnumEntryName(
                this,
                "crystalpvp.cc"
            )
        }*/

        override fun toString() : String {
            return "crystalpvp.cc"
        }
    }
}