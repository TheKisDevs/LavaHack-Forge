package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.Arrays;

public class AntiBot extends Module {
    private Setting mode = new Setting("Mode", this, "WellMore", Arrays.asList("WellMore", "Matrix 6.3"));

	public AntiBot() {
		super("AntiBot", "Prevents you from targetting bots", Category.COMBAT);

        setmgr.rSetting(mode);
	}
	
	@SubscribeEvent
	public void onTick(PlayerTickEvent e) {
        for (final EntityPlayer bot : AntiBot.mc.world.playerEntities) {
            if (bot != AntiBot.mc.player) {
                if (bot.isDead) continue;
                if (mode.getValString().equalsIgnoreCase("WellMore")) {
                    if (bot.isInvisible()) bot.isDead = true;
                    if (bot.getName().length() != 8 || mc.player.getDistance(bot) > 5.0f || Math.round(bot.posY) != Math.round(mc.player.posY + 2.0)) continue;
                    bot.isDead = true;
                    ChatUtils.complete(bot.getName() + " was been deleted!");
                } else {
                    final boolean contains = RotationUtils.isInFOV(bot, mc.player, 100.0) && AntiBot.mc.player.getDistance(bot) <= 6.5 && bot.canEntityBeSeen(mc.player);
                    final boolean speedAnalysis = bot.getActivePotionEffect(MobEffects.SPEED) == null && bot.getActivePotionEffect(MobEffects.JUMP_BOOST) == null && bot.getActivePotionEffect(MobEffects.LEVITATION) == null && !bot.isInWater() && bot.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA && EntityUtil.getSpeedBPS(bot) >= 11.9;
                    if (!contains || !speedAnalysis || bot.isDead) continue;
                    bot.isDead = true;
                    ChatUtils.complete(bot.getName() + " was been deleted!");
                }
            }
        }
	}
}