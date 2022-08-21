package com.kisman.cc.loader.objectweb

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

/**
 * @author _kisman_
 * @since 14:55 of 15.08.2022
 */
class Injector(
    private val clazz : Class<*>,
    private val method : String
) {
    fun injectHEAD(
        runnable : Runnable
    ) {
        inject(
            runnable,
            null
        )
    }

    fun injectTAIL(
        runnable : Runnable
    ) {
        inject(
            null,
            runnable
        )
    }

    private fun inject(
        start : Runnable?,
        exit : Runnable?
    ) {
        val reader = getClassReader(clazz)
        val node = ClassNode()
        reader.accept(node, ClassReader.EXPAND_FRAMES)
        reader.accept(CustomClassVisitor(node, method, start, exit), 0)
    }
}