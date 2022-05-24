package com.kisman.cc.module.client;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtilKt;
import com.kisman.cc.util.enums.FontStyles;

import java.util.*;

public class CustomFontModule extends Module {
    private final Setting antiAlias = register(new Setting("Anti Alias", this, true));
    private final Setting fractionMetrics = register(new Setting("Fraction Metrics", this, true));
    public Setting mode = register(new Setting("Mode", this, "Comfortaa", new ArrayList<>(Arrays.asList("Verdana", "Comfortaa", "Comfortaa Light", "Comfortaa Bold", "Consolas", "LexendDeca", "Futura", "SfUi", "Century"))));
    public final Setting style = register(new Setting("Style", this, FontStyles.Plain));

    public static boolean turnOn = false;

    public static CustomFontModule instance;

    private Enum<?> lastStyle = style.getValEnum();

    public CustomFontModule() {
        super("CustomFont", "custom font", Category.CLIENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        instance = this;
    }

    public void update() {
        turnOn = true;

        if(CustomFontUtilKt.Companion.getAntiAlias() != antiAlias.getValBoolean()) CustomFontUtilKt.Companion.setAntiAlias(antiAlias.getValBoolean());
        if(CustomFontUtilKt.Companion.getFractionMetrics() != fractionMetrics.getValBoolean()) {
            CustomFontUtilKt.Companion.setFractionalMetrics(fractionMetrics.getValBoolean());
            CustomFontUtilKt.Companion.setAntiAlias(antiAlias.getValBoolean());
        }

        if(lastStyle != style.getValEnum()) {
            CustomFontUtilKt.Companion.setFonts((FontStyles) style.getValEnum());
        }

        lastStyle = style.getValEnum();
    }
    public void onDisable(){
        turnOn = false;
    }
}
