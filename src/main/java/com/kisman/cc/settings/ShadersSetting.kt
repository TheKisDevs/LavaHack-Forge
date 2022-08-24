package com.kisman.cc.settings

import com.kisman.cc.util.enums.ShadersObjectTypes
import com.kisman.cc.util.enums.ShadersSettingTypes
import com.kisman.cc.util.enums.ShadersShaders
import com.kisman.cc.util.enums.dynamic.ShadersObjectsEnum

/**
 * @author _kisman_
 * @since 14:39 of 16.08.2022
 */
class ShadersSetting(
    val setting : Setting,
    val type : ShadersObjectTypes,
    val option : ShadersObjectsEnum.ShadersObjects,
    val shader : ShadersShaders?,
    val typeS : ShadersSettingTypes,
    val shaderUniformIndex : Int
)