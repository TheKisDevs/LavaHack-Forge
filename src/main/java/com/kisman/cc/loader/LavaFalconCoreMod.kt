package com.kisman.cc.loader

import com.kisman.cc.Kisman
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.*

/**
 * @author _kisman_
 * @since 12:13 of 04.07.2022
 */
@Name(LavaFalconMod.NAME)
@MCVersion("1.12.2")
class LavaFalconCoreMod : IFMLLoadingPlugin {
    private val lavahackMixinLoader : Any

    init {
        if(Utility.runningFromIntelliJ()) {
            Kisman.LOGGER.debug("Not loading due to running in debugging environment!")
        } else {
            load()

        }

        lavahackMixinLoader = Class.forName("com.kisman.cc.mixin.KismanMixinLoader").newInstance()

        /*try {
            Class.forName("com.kisman.cc.Kisman").getMethod("init").invoke(Class.forName("com.kisman.cc.Kisman").getDeclaredField("instance")[null])
        } catch (e: Exception) {
            e.printStackTrace()
            exit()
        }*/
    }

    private fun exit() {
        println("Cant find main class of lavahack or preInit/init method or instance field! Shutdown!")
        Minecraft.getMinecraft().shutdown()
    }

    override fun getModContainerClass(): String? = null
    override fun getASMTransformerClass(): Array<String> = emptyArray()
    override fun getSetupClass(): String? = null
    override fun injectData(data: MutableMap<String, Any>?) {
        lavahackMixinLoader::class.java.getMethod("injectData", Map::class.java).invoke(lavahackMixinLoader, data)
    }
    override fun getAccessTransformerClass(): String? = null
}