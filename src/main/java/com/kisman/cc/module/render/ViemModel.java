package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.*;

public class ViemModel extends Module {
    double x = 0;
    double y = 0;
    double z = 0;

    double rx = 0, ry = 0, rz = 0;

    double sx = 1, sy = 1, sz = 1;

    boolean xd;
    boolean yd;
    boolean zd;

    public ViemModel() {
        super("ViemModel", "modeL vieM", Category.RENDER);
        Kisman.instance.settingsManager.rSetting(new Setting("XLine", this, "X"));
        Kisman.instance.settingsManager.rSetting(new Setting("X", this, x, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("XDefault", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("YLine", this, "Y"));
        Kisman.instance.settingsManager.rSetting(new Setting("Y", this, y, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("YDefault", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ZLine", this, "Z"));
        Kisman.instance.settingsManager.rSetting(new Setting("Z", this, z, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ZDefault", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotationLine", this, "Rotation"));
        Kisman.instance.settingsManager.rSetting(new Setting("XR", this, rx, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("YR", this, ry, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ZR", this, rz, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeLine", this, "Size"));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeX", this, sx, 0, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeY", this, sy, 0, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeZ", this, sz, 0, 2, false));
    }

    public void update() {
        this.xd = Kisman.instance.settingsManager.getSettingByName(this, "XDefault").getValBoolean();
        this.yd = Kisman.instance.settingsManager.getSettingByName(this, "YDefault").getValBoolean();
        this.zd = Kisman.instance.settingsManager.getSettingByName(this, "ZDefault").getValBoolean();

        this.x = Kisman.instance.settingsManager.getSettingByName(this, "X").getValDouble();
        this.y = Kisman.instance.settingsManager.getSettingByName(this, "Y").getValDouble();
        this.z = Kisman.instance.settingsManager.getSettingByName(this, "Z").getValDouble();

        this.rx = Kisman.instance.settingsManager.getSettingByName(this, "XR").getValDouble();
        this.ry = Kisman.instance.settingsManager.getSettingByName(this, "YR").getValDouble();
        this.rz = Kisman.instance.settingsManager.getSettingByName(this, "ZR").getValDouble();

        this.sx = Kisman.instance.settingsManager.getSettingByName(this, "SizeX").getValDouble();
        this.sy = Kisman.instance.settingsManager.getSettingByName(this, "SizeY").getValDouble();
        this.sz = Kisman.instance.settingsManager.getSettingByName(this, "SizeZ").getValDouble();

        if(xd) {
            this.x = 0;
            Kisman.instance.settingsManager.getSettingByName(this, "XDefault").setValBoolean(false);
        }
        if(yd) {
            this.y = 0;
            Kisman.instance.settingsManager.getSettingByName(this, "YDefault").setValBoolean(false);
        }
        if(zd) {
            this.z = 0;
            Kisman.instance.settingsManager.getSettingByName(this, "ZDefault").setValBoolean(false);
        }
    }

    @SubscribeEvent
    public void onRenderArms(final RenderSpecificHandEvent event) {
        glTranslated(x, y, z);
        glRotated(rx, 1, 0, 0);
        glRotated(ry, 0, 1, 0);
        glRotated(rz, 0, 0, 1);
        glScaled(sx, sy, sz);
    }
}
