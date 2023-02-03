package com.kisman.cc.util.client.collections

/**
 * @author _kisman_
 * @since 17:54 of 03.02.2023
 */
class Quad<T>(
    first : T,
    second : T,
    third : T,
    fourth : T
) : QuadBind<T, T, T, T>(
    first,
    second,
    third,
    fourth
)