package com.kisman.cc.util

import com.kisman.cc.Kisman
import com.kisman.cc.websockets.reportIssue
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URL
import java.net.URLClassLoader

/**
 * @author _kisman_
 * @since 14:16 of 27.07.2022
 */
@Throws(NoSuchFieldException::class, IllegalAccessException::class)
fun setFinalField(
    field : Field,
    `object` : Any?,
    value : Any
) {
    field.isAccessible = true
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
    field[`object`] = value
}

@Throws(NoSuchFieldException::class, IllegalAccessException::class)
fun setFinalStaticField(
    field : Field,
    value : Any
) {
    setFinalField(field, null, value)
}

@Throws(NoSuchFieldException::class)
fun getField(
    clazz : Class<*>,
    vararg mappings : String?
) : Field? {
    for (s in mappings) {
        try {
            return clazz.getDeclaredField(s)
        } catch (ignored : NoSuchFieldException) { }
    }
    throw NoSuchFieldException(
        "No Such field: " + clazz.name + "-> " + mappings.contentToString()
    )
}

fun changeEnumEntryName(
    enum : Enum<*>,
    name : String
) : Boolean {
    try {
        enum::class.java.getDeclaredField("name").also {
            it.isAccessible = true
            it.set(
                enum,
                name
            )

            return true
        }
    } catch(_ : Throwable) {
        Kisman.LOGGER.error("Cannot change a name of \"${enum::class.java.simpleName}:${enum.name}\" enum to \"$name\"")
    }

    return false
}

fun annotationCheck(
    option : Enum<*>,
    annotation : Class<out Annotation>
) : Boolean = try {
    option::class.java.getField(option.name).isAnnotationPresent(annotation)
} catch(e : Exception) {
    reportIssue("Got an exception in \"annotationCheck\" method: ${e.message}, key is ${AccountData.key}")
    false
}

val securityManager = SecurityManagerImplementation()

fun callerClass() : Class<*>? = callerClass(4)

fun callerClass(
    depth : Int
) : Class<*>? = securityManager.callerClass(depth)

class SecurityManagerImplementation : SecurityManager() {
    fun callerClass(
        depth : Int
    ) : Class<*>? {
        val stack = classContext

        return if(stack.size < depth + 1) {
            null
        } else {
            stack[depth]
        }
    }
}

@Throws(Exception::class)
fun addToClassPath(
    classLoader : URLClassLoader,
    url : URL
) {
    val method = URLClassLoader::class.java.getMethod("addURL", URL::class.java).also { it.isAccessible = true }

    method.invoke(classLoader, url)
}

fun baseClass(
    type : Type
) = if(type is Class<*>) {
    type
} else {
    if(type is ParameterizedType) {
        type.rawType as Class<*>
    } else {
        null
    }
}