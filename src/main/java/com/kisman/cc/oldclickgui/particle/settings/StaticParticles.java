package com.kisman.cc.oldclickgui.particle.settings;

import com.kisman.cc.module.client.Config;

import java.awt.*;

public class StaticParticles {
    //Particles Color
    public static Color color = Color.ORANGE;

    //Update void
    public static void onUpdate() {
        //sets particles color to the one from settings
        color = Config.instance.particlesColor.getColour().getColor();
    }
}
