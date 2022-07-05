package com.kisman.cc.loader.antidump

import sun.security.util.SecurityConstants
import java.lang.reflect.Field
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * @author _kisman_
 * @since 12:39 of 04.07.2022
 */
fun replaceSecurityManager(sm: SecurityManager?) {
    if (sm != null && sm.javaClass.classLoader != null) {
        AccessController.doPrivileged(PrivilegedAction<Any?> {
            sm.javaClass.protectionDomain.implies(SecurityConstants.ALL_PERMISSION)
        })
    }

    @Suppress("UNCHECKED_CAST")
    val jvmFields = Class::class.java.getDeclaredMethod(
        "getDeclaredFields0",
        Boolean::class.javaPrimitiveType
    ).also {
        it.isAccessible = true
    }.invoke(System::class.java, false) as Array<Field>

    for (field in jvmFields) {
        if (field.name == "security") {
            field.isAccessible = true
            field[null] = sm
            return
        }
    }
}