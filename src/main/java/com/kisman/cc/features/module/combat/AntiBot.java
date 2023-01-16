package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.world.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

public class AntiBot extends Module {
    public Setting mode = register(new Setting("Mode", this, "WellMore", Arrays.asList("Matrix 6.3", "Ping", "Vanish", "Zamorozka")));

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
        } else if(mode.checkValString("Matrix 6.3")) {
            for (EntityPlayer entity : mc.world.playerEntities) {
                if (entity != mc.player && !entity.isDead) {
                    boolean contains = RotationUtils.isInFOV(entity, mc.player, 100.0) && AntiBot.mc.player.getDistance(entity) <= 6.5 && entity.canEntityBeSeen(mc.player);
                    boolean speedAnalysis = entity.getActivePotionEffect(MobEffects.SPEED) == null && entity.getActivePotionEffect(MobEffects.JUMP_BOOST) == null && entity.getActivePotionEffect(MobEffects.LEVITATION) == null && !entity.isInWater() && entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA;
                    if (!contains || !speedAnalysis || entity.isDead) continue;
                    entity.isDead = true;
                    ChatUtility.complete().printClientModuleMessage(entity.getName() + " was been deleted!");
                }
            }
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