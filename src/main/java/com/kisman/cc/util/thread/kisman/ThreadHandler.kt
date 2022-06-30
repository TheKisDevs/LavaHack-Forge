package com.kisman.cc.util.thread.kisman

import com.kisman.cc.util.TimerUtils
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 22:23 of 22.06.2022
 */
class ThreadHandler(
    val delay : Supplier<Long>,
    val threadded : Supplier<Boolean>
) : GlobalThreads {
    private val timer = TimerUtils()

    fun reset() {
        timer.reset()
    }

    fun update(task : Runnable) {
        if(timer.passedMillis(delay.get())) {
            timer.reset()

            if(threadded.get()) {
                executor.submit(task)
            } else {
                task.run()
            }
        }
    }
}