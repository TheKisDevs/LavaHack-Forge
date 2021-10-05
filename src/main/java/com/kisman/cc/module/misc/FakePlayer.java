package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;

import java.util.UUID;

public class FakePlayer extends Module {
    private String name;

    public FakePlayer() {
        super("FakePlayer", "FakePlayer", Category.MISC);

        Kisman.instance.settingsManager.rSetting(new Setting("Name", this, "FinLicorice", "FinLicorice", true));

        Kisman.instance.settingsManager.rSetting(new Setting("CopyInv", this, false));
    }

  /*private ItemStack[] armour;*/

/*    public void update() {
        if (mc.player == null && mc.world == null) return;

        this.armour = new ItemStack[] {
                new ItemStack(mc.player.inventory.armorInventory.get(0).getItem()),
                new ItemStack(mc.player.inventory.armorInventory.get(1).getItem()),
                new ItemStack(mc.player.inventory.armorInventory.get(2).getItem()),
                new ItemStack(mc.player.inventory.armorInventory.get(3).getItem()),
                new ItemStack(mc.player.inventory.offHandInventory.get(45).getItem()),
                new ItemStack(mc.player.inventory.mainInventory.get(mc.player.inventory.currentItem).getItem())
        };
    }*/

/*    public void update() {
    }*/

    public void onEnable() {
        this.name = Kisman.instance.settingsManager.getSettingByName(this, "Name").getValString();

        if(mc.player == null && mc.world == null) {
            if(super.isToggled()) {
                super.setToggled(false);
            }
            return;
        }

        boolean armor = Kisman.instance.settingsManager.getSettingByName(this, "CopyInv").getValBoolean();

        if(mc.world == null && mc.player == null) {
            this.onDisable();
            return;
        }

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("dbc45ea7-e8bd-4a3e-8660-ac064ce58216"), this.name));
        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.world.addEntityToWorld(-1337, clonedPlayer);

/*        try {
            if(armor) {
                for(int i = 0; i < 6; i++) {
                    ItemStack item = armour[i];
                    if(i >= 0 && i <= 3) clonedPlayer.inventory.armorInventory.set(i, item);
                    else if(i == 4) clonedPlayer.inventory.offHandInventory.set(i ,item);
                    else if(i == 5) clonedPlayer.inventory.mainInventory.set(i, item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        clonedPlayer.onLivingUpdate();
    }

    public void onDisable() {
        if(mc.world == null && mc.player == null) return;

        mc.world.removeEntityFromWorld(-1337);
    }
}
