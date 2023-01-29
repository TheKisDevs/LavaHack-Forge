package com.kisman.cc.features.hud

import com.kisman.cc.util.client.annotations.FakeThing

/**
 * @author _kisman_
 * @since 14:14 of 21.01.2023
 */
@FakeThing
class FakeHudModule(
    name : String
) : HudModule(
    name
) {
    init {
        isToggled = true
        toggleable = true
    }
}