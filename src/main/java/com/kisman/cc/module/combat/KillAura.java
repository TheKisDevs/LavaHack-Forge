package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;

public class KillAura extends Module {
    public static KillAura instance;

    private boolean player;
    private boolean monster;
    private boolean passive;
    private boolean hitsound;

    private float distance;

    public Entity target;

    private Setting hitLine = new Setting("HitLine", this, "Hit");
    private Setting shieldBreaker = new Setting("ShieldBreaker", this, true);
    private Setting onlyCritHit = new Setting("OnlyCritHit", this, false);
    private Setting onlyCritHitSound = new Setting("OnlyCritHitSound", this, true);
    private Setting critFix = new Setting("CritFix", this, false);

    private Setting weapon = new Setting("Weapon", this, "Sword", new ArrayList<>(Arrays.asList("Sword", "Axe", "Both", "None")));

    private Setting invisible = new Setting("Invisible", this, false);

    private Setting fallDistance = new Setting("FallDistance", this, 0.1, 0, 0.2, false);

    private Setting renderLine = new Setting("RenderLine", this, "Render");
    private Setting targetEsp = new Setting("TargetESP", this, true);

    private Setting calcMode = new Setting("Calc", this, "Multi", new ArrayList<>(Arrays.asList("Multi", "Single")));

    private Setting switchMode = new Setting("SwitchMode", this, "None", new ArrayList<>(Arrays.asList("None", "Normal", "Silent")));
    private Setting packetSwitch = new Setting("PacketSwitch", this, true);

