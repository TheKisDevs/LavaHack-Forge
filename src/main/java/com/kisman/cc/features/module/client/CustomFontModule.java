package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.customfont.CustomFontUtilKt;
import com.kisman.cc.util.enums.FontStyles;

import java.util.*;

public class CustomFontModule extends Module {
    private final Setting antiAlias = register(new Setting("Anti Alias", this, true));
    private final Setting fractionMetrics = register(new Setting("Fraction Metrics", this, true));
    public Setting mode = register(new Setting("Mode", this, "Comfortaa", Arrays.asList("Verdana", "Comfortaa", "Comfortaa Light", "Comfortaa Bold", "Consolas", "LexendDeca", "Futura", "SfUi", "Century")));
    public final Setting style = register(new Setting("Style", this, FontStyles.Plain));
    public final Setting test = register(new Setting("Test", this, false));
    public final Setting test2 = register(new Setting("Test 2", this, false));
    public final Setting fallbackFont = register(new Setting("Fallback Font", this, false));
    public final Setting fallbackMode = register(new Setting("Fallback Mode", this, "Futura", Arrays.asList("Verdana", "Comfortaa", "Comfortaa Light", "Comfortaa Bold", "Consolas", "LexendDeca", "Futura", "SfUi", "Century")));
    public final Setting customSize = register(new Setting("Custom Size", this, false));
    public final Setting size = register(new Setting("Size", this, 18.0, 5.0, 30.0, true).setVisible(customSize::getValBoolean));

    public static boolean turnOn = false;

    public static CustomFontModule instance;

    public CustomFontModule() {
        super("CustomFont", "custom font", Category.CLIENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + (fallbackFont.getValBoolean() ? " | " + fallbackMode.getValString() : "") + "]");

        instance = this;
    }

    public void update() {
        turnOn = true;

        if(CustomFontUtilKt.Companion.getAntiAlias() != antiAlias.getValBoolean()) CustomFontUtilKt.Companion.setAntiAlias(antiAlias.getValBoolean());
        if(CustomFontUtilKt.Companion.getFractionMetrics() != fractionMetrics.getValBoolean()) {
            CustomFontUtilKt.Companion.setFractionalMetrics(fractionMetrics.getValBoolean());
            CustomFontUtilKt.Companion.setAntiAlias(antiAlias.getValBoolean());
        }
    }
    public void onDisable(){
        turnOn = false;
    }
}
