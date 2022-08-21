package com.kisman.cc.loader.objectweb

import com.kisman.cc.loader.LavaHackLoaderMod
import org.objectweb.asm.ClassReader

/**
 * @author _kisman_
 * @since 15:03 of 15.08.2022
 */

fun getClassReader(clazz : Class<*>) : ClassReader = getClassReader(formatClassName(clazz))
fun getClassReader(clazz : String) : ClassReader = ClassReader(LavaHackLoaderMod::class.java.classLoader.getResourceAsStream(clazz))
fun formatClassName(clazz : Class<*>) : String = "/${clazz.name.replace(".", "/")}.class"