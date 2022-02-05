package com.kisman.cc.oldclickgui.particle;

import com.kisman.cc.module.client.Config;

import java.awt.*;

public class StaticParticles {
    //Particles Color
    public static Color color = Color.ORANGE;

    //Start Gradient Particles Color
    public static Color startColor = Color.CYAN;

    //End Gradient Particles Color
    public static Color endColor = Color.PINK;

    //Will make particles color gradient if enabled
    public static boolean IsTwoGParticlesEnabled = true;

    //The var on what will depend particles width
    public static float particleWidth = 2.5f;

    //Update void
    public static void onUpdate() {
        //sets particles color to the one from settings
        color = Config.instance.particlesColor.getColour().getColor();

        //sets particles start gradient color to the one from settings
        startColor = Config.instance.particlesGStartColor.getColour().getColor();

        //sets particles end gradient color to the one from settings
        endColor = Config.instance.particlesGEndColor.getColour().getColor();

        //sets particles gradient var to the one from settings
        IsTwoGParticlesEnabled = Config.instance.TwoGradientParticles.getValBoolean();

        //sets particles width var to the one from settings
        particleWidth = Config.instance.particlesWidth.getValFloat();
    }
}
