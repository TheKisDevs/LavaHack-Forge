package com.kisman.cc.loader

import kotlin.concurrent.thread


class Timer {
        var ended = true

        fun start(
            ms : Int
        ) {
            var cMs = ms

            thread {
                ended = false

                while (cMs > 0) {
                    cMs--

                    Thread.sleep(1)
                }

                ended = true
            }
        }
    }