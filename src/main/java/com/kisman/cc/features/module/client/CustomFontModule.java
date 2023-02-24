package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.enums.FontStyles;
import com.kisman.cc.util.enums.Fonts;
import com.kisman.cc.util.render.customfont.CustomFontUtilKt;

import java.util.Arrays;

public class CustomFontModule extends Module {
    public SettingEnum<Fonts> font;/*Fonts> font = register(new SettingEnum<>("Mode", this, Fonts.Verdana));*/
    private Setting antiAlias;/* = register(new Setting("Anti Alias", this, true));*/
    private Setting fractionMetrics;/* = register(new Setting("Fraction Metrics", this, true));*/
    public Setting style;/* = register(new Setting("Style", this, FontStyles.Plain));*/
    public Setting test;/* = register(new Setting("Test", this, false));*/
    public Setting test2;/* = register(new Setting("Test 2", this, false));*/
    public Setting multiLineOffset;/* = register(new Setting("Multi Line Offset", this, 2, 0, 15, true));*/
    public Setting fallbackFont;/* = *//*register*//*(new Setting("Fallback Font", this, false));*/
    public Setting fallbackMode;/* = *//*register*//*(new Setting("Fallback Mode", this, "Futura", Arrays.asList("Verdana", "Comfortaa", "Comfortaa Light", "Comfortaa Bold", "Consolas", "LexendDeca", "Futura", "SfUi", "Century")));*/
    public Setting customSize;/* = register(new Setting("Custom Size", this, false));*/
    public Setting size;/* = register(new Setting("Size", this, 18.0, 5.0, 30.0, true).setVisible(customSize::getValBoolean));*/

    public static CustomFontModule instance = new CustomFontModule();

    private CustomFontModule() {
        super("CustomFont", "custom font", Category.CLIENT);
        super.setDisplayInfo(() -> "[" + font.getValString() + (fallbackFont.getValBoolean() ? " | " + fallbackMode.getValString() : "") + "]");
    }

    public void update() {
        if(CustomFontUtilKt.Companion.getAntiAlias() != antiAlias.getValBoolean()) CustomFontUtilKt.Companion.setAntiAlias(antiAlias.getValBoolean());
        if(CustomFontUtilKt.Companion.getFractionMetrics() != fractionMetrics.getValBoolean()) {
            CustomFontUtilKt.Companion.setFractionalMetrics(fractionMetrics.getValBoolean());
            CustomFontUtilKt.Companion.setAntiAlias(antiAlias.getValBoolean());
        }

        CustomFontUtilKt.Companion.setMultiLineOffset(multiLineOffset.getValInt());
    }

    public void registerSettings() {
        font = register(new SettingEnum<>("Mode", this, Fonts.Verdana));
        antiAlias = register(new Setting("Anti Alias", this, true));
        fractionMetrics = register(new Setting("Fraction Metrics", this, true));
        style = register(new Setting("Style", this, FontStyles.Plain));
        test = register(new Setting("Test", this, false));
        test2 = register(new Setting("Test 2", this, false));
        multiLineOffset = register(new Setting("Multi Line Offset", this, 2, 0, 15, true));
        fallbackFont = /*register*/(new Setting("Fallback Font", this, false));
        fallbackMode = /*register*/(new Setting("Fallback Mode", this, "Futura", Arrays.asList("Verdana", "Comfortaa", "Comfortaa Light", "Comfortaa Bold", "Consolas", "LexendDeca", "Futura", "SfUi", "Century")));
        customSize = register(new Setting("Custom Size", this, false));
        size = register(new Setting("Size", this, 18.0, 5.0, 30.0, true).setVisible(customSize::getValBoolean));
    }
}
