package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author _kisman_(Value)
 * @author NekoPvP(Item FOV)
 */

public class ViewModel extends Module {
    public static ViewModel instance;

    //item FOV
    public Setting itemFOV = new Setting("ItemFOV", this, false);
    public Setting fov = new Setting("FOV", this, 130, 70, 200, true);

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

    //hand pos modifier
    private Setting handLine = new Setting("HandLine", this, "Hand");
    public Setting hands = new Setting("Hands", this, false);
    public Setting handX = new Setting("HandX", this, 0, -2, 2, false);
    public Setting handY = new Setting("HandY", this, 0, -2, 2, false);
    public Setting handZ = new Setting("HandZ", this, 0, -2, 2, false);
    public Setting handRotateX = new Setting("HandRotateX", this, 0, 0, 360, false);
    public Setting handRotateY = new Setting("HandRotateY", this, 0, 0, 360, false);
    public Setting handRotateZ = new Setting("HandRotateZ", this, 0, 0, 360, false);
    public Setting handScaleX = new Setting("HandX", this, 0, -2, 2, false);
    public Setting handScaleY = new Setting("HandY", this, 0, -2, 2, false);
    public Setting handScaleZ = new Setting("HandZ", this, 0, -2, 2, false);

    //custom items alpha
    private Setting itemLine = new Setting("ItenLine", this, "Item");
    public Setting alpha = new Setting("Alpha", this, 255, 0, 255, true);


    public ViewModel() {
        super("ViewModel", "modeL vieM", Category.RENDER);
        instance = this;

        setmgr.rSetting(itemFOV);
        setmgr.rSetting(fov);

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

        setmgr.rSetting(handLine);
        setmgr.rSetting(hands);
        setmgr.rSetting(handX);
        setmgr.rSetting(handY);
        setmgr.rSetting(handZ);
        setmgr.rSetting(handRotateX);
        setmgr.rSetting(handRotateY);
        setmgr.rSetting(handRotateZ);
        setmgr.rSetting(handScaleX);
        setmgr.rSetting(handScaleY);
        setmgr.rSetting(handScaleZ);

        setmgr.rSetting(itemLine);
        setmgr.rSetting(alpha);
    }

    @SubscribeEvent
    public void onItemFOV(EntityViewRenderEvent.FOVModifier event) {
        if(itemFOV.getValBoolean()) {
            event.setFOV((float) fov.getValDouble());
        }
    }

    @SubscribeEvent
    public void onRenderArms(RenderSpecificHandEvent event) {
        if(hands.getValBoolean()) {
            glTranslated(handX.getValDouble(), handY.getValDouble(), handZ.getValDouble());
            glRotated(handRotateX.getValDouble(), 1, 0, 0);
            glRotated(handRotateY.getValDouble(), 0, 1, 0);
            glRotated(handRotateZ.getValDouble(), 0, 0, 1);
            glScaled(handScaleX.getValDouble(), handScaleY.getValDouble(), handScaleZ.getValDouble());
        }
    }
}
