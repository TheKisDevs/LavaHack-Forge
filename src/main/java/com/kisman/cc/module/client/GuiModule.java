package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import net.minecraft.util.ResourceLocation;

public class GuiModule extends Module {
    public final Setting primaryColor = register(new Setting("Primary Color", this, "Primary Color", new Colour(255, 0, 0)));
    public final Setting background = register(new Setting("Background", this, true));
    public final Setting backgroundColor = register(new Setting("Background Color", this, "Background Color", new Colour(30, 30, 30, 121)).setVisible(background::getValBoolean));
    public final Setting shadow = register(new Setting("Shadow", this, false));
    public final Setting test = register(new Setting("test", this, false));
    public final Setting shadowRects = register(new Setting("Shadow Rects", this, false));
    public final Setting line = register(new Setting("Line", this, true));

    public static GuiModule instance;

    public GuiModule() {
        super("Gui", Category.CLIENT);

        instance = this;
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.halqGui);
        super.setToggled(false);
        if(Config.instance.guiBlur.getValBoolean()) mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }
}
