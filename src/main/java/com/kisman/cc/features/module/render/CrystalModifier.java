package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.Colour;
import net.minecraft.entity.item.EntityEnderCrystal;

public class CrystalModifier extends Module {
    public static CrystalModifier instance;


    /**
     * front - 0
     * back - 1
     * top - 2
     * bottom - 3
     * left - 4
     * right - 5
     */
    public static int rotatingSide = 0;

    public static long lastTime = 0;


    public static final int ANIMATION_LENGTH = 400;
    public static final double CUBELET_SCALE = 0.4;

    public Setting mode = /*register*/(new Setting("Mode", this, Modes.Fill));
    public Setting preview = /*register*/(new Setting("Crystal", this, "Crystal", new EntityEnderCrystal(mc.world)));

    private final SettingGroup model = register(new SettingGroup(new Setting("Model", this)));
    private final SettingGroup render = register(new SettingGroup(new Setting("Render", this)));

    private final SettingGroup rubiksCrystalGroup = register(model.add(new SettingGroup(new Setting("Rubiks Crystal", this))));
    public Setting rubiksCrystal = register(rubiksCrystalGroup.add(new Setting("Rubiks Crystal", this, false)));
    public Setting rubiksCrystalRotationDirection = register(rubiksCrystalGroup.add(new Setting("Rubiks Crystal Rotation Direction", this, RubiksCrystalRotationDirection.Left).setVisible(rubiksCrystal).setTitle("Rotation Dir")));

    private final SettingGroup scaleGroup = register(model.add(new SettingGroup(new Setting("Scale", this))));
    public Setting scale = register(scaleGroup.add(new Setting("Scale", this,false)));
    public Setting scaleX = register(scaleGroup.add(new Setting("Scale X", this, 1, 0.1, 2, false).setVisible(scale).setTitle("X")));
    public Setting scaleY = register(scaleGroup.add(new Setting("Scale Y", this, 1, 0.1, 2, false).setVisible(scale).setTitle("Y")));
    public Setting scaleZ = register(scaleGroup.add(new Setting("Scale Z", this, 1, 0.1, 2, false).setVisible(scale).setTitle("Z")));

    private final SettingGroup translateGroup = register(model.add(new SettingGroup(new Setting("Translate", this))));
    public Setting translate = register(translateGroup.add(new Setting("Translate", this,false)));
    public Setting translateX = register(translateGroup.add(new Setting("Translate X", this, 0, -2, 2, false).setVisible(translate).setTitle("X")));
    public Setting translateY = register(translateGroup.add(new Setting("Translate Y", this, 0, -2, 2, false).setVisible(translate).setTitle("Y")));
    public Setting translateZ = register(translateGroup.add(new Setting("Translate Z", this, 0, -2, 2, false).setVisible(translate).setTitle("Z")));

    private final SettingGroup elements = register(model.add(new SettingGroup(new Setting("Elements", this))));

    private final SettingGroup baseGroup = register(elements.add(new SettingGroup(new Setting("Base", this))));
    public Setting base = register(baseGroup.add(new Setting("Base", this, true)));
    public Setting alwaysBase = register(baseGroup.add(new Setting("Always Base", this, false).setVisible(base).setTitle("Always")));

    private final SettingGroup cubes = register(elements.add(new SettingGroup(new Setting("Cubes", this))));

    public Setting insideCube = register(cubes.add(new Setting("Inside", this, CubeModes.Cube).setTitle("In")));
    public Setting outsideCube = register(cubes.add(new Setting("Outside", this, CubeModes.Glass).setTitle("Out")));
    public Setting outsideCube2 = register(cubes.add(new Setting("Outside 2", this, CubeModes.Glass).setTitle("Out 2")));


    public Setting texture = /*register*/(render.add(new Setting("Texture", this, false)));

    public Setting outline = /*register*/(new Setting("Outline", this, false));
    public Setting outlineMode = /*register*/(new Setting("OutlineMode", this, OutlineModes.Wire));
    public Setting lineWidth = /*register*/(new Setting("LineWidth", this, 3, 0.5, 5, false));
    public Setting color = /*register*/(new Setting("Outline Color", this, "Color", new Colour(255, 0, 0)));
    
    public Setting speed = register(render.add(new Setting("Crystal Speed", this, 3, 0, 50, false)));
    public Setting bounce = register(render.add(new Setting("Crystal Bounce", this, 0.2f, 0, 10, false)));

    public CrystalModifier() {
        super("CrystalModifier", "Modify crystal model renderer", Category.RENDER);
        super.setDisplayInfo(
                () ->
                        "[" +
                                (rubiksCrystal.getValBoolean() ? "Rubik's Mode | " : "") +
                                "C: " +
                                    (insideCube.getValBoolean() ? "I" : "") +
                                    (outsideCube.getValBoolean() ? "O" : "") +
                                    (outsideCube2.getValBoolean() ? "O" : "") +
                                "S: " + speed.getNumberType().getFormatter().apply(speed.getValDouble()) +
                                "B: " + bounce.getNumberType().getFormatter().apply(bounce.getValDouble()) +
                        "]"
        );

        instance = this;
    }

    public enum OutlineModes {Wire, Flat}
    public enum Modes {Fill, Wireframe}
    public enum RubiksCrystalRotationDirection {Left, Right}
    public enum CubeModes {Off, Cube, Glass}
}
