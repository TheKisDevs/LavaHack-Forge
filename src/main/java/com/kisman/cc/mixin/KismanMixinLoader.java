package com.kisman.cc.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("kys")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class KismanMixinLoader implements IFMLLoadingPlugin {
    public KismanMixinLoader(){
        MixinBootstrap.init();
        Mixins.addConfiguration/*s*/("mixins.Kisman.json"/*,*/ /*"mixins.baritone.json"*/);
//        Mixins.addConfiguration("mixins.baritone.json");
//        Kisman.instance.coreModInit();
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String[] getASMTransformerClass(){
        return new String[0];
    }

    @Override
    public String getModContainerClass(){
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass(){
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data){
//        MixinBootstrap.init();
//        Mixins.addConfiguration("mixins.Kisman.json");
//        Mixins.addConfiguration("mixins.baritone.json");
//        Kisman.instance.pluginHandler.coreModInit();
//        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String getAccessTransformerClass(){
        return null;
    }
}
