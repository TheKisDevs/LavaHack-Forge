package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.subsystem.subsystems.EnemyManager;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import com.kisman.cc.features.subsystem.subsystems.TargetsNearest;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.SlideRenderingRewritePattern;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.math.MathKt;
import com.kisman.cc.util.math.TrigonometryKt;
import com.kisman.cc.util.movement.BaritoneHandlerKt;
import com.kisman.cc.util.render.pattern.SlideRendererPattern;
import com.kisman.cc.util.world.Holes;
import com.kisman.cc.util.world.WorldUtilKt;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Targetable
@TargetsNearest
@ModuleInfo(
        name = "HoleSnap",
        category = Category.MOVEMENT
)
public class HoleSnap extends Module {
    @ModuleInstance
    public static HoleSnap instance;

    private final SettingEnum<Holes.Safety> holes = register(new SettingEnum<>("Holes", this, Holes.Safety.Mix));
    private final SettingEnum<HoleTypes> holeType = register(new SettingEnum<>("HoleType", this, HoleTypes.Single));

    private final Setting upperHoles = register(new Setting("UpperHoles", this, false));
    private final Setting balance = register(new Setting("Balance", this, 0.5, -4, 4, false));

    private final Setting holeRange = register(new Setting("HoleRange", this, 4, 1, 15, false));
    private final Setting holeYRange = register(new Setting("HoleYRange", this, 4, 1, 8, false));

    private final Setting nearestHoleToEnemy = register(new Setting("NearestHoleToEnemy", this, false));

    private final Setting timeout = register(new Setting("Timeout", this, true));
    private final Setting timeoutTicks = register(new Setting("TimeoutTicks", this, 20, 0, 100, true));

    private final SettingEnum<Stuck> onStuck = register(new SettingEnum<>("OnStuck", this, Stuck.None));
    private final Setting stuckTicks = register(new Setting("StuckTicks", this, 5, 0, 40, true));

    private final Setting speed = register(new Setting("Speed", this, 0.2873, 0.05, 0.5, false));

    private final Setting snap = register(new Setting("Snap", this, true));
    private final Setting snapBBScale = register(new Setting("SnapBBScale", this, 0.05, -0.5, 0.5, false));

    private final Setting useTimer = register(new Setting("Timer", this, false));
    private final Setting timerSpeed = register(new Setting("TimerSpeed", this, 1.088,  1, 5, false));

    private final Setting autoStep = register(new Setting("AutoStep", this, false));

    private final SlideRenderingRewritePattern pattern = new SlideRenderingRewritePattern(this).group(register(new SettingGroup(new Setting("Renderer", this)))).preInit().init();

    private final SlideRendererPattern renderer = new SlideRendererPattern();

    public HoleSnap() {
        super.setDisplayInfo(() -> target != null ? "[" + (target == mc.player ? "Self" : target.getName()) + "]" : "");
    }

    @Target
    public EntityPlayer target = null;

    private final Set<Holes.Hole> bannedHoles = new HashSet<>();

    private int ticks = 0;
    private int colissionTicks = 0;
    private Holes.Hole hole = null;

    private float oldTickLength;

