package com.kisman.cc.gui.particle;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.util.Colour;

import java.awt.*;

public class StaticParticles {
    //Particles Color
    public static Colour color = new Colour(Color.ORANGE);

    //Start Gradient Particles Color
    public static Colour startColor = new Colour(Color.CYAN);

    //End Gradient Particles Color
    public static Colour endColor = new Colour(Color.PINK);

    //End Gradient Particles Color
    public static Colour middleColor = new Colour(Color.PINK);

    //Will make particles color gradient if enabled
    public static boolean IsTwoGParticlesEnabled = true;

    //The var on what will depend particles width
    public static float particleWidth = 2.5f;

    //Particles Current Mode
    public static String mode = "Default";

    //Particles Mode Type
    public static String modeDEfType = Config.ParticlesGradientMode.TwoGradient.name();

    //Particles Three Gradient Mode Type
    public static String modeTGfType = Config.ParticlesGradientMode.ThreeGradient.name();

    public static boolean lines = false;
    public static boolean randomPointsAlpha = false;
    public static float pointSizeModifier = 1f;
    public static boolean points = true;
    public static int startPointsCount = 300;

    //Update void
    public static void onUpdate() {
        //sets particles color to the one from settings
        color = Config.instance.particlesColor.getColour();

        //sets particles start gradient color to the one from settings
        startColor = Config.instance.particlesGStartColor.getColour();

        //sets particles end gradient color to the one from settings
        endColor = Config.instance.particlesGEndColor.getColour();

        //sets particles end gradient color to the one from settings
        middleColor = Config.instance.particlesGMiddleColor.getColour();

        //sets particles gradient var to the one from settings
        IsTwoGParticlesEnabled = !Config.instance.particlesGradientMode.getValString().equals(Config.ParticlesGradientMode.None.name());

        //sets particles width var to the one from settings
        particleWidth = Config.instance.particlesWidth.getValFloat();

        //sets particles current rendering mode to the one from settings
        mode = Config.instance.particlesGradientMode.getValString();

        lines = Config.instance.particlesRenderLine.getValBoolean();
        randomPointsAlpha = Config.instance.particlePointsRandomAlpha.getValBoolean();
        pointSizeModifier = Config.instance.particlePointSizeModifier.getValFloat();
        points = Config.instance.particlesRenderPoints.getValBoolean();
        startPointsCount = Config.instance.particlesStartPointsCount.getValInt();
    }
}
