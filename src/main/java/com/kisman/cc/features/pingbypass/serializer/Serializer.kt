package com.kisman.cc.features.pingbypass.serializer

/**
 * @author _kisman_
 * @since 20:36 of 19.08.2022
 */
interface Serializer<T> {
    fun serializeAndSend(t : T)
}