    private float oldStepHeight;

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(packetListener);
        Kisman.EVENT_BUS.subscribe(moveListener);
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        oldTickLength = mc.timer.tickLength;
        oldStepHeight = mc.player.stepHeight;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(packetListener);
        Kisman.EVENT_BUS.unsubscribe(moveListener);
        mc.timer.tickLength = oldTickLength;
        mc.player.stepHeight = oldStepHeight;
        ticks = 0;
        colissionTicks = 0;
        hole = null;
        bannedHoles.clear();
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null || colissionTicks != 0 || !BaritoneHandlerKt.active() || onStuck.getValEnum() != Stuck.Baritone) return;

        BaritoneHandlerKt.stop();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        renderer.handleRenderWorld(pattern, hole == null ? null : hole.getHoleBlocks().get(0), null);
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
        if(event.type != MoverType.SELF) return;

        target = EnemyManager.INSTANCE.nearest();

        if(hole == null) hole = findHole();

        if(hole == null) {
            ticks++;
            mc.player.stepHeight = oldStepHeight;

            if(timeout.getValBoolean() && ticks > timeoutTicks.getValInt()) toggle();

            return;
        } else ticks = 0;

        double currentSpeed = MathKt.hypot(mc.player.motionX, mc.player.motionZ);

        if(onStuck.getValEnum() != Stuck.None && colissionTicks > stuckTicks.getValInt()){
            if(onStuck.getValEnum() == Stuck.Toggle) toggle();
            else if(onStuck.getValEnum() == Stuck.NextHole) bannedHoles.add(hole);
            else if(onStuck.getValEnum() == Stuck.Baritone) BaritoneHandlerKt.gotoPos(hole.getHoleBlocks().get(0));
        }

        Vec3d center = getCenter(hole.getAabb());

        if(snap.getValBoolean() && contains(hole.getAabb(), mc.player.getEntityBoundingBox().expand(0, snapBBScale.getValDouble(), 0))){
            mc.player.connection.sendPacket(new CPacketPlayer.Position(center.x, mc.player.posY, center.z, mc.player.onGround));
            mc.player.setPosition(center.x, mc.player.posY, center.z);
            toggle();
            return;
        }

        if(useTimer.getValBoolean()) mc.timer.tickLength = 50.0f / timerSpeed.getValFloat();

        Vec3d playerPos = mc.player.getPositionVector();
        double yaw = TrigonometryKt.toRadians(WorldUtilKt.rotation(center)[0]);
        double d = MathKt.hypot(center.x - playerPos.x, center.z - playerPos.z);

        if(!snap.getValBoolean() && d == 0){
            toggle();
            return;
        }

        if(autoStep.getValBoolean() || hole.getAabb().minY >= Math.floor(mc.player.posY))
            mc.player.stepHeight = 2f;

        double baseSpeed = EntityUtil.applySpeedEffect(mc.player, this.speed.getValDouble());
        double speed = mc.player.onGround ? baseSpeed : Math.max(currentSpeed + 0.02, baseSpeed);
        double cappedSpeed = Math.min(speed, d);

        mc.player.motionX = 0;
        mc.player.motionZ = 0;
        event.x = -Math.sin(yaw) * cappedSpeed;
        event.z = Math.cos(yaw) * cappedSpeed;
        event.cancel();

        if(mc.player.collidedHorizontally) colissionTicks++;
        else colissionTicks = 0;
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

    //Pretty bad implementation of my idea - _kisman_
    private Holes.Hole findHole(){
        EntityPlayer entity = (nearestHoleToEnemy.getValBoolean() && target != null) ? target : mc.player;
        ArrayList<Holes.Hole> list = new ArrayList<>();

        for(Holes.Hole hole : Holes.getHoles(entity, holeRange.getValDouble())) {
            Holes.Type type = hole.getType();
            Holes.Safety safety = hole.getSafety();

            if(safetyCheck(holes.getValEnum(), safety) && holeType.getValEnum().check(type)) list.add(hole);
        }

        return list.stream()
                .filter(hole -> hole.getAabb().minY < (upperHoles.getValBoolean() ? mc.player.posY + 2 : mc.player.posY))
                .filter(hole -> mc.player.posX - hole.getAabb().minY <= holeYRange.getValDouble())
                .filter(hole -> !bannedHoles.contains(hole))
                .min(Comparator.comparingDouble(hole -> entity.getDistance(getCenter(hole.getAabb()).x, getCenter(hole.getAabb()).y, getCenter(hole.getAabb()).z) + (hole.getAabb().minY >= Math.floor(mc.player.posY) ? balance.getValDouble() : 0)))
                .orElse(null);
    }

    private boolean safetyCheck(Holes.Safety safety1, Holes.Safety safety2) {
        return safety1 == Holes.Safety.Mix || safety1 == safety2;
    }

    private enum HoleTypes {
        Single(type -> type == Holes.Type.Single),
        Double(type -> type == Holes.Type.Double || type == Holes.Type.UnsafeDouble),
        Both(type -> Single.check(type) || Double.check(type));

        private final Predicate<Holes.Type> check;

        HoleTypes(Predicate<Holes.Type> check) {
            this.check = check;
        }

        public boolean check(Holes.Type holeType) {
            return check.test(holeType);
        }
    }

    private enum Stuck {
        Toggle,
        NextHole,
        Baritone,
        None
    }
}
