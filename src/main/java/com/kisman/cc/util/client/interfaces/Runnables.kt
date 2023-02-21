package com.kisman.cc.util.client.interfaces

/**
 * @author _kisman_
 * @since 16:52 of 20.02.2023
 */
interface Runnable1<P, R> {
    fun run(
        p : P
    ) : R
}

interface Runnable2<P1, P2, R> {
    fun run(
        p1 : P1,
        p2 : P2
    ) : R
}

interface Runnable3<P1, P2, P3, R> {
    fun run(
        p1 : P1,
        p2 : P2,
        p3 : P3
    ) : R
}

interface Runnable0<R> {
    fun run() : R
}

interface Runnable1P<P> {
    fun run(
        p : P
    )
}

interface Runnable2P<P1, P2> {
    fun run(
        p1 : P1,
        p2 : P2
    )
}

interface Runnable3P<P1, P2, P3> {
    fun run(
        p1 : P1,
        p2 : P2,
        p3 : P3
    )
}