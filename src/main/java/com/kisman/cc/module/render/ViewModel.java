package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.*;

public class ViewModel extends Module {
    public static ViewModel instance;

    //scale
    public Setting scaleRightX = new Setting("ScaleRigthX", this, 1, -2, 2, false);
    public Setting scaleRightY = new Setting("ScaleRigthY", this, 1, -2, 2, false);
    public Setting scaleRightZ = new Setting("ScaleRigthZ", this, 1, -2, 2, false);
    public Setting scaleLeftX = new Setting("ScaleLeftX", this, 1, -2, 2, false);
    public Setting scaleLeftY = new Setting("ScaleLeftY", this, 1, -2, 2, false);
    public Setting scaleLeftZ = new Setting("ScaleLeftZ", this, 1, -2, 2, false);

    //auto rotate
    public Setting autoRotateRigthX = new Setting("AutoRotateRigthX", this, false);
    public Setting autoRotateRigthY = new Setting("AutoRotateRigthY", this, false);
    public Setting autoRotateRigthZ = new Setting("AutoRotateRigthZ", this, false);
    public Setting autoRotateLeftX = new Setting("AutoRotateLeftX", this, false);
    public Setting autoRotateLeftY = new Setting("AutoRotateLeftY", this, false);
    public Setting autoRotateLeftZ = new Setting("AutoRotateLeftZ", this, false);

    public ViewModel() {
        super("ViewModel", "modeL vieM", Category.RENDER);
        instance = this;

        Kisman.instance.settingsManager.rSetting(new Setting("RightX", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RightY", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RightZ", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateRightX", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateRightY", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateRightZ", this, 0, 0, 360, false));
        setmgr.rSetting(autoRotateRigthX);
        setmgr.rSetting(autoRotateRigthY);
        setmgr.rSetting(autoRotateRigthZ);
        setmgr.rSetting(scaleRightX);
        setmgr.rSetting(scaleRightY);
        setmgr.rSetting(scaleRightZ);

        Kisman.instance.settingsManager.rSetting(new Setting("LeftX", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LeftY", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LeftZ", this, 0, -2, 2, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateLeftX", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateLeftY", this, 0, 0, 360, false));
        Kisman.instance.settingsManager.rSetting(new Setting("RotateLeftZ", this, 0, 0, 360, false));
        setmgr.rSetting(autoRotateLeftX);
        setmgr.rSetting(autoRotateLeftY);
        setmgr.rSetting(autoRotateLeftZ);
        setmgr.rSetting(scaleLeftX);
        setmgr.rSetting(scaleLeftY);
        setmgr.rSetting(scaleLeftZ);
    }

    @SubscribeEvent
    public void onRenderArms(final RenderSpecificHandEvent event) {
/*        glTranslated(x, y, z);
        glRotated(rx, 1, 0, 0);
        glRotated(ry, 0, 1, 0);
        glRotated(rz, 0, 0, 1);
        glScaled(sx, sy, sz);*/

        if(event.getHand().equals(EnumHand.MAIN_HAND)) {
            GlStateManager.translate(getSet("RightX").getValDouble(), getSet("RightY").getValDouble(), getSet("RightZ").getValDouble());
            GlStateManager.rotate((!ViewModel.instance.autoRotateRigthX.getValBoolean() ? ((float) (getSet("RotateRightX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
            GlStateManager.rotate((!ViewModel.instance.autoRotateRigthY.getValBoolean() ? ((float) (getSet("RotateRightY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
            GlStateManager.rotate((!ViewModel.instance.autoRotateRigthZ.getValBoolean() ? ((float) (getSet("RotateRightZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
            GlStateManager.scale(ViewModel.instance.scaleRightX.getValDouble(), ViewModel.instance.scaleRightY.getValDouble(), ViewModel.instance.scaleRightZ.getValDouble());
        }
        if (event.getHand().equals(EnumHand.OFF_HAND)) {
            GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
            GlStateManager.rotate((!ViewModel.instance.autoRotateLeftX.getValBoolean() ? ((float) (getSet("RotateLeftX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
            GlStateManager.rotate((!ViewModel.instance.autoRotateLeftY.getValBoolean() ? ((float) (getSet("RotateLeftY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
            GlStateManager.rotate((!ViewModel.instance.autoRotateLeftZ.getValBoolean() ? ((float) (getSet("RotateLeftZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
            GlStateManager.scale(ViewModel.instance.scaleLeftX.getValDouble(), ViewModel.instance.scaleLeftY.getValDouble(), ViewModel.instance.scaleLeftZ.getValDouble());
        }
    }

    private Setting getSet(String name) {
        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViemModel"), name);
    }
}
