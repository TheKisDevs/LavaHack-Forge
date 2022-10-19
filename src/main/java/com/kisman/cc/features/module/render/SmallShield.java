package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

public class SmallShield extends Module {

    public final Setting mainX = register(new Setting("MainHandX", this, 0, -1, 1, false));
    public final Setting mainY = register(new Setting("MainHandY", this, 0, -1, 1, false));
    public final Setting offX = register(new Setting("MainHandX", this, 0, -1, 1, false));
    public final Setting offY = register(new Setting("MainHandY", this, 0, -1, 1, false));

    public static SmallShield INSTANCE;

    public SmallShield(){
        super("SmallShield", Category.RENDER);
        INSTANCE = this;
    }
}
