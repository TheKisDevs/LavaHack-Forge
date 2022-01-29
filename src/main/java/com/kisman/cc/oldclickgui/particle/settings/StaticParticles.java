package com.kisman.cc.oldclickgui.particle.settings;

import com.kisman.cc.Kisman;

import java.awt.*;

public class StaticParticles {

    //Particles Color
    public static Color color = Color.ORANGE;

    //Update void
    public static void onUpdate()
    {
        //sets particles color to the one from settings
        color = Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("VegaGui"), "Particles Color").getColour().getColor();
    }

}
