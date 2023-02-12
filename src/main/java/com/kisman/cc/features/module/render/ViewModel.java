package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author _kisman_(Value & HandModel & Item Alpha)
 * @author NekoPvP(Item FOV)
 */

public class ViewModel extends Module {
    @ModuleInstance
    public static ViewModel instance;

    public Setting customEating = register(new Setting("Custom Eating", this, false));
    public Setting translate = register(new Setting("Translate", this, true));

    //item FOV
    public Setting itemFOV = register(new Setting("ItemFOV", this, false));
    public Setting fov = register(new Setting("FOV", this, 130, 70, 200, true).setVisible(itemFOV::getValBoolean));

    //translate
    private final SettingGroup translates = register(new SettingGroup(new Setting("Translates", this)));
    public Setting translateRightX = register(translates.add(new Setting("RightX", this, 0, -2, 2, false)));
    public Setting translateRightY = register(translates.add(new Setting("RightY", this, 0, -2, 2, false)));
    public Setting translateRightZ = register(translates.add(new Setting("RightZ", this, 0, -2, 2, false)));
    public Setting translateLeftX = register(translates.add(new Setting("LeftX", this, 0, -2, 2, false)));
    public Setting translateLeftY = register(translates.add(new Setting("LeftY", this, 0, -2, 2, false)));
    public Setting translateLeftZ = register(translates.add(new Setting("LeftZ", this, 0, -2, 2, false)));

    //rotate
    private final SettingGroup rotates = register(new SettingGroup(new Setting("Rotates", this)));
    public Setting rotateRightX = register(rotates.add(new Setting("RotateRightX", this, 0, 0, 360, false)));
    public Setting rotateRightY = register(rotates.add(new Setting("RotateRightY", this, 0, 0, 360, false)));
    public Setting rotateRightZ = register(rotates.add(new Setting("RotateRightZ", this, 0, 0, 360, false)));
    public Setting rotateLeftX = register(rotates.add(new Setting("RotateLeftX", this, 0, 0, 360, false)));
    public Setting rotateLeftY = register(rotates.add(new Setting("RotateLeftY", this, 0, 0, 360, false)));
    public Setting rotateLeftZ = register(rotates.add(new Setting("RotateLeftZ", this, 0, 0, 360, false)));

    //scale
    private final SettingGroup scales = register(new SettingGroup(new Setting("Scales", this)));
    public Setting scaleRightX = register(scales.add(new Setting("ScaleRigthX", this, 1, -2, 2, false)));
    public Setting scaleRightY = register(scales.add(new Setting("ScaleRigthY", this, 1, -2, 2, false)));
    public Setting scaleRightZ = register(scales.add(new Setting("ScaleRigthZ", this, 1, -2, 2, false)));
    public Setting scaleLeftX = register(scales.add(new Setting("ScaleLeftX", this, 1, -2, 2, false)));
    public Setting scaleLeftY = register(scales.add(new Setting("ScaleLeftY", this, 1, -2, 2, false)));
    public Setting scaleLeftZ = register(scales.add(new Setting("ScaleLeftZ", this, 1, -2, 2, false)));

    //auto rotate
    private final SettingGroup autoRotateG = register(new SettingGroup(new Setting("Auto Rotates", this)));
    public Setting autoRotateRigthX = register(autoRotateG.add(new Setting("AutoRotateRigthX", this, false)));
    public Setting autoRotateRigthY = register(autoRotateG.add(new Setting("AutoRotateRigthY", this, false)));
    public Setting autoRotateRigthZ = register(autoRotateG.add(new Setting("AutoRotateRigthZ", this, false)));
    public Setting autoRotateLeftX = register(autoRotateG.add(new Setting("AutoRotateLeftX", this, false)));
    public Setting autoRotateLeftY = register(autoRotateG.add(new Setting("AutoRotateLeftY", this, false)));
    public Setting autoRotateLeftZ = register(autoRotateG.add(new Setting("AutoRotateLeftZ", this, false)));