    public KillAura() {
        super("KillAura", "8", Category.COMBAT);

        instance = this;

        setmgr.rSetting(hitLine);
        setmgr.rSetting(shieldBreaker);
        Kisman.instance.settingsManager.rSetting(new Setting("HitSound", this, false));
        setmgr.rSetting(onlyCritHit);
        setmgr.rSetting(onlyCritHitSound);
        setmgr.rSetting(critFix);

        setmgr.rSetting(new Setting("WeaponLine", this, "Weapon"));
        setmgr.rSetting(weapon);

        Kisman.instance.settingsManager.rSetting(new Setting("TargetsLine", this, "Targets"));
        Kisman.instance.settingsManager.rSetting(new Setting("Player", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Monster", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Passive", this, true));
        setmgr.rSetting(invisible);

        Kisman.instance.settingsManager.rSetting(new Setting("DistanceLine", this, "Distance"));

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 4.25f, 0, 4.25f, false));
        setmgr.rSetting(fallDistance);

        setmgr.rSetting(renderLine);
        setmgr.rSetting(targetEsp);

        setmgr.rSetting(new Setting("OtherLine", this, "Other"));
        setmgr.rSetting(calcMode);

        setmgr.rSetting(new Setting("SwitchLine", this, "Switch"));
        setmgr.rSetting(switchMode);
        setmgr.rSetting(packetSwitch);
    }

    public void onEnable() {
        this.player = Kisman.instance.settingsManager.getSettingByName(this,"Player").getValBoolean();
        this.monster = Kisman.instance.settingsManager.getSettingByName(this,"Monster").getValBoolean();
        this.passive = Kisman.instance.settingsManager.getSettingByName(this,"Passive").getValBoolean();

        this.hitsound = Kisman.instance.settingsManager.getSettingByName(this,"HitSound").getValBoolean();

        this.distance = (float) Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(mc.player.isDead) return;

        if(critFix.getValBoolean()) {

        }

        this.player = Kisman.instance.settingsManager.getSettingByName(this,"Player").getValBoolean();
        this.monster = Kisman.instance.settingsManager.getSettingByName(this,"Monster").getValBoolean();
        this.passive = Kisman.instance.settingsManager.getSettingByName(this,"Passive").getValBoolean();

        this.hitsound = Kisman.instance.settingsManager.getSettingByName(this,"HitSound").getValBoolean();

        this.distance = (float) Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();

        if(calcMode.getValString().equalsIgnoreCase("Multi")) {
            for (int i = 0; i < mc.world.loadedEntityList.size(); i++) {
                if (mc.world.loadedEntityList.get(i) != null && ((mc.world.loadedEntityList.get(i) instanceof EntityPlayer && this.player) || (mc.world.loadedEntityList.get(i) instanceof EntityMob && this.monster) || (mc.world.loadedEntityList.get(i) instanceof EntityAnimal && this.passive))) {
                    Entity entity = mc.world.loadedEntityList.get(i);
                    if (Config.instance.friends.getValBoolean() && entity instanceof EntityPlayer && Kisman.instance.friendManager.isFriend((EntityPlayer) entity)) {
                        continue;
                    }

                    if (mc.player.getDistance(mc.world.loadedEntityList.get(i)) <= 4.15 && mc.world.loadedEntityList.get(i).ticksExisted % 20 == 0 && mc.world.loadedEntityList.get(i) != mc.player) {
                        final int oldSlot = mc.player.inventory.currentItem;

                        boolean isHit = false;
                        boolean isCrit = onlyCritHit.getValBoolean() && (mc.player.getCooledAttackStrength(1) == 1 || mc.player.getCooledAttackStrength(1) == 0.8);
                        if(!switchMode.getValString().equalsIgnoreCase("None") && (!onlyCritHit.getValBoolean() || isCrit)) {
                            int weaponSlot = getBestItemSlot(mc.world.loadedEntityList.get(i));

                            switch (switchMode.getValString()) {
                                case "Normal": {
                                    if(packetSwitch.getValBoolean()) {
                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(weaponSlot));
                                    } else {
                                        mc.player.inventory.currentItem = weaponSlot;
                                    }

                                    break;
                                }
                                case "Silent": {
                                    InventoryUtil.switchToSlot(weaponSlot, true);
                                    break;
                                }
                            }
                        }

                        if(onlyCritHit.getValBoolean()) {
                            if(mc.player.getCooledAttackStrength(1) == 1 || mc.player.getCooledAttackStrength(1) == 0.8) {
                                mc.playerController.attackEntity(mc.player, mc.world.loadedEntityList.get(i));
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                                mc.player.resetCooldown();
                                isHit = true;
                            }
                        } else {
                            mc.playerController.attackEntity(mc.player, mc.world.loadedEntityList.get(i));
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            mc.player.resetCooldown();
                            isHit = true;
                        }

                        if (this.hitsound && isHit) {
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_STONE_BREAK, 1));
                        }

                        if(switchMode.getValString().equalsIgnoreCase("Silent")) {
                            InventoryUtil.switchToSlot(oldSlot, true);
                        }
                    }
                }
            }
        } else if(calcMode.getValString().equalsIgnoreCase("Single")) {
            target = getTarget();

            if(target != null) {
                float[] rots = RotationUtils.getMatrixRotations(target, true);

                mc.player.rotationYaw = rots[0];
                mc.player.rotationPitch = rots[1];

                mc.timer.renderPartialTicks = 0.05f;

                mc.player.renderYawOffset = rots[0];
                mc.player.rotationYawHead = rots[0];

                boolean isCrit = onlyCritHit.getValBoolean() && (mc.player.getCooledAttackStrength(1) == 1 || mc.player.getCooledAttackStrength(1) == 0.8);

                if(isCrit) {
                    if(mc.player.getCooledAttackStrength(1) == 1) {
                        if(mc.player.fallDistance > fallDistance.getValDouble()) return;

                        mc.player.connection.sendPacket(new CPacketUseEntity(target));
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.resetCooldown();
                    } else if(mc.player.getCooledAttackStrength(1) == 0.8) {
                        if(mc.player.fallDistance > fallDistance.getValDouble()) return;

                        mc.player.connection.sendPacket(new CPacketUseEntity(target));
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.resetCooldown();
                    }
                } else {
                    mc.player.connection.sendPacket(new CPacketUseEntity(target));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.resetCooldown();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(!targetEsp.getValBoolean()) return;
        if(target == null) return;
        if(!(target instanceof EntityPlayer)) return;

        float yDist = 4;
        int yTotal = 0;

        EntityPlayer target = (EntityPlayer) this.target;

        if (target.getHealth() > 0.0f) {
            GL11.glPushMatrix();
            int color = target.hurtResistantTime > 15 ? ColorUtils.getColor(255,100,100) : ColorUtils.rainbow(1,10);
            double x =  target.lastTickPosX + (target.posX -target.lastTickPosX)
                    * (double) mc.timer.renderPartialTicks - mc.renderManager.renderPosX;
            double y = target.lastTickPosY + (target.posY - target.lastTickPosY)
                    * (double) mc.timer.renderPartialTicks - mc.renderManager.renderPosY;
            double z = target.lastTickPosZ + (target.posZ -target.lastTickPosZ)
                    * (double) mc.timer.renderPartialTicks - mc.renderManager.renderPosZ;
            double d = (double) target.getEyeHeight() + 0.15;
            double d2 = target.isSneaking() ? 0.25 : 0.0;
            double mid = 0.5;
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glTranslated((x -= 0.5) + mid, (y += d - d2) + mid, (z -= 0.5) + mid);
            GL11.glRotated(-target.rotationYaw % 360.0f, 0.0, 1.0,
                    0.0);
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

    public boolean isBlocking(EntityPlayer player) {
        return player.isHandActive() && player.getActiveItemStack().getItem().getItemUseAction(player.getActiveItemStack()) == EnumAction.BLOCK;
    }

    private int getBestItemSlot(Entity target) {
        int slot = -1;

        EntityPlayer targetPlayer = null;

        boolean shield = false;

        if(target instanceof EntityPlayer) {
            shield = shieldBreaker.getValBoolean() && isBlocking((EntityPlayer) target);
        }

        boolean sword = weapon.getValString().equalsIgnoreCase("Sword") || weapon.getValString().equalsIgnoreCase("Both");

        block1: {
            axe1: {
                if(shield || weapon.getValString().equalsIgnoreCase("Axe") || weapon.getValString().equalsIgnoreCase("Both")) {
                    block2: {
                        if(InventoryUtil.findItem(Items.DIAMOND_AXE, 0, 9) == -1) {
                            block3: {
                                if(InventoryUtil.findItem(Items.GOLDEN_AXE, 0, 9) == -1) {
                                    block4: {
                                        if(InventoryUtil.findItem(Items.IRON_AXE, 0, 9) == -1) {
                                            block5: {
                                                if(InventoryUtil.findItem(Items.STONE_AXE, 0, 9) == -1) {
                                                    block6: {
                                                        if(InventoryUtil.findItem(Items.WOODEN_AXE, 0, 9) == -1) {
                                                            break axe1;
                                                        } else {
                                                            slot = InventoryUtil.findItem(Items.WOODEN_AXE, 0, 9);
                                                            break block1;
                                                        }
                                                    }
                                                } else {
                                                    slot = InventoryUtil.findItem(Items.STONE_AXE, 0, 9);
                                                    break block1;
                                                }
                                            }
                                        } else {
                                            slot = InventoryUtil.findItem(Items.IRON_AXE, 0, 9);
                                            break block1;
                                        }
                                    }
                                } else {
                                    slot = InventoryUtil.findItem(Items.GOLDEN_AXE, 0, 9);
                                    break block1;
                                }
                            }
                        } else {
                            slot = InventoryUtil.findItem(Items.DIAMOND_AXE, 0, 9);
                            break block1;
                        }
                    }
                } else {
                    break axe1;
                }
            }

            sword: {
                if(sword) {
                    block7: {
                        if(InventoryUtil.findItem(Items.DIAMOND_SWORD, 0, 9) == -1) {
                            block8: {
                                if(InventoryUtil.findItem(Items.GOLDEN_SWORD, 0, 9) == -1) {
                                    block9: {
                                        if(InventoryUtil.findItem(Items.IRON_SWORD, 0, 9) == -1) {
                                            block10: {
                                                if(InventoryUtil.findItem(Items.STONE_SWORD, 0, 9) == -1) {
                                                    block11: {
                                                        if(InventoryUtil.findItem(Items.WOODEN_SWORD, 0, 9) == -1) {
                                                            break block1;
                                                        } else {
                                                            slot = InventoryUtil.findItem(Items.WOODEN_SWORD, 0, 9);
                                                            break block1;
                                                        }
                                                    }
                                                } else {
                                                    slot = InventoryUtil.findItem(Items.STONE_SWORD, 0, 9);
                                                    break block1;
                                                }
                                            }
                                        } else {
                                            slot = InventoryUtil.findItem(Items.IRON_SWORD, 0, 9);
                                            break block1;
                                        }
                                    }
                                } else {
                                    slot = InventoryUtil.findItem(Items.GOLDEN_SWORD, 0, 9);
                                    break block1;
                                }
                            }
                        } else {
                            slot = InventoryUtil.findItem(Items.DIAMOND_SWORD, 0, 9);
                            break block1;
                        }
                    }
                } else {
                    slot = -1;
                    break block1;
                }
            }
        }

        return slot;
    }

    private Entity getTarget() {
        Entity target = null;
        double dist = distance;
        for (Entity entity : mc.world.loadedEntityList) {
            if(entity != mc.player && isValidEntity(entity)) {
                if(mc.player.getDistance(entity) <= dist) {
                    target = entity;
                    dist = mc.player.getDistance(entity);
                }
            }
        }
        return target;
    }

    private boolean isValidEntity(Entity entity) {
        if((entity instanceof EntityPlayer && this.player) || (entity instanceof EntityMob && this.monster) || (entity instanceof EntityAnimal && this.passive))     {
            if(entity instanceof EntityPlayer) {
                if(entity.isInvisible() && invisible.getValBoolean()) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}