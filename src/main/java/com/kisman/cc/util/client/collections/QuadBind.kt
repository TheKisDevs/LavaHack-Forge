package com.kisman.cc.util.client.collections

/**
 * @author _kisman_
 * @since 17:51 of 03.02.2023
 */
open class QuadBind<A, B, C, D>(
    @JvmField var first : A,
    @JvmField var second : B,
    @JvmField var third : C,
    @JvmField var fourth : D
)