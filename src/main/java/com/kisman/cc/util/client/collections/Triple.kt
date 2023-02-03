package com.kisman.cc.util.client.collections

/**
 * @author _kisman_
 * @since 17:57 of 03.02.2023
 */
class Triple<T>(
    first : T,
    second : T,
    third : T
) : TripleBind<T, T, T>(
    first,
    second,
    third
)