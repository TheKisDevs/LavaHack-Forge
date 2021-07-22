package com.kisman.cc.mixin;

import java.util.Map;

import javax.annotation.Nullable;

import com.kisman.cc.Kisman;
import net.minecraftforge.fml.common.LoaderException;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class KismanMixinLoader implements IFMLLoadingPlugin {

    public KismanMixinLoader(){
            //Kisman.LOGGER.info("mixin init");
            MixinBootstrap.init();
            Mixins.addConfiguration("mixins.Kisman.json");
            //MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
            //Kisman.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
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
        //isObfuscatedEnvironment = (boolean) (Boolean) data.get ( "runtimeDeobfuscationEnabled" );
    }

    @Override
    public String getAccessTransformerClass(){
        return null;
    }
}
