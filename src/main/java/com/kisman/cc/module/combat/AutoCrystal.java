package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.module.chat.Notification;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.*;

public class AutoCrystal extends Module{
    private Setting ticks = new Setting("Ticks", this, 1, 0, 10, true);


    private Setting modes = new Setting("Modes", this, "Mode");

    private Setting mode = new Setting("Mode", this, "ClientTick", new ArrayList<>(Arrays.asList("ClientTick", "MotionTick")));
    private Setting placeMode = new Setting("PlaceMode", this, "None", new ArrayList<>(Arrays.asList("None", "Nearest", "Priority", "MostDamage")));
    private Setting destroyMode = new Setting("DestroyMode", this, "None", new ArrayList<>(Arrays.asList("None", "Smart", "Always", "OnlyOwn")));


    private Setting target = new Setting("Target", this, "Target");

    private Setting player = new Setting("Player", this, false);
    private Setting animals = new Setting("Animals", this, false);
    private Setting tamed = new Setting("Tamed", this, false);
    private Setting hostile = new Setting("Hostile", this, false);
    private Setting resetRotationNoTarget = new Setting("ResetRotationNoTarget", this, false);


    private Setting place = new Setting("Place", this, "Place");
    private Setting facePlace = new Setting("FacePlace", this, false);
    private Setting multiPlace = new Setting("MultiPlace", this, false);
    private Setting onlyPlaceWithCrystal = new Setting("OnlyPlaceWithCrystal", this, false);
    private Setting placeObsidianIfNoValidSpots = new Setting("PlaceObsidianIfNoValidSpots", this, false);


    private Setting _break = new Setting("Break", this, "Break");

    private Setting antiWeakness = new Setting("AntiWeakness", this, false);


    private Setting range = new Setting("Range", this, "Range/Distance");

    private Setting placeRange = new Setting("PlaceRange", this, 4f, 0f, 5f, false);
    private Setting destroyRange = new Setting("DestroyRange", this, 4f, 0f, 5f, false);
    private Setting wallsRange = new Setting("WallsRange", this, 3.5f, 0f, 5f, false);


    private Setting damage = new Setting("Damage", this, "Damage");

