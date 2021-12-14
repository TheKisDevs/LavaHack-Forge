package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.awt.*;

public class Ambience extends Module {
    public static Ambience instance;

    public Setting red = new Setting("Red", this, 0.2, 0, 1, false);
    public Setting green = new Setting("Green", this, 0.2, 0, 1, false);
    public Setting blue = new Setting("Blue", this, 0.2, 0, 1, false);
    public Setting alpha = new Setting("Alpha", this, 1, 0, 1, false);

    public Setting useSaturation = new Setting("UseSaturation", this, false);
    public Setting saturation = new Setting("Saturation", this, 0.5, 0, 1, false);

    public Ambience() {
        super("Ambience", "minecraqft color", Category.RENDER);

        instance = this;

        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(alpha);
        setmgr.rSetting(useSaturation);
        setmgr.rSetting(saturation);
    }

    public Color getColor() {
        return new Color((float) red.getValDouble(),(float) green.getValDouble(), (float) blue.getValDouble(), (float) alpha.getValDouble());
    }
}
