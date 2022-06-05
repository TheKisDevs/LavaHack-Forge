package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;

public class Charms extends Module {
    public Setting wallHack = register(new Setting("WallHack", this, true));
    public Setting targetRender = register(new Setting("Targets", this, true));
    public Setting friends = register(new Setting("Friends", this, true));
    public Setting customColor = register(new Setting("Use Color", this, false));
    public Setting color = register(new Setting("Color", this, "Color", new Colour(255, 0, 0)).setVisible(customColor::getValBoolean));

    public static Charms instance;

    public Charms() {
        super("Charms", "Charms", Category.RENDER);
        instance = this;
    }
}
