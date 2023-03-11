@file:Suppress("PropertyName")

package com.kisman.cc.util.client

import com.kisman.cc.util.client.interfaces.IBindable
import me.zero.alpine.listener.Listenable

/**
 * @author _kisman_
 * @since 8:52 of 19.02.2023
 */
interface IFeature : Listenable, IBindable

/**
 * I used `?:` operator cuz fucking kotlin throws some exception
 * if `DisplayableFeature` class is in `Features.kt` file
 *
 * I used `tryCatch` cuz is throws some exception sometimes cuz of arraylist instance,
 * also i rewrote logic of arraylist instance cuz it can throw some exception in hud module manager when initializing arraylist
 *
 * DM me id you dont understand this comment
 *
 * My discord is `_kisman_#5039`
 */
/*
abstract class DisplayableFeature : Feature {
    @JvmField val ENABLE_ANIMATION = tryCatch(
        { AnimationExtended({ ArrayListModule.instance?.easing?.valEnum ?: EasingEnum.Easing.Linear }, { ArrayListModule.instance?.length?.valLong ?: 750 }, false) },
        { Animation(0.0, 1.0, 750) }
    )

    @JvmField val DISABLE_ANIMATION = tryCatch(
        { AnimationExtended({ ArrayListModule.instance?.easing?.valEnum ?: EasingEnum.Easing.Linear }, { ArrayListModule.instance?.length?.valLong ?: 750 }, true) },
        { Animation(1.0, 0.0, 750) }
    )
}*/
