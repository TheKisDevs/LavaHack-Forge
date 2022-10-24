package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEntitySpawn;
import com.kisman.cc.event.events.EventRemoveEntity;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.math.TimeAnimation;
import me.zero.alpine.listener.Listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public static HashMap<Integer, List<TimeAnimation>> scaleTimes = new HashMap<>();

    private final SettingGroup rubiksCrystalGroup = register(new SettingGroup(new Setting("Rubiks Crystal", this)));
    public Setting rubiksCrystal = register(rubiksCrystalGroup.add(new Setting("Rubiks Crystal", this, false)));
    public Setting rubiksCrystalRotationDirection = register(rubiksCrystalGroup.add(new Setting("Rubiks Crystal Rotation Direction", this, RubiksCrystalRotationDirection.Left).setVisible(rubiksCrystal).setTitle("Rotation Dir")));
    private final SettingGroup rubiksCrystalCubes = register(rubiksCrystalGroup.add(new SettingGroup(new Setting("Cubes", this))));
    public Setting rubiksCrystalInside = register(rubiksCrystalCubes.add(new Setting("Rubiks Crystal Inside", this, true).setTitle("In")));
    public Setting rubiksCrystalOutside = register(rubiksCrystalCubes.add(new Setting("Rubiks Crystal Outside", this, false).setTitle("Out")));
    public Setting rubiksCrystalOutside2 = register(rubiksCrystalCubes.add(new Setting("Rubiks Crystal Outside 2", this, false).setTitle("Out 2")));

    private final SettingGroup scaleGroup = register(new SettingGroup(new Setting("Scale", this)));
    public Setting scale = register(scaleGroup.add(new Setting("Scale", this,false)));
    public Setting scaleX = register(scaleGroup.add(new Setting("Scale X", this, 1, 0.1, 2, false).setVisible(scale).setTitle("X")));
    public Setting scaleY = register(scaleGroup.add(new Setting("Scale Y", this, 1, 0.1, 2, false).setVisible(scale).setTitle("Y")));
    public Setting scaleZ = register(scaleGroup.add(new Setting("Scale Z", this, 1, 0.1, 2, false).setVisible(scale).setTitle("Z")));

    private final SettingGroup translateGroup = register(new SettingGroup(new Setting("Translate", this)));
    public Setting translate = register(translateGroup.add(new Setting("Translate", this,false)));
    public Setting translateX = register(translateGroup.add(new Setting("Translate X", this, 0, -2, 2, false).setVisible(translate).setTitle("X")));
    public Setting translateY = register(translateGroup.add(new Setting("Translate Y", this, 0, -2, 2, false).setVisible(translate).setTitle("Y")));
    public Setting translateZ = register(translateGroup.add(new Setting("Translate Z", this, 0, -2, 2, false).setVisible(translate).setTitle("Z")));

    private final SettingGroup elements = register(new SettingGroup(new Setting("Elements", this)));

    private final SettingGroup baseGroup = register(elements.add(new SettingGroup(new Setting("Base", this))));
    public Setting base = register(baseGroup.add(new Setting("Base", this, true)));
    public Setting baseFadeOutDelay = register(baseGroup.add(new Setting("Base Fade Out Delay", this, 0, 0, 10000, NumberType.TIME).setTitle("Fade Out")));
    public Setting alwaysBase = register(baseGroup.add(new Setting("Always Base", this, false).setVisible(base).setTitle("Always")));

    private final SettingGroup cubes = register(elements.add(new SettingGroup(new Setting("Cubes", this))));

    private final SettingGroup insideGroup = register(cubes.add(new SettingGroup(new Setting("Inside", this))));
    public Setting insideCube = register(insideGroup.add(new Setting("Inside Tex", this, CubeModes.In).setTitle("Tex")));
    public Setting insideModel = register(insideGroup.add(new Setting("Inside Model", this, ModelModes.Cube).setTitle("Model")));
    private final SettingGroup insideSpeeds = register(insideGroup.add(new SettingGroup(new Setting("Speeds", this))));
    public Setting insideSpinSpeed = register(insideSpeeds.add(new Setting("Inside Spin Speed", this, 3, 0, 50, false).setTitle("Spin")));
    public Setting insideFadeOutDelay = register(insideGroup.add(new Setting("Inside Fade Out Delay", this, 0, 0, 10000, NumberType.TIME).setTitle("Fade Out")));
    private final SettingGroup outsideGroup = register(cubes.add(new SettingGroup(new Setting("Outside", this))));
    public Setting outsideCube = register(outsideGroup.add(new Setting("Outside Tex", this, CubeModes.Out).setTitle("Tex")));
    public Setting outsideModel = register(outsideGroup.add(new Setting("Outside Model", this, ModelModes.Glass).setTitle("Model")));
    private final SettingGroup outsideSpeeds = register(outsideGroup.add(new SettingGroup(new Setting("Speeds", this))));
    public Setting outsideSpinSpeed = register(outsideSpeeds.add(new Setting("Outside Spin Speed", this, 3, 0, 50, false).setTitle("Spin")));
    public Setting outsideFadeOutDelay = register(outsideGroup.add(new Setting("Outside Fade Out Delay", this, 0, 0, 10000, NumberType.TIME).setTitle("Fade Out")));
    private final SettingGroup outsideGroup2 = register(cubes.add(new SettingGroup(new Setting("Outside 2", this))));
    public Setting outsideCube2 = register(outsideGroup2.add(new Setting("Outside 2 Tex", this, CubeModes.Out).setTitle("Tex")));
    public Setting outsideModel2 = register(outsideGroup2.add(new Setting("Outside 2 Model", this, ModelModes.Glass).setTitle("Model")));
    private final SettingGroup outsideSpeeds2 = register(outsideGroup2.add(new SettingGroup(new Setting("Speeds", this))));
    public Setting outsideSpinSpeed2 = register(outsideSpeeds2.add(new Setting("Outside 2 Spin Speed", this, 3, 0, 50, false).setTitle("Spin")));
    public Setting outsideFadeOutDelay2 = register(outsideGroup2.add(new Setting("Outside 2 Fade Out Delay", this, 0, 0, 10000, NumberType.TIME).setTitle("Fade Out")));

    public Setting bounce = register((new Setting("Bounce Speed", this, 0.2f, 0, 10, false)));

    public CrystalModifier() {
        super("CrystalModifier", "Modify crystal model renderer", Category.RENDER);

        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(spawn);
        Kisman.EVENT_BUS.subscribe(remove);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(spawn);
        Kisman.EVENT_BUS.unsubscribe(remove);
    }

    private final Listener<EventEntitySpawn> spawn = new Listener<>(event -> {
        scaleTimes.put(
                event.getEntity().entityId,
                Arrays.asList(
                        new TimeAnimation(
                                baseFadeOutDelay.getValLong(),
                                0.1,
                                1.0
                        ),
                        new TimeAnimation(
                                insideFadeOutDelay.getValLong(),
                                0.1,
                                1.0
                        ),
                        new TimeAnimation(
                                outsideFadeOutDelay.getValLong(),
                                0.1,
                                1.0
                        ),
                        new TimeAnimation(
                                outsideFadeOutDelay2.getValLong(),
                                0.1,
                                1.0
                        )
                )
        );
    });

    private final Listener<EventRemoveEntity> remove = new Listener<>(event -> {
        if(scaleTimes.containsKey(event.getId())) scaleTimes.get(event.getId());
    });
    
    public enum RubiksCrystalRotationDirection {Left, Right}
    public enum Cube {Off, In, Out}
    public enum ModelModes {Cube, Glass}
}
