package com.kisman.cc.util

/**
 * @author _kisman_
 * @since 19:23 of 26.11.2022
 */
interface ReturnableRunnable<T> {
    fun run() : T
}