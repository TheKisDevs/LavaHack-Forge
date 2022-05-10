package com.kisman.cc.util.enums

enum class CPvPAIPrimaryHolesToFill(
        val bedrock : Boolean,
        val double : Boolean
) {
    BedrockSingle(true, false),
    ObbySingle(false, false),
    BedrockDouble(true, true),
    ObbyDouble(false, true)
}