package com.kisman.cc.loader

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin

/**
 * @author _kisman_
 * @since 12:13 of 04.07.2022
 */
class LavaFalconCoreMod : IFMLLoadingPlugin {
    private val lavahackMixinLoader : Any

    init {
        load()

        lavahackMixinLoader = Class.forName("com.kisman.cc.mixin.KismanMixinLoader").newInstance()
    }

    override fun getModContainerClass(): String? = null
    override fun getASMTransformerClass(): Array<String> = emptyArray()
    override fun getSetupClass(): String? = null
    override fun injectData(data: MutableMap<String, Any>?) {
        lavahackMixinLoader::class.java.getMethod("injectData", Map::class.java).invoke(lavahackMixinLoader, data)
    }
    override fun getAccessTransformerClass(): String? = null
}