package com.kisman.cc.loader.objectweb

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class CustomMethodVisitor(
    mv : MethodVisitor,
    access : Int,
    name : String,
    desc : String,
    private val enter : Runnable?,
    private val exit : Runnable?
) : AdviceAdapter(
    Opcodes.ASM4,
    mv,
    access,
    name,
    desc
) {
    override fun onMethodEnter() {
        if(enter != null) {
            enter?.run()
        }
    }

    override fun onMethodExit(opcode: Int) {
        if(exit != null) {
            exit?.run()
        }
    }
}