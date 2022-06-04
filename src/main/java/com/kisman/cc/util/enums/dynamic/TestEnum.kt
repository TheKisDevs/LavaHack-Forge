package com.kisman.cc.util.enums.dynamic

import org.cubic.dynamictask.AbstractTask
import org.cubic.dynamictask.ArgumentFetcher

/**
 * @author _kisman_
 * @since 11:45 of 04.06.2022
 */
class TestEnum {
    companion object {
        private val task : AbstractTask.DelegateAbstractTask<Void> = AbstractTask.types(
            Void::class.java,
            String::class.java
        )
    }

    enum class Test(
        val task : AbstractTask<Void>
    ) {
        Test1(task.task { arg :  ArgumentFetcher ->
            println("Test1: ${arg.fetch<String>(0)}")
            null
        })
        /*Test2(SwapEnum2.task.task { arg: ArgumentFetcher ->
            if (arg.fetch(1)) return@task null
            SwapEnum2.mc.player.connection.sendPacket(CPacketHeldItemChange(arg.fetch(0)))
            SwapEnum2.mc.player.inventory.currentItem = arg.fetch(0)
            null
        })*/
    }
}