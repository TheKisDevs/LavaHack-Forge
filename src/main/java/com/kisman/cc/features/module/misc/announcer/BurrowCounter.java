package com.kisman.cc.features.module.misc.announcer;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(
        name = "BurrowCounter",
        display = "Burrows",
        submodule = true
)
public class BurrowCounter extends Module {
    private final ConcurrentHashMap<EntityPlayer, Integer> players = new ConcurrentHashMap<>();
    private final List<EntityPlayer> anti_spam = new ArrayList<>();

    public void update() {
        if(mc.player == null || mc.world == null) return;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (anti_spam.contains(player)) continue;
            BlockPos pos = new BlockPos(player.posX, player.posY + 0.2D, player.posZ);
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)) {
                add_player(player);
                anti_spam.add(player);
            }
        }
    }

    private void add_player(EntityPlayer player) {
        if (player == null) return;
        if (players.containsKey(player)) {
            int value = players.get(player) + 1;
            players.put(player, value);
            ChatUtility.warning().printClientModuleMessage(player.getName() + TextFormatting.DARK_RED + " has burrowed " + value + " times");
        } else {
            players.put(player, 1);
            ChatUtility.warning().printClientModuleMessage(player.getName() + TextFormatting.DARK_RED + " has burrowed");
        }
    }
}
