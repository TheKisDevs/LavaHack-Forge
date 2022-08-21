package com.kisman.cc.util.thread.kisman

/**
 * @author _kisman_
 * @since 15:03 of 17.08.2022
 */
class ThreadSafeValue<V>(
    @set:Synchronized
    @get:JvmName("get")
    @set:JvmName("set")
    var value : V?
)
