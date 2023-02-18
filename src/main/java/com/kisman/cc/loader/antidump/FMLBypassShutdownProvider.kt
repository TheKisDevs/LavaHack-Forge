@file:Suppress("UNCHECKED_CAST")

package com.kisman.cc.loader.antidump

import sun.security.util.SecurityConstants
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.security.AccessController
import java.security.PrivilegedAction
import kotlin.RuntimeException
import kotlin.arrayOf

/**
 * @author _kisman_
 * @since 20:14 of 31.08.2022
 */
fun initProvider() {
    val securityManager = CustomSecurityManager()

    AccessController.doPrivileged(PrivilegedAction {
        java.lang.Boolean.valueOf(securityManager::class.java.protectionDomain.implies(SecurityConstants.ALL_PERMISSION))
    })

    try {
        val jvmFields =
            Class::class.java.getDeclaredMethod(
                "getDeclaredFields0",
                *arrayOf<Class<*>?>(Boolean::class.javaPrimitiveType)
            )

        jvmFields.isAccessible = true

        for (field in jvmFields.invoke(System::class.java, false) as Array<Field>) {
            if (field.name.equals("security")) {
                field.isAccessible = true
                if (field.get(null)::class.java.name.startsWith("net.futureclient.")) break
                field.set(null, securityManager)
                break
            }
        }
    } catch(e : NoSuchFieldException) {
        throw RuntimeException(e)
    } catch(e : IllegalAccessException) {
        throw RuntimeException(e)
    } catch(e : InvocationTargetException) {
        throw RuntimeException(e)
    }
}