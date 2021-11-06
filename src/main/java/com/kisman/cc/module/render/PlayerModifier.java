package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerModifier extends Module {
    private Setting preview = new Setting("Preview", this, "Player", (EntityPlayer) mc.player);
    private Setting rotateLine = new Setting("RotateLine", this, "Rotate");

    public Setting rotateX = new Setting("RotateX", this, 0, 0, 360, true);
    public Setting rotateY = new Setting("RotateY", this, 0, 0, 360, true);
    public Setting rotateZ = new Setting("RotateZ", this, 0, 0, 360, true);

    public Setting autoRotateX = new Setting("AutoRotateX", this, false);
    public Setting autoRotateY = new Setting("AutoRotateY", this, false);
    public Setting autoRotateZ = new Setting("AutoRotateZ", this, false);

    private Setting scaleLine = new Setting("ScaleLine", this, "Scale");

    public PlayerModifier() {
        super("PlayerModifier", "", Category.RENDER);

        setmgr.rSetting(preview);

        setmgr.rSetting(rotateLine);
        setmgr.rSetting(rotateX);
        setmgr.rSetting(rotateY);
        setmgr.rSetting(rotateZ);
        setmgr.rSetting(autoRotateX);
        setmgr.rSetting(autoRotateY);
        setmgr.rSetting(autoRotateZ);

        setmgr.rSetting(scaleLine);
    }
}
