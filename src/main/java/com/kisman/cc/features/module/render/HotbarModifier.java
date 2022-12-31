package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;

import java.awt.*;

public class HotbarModifier extends Module {
    public final Setting containerShadow = register(new Setting("Shadow", this, false));
    public final Setting color = register(new Setting("Color", this, new Colour(255, 255, 255, 152)).setVisible(containerShadow::getValBoolean));
    public final Setting offhand = register(new Setting("Offhand", this, true).setVisible(containerShadow::getValBoolean));
    public final Setting offhandGradient = register(new Setting("Offhand Gradient", this, false).setVisible(() -> offhand.getValBoolean() && containerShadow.getValBoolean()));

    @ModuleInstance
    public static HotbarModifier instance;

    public HotbarModifier() {
        super("HotbarModifier", "Extra features of your hotbar!", Category.RENDER);
    }

    public static Color getPrimaryColor() {
        return instance.color.getColour().getColor();
    }
}
