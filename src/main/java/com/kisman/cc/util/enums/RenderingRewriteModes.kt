package com.kisman.cc.util.enums

import com.kisman.cc.util.render.Rendering

enum class RenderingRewriteModes(
    val mode : Rendering.Mode?
) {
    None(null),
    Filled(Rendering.Mode.BOX),
    Outline(Rendering.Mode.OUTLINE),
    Wire(Rendering.Mode.WIRE),
    FilledOutline(Rendering.Mode.BOX_OUTLINE),
    FilledWire(Rendering.Mode.BOX_WIRE),
    FilledWireOutline(Rendering.Mode.BOX_WIRE_OUTLINE),
    WireOutline(Rendering.Mode.WIRE_OUTLINE),
    FilledGradient(Rendering.Mode.BOX_GRADIENT),
    WireGradient(Rendering.Mode.WIRE_GRADIENT),
    OutlineGradient(Rendering.Mode.OUTLINE_GRADIENT),
    FilledOutlineGradient(Rendering.Mode.BOX_OUTLINE_GRADIENT),
    FilledWireGradient(Rendering.Mode.BOX_WIRE_GRADIENT),
    FilledWireOutlineGradient(Rendering.Mode.BOX_WIRE_OUTLINE_GRADIENT),
    WireOutlineGradient(Rendering.Mode.WIRE_OUTLINE_GRADIENT)
}