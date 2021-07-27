package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoJump extends Module {
    public AutoJump() {
        super("AutoJump", "Automatic jump", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void update(TickEvent.ClientTickEvent event) {
        Minecraft.getMinecraft().gameSettings.autoJump = true;
    }

    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.autoJump = false;
    }
}
