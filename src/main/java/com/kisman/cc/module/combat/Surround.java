package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.DestroyBlockEvent;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.event.events.subscribe.TotemPopEvent;
import com.kisman.cc.mixin.mixins.accessor.ICPacketPlayer;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.InventoryUtil.*;
import com.kisman.cc.util.RenderBuilder.*;
import com.kisman.cc.util.Rotation.*;
import com.kisman.cc.util.cosmos.CosmosRenderUtil;
import com.kisman.cc.util.cosmos.Raytrace;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.kisman.cc.module.combat.Surround.Center.*;

public class Surround extends Module {
    private Setting surroundVec = new Setting("SurroundVec", this, SurroundVectors.BASE);
    private Setting completion = new Setting("Completion", this, Completion.AIR);
    private Setting center = new Setting("Center", this, TELEPORT);
    private Setting autoSwitch = new Setting("Switch", this, Switch.NORMAL);
    private Setting hand = new Setting("Hand", this, PlayerUtil.Hand.MAINHAND);
    private Setting blocksPerTick = new Setting("BlocksPerTick", this, 4, 0, 10, true);
    private Setting raytrace = new Setting("RayTrace", this, false);
    private Setting packet = new Setting("Packet", this, false);
    private Setting confirm = new Setting("Confirm", this, false);
    private Setting reactive = new Setting("Reactive", this, true);
    private Setting chainPop = new Setting("ChainPop", this, false);

    private Setting rotate = new Setting("Rotate", this, Rotation.Rotate.NONE);
    private Setting rotateCenter = new Setting("RotateCenter", this, false);
    private Setting rotateRandom = new Setting("RotateRandom", this, false);

    private Setting render = new Setting("Render", this, true);
    private Setting renderMode = new Setting("RenderMode", this, Box.FILL);
    private Setting renderSafeColor = new Setting("SafeColor", this, "SafeColor", new float[] {0.08f, 1, 0, 1}, false);
    private Setting renderUnSafeColor = new Setting("UnSafeColor", this, "UnSafeColor", new float[] {120 / 355, 1, 1, 1}, false);

    int previousSlot = -1;
    int surroundPlaced = 0;
    BlockPos previousPosition = BlockPos.ORIGIN;
    BlockPos surroundPosition = BlockPos.ORIGIN;
    Rotation surroundRotation = new Rotation(Float.NaN, Float.NaN, (Rotate) rotate.getValEnum());

    public Surround() {
        super("Surround", "Surround", Category.COMBAT);

        setmgr.rSetting(surroundVec);
        setmgr.rSetting(completion);
        setmgr.rSetting(center);
        setmgr.rSetting(autoSwitch);
        setmgr.rSetting(hand);
        setmgr.rSetting(blocksPerTick);
        setmgr.rSetting(raytrace);
        setmgr.rSetting(packet);
        setmgr.rSetting(confirm);
        setmgr.rSetting(reactive);
        setmgr.rSetting(chainPop);

        setmgr.rSetting(rotate);
        setmgr.rSetting(rotateCenter);
        setmgr.rSetting(rotateRandom);

        setmgr.rSetting(render);
        setmgr.rSetting(renderMode);
        setmgr.rSetting(renderSafeColor);
        setmgr.rSetting(renderUnSafeColor);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);

        previousPosition = new BlockPos(new Vec3d(MathUtil.roundFloat(mc.player.getPositionVector().x, 0), MathUtil.roundFloat(mc.player.getPositionVector().y, 0), MathUtil.roundFloat(mc.player.getPositionVector().z, 0)));

