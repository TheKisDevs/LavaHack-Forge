package com.kisman.cc.loader

import com.kisman.cc.Kisman
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins

/**
 * @author _kisman_
 * @since 12:13 of 04.07.2022
 */
@MCVersion("1.12.2")
class LavaHackLoaderCoreMod : IFMLLoadingPlugin {
//    private val lavahackMixinLoader : Any

    init {
        if(Utility.runningFromIntelliJ()) {
            Kisman.LOGGER.debug("Not loading due to running in debugging environment!")
        } else {
            initLoader()
            suspend()
        }

//        initHook()

        MixinBootstrap.init()
        Mixins.addConfiguration("mixins.Kisman.json")
        Mixins.addConfiguration("mixins.loader.json")
        MixinEnvironment.getDefaultEnvironment().obfuscationContext = "searge"

//        lavahackMixinLoader = Class.forName("com.kisman.cc.mixin.KismanMixinLoader").newInstance()
    }

    override fun getModContainerClass(): String? = null
    override fun getASMTransformerClass(): Array<String> = emptyArray()
    override fun getSetupClass(): String? = null
    override fun injectData(data: MutableMap<String, Any>?) {
//        lavahackMixinLoader::class.java.getMethod("injectData", Map::class.java).invoke(lavahackMixinLoader, data)
    }
    override fun getAccessTransformerClass(): String? = null

    companion object {
        @JvmStatic private val thread = Thread.currentThread()
        @JvmStatic fun resume() { thread.resume() }
        @JvmStatic fun suspend() { thread.suspend() }

        @JvmStatic var loaded = false
    }
}