package com.kisman.cc.gui.api

import com.kisman.cc.util.collections.Bind

/**
 * @author _kisman_
 * @since 18:27 of 19.01.2023
 */
interface Shaderable {
    fun normalRender() : Runnable/*pre render thing*/
    fun shaderRender() : Bind<Runnable/*shaderable thing*/, Runnable/*post render thing*/>
}