        switch ((Center) center.getValEnum()) {
            case TELEPORT:
                double xPosition = mc.player.getPositionVector().x;
                double zPosition = mc.player.getPositionVector().z;

                if (Math.abs((previousPosition.getX() + 0.5) - mc.player.getPositionVector().x) >= 0.2) {
                    int xDirection = (previousPosition.getX() + 0.5) - mc.player.getPositionVector().x > 0 ? 1 : -1;
                    xPosition += 0.3 * xDirection;
                }

                if (Math.abs((previousPosition.getZ() + 0.5) - mc.player.getPositionVector().z) >= 0.2) {
                    int zDirection = (previousPosition.getZ() + 0.5) - mc.player.getPositionVector().z > 0 ? 1 : -1;
                    zPosition += 0.3 * zDirection;
                }

                TeleportUtil.teleportPlayer(xPosition, mc.player.posY, zPosition);
                break;
            case MOTION:
                mc.player.motionX = ((Math.floor(mc.player.posX) + 0.5) - mc.player.posX) / 2;
                mc.player.motionZ = ((Math.floor(mc.player.posZ) + 0.5) - mc.player.posZ) / 2;
                break;
            case NONE:
                break;
        }
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        surroundPlaced = 0;

        switch ((Completion) completion.getValEnum()) {
            case AIR:
                if (!previousPosition.equals(new BlockPos(new Vec3d(MathUtil.roundFloat(mc.player.getPositionVector().x, 0), MathUtil.roundFloat(mc.player.getPositionVector().y, 0), MathUtil.roundFloat(mc.player.getPositionVector().z, 0)))) || mc.player.posY > previousPosition.getY()) {
                    super.setToggled(false);
                    return;
                }

                break;
            case SURROUNDED:
                if (HoleUtil.isInHole(mc.player)) {
                    super.setToggled(false);
                    return;
                }

                break;
            case PERSISTENT:
                break;
        }

        handleSurround();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (render.getValBoolean()) {
            for (Vec3d surroundVectors : ((SurroundVectors) surroundVec.getValEnum()).vectors) {
                CosmosRenderUtil.drawBox(
                        new RenderBuilder().position(
                                new BlockPos(
                                        surroundVectors.add(
                                                new Vec3d(
                                                        mc.player.posX,
                                                        Math.round(mc.player.posY),
                                                        mc.player.posZ))))
                                .color(
                                        (Objects.equals(BlockUtil.getBlockResistance(
                                                new BlockPos(
                                                        surroundVectors.add(
                                                                new Vec3d(
                                                                        mc.player.posX,
                                                                        Math.round(mc.player.posY),
                                                                        mc.player.posZ)))),
                                                BlockUtil.BlockResistance.RESISTANT) ||
                                                Objects.equals(BlockUtil.getBlockResistance(
                                                        new BlockPos(
                                                                surroundVectors.add(
                                                                        new Vec3d(
                                                                                mc.player.posX,
                                                                                Math.round(mc.player.posY),
                                                                                mc.player.posZ)))),
                                                        BlockUtil.BlockResistance.UNBREAKABLE)) ?
                                                renderSafeColor.getColour().getColor() : renderUnSafeColor.getColour().getColor()).box((Box) renderMode.getValEnum()).setup().line(1.5F).cull(((Box) renderMode.getValEnum()).equals(Box.GLOW) || ((Box) renderMode.getValEnum()).equals(Box.REVERSE)).shade(((Box) renderMode.getValEnum()).equals(Box.GLOW) || ((Box) renderMode.getValEnum()).equals(Box.REVERSE)).alpha(((Box) renderMode.getValEnum()).equals(Box.GLOW) || ((Box) renderMode.getValEnum()).equals(Box.REVERSE)).depth(true).blend().texture());
            }
        }
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        if (!HoleUtil.isInHole(mc.player) && event.getPopEntity().equals(mc.player) && chainPop.getValBoolean()) {
            InventoryUtil.switchToSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), (Switch) autoSwitch.getValEnum());

            placeSurround();

            InventoryUtil.switchToSlot(previousSlot, Switch.NORMAL);
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayer && !Float.isNaN(surroundRotation.getYaw()) && !Float.isNaN(surroundRotation.getPitch())) {
            ((ICPacketPlayer) event.getPacket()).setYaw(surroundRotation.getYaw());
            ((ICPacketPlayer) event.getPacket()).setPitch(surroundRotation.getPitch());
        }
    });

