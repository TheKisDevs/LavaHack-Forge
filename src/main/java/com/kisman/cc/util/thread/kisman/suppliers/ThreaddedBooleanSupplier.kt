package com.kisman.cc.util.thread.kisman.suppliers

/**
 * @author _kisman_
 * @since 16:46 of 17.08.2022
 */
class ThreaddedBooleanSupplier(
    get0 : () -> Boolean
) : ThreaddedSupplier<Boolean>(
    get0
) {
    override fun get(): Boolean {
        return super.get() ?: false
    }
}