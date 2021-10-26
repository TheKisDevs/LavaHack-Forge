package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockInteractionHelper;
import com.kisman.cc.util.PlayerUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Scaffold extends Module {
    private Setting mode = new Setting("Mode", this, Modes.Tower);
    private Setting stopMotion = new Setting("StopMotion", this, true);
    private Setting delay = new Setting("Delay", this, 0, 0, 1, false);

    private TimerUtils timer = new TimerUtils();
    private TimerUtils towerPauseTimer = new TimerUtils();
    private TimerUtils towerTimer = new TimerUtils();

    public Scaffold() {
        super("Scaffold", "Scaffold", Category.MOVEMENT);

        setmgr.rSetting(mode);
        setmgr.rSetting(stopMotion);
        setmgr.rSetting(delay);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if (event.isCancelled())
            return;

        if (event.getEra() != Event.Era.PRE)
            return;

        if (!timer.passedMillis((long) (delay.getValDouble() * 1000)))
            return;

        // verify we have a block in our hand
        ItemStack stack = mc.player.getHeldItemMainhand();

        int prevSlot = -1;

        if (!verifyStack(stack)) {
            for (int i = 0; i < 9; ++i) {
                stack = mc.player.inventory.getStackInSlot(i);

                if (verifyStack(stack)) {
                    prevSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = i;
                    mc.playerController.updateController();
                    break;
                }
            }
        }

        if (!verifyStack(stack))
            return;

        timer.reset();

        BlockPos toPlaceAt = null;

        BlockPos feetBlock = PlayerUtil.GetLocalPlayerPosFloored().down();

        boolean placeAtFeet = isValidPlaceBlockState(feetBlock);

        // verify we are on tower mode, feet block is valid to be placed at, and
        if (mode.getValEnum() == Modes.Tower && placeAtFeet && mc.player.movementInput.jump && towerTimer.passedMillis(250) && !mc.player.isElytraFlying()) {
            // todo: this can be moved to only do it on an SPacketPlayerPosLook?
            if (towerPauseTimer.passedMillis(1500)) {
                towerPauseTimer.reset();
                mc.player.motionY = -0.28f;
            } else {
                final float towerMotion = 0.41999998688f;

                mc.player.setVelocity(0, towerMotion, 0);
            }
        }

        if (placeAtFeet)
            toPlaceAt = feetBlock;
        else {// find a supporting position for feet block
            BlockInteractionHelper.ValidResult result = BlockInteractionHelper.valid(feetBlock);

            // find a supporting block
            if (result != BlockInteractionHelper.ValidResult.Ok && result != BlockInteractionHelper.ValidResult.AlreadyBlockThere) {
                BlockPos[] array = { feetBlock.north(), feetBlock.south(), feetBlock.east(), feetBlock.west() };

                BlockPos toSelect = null;
                double lastDistance = 420.0;

                for (BlockPos pos : array) {
                    if (!isValidPlaceBlockState(pos))
                        continue;

                    double dist = pos.getDistance((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
                    if (lastDistance > dist) {
                        lastDistance = dist;
                        toSelect = pos;
                    }
                }

                // if we found a position, that's our selection
                if (toSelect != null)
                    toPlaceAt = toSelect;
            }

        }

        if (toPlaceAt != null) {
            // PositionRotation
            // CPacketPlayerTryUseItemOnBlock
            // CPacketAnimation

            final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);

            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbor = toPlaceAt.offset(side);
                final EnumFacing side2 = side.getOpposite();

                if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                    final Vec3d hitVec = new Vec3d((Vec3i) neighbor).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                    if (eyesPos.distanceTo(hitVec) <= 5.0f) {
                        float[] rotations = BlockInteractionHelper.getFacingRotations(toPlaceAt.getX(), toPlaceAt.getY(), toPlaceAt.getZ(), side);

                        event.cancel();
                        PlayerUtil.packetFacePitchAndYaw(rotations[1], rotations[0]);
                        break;
                    }
                }
            }

            if (BlockInteractionHelper.place(toPlaceAt, 5.0f, false, false, true) == BlockInteractionHelper.PlaceResult.Placed) {
                // swinging is already in the place function.
            }
        } else
            towerPauseTimer.reset();

        // set back our previous slot
        if (prevSlot != -1) {
            mc.player.inventory.currentItem = prevSlot;
            mc.playerController.updateController();
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook) {
            towerTimer.reset();
        }
    });

    @EventHandler
    private final Listener<EventPlayerMove> listener2 = new Listener<>(event -> {
        if (!stopMotion.getValBoolean())
            return;

        double x = event.x;
        double y = event.y;
        double z = event.z;

        if (mc.player.onGround && !mc.player.noClip) {
            double increment;
            for (increment = 0.05D; x != 0.0D && isOffsetBBEmpty(x, -1.0f, 0.0D);) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
            }
            for (; z != 0.0D && isOffsetBBEmpty(0.0D, -1.0f, z);) {
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
            for (; x != 0.0D && z != 0.0D && isOffsetBBEmpty(x, -1.0f, z);) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }

                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
        }

        event.x = x;
        event.y = y;
        event.z = z;
        event.cancel();
    });

    private boolean isOffsetBBEmpty(double x, double y, double z)
    {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x, y, z)).isEmpty();
    }

    private boolean isValidPlaceBlockState(BlockPos pos)
    {
        BlockInteractionHelper.ValidResult result = BlockInteractionHelper.valid(pos);

        if (result == BlockInteractionHelper.ValidResult.AlreadyBlockThere)
            return mc.world.getBlockState(pos).getMaterial().isReplaceable();

        return result == BlockInteractionHelper.ValidResult.Ok;
    }

    private boolean verifyStack(ItemStack stack)
    {
        return !stack.isEmpty() && stack.getItem() instanceof ItemBlock;
    }

    public enum Modes {
        Tower,
        Normal
    }
}
