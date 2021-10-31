package com.kisman.cc.module.combat;

import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.InventoryUtil;
import com.kisman.cc.util.RotationUtils;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public class AntiTrap extends Module {
    private Setting delay = new Setting("Delay", this, 400, 0, 1000, true);
    private Setting switchMode = new Setting("SwitchMode", this, SwitchModes.None);
    private Setting rotate = new Setting("Rotate", this, Rotate.NONE);
    private Setting sortY = new Setting("SortY", this, true);

    private TimerUtils timer = new TimerUtils();

    private Set<BlockPos> placedPos = new HashSet<>();
    private final Vec3d[] surroundTargets = new Vec3d[] { new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 1.0, 1.0) };

    private int lastHotbarSlot = -1;
    private boolean switchedItem;
    private boolean offhand = false;

    public AntiTrap() {
        super("AntiTrap", "", Category.COMBAT);

        setmgr.rSetting(delay);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(rotate);
        setmgr.rSetting(sortY);
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if(event.getEra() == Event.Era.PRE) {
            doAntiTrap();
        }
    });

    private void doAntiTrap() {

    }

    private void placeCrystal(final BlockPos pos) {
        final boolean bl;
        final boolean mainhand = bl = (AntiTrap.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        if (!mainhand && !this.offhand && !this.switchItem(false)) {
            super.onDisable();
            return;
        }
        final RayTraceResult result = AntiTrap.mc.world.rayTraceBlocks(new Vec3d(AntiTrap.mc.player.posX, AntiTrap.mc.player.posY + AntiTrap.mc.player.getEyeHeight(), AntiTrap.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        final float[] angle = AngleUtil.calculateAngle(AntiTrap.mc.player.getPositionEyes(AntiTrap.mc.getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() - 0.5f), (double)(pos.getZ() + 0.5f)));
        switch ((Rotate) this.rotate.getValEnum()) {
            case NORMAL: {
                RotationUtils.setPlayerRotations(angle[0], angle[1]);
                break;
            }
            case PACKET: {
                AntiTrap.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], (float) MathHelper.normalizeAngle((int)angle[1], 360), AntiTrap.mc.player.onGround));
                break;
            }
        }
        placedPos.add(pos);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        this.timer.reset();
    }

    private boolean switchItem(final boolean back) {
        if (this.offhand) {
            return true;
        }
        InventoryUtil.switchToSlot(InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9), this.switchMode.getValEnum().equals(SwitchModes.Silent));
        this.switchedItem = true;
        return true;
    }

    public enum Rotate {
        NONE,
        NORMAL,
        PACKET
    }

    public enum SwitchModes {
        None,
        Normal,
        Silent
    }
}
