package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

public class SwingProgress extends Module {

    public final Setting progress = register(new Setting("Progress", this, 0, 0, 6, true));

    public static SwingProgress INSTANCE;

    public SwingProgress(){
        super("SwingProgress", Category.RENDER);
        INSTANCE = this;
    }
}
