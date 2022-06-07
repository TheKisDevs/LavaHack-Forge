package com.kisman.cc.util.enums.dynamic

import org.cubic.dynamictask.AbstractTask

/**
 * @author _kisman_
 * @since 12:13 of 06.06.2022
 */
enum class TestEnum2(
    val task : AbstractTask<Void>
) {
    Test2(task.task { arg : org.cubic.dynamictask.ArgumentFetcher ->
        println("Test2 ${arg.fetch<String>(0)}")
        return@task null
    })
}

private val task : AbstractTask.DelegateAbstractTask<Void> = AbstractTask.types(
    Void::class.java,
    String::class.java
)