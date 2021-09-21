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

    boolean test = false;

    public ViemModel() {
        super("ViemModel", "modeL vieM", Category.RENDER);

        /*Kisman.instance.settingsManager.rSetting(new Setting("test", this, false));*/

        /*Kisman.instance.settingsManager.rSetting(new Setting("XLine", this, "X"));
        Kisman.instance.settingsManager.rSetting(new Setting("XA", this, x, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("YLine", this, "Y"));
        Kisman.instance.settingsManager.rSetting(new Setting("YA", this, y, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ZLine", this, "Z"));
        Kisman.instance.settingsManager.rSetting(new Setting("ZA", this, z, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotationLine", this, "Rotation"));
        Kisman.instance.settingsManager.rSetting(new Setting("XR", this, rx, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("YR", this, ry, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ZR", this, rz, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeLine", this, "Size"));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeX", this, sx, 0, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeY", this, sy, 0, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("SizeZ", this, sz, 0, 2, false));*/
        Kisman.instance.settingsManager.rSetting(new Setting("RightX", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RightY", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RightZ", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateRightX", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateRightY", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateRightZ", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LeftX", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LeftY", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LeftZ", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateLeftX", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateLeftY", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateLeftZ", this, 0, 0, 360, false));
    }

/*    public void update() {
        this.test = Kisman.instance.settingsManager.getSettingByName(this, "test").getValBoolean();

        this.x = Kisman.instance.settingsManager.getSettingByName(this, "XA").getValDouble();
        this.y = Kisman.instance.settingsManager.getSettingByName(this, "YA").getValDouble();
        this.z = Kisman.instance.settingsManager.getSettingByName(this, "ZA").getValDouble();

        this.rx = Kisman.instance.settingsManager.getSettingByName(this, "XR").getValDouble();
        this.ry = Kisman.instance.settingsManager.getSettingByName(this, "YR").getValDouble();
        this.rz = Kisman.instance.settingsManager.getSettingByName(this, "ZR").getValDouble();

        this.sx = Kisman.instance.settingsManager.getSettingByName(this, "SizeX").getValDouble();
        this.sy = Kisman.instance.settingsManager.getSettingByName(this, "SizeY").getValDouble();
        this.sz = Kisman.instance.settingsManager.getSettingByName(this, "SizeZ").getValDouble();
    }*/

/*    @SubscribeEvent
    public void onRenderArms(final RenderSpecificHandEvent event) {
        if(test) {
            glTranslated(x, y, z);
            glRotated(rx, 1, 0, 0);
            glRotated(ry, 0, 1, 0);
            glRotated(rz, 0, 0, 1);
            glScaled(sx, sy, sz);
        }

    }*/
}
