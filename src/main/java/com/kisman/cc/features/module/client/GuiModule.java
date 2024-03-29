package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.ShaderPattern;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.enums.dynamic.EasingEnum;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "Gui",
        category = Category.CLIENT,
        key = Keyboard.KEY_RSHIFT
)
public class GuiModule extends Module {
    private final SettingGroup colorGroup = register(new SettingGroup(new Setting("Colors", this)));
    public final Setting primaryColor = register(colorGroup.add(new Setting("Primary Color", this, "Primary", new Colour(255, 0, 0))));
    public final Setting background = register(colorGroup.add(new Setting("Background", this, true).setTitle("Background")));
    public final Setting backgroundColor = register(colorGroup.add(new Setting("Background Color", this, "Background", new Colour(30, 30, 30, 121)).setVisible(background::getValBoolean)));
    public final Setting hoverColor = register(colorGroup.add(new Setting("Hover Color", this, "Hover", new Colour(255, 255, 255, 60))));
    public final Setting descriptionColor = register(colorGroup.add(new Setting("Description Color", this, "Description", new Colour(0, 0, 0, 120))));
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
    public final Setting minPrimaryAlpha = register(new Setting("Min Primary Alpha", this, 30, 30, 255, true));
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

    public final SettingGroup shadersGroup = register(new SettingGroup(new Setting("Shaders", this)));
    public final Setting shaderState = register(shadersGroup.add(new Setting("Shader State", this, false).setTitle("State")));
    public final ShaderPattern shaders = new ShaderPattern(this).group(shadersGroup).prefix("Shaders").preInit().init();

    public final Setting gradientFrameDiff = register(new Setting("Gradient Frame Diff", this, 0.0, 0.0, 20, NumberType.TIME));
    private final SettingGroup animationGroup = register(new SettingGroup(new Setting("Animations", this)));
    public final Setting animationState = register(animationGroup.add(new Setting("Animation State", this, true).setTitle("State")));
    public final Setting animationSpeed = register(animationGroup.add(new Setting("Animation Speed", this, 750, 100, 1000, NumberType.TIME).setTitle("Length")));
    public final SettingEnum<EasingEnum.Easing> animationEasing = register(animationGroup.add(new SettingEnum<>("Animation Easing", this, EasingEnum.Easing.Curve).setTitle("Easing")));
    public final Setting animationCoolAnimation = register(animationGroup.add(new Setting("Cool Animation", this, false)));
    public final Setting animationDirection = register(animationGroup.add(new Setting("Animation Direction", this, true).setTitle("Direction")));
    public final Setting animationReverseDirection = register(animationGroup.add(new Setting("Animation Reverse Direction", this, false).setTitle("Reverse Direction")));
    public final Setting animationAlpha = register(animationGroup.add(new Setting("Animation Alpha", this, false).setTitle("Alpha")));
    public final Setting animationBothSide = register(animationGroup.add(new Setting("Animation Both Side", this, false).setTitle("Both Side")));
    private final SettingGroup animationTypes = register(animationGroup.add(new SettingGroup(new Setting("Types", this))));
    public final Setting animateToggleable = register(animationTypes.add(new Setting("Animate Toggleable", this, true).setTitle("Toggleable")));
    public final Setting animateHover = register(animationTypes.add(new Setting("Animate Hover", this, false).setTitle("Hover")));
    public final Setting animateSlider = register(animationTypes.add(new Setting("Animate Slider", this, false).setTitle("Slider")));
    public final Setting openIndicator = register(new Setting("Open Indicator", this, true));
    public final Setting layerStepOffset = register(new Setting("Layer Step Offset", this, 5, 0, 10, true));
    public final Setting scale = register(new Setting("Scale", this, 1, 0.5, 2, false));
    public final Setting searchSettings = register(new Setting("Search Settings", this, false).setTitle("Settings"));

    @ModuleInstance
    public static GuiModule instance;

    public GuiModule() {
        super.dontSendToggleMessages();
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.halqGui.setLastGui(null));
        super.setToggled(false);
    }
}
