package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.awt.*;
import java.util.Arrays;

public class NameTags extends Module {
    private Setting range = new Setting("Range", this, 0, 50 ,100, false);
    private Setting scale = new Setting("Scale", this, 0.8, 0.1, 1.5, false);
    private Setting yPos = new Setting("YPos", this, 0.1, 0, 1.5, false);
    private Setting bg = new Setting("Background", this, true);
    private Setting bgLight = new Setting("BGLight", this, 15, 0, 100, true);
    private Setting bgAlpha = new Setting("BGAlpha", this, 0, 0, 30, true);
    private Setting outline = new Setting("OutLine", this, true);
    private Setting outlineColor = new Setting("OutlineColor", this, "Outline", new float[] {0.3f, 0.3f, 0.3f, 1});

    private Setting textR = new Setting("TextR", this, 200, 0, 255, true);
    private Setting textG = new Setting("TextG", this, 200, 0, 255, true);
    private Setting textB = new Setting("TextB", this, 200, 0, 255, true);
    private Setting textA = new Setting("TextA", this, 255, 0, 255, true);
    private Setting textColorMode = new Setting("ColorMode", this, "Astolfo", Arrays.asList("Astolfo", "Rainbow", "Static"));

    private Setting saturatuon = new Setting("Saturation", this, 1, 0,1, false);
    private Setting bringhtness = new Setting("Bringhtness", this, 1, 0, 1,  false);
    private Setting delay = new Setting("Delay", this, 100, 1, 2000, true);

    public static NameTags instance;

    public NameTags() {
        super("NameTags", Category.RENDER);

        instance = this;

        setmgr.rSetting(range);
        setmgr.rSetting(scale);
        setmgr.rSetting(new Setting("BackgroundLine", this, "Background"));
        setmgr.rSetting(bg);
        setmgr.rSetting(bgLight);
        setmgr.rSetting(bgAlpha);
        setmgr.rSetting(outline);
        setmgr.rSetting(outlineColor);

        setmgr.rSetting(new Setting("TextLine", this, "Text"));
        setmgr.rSetting(textR);
        setmgr.rSetting(textG);
        setmgr.rSetting(textB);
        setmgr.rSetting(textA);
        setmgr.rSetting(textColorMode);

        setmgr.rSetting(new Setting("ColorLine", this, "Color"));
        setmgr.rSetting(saturatuon);
        setmgr.rSetting(bringhtness);
        setmgr.rSetting(delay);
    }

    public Color getColor() {
        switch (textColorMode.getValString()) {
            case "Astolfo": {
                return ColorUtils.astolfoColorsToColorObj(100, 100);
            }
            case "Rainbow": {
                return ColorUtils.rainbow(delay.getValInt(), saturatuon.getValFloat(), bringhtness.getValFloat());
            }
            default: return new Color(textR.getValInt(), textG.getValInt(), textB.getValInt(), textA.getValInt());
        }
    }
}
