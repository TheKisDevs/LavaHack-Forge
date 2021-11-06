package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.entity.item.EntityEnderCrystal;

public class CrystalModifier extends Module {
    public static CrystalModifier instance;

    public Setting mode = new Setting("Mode", this, Modes.Fill);


    private Setting preview = new Setting("Crystal", this, "Crystal", new EntityEnderCrystal(mc.world));


    private Setting scaleLine = new Setting("ScaleLine", this, "Scale");

    public Setting scaleX = new Setting("ScaleX", this, 0, -2, 2, false);
    public Setting scaleY = new Setting("ScaleY", this, 0, -2, 2, false);
    public Setting scaleZ = new Setting("ScaleZ", this, 0, -2, 2, false);

    private Setting translateLine = new Setting("TranslateLine", this, "Translate");

    public Setting translateX = new Setting("TranslateX", this, 0, -2, 2, false);
    public Setting translateY = new Setting("TranslateY", this, 0, -2, 2, false);
    public Setting translateZ = new Setting("TranslateZ", this, 0, -2, 2, false);


    private Setting rotateLine = new Setting("RotateLine", this, "Rotate");

    public Setting rotateX = new Setting("RotateX", this, 0, 0, 360, true);
    public Setting rotateY = new Setting("RotateY", this, 0, 0, 360, true);
    public Setting rotateZ = new Setting("RotateZ", this, 0, 0, 360, true);

    private Setting crystalSettingLine = new Setting("CrystalSettingLine", this, "CrystalSetting");

    public Setting insideCube = new Setting("InsideCube", this, true);
    public Setting outsideCube = new Setting("OutsideCube", this, true);
    public Setting outsideCube2 = new Setting("OutsideCube2", this, true);
    public Setting customColor = new Setting("CustomColor", this, false);
    public Setting crystalColor = new Setting("CrystalColor", this, "Color", new float[] {0, 0, 1, 1});

    private Setting outlineLine = new Setting("OutLineLine", this, "OutLine");

    public Setting outline = new Setting("Outline", this, false);
    public Setting outlineMode = new Setting("OutlineMode", this, OutlineModes.Wire);
    public Setting lineWidth = new Setting("LineWidht", this, 3, 0.5, 5, false);
    public Setting color = new Setting("Color", this, "Color", new float[] {1, 0, 0, 1});


    private Setting enchantedLine = new Setting("EnchantedLine", this, "Enchanted");

    public Setting enchanted = new Setting("Enchanted", this, false);
    public Setting enchColor = new Setting("EnchColor", this, "Color", new float[] {0, 1, 0, 1});

    public CrystalModifier() {
        super("CrystalCharms", "r", Category.RENDER);

        instance = this;

        setmgr.rSetting(mode);

        setmgr.rSetting(preview);

        setmgr.rSetting(scaleLine);
        setmgr.rSetting(scaleX);
        setmgr.rSetting(scaleY);
        setmgr.rSetting(scaleZ);

        setmgr.rSetting(translateLine);
        setmgr.rSetting(translateX);
        setmgr.rSetting(translateY);
        setmgr.rSetting(translateZ);

        setmgr.rSetting(rotateLine);
        setmgr.rSetting(rotateX);
        setmgr.rSetting(rotateY);
        setmgr.rSetting(rotateZ);

        setmgr.rSetting(crystalSettingLine);
        setmgr.rSetting(insideCube);
        setmgr.rSetting(outsideCube);
        setmgr.rSetting(outsideCube2);
        setmgr.rSetting(customColor);
        setmgr.rSetting(crystalColor);

        setmgr.rSetting(outlineLine);
        setmgr.rSetting(outlineMode);
        setmgr.rSetting(lineWidth);
        setmgr.rSetting(color);

        setmgr.rSetting(enchantedLine);
        setmgr.rSetting(enchanted);
        setmgr.rSetting(enchColor);
    }

    public enum OutlineModes {
        Wire,
        Flat
    }

    public enum Modes {
        Fill,
        Wireframe
    }
}