/*    @EventHandler
    private final Listener<DestroyBlockEvent> listener1 = new Listener<>(event -> {
        if (HoleUtil.isPartOfHole(event.getBlockPos().down()) && reactive.getValBoolean()) {
            BlockUtil.placeBlock(event.getBlockPos().down(), packet.getValBoolean(), confirm.getValBoolean());
        }
    });*/

    public void handleSurround() {
        previousSlot = mc.player.inventory.currentItem;

        if (!HoleUtil.isInHole(mc.player)) {
            InventoryUtil.switchToSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), (Switch) autoSwitch.getValEnum());

            placeSurround();

            InventoryUtil.switchToSlot(previousSlot, Switch.NORMAL);
        }
    }


    public void placeSurround() {
        for (Vec3d surroundVectors : ((SurroundVectors) surroundVec.getValEnum()).vectors) {
            if (Objects.equals(BlockUtil.getBlockResistance(new BlockPos(surroundVectors.add(new Vec3d(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ)))), BlockUtil.BlockResistance.BLANK) && surroundPlaced <= blocksPerTick.getValDouble()) {
                surroundPosition = new BlockPos(surroundVectors.add(new Vec3d(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ)));

                if (RaytraceUtil.raytraceBlock(surroundPosition, Raytrace.NORMAL) && raytrace.getValBoolean())
                    return;

                if (surroundPosition != BlockPos.ORIGIN) {
                    if (!rotate.getValEnum().equals(Rotate.NONE)) {
                        float[] surroundAngles = rotateCenter.getValBoolean() ? AngleUtil.calculateCenter(surroundPosition) : AngleUtil.calculateAngles(surroundPosition);
                        surroundRotation = new Rotation((float) (surroundAngles[0] + (rotateRandom.getValBoolean() ? ThreadLocalRandom.current().nextDouble(-4, 4) : 0)), (float) (surroundAngles[1] + (rotateRandom.getValBoolean() ? ThreadLocalRandom.current().nextDouble(-4, 4) : 0)),(Rotate) rotate.getValEnum());

                        if (!Float.isNaN(surroundRotation.getYaw()) && !Float.isNaN(surroundRotation.getPitch()))
                            surroundRotation.updateModelRotations();
                    }
                }

                for (Entity item : mc.world.loadedEntityList) {
                    if (item instanceof EntityItem && ((EntityItem) item).getItem().getItem().equals(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                        item.setDead();
                        mc.world.removeEntityFromWorld(item.getEntityId());
                    }
                }

                BlockUtil.placeBlock(new BlockPos(surroundVectors.add(new Vec3d(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ))), packet.getValBoolean(), confirm.getValBoolean());
                PlayerUtil.swingArm((PlayerUtil.Hand) hand.getValEnum());
                surroundPlaced++;
            }
        }
    }

    public enum SurroundVectors {
        BASE(new ArrayList<>(Arrays.asList(new Vec3d(0, -1, 0), new Vec3d(1, -1, 0), new Vec3d(0, -1, 1), new Vec3d(-1, -1, 0), new Vec3d(0, -1, -1), new Vec3d(1, 0, 0), new Vec3d(0, 0, 1), new Vec3d(-1, 0, 0), new Vec3d(0, 0, -1)))),
        STANDARD(new ArrayList<>(Arrays.asList(new Vec3d(0, -1, 0), new Vec3d(1, 0, 0), new Vec3d(-1, 0, 0), new Vec3d(0, 0, 1), new Vec3d(0, 0, -1)))),
        PROTECT(new ArrayList<>(Arrays.asList(
                new Vec3d(0, -1, 0),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2),
                new Vec3d(3, 0, 0),
                new Vec3d(-3, 0, 0),
                new Vec3d(0, 0, 3),
                new Vec3d(0, 0, -3)
        ))),
        PROTECTplus(new ArrayList<>(Arrays.asList(
                new Vec3d(0, -1, 0),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2),
                new Vec3d(3, 0, 0),
                new Vec3d(-3, 0, 0),
                new Vec3d(0, 0, 3),
                new Vec3d(0, 0, -3)
        )));


        private final List<Vec3d> vectors;

        SurroundVectors(List<Vec3d> vectors) {
            this.vectors = vectors;
        }

        public List<Vec3d> getVectors() {
            return this.vectors;
        }
    }

    public enum Center {
        TELEPORT, MOTION, NONE
    }

    public enum Completion {
        AIR, SURROUNDED, PERSISTENT
    }
}
