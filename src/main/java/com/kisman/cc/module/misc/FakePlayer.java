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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

import java.util.UUID;

public class FakePlayer extends Module {
    private Setting name = new Setting("Name", this, "FinLicorice", "FinLicorice", true);

    public FakePlayer() {
        super("FakePlayer", "FakePlayer", Category.MISC);
        super.setDisplayInfo("[" + name.getValString() + TextFormatting.GRAY + "]");

        setmgr.rSetting(name);

        Kisman.instance.settingsManager.rSetting(new Setting("CopyInv", this, false));
    }

    public void onEnable() {
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

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("dbc45ea7-e8bd-4a3e-8660-ac064ce58216"), name.getValString()));
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
