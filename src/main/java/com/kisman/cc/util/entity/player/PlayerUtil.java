package com.kisman.cc.util.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

public class PlayerUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final Map<Integer, EntityOtherPlayerMP> FAKE_PLAYERS =
            new HashMap<>();

    public static EntityOtherPlayerMP createFakePlayerAndAddToWorld(GameProfile profile) {
        return createFakePlayerAndAddToWorld(profile, EntityOtherPlayerMP::new);
    }

    public static EntityOtherPlayerMP createFakePlayerAndAddToWorld(GameProfile profile, BiFunction<World, GameProfile, EntityOtherPlayerMP> create) {
        EntityOtherPlayerMP fakePlayer = createFakePlayer(profile, create);
        int randomID = -1000;
        while (FAKE_PLAYERS.containsKey(randomID)
                || mc.world.getEntityByID(randomID) != null) {
            randomID = ThreadLocalRandom.current().nextInt(-100000, -100);
        }

        FAKE_PLAYERS.put(randomID, fakePlayer);
        mc.world.addEntityToWorld(randomID, fakePlayer);
        return fakePlayer;
    }

    public static EntityOtherPlayerMP createFakePlayer(GameProfile profile, BiFunction<World, GameProfile, EntityOtherPlayerMP> create)
    {
        EntityOtherPlayerMP fakePlayer = create.apply(mc.world, profile);

        fakePlayer.inventory = mc.player.inventory;
        fakePlayer.inventoryContainer = mc.player.inventoryContainer;
        fakePlayer.setPositionAndRotation(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        fakePlayer.onGround = mc.player.onGround;
        fakePlayer.setSneaking(mc.player.isSneaking());
        fakePlayer.setHealth(mc.player.getHealth());
        fakePlayer.setAbsorptionAmount(mc.player.getAbsorptionAmount());

        for (PotionEffect effect : mc.player.getActivePotionEffects())
        {
            fakePlayer.addPotionEffect(effect);
        }

        return fakePlayer;
    }

    public static void removeFakePlayer(EntityOtherPlayerMP fakePlayer) {
        mc.addScheduledTask(() -> {
            FAKE_PLAYERS.remove(fakePlayer.getEntityId());
            fakePlayer.isDead = true; // setDead might be overridden
            if (mc.world != null) {
                mc.world.removeEntity(fakePlayer);
            }
        });
    }

    public static boolean isEating() {
        return mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.player.isHandActive() && mc.player.getActiveHand().equals(EnumHand.MAIN_HAND);
    }

    public static boolean isEatingOffhand() {
        return mc.player != null && mc.player.getHeldItemOffhand().getItem() instanceof ItemFood && mc.player.isHandActive() && mc.player.getActiveHand().equals(EnumHand.OFF_HAND);
    }

    public static int getItemSlot(Item input) {
        if (mc.player == null) return 0;

        for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); ++i) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8) continue;

            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

            if (s.isEmpty()) continue;
            if (s.getItem() == input) return i;
        }
        return -1;
    }

    public static int getRecursiveItemSlot(Item input) {
        if (mc.player == null) return 0;

        for (int i = mc.player.inventoryContainer.getInventory().size() - 1; i > 0; --i) {
            if (i == 5 || i == 6 || i == 7 || i == 8) continue;

            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

            if (s.isEmpty()) continue;
            if (s.getItem() == input) return i;
        }
        return -1;
    }
}
