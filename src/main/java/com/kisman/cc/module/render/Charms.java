package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class Charms extends Module {
    public Setting textureMode = new Setting("TextureMode", this, "Texture", new ArrayList<>(Arrays.asList("Texture", "GL")));
    public Setting polygonOffset = new Setting("PolygonOffset", this, true);
    public Setting polygonMode = new Setting("PolygonMode", this, "RenderModel", new ArrayList<>(Arrays.asList("RenderModel", "doRender")));
    public Setting targetRender = new Setting("TargetRender", this, true);
    public Setting render = new Setting("Redner", this, false);
    public Setting customColor = new Setting("CustomColor", this, false);
    public Setting color = new Setting("Color", this, "Color", new float[] {1, 0, 0, 1});

    public static Charms instance;

    public Charms() {
        super("Charms", "Charms", Category.RENDER);

        instance = this;

        setmgr.rSetting(textureMode);
        setmgr.rSetting(polygonOffset);
        setmgr.rSetting(polygonMode);

        Kisman.instance.settingsManager.rSetting(new Setting("Texture", this, false));
        setmgr.rSetting(render);
        setmgr.rSetting(targetRender);

        setmgr.rSetting(customColor);
        setmgr.rSetting(color);
    }

    public void onEnable() {
        if(Kisman.instance.moduleManager.getModule("KismanESP").isToggled()) {
            Kisman.instance.moduleManager.getModule("KismanESP").setToggled(false);
        }
    }
}
