package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;

public class KillAura extends Module {
    public static KillAura instance;

    public EntityPlayer target;

    private Setting mode = new Setting("Mode", this, "Sword", Arrays.asList("Single", "Multi"));

    private Setting hitLine = new Setting("HitLine", this, "Hit");
    private Setting useFallDist = new Setting("Use Fall Dist", this, false);
    private Setting fallDistance = new Setting("Fall Distance", this, 0.25, 0, 1, false);
    private Setting shieldBreaker = new Setting("Shield Breaker", this, true);
    private Setting packetAttack = new Setting("Packet Attack", this, false);
    private Setting rotations = new Setting("Rotations", this, RotateMode.Silent);

    private Setting weapon = new Setting("Weapon", this, "Sword", new ArrayList<>(Arrays.asList("Sword", "Axe", "Both", "None")));

    private Setting invisible = new Setting("Invisible", this, false);

    private Setting renderLine = new Setting("RenderLine", this, "Render");
    private Setting targetEsp = new Setting("Target ESP", this, true);

    private Setting switchMode = new Setting("Switch Mode", this, "None", new ArrayList<>(Arrays.asList("None", "Normal", "Silent")));
    private Setting packetSwitch = new Setting("Packet Switch", this, true);

    public KillAura() {
        super("KillAura", "8", Category.COMBAT);

        instance = this;

        setmgr.rSetting(mode);

        setmgr.rSetting(hitLine);
        setmgr.rSetting(useFallDist);
        setmgr.rSetting(fallDistance);
        setmgr.rSetting(shieldBreaker);
        Kisman.instance.settingsManager.rSetting(new Setting("HitSound", this, false));
        setmgr.rSetting(packetAttack);
        setmgr.rSetting(rotations);

        setmgr.rSetting(new Setting("WeaponLine", this, "Weapon"));
        setmgr.rSetting(weapon);

        Kisman.instance.settingsManager.rSetting(new Setting("TargetsLine", this, "Targets"));
        Kisman.instance.settingsManager.rSetting(new Setting("Player", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Monster", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Passive", this, true));
        setmgr.rSetting(invisible);

        Kisman.instance.settingsManager.rSetting(new Setting("DistanceLine", this, "Distance"));

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 4.25f, 0, 4.25f, false));

        setmgr.rSetting(renderLine);
        setmgr.rSetting(targetEsp);

        setmgr.rSetting(new Setting("SwitchLine", this, "Switch"));
        setmgr.rSetting(switchMode);
        setmgr.rSetting(packetSwitch);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(mc.player.isDead) return;

        boolean player = Kisman.instance.settingsManager.getSettingByName(this, "Player").getValBoolean();
        boolean monster = Kisman.instance.settingsManager.getSettingByName(this, "Monster").getValBoolean();
        boolean passive = Kisman.instance.settingsManager.getSettingByName(this, "Passive").getValBoolean();

        boolean hitsound = Kisman.instance.settingsManager.getSettingByName(this, "HitSound").getValBoolean();

        float distance = Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValFloat();

        if(mode.getValString().equalsIgnoreCase("Multi")) {
            for (int i = 0; i < mc.world.loadedEntityList.size(); i++) {
                if (mc.world.loadedEntityList.get(i) != null && ((mc.world.loadedEntityList.get(i) instanceof EntityPlayer && player) || (mc.world.loadedEntityList.get(i) instanceof EntityMob && monster) || (mc.world.loadedEntityList.get(i) instanceof EntityAnimal && passive))) {
                    Entity entity = mc.world.loadedEntityList.get(i);
                    if (Config.instance.friends.getValBoolean() && entity instanceof EntityPlayer && Kisman.instance.friendManager.isFriend((EntityPlayer) entity))  continue;
                    if(!weaponCheck()) return;
                    if(!fallCheck() && useFallDist.getValBoolean()) return;
                    doKillAura(entity, hitsound, false);
                }
            }
        } else if(mode.getValString().equalsIgnoreCase("Single")) {
           target = EntityUtil.getTarget(distance);

           if(target == null) return;
           if(!weaponCheck()) return;
            if(!fallCheck() && useFallDist.getValBoolean()) return;
           doKillAura(target, hitsound, true);
        }
    }

    private boolean fallCheck() {
        return mc.player.fallDistance > fallDistance.getValFloat();
    }

    private void doKillAura(Entity entity, boolean hitsound, boolean single) {
        if(mc.player.getDistance(entity) <= 4.15 && entity.ticksExisted % 20 == 0 && mc.player != entity) {
            boolean isShieldActive = false;

            if(shieldBreaker.getValBoolean() && single) if (target.getHeldItemMainhand().getItem() instanceof ItemShield || target.getHeldItemOffhand().getItem() instanceof ItemShield) if (target.isHandActive()) isShieldActive = true;

            int oldSlot = mc.player.inventory.currentItem;
            int weaponSlot = InventoryUtil.findWeaponSlot(0, 9, isShieldActive);

            boolean isHit = false;
            if(!switchMode.getValString().equalsIgnoreCase("None")) {
                if(weaponSlot != -1) {
                    switch (switchMode.getValString()) {
                        case "Normal": {
                            InventoryUtil.switchToSlot(weaponSlot, false);
                            break;
                        }
                        case "Silent": {
                            InventoryUtil.switchToSlot(weaponSlot, true);
                            break;
                        }
                    }
                } else return;
            }

            attack(entity);
            isHit = true;

            if (hitsound && isHit) mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_STONE_BREAK, 1));
            if(switchMode.getValString().equalsIgnoreCase("Silent") && oldSlot != -1) InventoryUtil.switchToSlot(oldSlot, true);
        }
    }

    private void attack(Entity entity) {
        float oldYaw = mc.player.rotationYaw, oldPitch = mc.player.rotationPitch;
        rotation(entity);

        if(packetAttack.getValBoolean()) mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        else mc.playerController.attackEntity(mc.player, entity);

        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.resetCooldown();

        if(rotations.getValString().equals("Silent")) {
            mc.player.rotationYaw = oldYaw;
            mc.player.rotationPitch = oldPitch;
        }
    }

    private void rotation(Entity entity) {
        switch (rotations.getValString()) {
            case "None": break;
            case "Normal":
            case "Silent": {
                float[] rots = RotationUtils.getRotation(entity);
                mc.player.rotationYaw = rots[0];
                mc.player.rotationPitch = rots[1];
                break;
            }
            case "WellMore": {
                float[] rots = RotationUtils.lookAtRandomed(entity);
                mc.player.rotationYaw = rots[0];
                mc.player.rotationPitch = rots[1];
                break;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(!targetEsp.getValBoolean() || target == null) return;
        if (target.getHealth() > 0.0f) {
            GL11.glPushMatrix();
            int color = target.hurtResistantTime > 15 ? ColorUtils.getColor(255,100,100) : ColorUtils.rainbow(1,10);
            double x =  target.lastTickPosX + (target.posX -target.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.renderManager.renderPosX;
            double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.renderManager.renderPosY;
            double z = target.lastTickPosZ + (target.posZ -target.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.renderManager.renderPosZ;
            double d = (double) target.getEyeHeight() + 0.15;
            double d2 = target.isSneaking() ? 0.25 : 0.0;
            double mid = 0.5;
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glTranslated((x -= 0.5) + mid, (y += d - d2) + mid, (z -= 0.5) + mid);
            GL11.glRotated(-target.rotationYaw % 360.0f, 0.0, 1.0, 0.0);
            GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GLUtils.glColor(color);
            RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 0.05, z + 1.0));
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }

    private boolean weaponCheck() {
        if(switchMode.getValString().equals("None")) {
            switch (weapon.getValString()) {
                case "None": break;
                case "Sword": if(!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) return false;
                case "Axe": if(!(mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)) return false;
                case "Both": if(!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)) return false;
            }
        }

        return true;
    }

    public enum RotateMode {None, Normal, Silent, WellMore}
}