package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;

public class CameraClip extends Module {
    @ModuleInstance
    public static CameraClip instance;

    public CameraClip() {
        super("CameraClip", Category.PLAYER);
    }
}
