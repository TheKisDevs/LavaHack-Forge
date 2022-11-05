package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.world.CrystalUtils;
import com.kisman.cc.util.world.HoleUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.Predicate;

public class HoleSnap extends Module {

    private final SettingEnum<Holes> holes = new SettingEnum<>("Holes", this, Holes.Both).register();
    private final SettingEnum<HoleTypes> holeType = new SettingEnum<>("HoleType", this, HoleTypes.Single).register();

    private final Setting holeRange = register(new Setting("HoleRange", this, 4, 1, 15, false));
    private final Setting holeYRange = register(new Setting("HoleYRange", this, 4, 1, 8, false));

    private final Setting timeout = register(new Setting("Timeout", this, true));
    private final Setting timeoutTicks = register(new Setting("TimeoutTicks", this, 20, 0, 100, true));
    private final Setting stuckTicks = register(new Setting("StuckTicks", this, 5, 0, 40, true));

    private final Setting speed = register(new Setting("Speed", this, 0.2873, 0.05, 0.5, false));
    private final Setting useTimer = register(new Setting("Timer", this, false));
    private final Setting timerSpeed = register(new Setting("TimerSpeed", this, 1.088,  1, 5, false));

    private final Setting render = register(new Setting("Render", this, true));
    private final Setting lineWidth = register(new Setting("LineWidth", this, 1.5, 0.5, 4, false));
    private final Setting color = register(new Setting("Color", this, new Colour(255, 255, 255, 255)));

    public HoleSnap(){
        super("HoleSnap", Category.MOVEMENT, true);
    }

    private int ticks = 0;
    private int colissionTicks = 0;
    private AxisAlignedBB hole = null;

    private float oldTickLength;

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        oldTickLength = mc.timer.tickLength;
        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(this);
        mc.timer.tickLength = oldTickLength;
        ticks = 0;
        colissionTicks = 0;
        hole = null;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        if(hole == null)
            return;

        if(!render.getValBoolean())
            return;

        Vec3d center = getCenter(hole);

        Rendering.setup();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glLineWidth(lineWidth.getValFloat());
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        GL11.glVertex3d(mc.player.posX - mc.renderManager.viewerPosX, mc.player.posY - mc.renderManager.viewerPosY, mc.player.posZ - mc.renderManager.viewerPosZ);
        GL11.glVertex3d(center.x - mc.renderManager.viewerPosX, center.y - mc.renderManager.viewerPosY, center.z - mc.renderManager.viewerPosZ);
        GL11.glEnd();

