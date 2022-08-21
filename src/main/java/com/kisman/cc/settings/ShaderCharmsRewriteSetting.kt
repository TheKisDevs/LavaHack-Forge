package com.kisman.cc.settings

import com.kisman.cc.util.enums.ShaderCharmsRewriteObjectTypes
import com.kisman.cc.util.enums.ShaderCharmsRewriteSettingTypes
import com.kisman.cc.util.enums.ShaderCharmsRewriteShaders
import com.kisman.cc.util.enums.dynamic.ShaderCharmsObjectsEnum

/**
 * @author _kisman_
 * @since 14:39 of 16.08.2022
 */
class ShaderCharmsRewriteSetting(
    val setting : Setting,
    val type : ShaderCharmsRewriteObjectTypes,
    val option : ShaderCharmsObjectsEnum.ShaderCharmsRewriteObjects,
    val shader : ShaderCharmsRewriteShaders?,
    val typeS : ShaderCharmsRewriteSettingTypes,
    val shaderUniformIndex : Int
)