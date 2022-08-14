package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class SwingAnimation extends Module {
    public static SwingAnimation instance;

    public Setting mode = new Setting("Mode", this, "Strong", new ArrayList<>(Arrays.asList("Hand", "Strong")));

    private final Setting handMode = new Setting("Hand Mode", this, "X", Arrays.asList("X", "Y", "Z"));

    public Setting ignoreEating = new Setting("IgnoreEating", this, true);

    public Setting strongMode = new Setting("StrongMode", this, StrongMode.Blockhit1);

    public Setting ifKillAura = new Setting("If KillAura", this, true);

    public SwingAnimation() {
        super("SwingAnimation", "SwingAnimation", Category.RENDER);

        instance = this;

        setmgr.rSetting(mode);
        setmgr.rSetting(handMode);

        setmgr.rSetting(strongMode);
        setmgr.rSetting(ignoreEating);
        setmgr.rSetting(ifKillAura);

        super.setDisplayInfo(() -> "[" + (mode.getValString().equalsIgnoreCase("Hand") ? Kisman.instance.settingsManager.getSettingByName(this, "SwingMode").getValString() : strongMode.getValString()) + "]");
    }

    @SubscribeEvent
    public void onRenderArms(final RenderSpecificHandEvent event) {
        if(mode.getValString().equalsIgnoreCase("Hand")) {
            if (event.getSwingProgress() > 0) {
                final float angle = (1f - event.getSwingProgress()) * 360f;

                switch (handMode.getValString()) {
                    case "X":
                        glRotatef(angle, 1, 0, 0);
                        break;
                    case "Y":
                        glRotatef(angle, 0, 1, 0);
                        break;
                    case "Z":
                        glRotatef(angle, 0, 0, 1);
                        break;
                }
            }
        }
    }

    public enum StrongMode {
        Blockhit1,
        Blockhit2,
        Knife
    }
}
