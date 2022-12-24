package com.kisman.cc.util.interfaces

/**
 * @author _kisman_
 * @since 23:12 of 24.12.2022
 */
interface Validable<T : Any> {
    fun valid(t : T) : Boolean
}