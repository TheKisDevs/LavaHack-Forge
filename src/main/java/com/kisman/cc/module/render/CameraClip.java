package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class CameraClip extends Module {
    public CameraClip() {
        super("CameraClip", "CameraClip", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }
}