    private Setting minDMG = new Setting("MinDMG", this, 4f, 0f, 20f, true);
    private Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 4f, 0f, 20f, true);
    private Setting facePlaceHP = new Setting("FacePlaceHP", this, 8f, 0f, 36f, true);
    private Setting noSuicide = new Setting("NoSuicide", this, true);


    private Setting pause = new Setting("Pause", this, "Pause");

    private Setting minHealthPause = new Setting("MinHealthPause", this, false);
    private Setting requiredHealth = new Setting("RequiredHealth", this, 11, 0, 36, true);
    private Setting pauseWhileEating = new Setting("PauseWhileEating", this, false);
    private Setting pauseIfHittingBlock = new Setting("PauseIfHittingBlock", this, false);


    private Setting hands = new Setting("Hands", this, "Hands");

    private Setting antiWeaknessHand = new Setting("AntiWeaknessHand", this, "MainHand", new ArrayList<>(Arrays.asList("MainHand", "OffHand")));
    private Setting breakHand = new Setting("BreakHand", this, "MainHand", new ArrayList<>(Arrays.asList("MainHand", "OffHand")));
    private Setting placeHand = new Setting("PlaceHand", this, "MainHand", new ArrayList<>(Arrays.asList("MainHand", "OffHand")));
    private Setting ghostHand = new Setting("GhostHand", this, false);
    private Setting ghostHandWeakness = new Setting("GhostHandWeakness", this, false);


    private Setting _multiplace = new Setting("MutliPlace", this, "MutliPlace");

    private Setting autoMultiPlace = new Setting("AutoMultiPlace", this, false);
    private Setting healthBelowAutoMultiplace = new Setting("HealthBelowAutoMultiplace", this, 11, 0, 36, true);


    private Setting swap = new Setting("Swap", this, "Swap");

    private Setting swapCrystal = new Setting("Crystal", this, false);
    private Setting swapObby = new Setting("Obby", this, false);
    private Setting switchBack = new Setting("SwitchBack", this, false);


    private Setting _render = new Setting("_Render", this, "Render");

    private Setting __render = new Setting("Render", this, true);
    private Setting render = new Setting("__Render", this, "Render", new float[] {1, 1, 1, 1}, false);



    private ArrayList<CPacketPlayer.PositionRotation> packets = new ArrayList<CPacketPlayer.PositionRotation>();
    private ArrayList<BlockPos> placedCrystal = new ArrayList<>();
    private EntityLivingBase e_target = null;
    private AimBot aimBot;
    private ICamera camera = new Frustum();
    private int waitTicks = 0;
    private int spoofTimerResetTicks = 0;

    public AutoCrystal() {
        super("AutoCrystal", "ezzz", Category.COMBAT);

        aimBot = AimBot.instance;

        setmgr.rSetting(ticks);

        setmgr.rSetting(modes);
        setmgr.rSetting(mode);
        setmgr.rSetting(placeMode);
        setmgr.rSetting(destroyMode);

        setmgr.rSetting(target);
        setmgr.rSetting(player);
        setmgr.rSetting(animals);
        setmgr.rSetting(tamed);
        setmgr.rSetting(hostile);
        setmgr.rSetting(resetRotationNoTarget);

        setmgr.rSetting(place);
        setmgr.rSetting(facePlace);
        setmgr.rSetting(multiPlace);
        setmgr.rSetting(onlyPlaceWithCrystal);
        setmgr.rSetting(placeObsidianIfNoValidSpots);

        setmgr.rSetting(_break);
        setmgr.rSetting(antiWeakness);

        setmgr.rSetting(range);
        setmgr.rSetting(placeRange);
        setmgr.rSetting(destroyRange);
        setmgr.rSetting(wallsRange);

        setmgr.rSetting(damage);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(facePlaceHP);
        setmgr.rSetting(noSuicide);

        setmgr.rSetting(pause);
        setmgr.rSetting(minHealthPause);
        setmgr.rSetting(requiredHealth);
        setmgr.rSetting(pauseWhileEating);
        setmgr.rSetting(pauseIfHittingBlock);

        setmgr.rSetting(hands);
        setmgr.rSetting(antiWeaknessHand);
        setmgr.rSetting(breakHand);
        setmgr.rSetting(placeHand);
        setmgr.rSetting(ghostHand);
        setmgr.rSetting(ghostHandWeakness);

        setmgr.rSetting(_multiplace);
        setmgr.rSetting(autoMultiPlace);
        setmgr.rSetting(healthBelowAutoMultiplace);

        setmgr.rSetting(swap);
        setmgr.rSetting(swapCrystal);
        setmgr.rSetting(swapObby);
        setmgr.rSetting(switchBack);

        setmgr.rSetting(_render);
        setmgr.rSetting(__render);
        setmgr.rSetting(render);
    }

    public void onEnable() {
        packets.clear();

        if(!aimBot.isToggled()) aimBot.setToggled(true);

        aimBot.rotationSpoof = null;
        waitTicks = (int) ticks.getValDouble();

        placedCrystal.clear();

        Kisman.EVENT_BUS.subscribe(playerMotionUpdateListener);
        Kisman.EVENT_BUS.subscribe(receiveListener);
    }

    public void onDisable() {
        packets.clear();

        aimBot.rotationSpoof = null;
        waitTicks = (int) ticks.getValDouble();

        placedCrystal.clear();

        Kisman.EVENT_BUS.unsubscribe(playerMotionUpdateListener);
        Kisman.EVENT_BUS.unsubscribe(receiveListener);
    }

    public void update() {
        if (mc.player == null && mc.world == null) {
            return;
        }

        if(!mode.getValString().equalsIgnoreCase("ClientTick")) return;

        if(pauseWhileEating.getValBoolean() && PlayerUtil.IsEating()) {
            waitTicks = 0;
            return;
        }

        if(needPause()) {
            waitTicks = 0;

            aimBot.rotationSpoof = null;
            return;
        }

        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> isValidCrystal(entity))
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(entityEnderCrystal -> mc.player.getDistance(entityEnderCrystal)))
                .orElse(null);

        int waitValue = (int) ticks.getValDouble();

        if(waitTicks < waitValue) {
            ++waitTicks;
            return;
        }

        waitTicks = 0;

        if(spoofTimerResetTicks > 0) {
            --spoofTimerResetTicks;
        }

        if(resetRotationNoTarget.getValBoolean()) {
            if(target == null && aimBot.rotationSpoof != null) {
                aimBot.rotationSpoof = null;
            }
        } else {
            if(spoofTimerResetTicks == 0) {
                spoofTimerResetTicks = 200;

                aimBot.rotationSpoof = null;
            }
        }

        if(multiPlace.getValBoolean()) {
            if(!destroyMode.getValString().equalsIgnoreCase("None")) {
                HandleBreakCrystals(crystal, null);
            }

            try {
                if(!placeMode.getValString().equalsIgnoreCase("None")) {
                    HandlePlaceCrystal(null);
                }
            } catch (Exception e) {}
        } else {
            try {
                if (!HandleBreakCrystals(crystal, null)) {
                    HandlePlaceCrystal(null);
                }
            } catch (Exception e) {}
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> playerMotionUpdateListener = new Listener<>(event -> {
        if(event.getEra() != Event.Era.PRE) return;
        
        if(!mode.getValString().equalsIgnoreCase("MotionTick")) return;

        //pauses
        if(pauseWhileEating.getValBoolean() && PlayerUtil.IsEating()) {
            waitTicks = 0;
            return;
        }

        if(needPause()) {
            waitTicks = 0;

            aimBot.rotationSpoof = null;
            return;
        }

        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> isValidCrystal(entity))
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(entityEnderCrystal -> mc.player.getDistance(entityEnderCrystal)))
                .orElse(null);

        int waitValue = (int) ticks.getValDouble();

        if(waitTicks < waitValue) {
            ++waitTicks;
            return;
        }

        waitTicks = 0;

        if(multiPlace.getValBoolean()) {
            boolean result = false;

            if(!destroyMode.getValString().equalsIgnoreCase("None")) {
                HandleBreakCrystals(crystal, event);
            }

            if(!placeMode.getValString().equalsIgnoreCase("None")) {
                try {
                    final BlockPos pos = HandlePlaceCrystal(event);

                    if(!result && pos != BlockPos.ORIGIN) {
                        result = true;
                    }
                } catch (Exception e) {}
            }

            if(result) {
                waitTicks = (int) ticks.getValDouble();
            }
        } else {
            if(!HandleBreakCrystals(crystal, event)) {
                try {
                    final BlockPos pos = HandlePlaceCrystal(event);

                    if(pos != BlockPos.ORIGIN) {
                        waitTicks = (int) ticks.getValDouble();
                    }
                } catch (Exception e) {}
            }
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

            if(packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for(Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if(entity instanceof EntityEnderCrystal) {
                        if(entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6f) {
                            entity.setDead();
                        }

                        placedCrystal.removeIf(blockPos ->
                                blockPos.getDistance(
                                        (int) packet.getX(),
                                        (int) packet.getY(),
                                        (int) packet.getZ()
                                ) <= 6
                        );
                    }
                }
            }
        }
    });

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(__render.getValBoolean()) {
            ArrayList<BlockPos> placedCrystal = new ArrayList<>(this.placedCrystal);

            for(BlockPos pos : placedCrystal) {
                if(pos == null) continue;

                final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX,
                        pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ,
                        pos.getX() + 1 - mc.getRenderManager().viewerPosX,
                        pos.getY() + (1) - mc.getRenderManager().viewerPosY,
                        pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);

                camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY,
                        mc.getRenderViewEntity().posZ);

                pushMatrix();
                enableBlend();
                disableDepth();
                tryBlendFuncSeparate(770, 771, 0, 1);
                disableTexture2D();
                depthMask(false);
                glEnable(GL_LINE_SMOOTH);
                glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
                GlStateManager.glLineWidth(1.5f);

