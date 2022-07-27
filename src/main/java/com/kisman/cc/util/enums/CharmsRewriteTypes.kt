package com.kisman.cc.util.enums

import com.kisman.cc.util.enums.CharmsRewriteTypeModes.*

/**
 * @author _kisman_
 * @since 0:07 of 14.07.2022
 */
enum class CharmsRewriteTypes(
    val mode : CharmsRewriteTypeModes
) {
    Mode(Global),
    Width(Global),
    WireDepth(Wire),
    WireLighting(Wire),
    WireCulling(Wire),
    WireBlend(Wire),
    WireTranslucent(Wire),
    WireTexture2D(Wire),
    WireCustomColor(Wire),
    WireColor(Wire),
    ModelDepth(Model),
    ModelLighting(Model),
    ModelCulling(Model),
    ModelBlend(Model),
    ModelTranslucent(Model),
    ModelTexture2D(Model),
    ModelCustomColor(Model),
    ModelColor(Model)
}