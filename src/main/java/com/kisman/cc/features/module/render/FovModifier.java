package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Deprecated
public class FovModifier extends Module {

    private final Setting fov = register(new Setting("SettingFOV", this, 1, 0, 10, false));
    private final Setting staticFov = register(new Setting("Static", this, true));

    public FovModifier(){
        super("FovModifier", Category.RENDER, true);
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent event){
        event.setNewfov(staticFov.getValBoolean() ? fov.getValFloat() : event.getFov() * fov.getValFloat());
    }
}