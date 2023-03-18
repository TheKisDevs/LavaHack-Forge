package com.kisman.cc.features.module.client;

import com.kisman.cc.features.aiimprovements.AIImprovementsMod;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

@ModuleInfo(
        name = "Optimizer",
        category = Category.CLIENT
)
public class Optimizer extends Module {
    private final Setting removeLookAi = register(new Setting("Remove Entity AI Watch Closest", this, false));
    private final Setting removeLookIdle = register(new Setting("Remove Entity AI LookIdle", this, false));
    private final Setting replaceLookHelper = register(new Setting("Replace Look Helper", this, true));
    public final Setting tileEntityRenderRange = register(new Setting("TileEntity Render Range(Squared)", this, 4096, 0, 4096, true));
    public final Setting customEntityRenderRange = register(new Setting("Custom Entity Render Range", this, false));
    public final Setting entityRenderRange = register(new Setting("Entity Render Range", this, 50, 0, 50, true).setVisible(customEntityRenderRange::getValBoolean));
    private final Setting lostFocus = register(new Setting("Lost Focus", this, false));

    @ModuleInstance
    public static Optimizer instance;

    private int maxFpsActive;

    public void onEnable() {
        super.onEnable();
        AIImprovementsMod.STATE = true;
        maxFpsActive = mc.gameSettings.limitFramerate;
    }

    public void onDisable() {
        super.onDisable();
        AIImprovementsMod.STATE = false;
    }

    public void update() {
        AIImprovementsMod.REMOVE_LOOK_AI = removeLookAi.getValBoolean();
        AIImprovementsMod.REMOVE_LOOK_IDLE = removeLookIdle.getValBoolean();
        AIImprovementsMod.REPLACE_LOOK_HELPER = replaceLookHelper.getValBoolean();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(mc.player == null || mc.world == null) return;

        if(lostFocus.getValBoolean()) {
            if(event.phase != TickEvent.Phase.START) {
                if(!Display.isActive()) {
                    if(mc.gameSettings.limitFramerate != 1) {
                        mc.gameSettings.limitFramerate = 1;
                    }
                } else if(mc.gameSettings.limitFramerate == 1) {
                    mc.gameSettings.limitFramerate = maxFpsActive;
                }
            }
        }
    }
}
