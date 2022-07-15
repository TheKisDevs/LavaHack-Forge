package com.kisman.cc.settings

import com.kisman.cc.util.enums.CharmsRewriteEntityTypes
import com.kisman.cc.util.enums.CharmsRewriteTypes
import com.kisman.cc.util.enums.dynamic.CharmsRewriteOptionsEnum

/**
 * @author _kisman_
 * @since 18:09 of 11.07.2022
 */
class CharmsRewriteSetting(
    val setting : Setting,
    val typeE : CharmsRewriteEntityTypes,
    val typeS : CharmsRewriteTypes,
    val option : CharmsRewriteOptionsEnum.CharmsRewriteOptions?
) {
    constructor(
        setting : Setting,
        typeE : CharmsRewriteEntityTypes,
        typeS : CharmsRewriteTypes
    ) : this(
        setting,
        typeE,
        typeS,
        null
    )
}