//                    RenderUtil.drawColorBox(bb, render.getR(), render.getG(), render.getB(), render.getA());
//                    RenderUtil.drawFilledBox(bb, l_Color);
                RenderUtil.drawBoundingBox(bb, 1f, render.getR(), render.getG(), render.getB(), render.getA());
                glDisable(GL_LINE_SMOOTH);
                depthMask(true);
                enableDepth();
                enableTexture2D();
                disableBlend();
                popMatrix();

                if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX,
                        bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ,
                        bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY,
                        bb.maxZ + mc.getRenderManager().viewerPosZ))) {

                }
            }
        }
    }

    private boolean isValidCrystal(Entity p_Entity)
    {
        if (!(p_Entity instanceof EntityEnderCrystal))
            return false;

        if (p_Entity.getDistance(mc.player) > (!mc.player.canEntityBeSeen(p_Entity) ? wallsRange.getValDouble() : destroyRange.getValDouble()))
            return false;

        switch (destroyMode.getValString())
        {
            case "Always":
                return true;
            case "OnlyOwn":
                /// create copy
                for (BlockPos l_Pos : new ArrayList<BlockPos>(placedCrystal)) {
                    if (l_Pos != null && l_Pos.getDistance((int)p_Entity.posX, (int)p_Entity.posY, (int)p_Entity.posZ) <= 3.0)
                        return true;
                }
                break;
            case "Smart":
                EntityLivingBase l_Target = e_target != null ? e_target : getNearTarget(p_Entity);

                if (l_Target == null)
                    return false;

                float l_TargetDMG = CrystalUtils.calculateDamage(mc.world, p_Entity.posX + 0.5, p_Entity.posY + 1.0, p_Entity.posZ + 0.5, l_Target, 0);
                float l_SelfDMG = CrystalUtils.calculateDamage(mc.world, p_Entity.posX + 0.5, p_Entity.posY + 1.0, p_Entity.posZ + 0.5, mc.player, 0);

                float l_MinDmg = (float) minDMG.getValDouble();

                /// FacePlace
                if (l_Target.getHealth() + l_Target.getAbsorptionAmount() <= facePlaceHP.getValDouble())
                    l_MinDmg = 1f;

                if (l_TargetDMG > l_MinDmg && l_SelfDMG < maxSelfDMG.getValDouble())
                    return true;

                break;
            default:
                break;
        }

        return false;
    }

    private boolean isValidTarget(Entity entity)
    {
        if (entity == null)
            return false;

        if (!(entity instanceof EntityLivingBase))
            return false;

        if (entity.isDead || ((EntityLivingBase)entity).getHealth() <= 0.0f)
            return false;

        if (entity.getDistance(mc.player) > 20.0f)
            return false;

        if (entity instanceof EntityPlayer && player.getValBoolean()) {
            if (entity == mc.player)
                return false;

            return true;
        }

        if (hostile.getValBoolean() && EntityUtil.isHostileMob(entity))
            return true;
        if (animals.getValBoolean() && EntityUtil.isPassive(entity))
            return true;
        if (tamed.getValBoolean() && entity instanceof AbstractChestHorse && ((AbstractChestHorse)entity).isTame())
            return true;

        return false;
    }

    private EntityLivingBase getNearTarget(Entity p_DistanceTarget)
    {
        return mc.world.loadedEntityList.stream()
                .filter(p_Entity -> isValidTarget(p_Entity))
                .map(p_Entity -> (EntityLivingBase) p_Entity)
                .min(Comparator.comparing(p_Entity -> p_DistanceTarget.getDistance(p_Entity)))
                .orElse(null);
    }

    private boolean HandleBreakCrystals(EntityEnderCrystal p_Crystal, @Nullable EventPlayerMotionUpdate p_Event)
    {
        if (p_Crystal != null)
        {
            final double l_Pos[] =  EntityUtil.calculateLookAt(
                    p_Crystal.posX + 0.5,
                    p_Crystal.posY - 0.5,
                    p_Crystal.posZ + 0.5,
                    mc.player);

            if (mode.getValString().equalsIgnoreCase("ClientTick"))
            {
                aimBot.rotationSpoof = new RotationSpoof((float)l_Pos[0], (float)l_Pos[1]);

                Random rand = new Random(2);

                aimBot.rotationSpoof.yaw += (rand.nextFloat() / 100);
                aimBot.rotationSpoof.pitch += (rand.nextFloat() / 100);
            }

            int l_PrevSlot = -1;

            if (antiWeakness.getValBoolean() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (mc.player.getHeldItemMainhand() == ItemStack.EMPTY || (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemTool))) {
                    for (int l_I = 0; l_I < 9; ++l_I) {
                        ItemStack l_Stack = mc.player.inventory.getStackInSlot(l_I);

                        if (l_Stack == ItemStack.EMPTY)
                            continue;

                        if (l_Stack.getItem() instanceof ItemTool || l_Stack.getItem() instanceof ItemSword) {
                            l_PrevSlot = mc.player.inventory.currentItem;
                            mc.player.inventory.currentItem = l_I;
                            mc.playerController.updateController();
                            break;
                        }
                    }
                }
            }

            if (mode.getValString().equalsIgnoreCase("MotionTick") && p_Event != null) ///< p_Event should not null
            {
                p_Event.cancel();

                SpoofRotationsTo((float)l_Pos[0], (float)l_Pos[1]);
            }

            mc.playerController.attackEntity(mc.player, p_Crystal);
            if(breakHand.getValString().equalsIgnoreCase("MainHand")) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            } else if(breakHand.getValString().equalsIgnoreCase("OffHand")) {
                mc.player.swingArm(EnumHand.OFF_HAND);
            }

            if (ghostHandWeakness.getValBoolean() && l_PrevSlot != -1)
            {
                mc.player.inventory.currentItem = l_PrevSlot;
                mc.playerController.updateController();
            }

            return true;
        }

        return false;
    }

    public boolean needPause() {
        if(minHealthPause.getValBoolean() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) < (int) requiredHealth.getValDouble()) {
            return true;
        }

        if(pauseIfHittingBlock.getValBoolean() &&  mc.playerController.isHittingBlock && mc.player.getHeldItemMainhand().getItem() instanceof  ItemTool) {
            return true;
        }

        return false;
    }

    private void SpoofRotationsTo(float p_Yaw, float p_Pitch)
    {
        boolean l_IsSprinting = mc.player.isSprinting();

        if (l_IsSprinting != mc.player.serverSprintState)
        {
            if (l_IsSprinting)
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
            else
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            mc.player.serverSprintState = l_IsSprinting;
        }

        boolean l_IsSneaking = mc.player.isSneaking();

        if (l_IsSneaking != mc.player.serverSneakState)
        {
            if (l_IsSneaking)
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            else
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            mc.player.serverSneakState = l_IsSneaking;
        }

        if (PlayerUtil.isCurrentViewEntity())
        {
            float l_Pitch = p_Pitch;
            float l_Yaw = p_Yaw;

            AxisAlignedBB axisalignedbb = mc.player.getEntityBoundingBox();
            double l_PosXDifference = mc.player.posX - mc.player.lastReportedPosX;
            double l_PosYDifference = axisalignedbb.minY - mc.player.lastReportedPosY;
            double l_PosZDifference = mc.player.posZ - mc.player.lastReportedPosZ;
            double l_YawDifference = (double)(l_Yaw - mc.player.lastReportedYaw);
            double l_RotationDifference = (double)(l_Pitch - mc.player.lastReportedPitch);
            ++mc.player.positionUpdateTicks;
            boolean l_MovedXYZ = l_PosXDifference * l_PosXDifference + l_PosYDifference * l_PosYDifference + l_PosZDifference * l_PosZDifference > 9.0E-4D || mc.player.positionUpdateTicks >= 20;
            boolean l_MovedRotation = l_YawDifference != 0.0D || l_RotationDifference != 0.0D;

            if (mc.player.isRiding())
            {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0D, mc.player.motionZ, l_Yaw, l_Pitch, mc.player.onGround));
                l_MovedXYZ = false;
            }
            else if (l_MovedXYZ && l_MovedRotation)
            {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, axisalignedbb.minY, mc.player.posZ, l_Yaw, l_Pitch, mc.player.onGround));
            }
            else if (l_MovedXYZ)
            {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, axisalignedbb.minY, mc.player.posZ, mc.player.onGround));
            }
            else if (l_MovedRotation)
            {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(l_Yaw, l_Pitch, mc.player.onGround));
            }
            else if (mc.player.prevOnGround != mc.player.onGround)
            {
                mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            }

            if (l_MovedXYZ)
            {
                mc.player.lastReportedPosX = mc.player.posX;
                mc.player.lastReportedPosY = axisalignedbb.minY;
                mc.player.lastReportedPosZ = mc.player.posZ;
                mc.player.positionUpdateTicks = 0;
            }

            if (l_MovedRotation)
            {
                mc.player.lastReportedYaw = l_Yaw;
                mc.player.lastReportedPitch = l_Pitch;
            }

            mc.player.prevOnGround = mc.player.onGround;
            mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
        }
    }

    private void findNewTarget() {
        e_target = getNearTarget(mc.player);
    }

    public int findStackHotbar(Block type) {
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack.getItem() instanceof ItemBlock) {
                final ItemBlock block = (ItemBlock) stack.getItem();

                if (block.getBlock() == type) {
                    return i;
                }
            }
        }
        return -1;
    }

    private BlockPos HandlePlaceCrystal(@Nullable EventPlayerMotionUpdate p_Event) throws Exception {
        if (onlyPlaceWithCrystal.getValBoolean()) {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL)
                return BlockPos.ORIGIN;
        }

        List<BlockPos> l_AvailableBlockPositions = CrystalUtils.findCrystalBlocks(mc.player, (int) placeRange.getValDouble());

        switch (placeMode.getValString()) {
            case "Nearest":
                findNewTarget();
                break;
            case "Priority":
                if (e_target == null || e_target.getDistance(mc.player) > placeRange.getValDouble() + 2f || e_target.isDead || e_target.getHealth() <= 0.0f) ///< Allow 2 tolerence
                    findNewTarget();
                break;
            case "MostDamage": {
                if (l_AvailableBlockPositions.isEmpty()) {
                    findNewTarget();
                } else {
                    EntityLivingBase l_Target = null;

                    float minDMG = (float) this.minDMG.getValDouble();
                    float maxSelfDMG = (float) this.maxSelfDMG.getValDouble();
                    float dMG = 0.0f;

                    /// Iterate through all players
                    for (EntityPlayer player : mc.world.playerEntities) {
                        if (!isValidTarget(player))
                            continue;

                        /// Iterate block positions for this entity
                        for (BlockPos pos : l_AvailableBlockPositions) {
                            if (player.getDistanceSq(pos) >= 169.0D)
                                continue;

                            float l_TempDMG = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, player, 0);

                            if (l_TempDMG < minDMG)
                                continue;

                            float l_SelfTempDMG = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.player, 0);

                            if (l_SelfTempDMG > maxSelfDMG)
                                continue;

                            if (wallsRange.getValDouble() > 0) {
                                if (!PlayerUtil.CanSeeBlock(pos))
                                    if (pos.getDistance((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ) > wallsRange.getValDouble())
                                        continue;
                            }

                            if (l_TempDMG > dMG) {
                                dMG = l_TempDMG;
                                l_Target = player;
                            }
                        }
                    }

                    if (e_target == null)
                        e_target = getNearTarget(mc.player);

                    if (e_target != null && l_Target != e_target && l_Target != null && Notification.instance.target.getValBoolean()) {
                        ChatUtils.message(String.format("Found new target %s", l_Target.getName()));
                    }

                    e_target = l_Target;
                }
                break;
            }
            default:
                break;
        }

        if (l_AvailableBlockPositions.isEmpty()) {
            if (placeObsidianIfNoValidSpots.getValBoolean() && e_target != null) {
                int l_Slot = findStackHotbar(Blocks.OBSIDIAN);

                if (l_Slot != -1) {
                    if (mc.player.inventory.currentItem != l_Slot) {
                        mc.player.inventory.currentItem = l_Slot;
                        mc.playerController.updateController();
                        return BlockPos.ORIGIN;
                    }

                    float l_Range = (float) placeRange.getValDouble();

                    float l_TargetDMG = 0.0f;
                    float l_MinDmg = (float) minDMG.getValDouble();

                    /// FacePlace
                    if (e_target.getHealth() + e_target.getAbsorptionAmount() <= facePlaceHP.getValDouble())
                        l_MinDmg = 1f;

                    BlockPos l_TargetPos = null;

                    for (BlockPos l_Pos : BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), (float) placeRange.getValDouble(), (int)l_Range, false, true, 0)) {
                        BlockInteractionHelper.ValidResult l_Result = BlockInteractionHelper.valid(l_Pos);

                        if (l_Result != BlockInteractionHelper.ValidResult.Ok)
                            continue;

                        if (!CrystalUtils.CanPlaceCrystalIfObbyWasAtPos(l_Pos))
                            continue;

                        float l_TempDMG = CrystalUtils.calculateDamage(mc.world, l_Pos.getX() + 0.5, l_Pos.getY() + 1.0, l_Pos.getZ() + 0.5, e_target, 0);

                        if (l_TempDMG < l_MinDmg)
                            continue;

                        if (l_TempDMG >= l_TargetDMG) {
                            l_TargetPos = l_Pos;
                            l_TargetDMG = l_TempDMG;
                        }
                    }

                    if (l_TargetPos != null) {
                        BlockInteractionHelper.place(l_TargetPos, (float) placeRange.getValDouble(), true, false); ///< sends a new packet, might be bad for ncp flagging tomany packets..
                        if(Notification.instance.placeObby.getValBoolean())  ChatUtils.warning(String.format("Tried to place obsidian at %s would deal %s dmg", l_TargetPos.toString(), l_TargetDMG));
                    }
                }
            }

            return BlockPos.ORIGIN;
        }


        if (e_target == null)
            return BlockPos.ORIGIN;

        if (autoMultiPlace.getValBoolean()) {
            if (e_target.getHealth() + e_target.getAbsorptionAmount() <= healthBelowAutoMultiplace.getValDouble())
                multiPlace.setValBoolean(true);
            else
                multiPlace.setValBoolean(false);
        }

        float l_MinDmg = (float) minDMG.getValDouble();
        float l_MaxSelfDmg = (float) maxSelfDMG.getValDouble();
        float l_FacePlaceHealth = (float) facePlaceHP.getValDouble();

        /// FacePlace
        if (e_target.getHealth() <= l_FacePlaceHealth)
            l_MinDmg = 1f;

        /// AntiSuicide
        if (noSuicide.getValBoolean()) {
            while (mc.player.getHealth() + mc.player.getAbsorptionAmount() < l_MaxSelfDmg)
                l_MaxSelfDmg /= 2;
        }

        BlockPos l_BestPosition = null;
        float l_DMG = 0.0f;

        /// todo: use this, but we will lose dmg... maybe new option, for LeastDMGToSelf? but seems useless
        float l_SelfDMG = 0.0f;

        for (BlockPos l_Pos : l_AvailableBlockPositions) {
            if (e_target.getDistanceSq(l_Pos) >= 169.0D)
                continue;

            float l_TempDMG = CrystalUtils.calculateDamage(mc.world, l_Pos.getX() + 0.5, l_Pos.getY() + 1.0, l_Pos.getZ() + 0.5, e_target, 0);

            if (l_TempDMG < l_MinDmg)
                continue;

            float l_SelfTempDMG = CrystalUtils.calculateDamage(mc.world, l_Pos.getX() + 0.5, l_Pos.getY() + 1.0, l_Pos.getZ() + 0.5, mc.player, 0);

            if (l_SelfTempDMG > l_MaxSelfDmg)
                continue;

            if (wallsRange.getValDouble() > 0) {
                if (!PlayerUtil.CanSeeBlock(l_Pos))
                    if (l_Pos.getDistance((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ) > wallsRange.getValDouble())
                        continue;
            }

            if (l_TempDMG > l_DMG) {
                l_DMG = l_TempDMG;
                l_SelfDMG = l_SelfTempDMG;
                l_BestPosition = l_Pos;
            }
        }

        if (l_BestPosition == null)
            return BlockPos.ORIGIN;

        /*for (Hole l_Hole : Holes.GetHoles())
        {
            float l_HoleFillDmg = CrystalUtils.calculateDamage(mc.world, l_Hole.getX() + 0.5, l_Hole.getY() + 1.0, l_Hole.getZ() + 0.5, l_Player, 0);

            if (l_HoleFillDmg > l_DMG)
            {
                m_HoleToFill = l_Hole;
                l_DMG = l_HoleFillDmg;
            }
        }

        if (m_HoleToFill != null)
        {
            SalHack.INSTANCE.logChat("Filling the hole at " + m_HoleToFill.toString() + " will deal " + l_DMG);
           // return;
        }*/

        int l_PrevSlot = -1;


        if (!ghostHand.getValBoolean()) {
            if(swapCrystal.getValBoolean()) {
                if (SwitchHandToItemIfNeed(Items.END_CRYSTAL)) {
                    return BlockPos.ORIGIN;
                }
            }
        } else {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && swapCrystal.getValBoolean()) {
                for (int l_I = 0; l_I < 9; ++l_I) {
                    ItemStack l_Stack = mc.player.inventory.getStackInSlot(l_I);

                    if (l_Stack == ItemStack.EMPTY)
                        continue;

                    if (l_Stack.getItem() == Items.END_CRYSTAL) {
                        l_PrevSlot = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = l_I;
                        mc.playerController.updateController();
                    }
                }
            }
        }

        final double l_Pos[] =  EntityUtil.calculateLookAt(
                l_BestPosition.getX() + 0.5,
                l_BestPosition.getY() - 0.5,
                l_BestPosition.getZ() + 0.5,
                mc.player);

        if (mode.getValString().equalsIgnoreCase("ClientTick"))
        {
            aimBot.rotationSpoof = new RotationSpoof((float)l_Pos[0], (float)l_Pos[1]);

            Random rand = new Random(2);

            aimBot.rotationSpoof.yaw += (rand.nextFloat() / 100);
            aimBot.rotationSpoof.pitch += (rand.nextFloat() / 100);
        }

        RayTraceResult l_Result = mc.world.rayTraceBlocks(
                new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
                new Vec3d(l_BestPosition.getX() + 0.5, l_BestPosition.getY() - 0.5,
                        l_BestPosition.getZ() + 0.5));

        EnumFacing l_Facing;

        if (l_Result == null || l_Result.sideHit == null)
            l_Facing = EnumFacing.UP;
        else
            l_Facing = l_Result.sideHit;

        if (mode.getValString().equalsIgnoreCase("MotionTick") && p_Event != null) ///< p_Event should not null
        {
            p_Event.cancel();

            SpoofRotationsTo((float)l_Pos[0], (float)l_Pos[1]);
        }


        if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(l_BestPosition, l_Facing,
                    mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
        }
        // mc.playerController.processRightClickBlock(mc.player, mc.world, l_BestPosition, EnumFacing.UP, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
        // SalHack.INSTANCE.logChat(String.format("%s%s DMG and SelfDMG %s %s %S", ChatFormatting.LIGHT_PURPLE, l_DMG, l_SelfDMG, l_Facing, m_Target.getName()));

        placedCrystal.add(l_BestPosition);

        if (l_PrevSlot != -1 && ghostHand.getValBoolean())
        {
            mc.player.inventory.currentItem = l_PrevSlot;
            mc.playerController.updateController();
        }

        return l_BestPosition;
    }

    private boolean SwitchHandToItemIfNeed(Item p_Item)
    {
        if (mc.player.getHeldItemMainhand().getItem() == p_Item || mc.player.getHeldItemOffhand().getItem() == p_Item)
            return false;

        for (int l_I = 0; l_I < 9; ++l_I)
        {
            ItemStack l_Stack = mc.player.inventory.getStackInSlot(l_I);

            if (l_Stack == ItemStack.EMPTY)
                continue;

            if (l_Stack.getItem() == p_Item)
            {
                mc.player.inventory.currentItem = l_I;
                mc.playerController.updateController();
                return true;
            }
        }

        return true;
    }
}
