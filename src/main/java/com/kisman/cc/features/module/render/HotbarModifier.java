package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.ColorUtils;

import java.awt.*;

public class HotbarModifier extends Module {
    public Setting containerShadow = register(new Setting("Shadow", this, false));
    public Setting color = register(new Setting("Color", this, new Colour(255, 255, 255, 152)).setVisible(containerShadow::getValBoolean));
    public Setting offhand = register(new Setting("Offhand", this, true).setVisible(containerShadow::getValBoolean));
    public Setting offhandGradient = register(new Setting("Offhand Gradient", this, false).setVisible(() -> offhand.getValBoolean() && containerShadow.getValBoolean()));

    public static HotbarModifier instance;

    public HotbarModifier() {
        super("HotbarModifier", Category.RENDER);

        instance = this;
    }

    public static Color getPrimaryColor() {
        return instance.color.getColour().getColor();
    }
}
