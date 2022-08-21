package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiModule extends Module {
    public final Setting primaryColor = register(new Setting("Primary Color", this, "Primary Color", new Colour(255, 0, 0)));
    public final Setting background = register(new Setting("Background", this, true));
    public final Setting backgroundColor = register(new Setting("Background Color", this, "Background Color", new Colour(30, 30, 30, 121)).setVisible(background::getValBoolean));
    public final Setting shadow = register(new Setting("Shadow", this, false));
    public final Setting test = register(new Setting("test", this, false));
    public final Setting shadowRects = register(new Setting("Shadow Rects", this, false));
    public final Setting line = register(new Setting("Line", this, true));
    public final Setting offsets = register(new Setting("Offsets test uwu owo", this, 0.0, 0.0, 5.0, true));
    public final Setting uwu = register(new Setting("UwU Locate Mod", this, HalqGui.LocateMode.Left));
    public final Setting test2 = register(new Setting("Test 2", this, true));
    public final Setting idkJustAlpha = register(new Setting("Idk Just Alpha", this, 30, 30, 255, true));
    public final Setting textOffsetX = register(new Setting("Text X Offset", this, 5, 0, 10, true));

    public static GuiModule instance;

    public GuiModule() {
        super("Gui", Category.CLIENT);
        super.setKeyboardKey(Keyboard.KEY_RSHIFT);
        super.setType(BindType.Keyboard);

        instance = this;
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.halqGui.setLastGui(null));
        super.setToggled(false);
        if(Config.instance.guiBlur.getValBoolean()) mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }
}
