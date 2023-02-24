package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

public class AntiBot extends Module {
    public Setting mode = register(new Setting("Mode", this, "WellMore", Arrays.asList("Ping", "Vanish", "Zamorozka")));

    @ModuleInstance
    public static AntiBot instance;
    private boolean clicked = false;

	public AntiBot() {
		super("AntiBot", "Prevents you from targetting bots", Category.COMBAT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
	}

	public void update() {
        if(mc.player == null || mc.world == null) return;

        if(mode.checkValString("Zamorozka") && mc.currentScreen == null && Mouse.isButtonDown(0)) {
            if(!clicked) {
                clicked = true;
                RayTraceResult result = mc.objectMouseOver;
                if(result == null || result.typeOfHit != RayTraceResult.Type.ENTITY) return;
                Entity entity = mc.objectMouseOver.entityHit;
                if(!(entity instanceof EntityPlayer)) return;
                Kisman.target_by_click = (EntityPlayer) entity;
                ChatUtility.complete().printClientModuleMessage("Current target is " + entity.getName());
            } else clicked = false;
        } else if(mode.checkValString("Ping")) {
            for(EntityPlayer entity : mc.world.playerEntities) if(UtilityKt.getPing(entity) == -1) entity.isDead = true;
        }
	}

    public void onEnable() {
        clicked = false;
    }

    public void onDisable() {
        Kisman.target_by_click = null;
    }
}