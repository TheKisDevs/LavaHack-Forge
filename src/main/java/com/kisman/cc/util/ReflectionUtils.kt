package com.kisman.cc.util

import com.kisman.cc.Kisman
import java.lang.reflect.Field
import java.lang.reflect.Modifier

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