    //hand pos modifier
    private final SettingGroup handsG = register(new SettingGroup(new Setting("Hands", this)));
    public Setting hands = register(handsG.add(new Setting("Hands", this, false)));
    public Setting handRightX = register(handsG.add(new Setting("HandRightX", this, 0, -4, 4, false).setVisible(hands::getValBoolean)));
    public Setting handRightY = register(handsG.add(new Setting("HandRightY", this, 0, -4, 4, false).setVisible(hands::getValBoolean)));
    public Setting handRightZ = register(handsG.add(new Setting("HandRightZ", this, 0, -4, 4, false).setVisible(hands::getValBoolean)));
    public Setting handRightRotateX = register(handsG.add(new Setting("HandRotateRightX", this, 0, 0, 360, false).setVisible(hands::getValBoolean)));
    public Setting handRightRotateY = register(handsG.add(new Setting("HandRotateRightY", this, 0, 0, 360, false).setVisible(hands::getValBoolean)));
    public Setting handRightRotateZ = register(handsG.add(new Setting("HandRotateRightZ", this, 0, 0, 360, false).setVisible(hands::getValBoolean)));
    public Setting handRightScaleX = register(handsG.add(new Setting("HandScaleRightX", this, 0, -2, 2, false).setVisible(hands::getValBoolean)));
    public Setting handRightScaleY = register(handsG.add(new Setting("HandScaleRightY", this, 0, -2, 2, false).setVisible(hands::getValBoolean)));
    public Setting handRightScaleZ = register(handsG.add(new Setting("HandScaleRightZ", this, 0, -2, 2, false).setVisible(hands::getValBoolean)));
    public Setting handLeftX = register(handsG.add(new Setting("HandLeftX", this, 0, -4, 4, false).setVisible(hands::getValBoolean)));
    public Setting handLeftY = register(handsG.add(new Setting("HandLeftY", this, 0, -4, 4, false).setVisible(hands::getValBoolean)));
    public Setting handLeftZ = register(handsG.add(new Setting("HandLeftZ", this, 0, -4, 4, false).setVisible(hands::getValBoolean)));
    public Setting handLeftRotateX = register(handsG.add(new Setting("HandRotateLeftX", this, 0, 0, 360, false).setVisible(hands::getValBoolean)));
    public Setting handLeftRotateY = register(handsG.add(new Setting("HandRotateLeftY", this, 0, 0, 360, false).setVisible(hands::getValBoolean)));
    public Setting handLeftRotateZ = register(handsG.add(new Setting("HandRotateLeftZ", this, 0, 0, 360, false).setVisible(hands::getValBoolean)));
    public Setting handLeftScaleX = register(handsG.add(new Setting("HandScaleLeftX", this, 0, -2, 2, false).setVisible(hands::getValBoolean)));
    public Setting handLeftScaleY = register(handsG.add(new Setting("HandScaleLeftY", this, 0, -2, 2, false).setVisible(hands::getValBoolean)));
    public Setting handLeftScaleZ = register(handsG.add(new Setting("HandScaleLeftZ", this, 0, -2, 2, false).setVisible(hands::getValBoolean)));

    //custom items alpha
    public Setting useAlpha = register(new Setting("Use Custom Alpha", this, false));
    public Setting alpha = register(new Setting("Alpha", this, 255, 0, 255, true).setVisible(useAlpha::getValBoolean));


    public ViewModel() {
        super("ViewModel", "modeL vieM", Category.RENDER);
    }

    public void hand(EnumHandSide side) {
        switch (side) {
            case RIGHT: {
                {
                    glTranslated(handRightX.getValDouble(), handRightY.getValDouble(), handRightZ.getValDouble());
                    glRotated(handRightRotateX.getValDouble(), 1, 0, 0);
                    glRotated(handRightRotateY.getValDouble(), 0, 1, 0);
                    glRotated(handRightRotateZ.getValDouble(), 0, 0, 1);
                    glScaled(handRightScaleX.getValDouble(), handRightScaleY.getValDouble(), handRightScaleZ.getValDouble());
                }
                break;
            }
            case LEFT: {
                {
                    glTranslated(handLeftX.getValDouble(), handLeftY.getValDouble(), handLeftZ.getValDouble());
                    glRotated(handLeftRotateX.getValDouble(), 1, 0, 0);
                    glRotated(handLeftRotateY.getValDouble(), 0, 1, 0);
                    glRotated(handLeftRotateZ.getValDouble(), 0, 0, 1);
                    glScaled(handLeftScaleX.getValDouble(), handLeftScaleY.getValDouble(), handLeftScaleZ.getValDouble());
                }
                break;
            }
        }
    }

    @SubscribeEvent public void onItemFOV(EntityViewRenderEvent.FOVModifier event) {if (itemFOV.getValBoolean()) event.setFOV(fov.getValFloat());}
}
