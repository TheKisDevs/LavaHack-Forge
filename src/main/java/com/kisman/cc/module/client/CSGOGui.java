package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class CSGOGui extends Module {
    public static CSGOGui instance;

    public Setting customSize = new Setting("CustomFontSize", this, false);

    public CSGOGui() {
        super("CsgoGui", "CSGOGui", Category.CLIENT);
        super.setKey(Keyboard.KEY_U);

        instance = this;

        setmgr.rSetting(customSize);
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.clickGuiNew);
        this.setToggled(false);

        if(Config.instance.guiBlur.getValBoolean())
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));

    }
}