        Rendering.release();
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook)
            toggle();
    });

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event){
        if(!(event.getMovementInput() instanceof MovementInputFromOptions))
            return;
        if(hole == null)
            return;
        MovementInput input = event.getMovementInput();
        input.moveForward = 0.0f;
        input.moveStrafe = 0.0f;
        input.forwardKeyDown = false;
        input.backKeyDown = false;
        input.leftKeyDown = false;
        input.rightKeyDown = false;
    }

    @EventHandler
    private final Listener<EventPlayerMove> moveListener = new Listener<>(event -> {
        if(event.type != MoverType.SELF)
            return;

        if(hole == null)
            hole = findHole();

        if(hole == null)
            ticks++;
        else
            ticks = 0;

        if(hole == null && timeout.getValBoolean() && ticks > timeoutTicks.getValInt()){
            toggle();
            return;
        }

        if(hole == null)
            return;

        double currentSpeed = Math.hypot(mc.player.motionX, mc.player.motionZ);

        if(colissionTicks > stuckTicks.getValInt()){
            toggle();
            return;
        }

        Vec3d center = getCenter(hole);

        if(contains(hole, mc.player.getEntityBoundingBox().expand(0, -0.05, 0))){
            mc.player.connection.sendPacket(new CPacketPlayer.Position(center.x, mc.player.posY, center.z, mc.player.onGround));
            mc.player.setPosition(center.x, mc.player.posY, center.z);
            toggle();
            return;
        }

        event.cancel();

        if(useTimer.getValBoolean())
            mc.timer.tickLength = 50.0f / timerSpeed.getValFloat();

        Vec3d playerPos = mc.player.getPositionVector();
        double yaw = Math.toRadians(AngleUtil.calculateAngle(playerPos, center)[0]);
        double d = Math.hypot(center.x - playerPos.x, center.z - playerPos.z);
        double baseSpeed = EntityUtil.applySpeedEffect(mc.player, this.speed.getValDouble());
        double speed = mc.player.onGround ? baseSpeed : Math.max(currentSpeed + 0.02, baseSpeed);
        double cappedSpeed = Math.min(speed, d);

        mc.player.motionX = 0;
        mc.player.motionZ = 0;
        event.x = -Math.sin(yaw) * cappedSpeed;
        event.z = Math.cos(yaw) * cappedSpeed;

        if(mc.player.collidedHorizontally)
            colissionTicks++;
        else
            colissionTicks = 0;
    });

    @SuppressWarnings("ALL")
    private boolean contains(AxisAlignedBB aabb1, AxisAlignedBB aabb2){
        boolean result = false;
        result |= aabb1.contains(new Vec3d(aabb2.minX, aabb2.minY, aabb2.minZ));
        result |= aabb1.contains(new Vec3d(aabb2.minX, aabb2.minY, aabb2.maxZ));
        result |= aabb1.contains(new Vec3d(aabb2.maxX, aabb2.minY, aabb2.minZ));
        result |= aabb1.contains(new Vec3d(aabb2.maxX, aabb2.minY, aabb2.maxZ));
        return result;
    }

    private Vec3d getCenter(AxisAlignedBB aabb){
        double x = aabb.maxX - aabb.minX;
        double z = aabb.maxZ - aabb.minZ;
        return new Vec3d(aabb.minX + (x / 2.0), aabb.minY, aabb.minZ + (z / 2.0));
    }

    private AxisAlignedBB findHole(){
        Set<BlockPos> possibleHoles = getPossibleHoles();
        List<AxisAlignedBB> holes = new ArrayList<>();
        for(BlockPos pos : possibleHoles){
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType == HoleUtil.HoleType.NONE)
                continue;
            if(!this.holes.getValEnum().check(safety))
                continue;
            if(!this.holeType.getValEnum().check(holeType))
                continue;
            holes.add(holeInfo.getCentre());
        }
        return holes.stream()
                .filter(aabb -> aabb.minY < mc.player.posY)
                .filter(aabb -> mc.player.posX - aabb.minY <= holeYRange.getValDouble())
                .min(Comparator.comparingDouble(aabb -> mc.player.getDistance(getCenter(aabb).x, getCenter(aabb).y, getCenter(aabb).z)))
                .orElse(null);
    }

    private Set<BlockPos> getPossibleHoles(){
        Set<BlockPos> possibleHoles = new HashSet<>();
        List<BlockPos> blockPosList = CrystalUtils.getSphere(holeRange.getValFloat(), true, false);
        for (BlockPos pos : blockPosList) {
            AxisAlignedBB aabb = new AxisAlignedBB(pos);
            if(!mc.world.getEntitiesWithinAABB(Entity.class, aabb).isEmpty())
                continue;
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                possibleHoles.add(pos);
        }
        return possibleHoles;
    }

    private enum Holes {
        Obsidian(safety -> safety != HoleUtil.BlockSafety.UNBREAKABLE),
        Bedrock(safety -> safety == HoleUtil.BlockSafety.UNBREAKABLE),
        Both(safety -> true);

        private final Predicate<HoleUtil.BlockSafety> check;

        Holes(Predicate<HoleUtil.BlockSafety> check) {
            this.check = check;
        }

        public boolean check(HoleUtil.BlockSafety safety){
            return check.test(safety);
        }
    }

    private enum HoleTypes {
        Single(type -> type == HoleUtil.HoleType.SINGLE),
        Double(type -> type == HoleUtil.HoleType.DOUBLE),
        Both(type -> type == HoleUtil.HoleType.SINGLE || type == HoleUtil.HoleType.DOUBLE);

        private final Predicate<HoleUtil.HoleType> check;

        HoleTypes(Predicate<HoleUtil.HoleType> check) {
            this.check = check;
        }

        public boolean check(HoleUtil.HoleType holeType) {
            return check.test(holeType);
        }
    }
}
