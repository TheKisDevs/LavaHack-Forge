package com.kisman.cc.util.thread.kisman.suppliers

import com.kisman.cc.util.thread.kisman.GlobalThreads
import com.kisman.cc.util.thread.kisman.ThreadSafeValue

/**
 * @author _kisman_
 * @since 14:57 of 17.08.2022
 */
open class ThreaddedSupplier<T>(
    private val get0 : () -> T
) : GlobalThreads {
    private val get1 = ThreadSafeValue<T>(null)

    open fun get() : T? {
        executor.submit {
            get1.value = get0.invoke()
        }

        return get1.value
    }
}