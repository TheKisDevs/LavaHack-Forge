package com.kisman.cc.loader.objectweb

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class CustomClassVisitor(
    cv : ClassVisitor,
    private val method : String,
    private val enter : Runnable?,
    private val exit : Runnable?
) : ClassVisitor(
    Opcodes.ASM4,
    cv
) {
    override fun visitMethod(
        access : Int,
        name : String,
        desc : String,
        signature : String?,
        exceptions : Array<String>?
    ): MethodVisitor {
        if (name != method) return super.visitMethod(access, name, desc, signature, exceptions)

        return CustomMethodVisitor(
            super.visitMethod(access, name, desc, signature, exceptions),
            access,
            name,
            desc,
            enter,
            exit
        )
    }
}