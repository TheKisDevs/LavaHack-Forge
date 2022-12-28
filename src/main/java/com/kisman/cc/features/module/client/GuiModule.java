package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.Colour;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiModule extends Module {
    private final SettingGroup colorGroup = register(new SettingGroup(new Setting("Colors", this)));
    public final Setting primaryColor = register(colorGroup.add(new Setting("Primary Color", this, "Primary", new Colour(255, 0, 0))));
    public final Setting background = register(colorGroup.add(new Setting("Background", this, true).setTitle("Background")));
    public final Setting backgroundColor = register(colorGroup.add(new Setting("Background Color", this, "Background", new Colour(30, 30, 30, 121)).setVisible(background::getValBoolean)));
    public final Setting hoverColor = register(colorGroup.add(new Setting("Hover Color", this, "Hover", new Colour(255, 255, 255, 60))));
    public final Setting componentHeight = register(new Setting("Component Height", this, 13, 1, 30, true));
    public final Setting headerOffset = register(new Setting("Header Offset", this, 5, 0, 20, true));
    public final Setting shadow = register(new Setting("Shadow", this, true));
    public final Setting hideAnnotations = register(new Setting("Hide Annotations", this, false));
    private final SettingGroup lineGroup = register(new SettingGroup(new Setting("Lines", this)));
    public final Setting horizontalLines = register(lineGroup.add(new Setting("Horizontal Lines", this, true).setTitle("Horizontal")));
    public final Setting verticalLines = register(lineGroup.add(new Setting("Vertical Lines", this, true).setTitle("Vertical")));
    public final Setting shadowRects = /*register*/(new Setting("Shadow Rects", this, false));
    public final Setting offsetsX = register(new Setting("Offsets test uwu owo", this, 1.0, 0.0, 5.0, false));
    public final Setting offsetsY = register(new Setting("ONG OFFSETS Y??", this, 1, 0, 5, false));
    public final Setting uwu = register(new Setting("UwU Locate Mod", this, HalqGui.LocateMode.Left));
    public final Setting test2 = register(new Setting("Test 2", this, true));
    public final Setting test2Color = register(new Setting("Test 2 Color", this, new Colour(30, 30, 30, 121)));
    public final Setting idkJustAlpha = register(new Setting("Idk Just Alpha", this, 30, 30, 255, true));
    public final Setting textOffsetX = register(new Setting("Text X Offset", this, 5, 0, 10, true));
    private final SettingGroup outlineGroup = register(new SettingGroup(new Setting("Outline", this)));
    public final Setting outlineColor = register(outlineGroup.add(new Setting("Outline Color", this, new Colour(0, 0, 0, 255)).setTitle("Color")));
    public final Setting outlineTest = register(outlineGroup.add(new Setting("Outline Test", this, true).setTitle("Test")));
    public final Setting outlineHeaders = register(outlineGroup.add(new Setting("Outline Headers", this, false).setTitle("Headers")));
    public final Setting outlineTest2 = register(outlineGroup.add(new Setting("Outline Test 2", this, true).setTitle("Test 2")));
    public final Setting componentsOutline = register(outlineGroup.add(new Setting("Components Outline", this, false).setTitle("Components")));
    public final Setting lineWidth = register(outlineGroup.add(new Setting("Line Width", this, 1, 0.1, 2, false)));
    public final SettingGroup colorPickerGroup = register(new SettingGroup(new Setting("Color Picker", this)));
    public final Setting colorPickerExtra = register(colorPickerGroup.add(new Setting("Color Picker Extra", this, false).setTitle("Extra")));
    public final Setting colorPickerClearColor = register(colorPickerGroup.add(new Setting("Color Picker Clear Color", this, false).setTitle("Clear Color")));
    public final Setting colorPickerCopyPaste = register(colorPickerGroup.add(new Setting("Color Picker Copy Paste", this, false).setTitle("Copy Paste")));

    @ModuleInstance
    public static GuiModule instance;

    public GuiModule() {
        super("Gui", Category.CLIENT);
        IBindable.bindKey(this, Keyboard.KEY_RSHIFT);
        super.dontSendToggleMessages();
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.halqGui.setLastGui(null));
        super.setToggled(false);
        if(Config.instance.guiBlur.getValBoolean()) mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }
}
