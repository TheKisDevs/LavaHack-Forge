package com.kisman.cc.features.module.movement.fly;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.event.events.EventPlayerUpdate;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.module.movement.fly.packetfly.AutoPacketFly;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.movement.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Cubic
 * @since 22.09.2022
 * Inspired by PacketFly of Konas
 */
@ModuleInfo(
        name = "PacketFly",
        display = "Packet",
        submodule = true,
        modules = {
                AutoPacketFly.class
        }
)
public class PacketFly extends Module {
    @ModuleInstance public static PacketFly instance;

    private final SettingEnum<Type> type = new SettingEnum<>("Type", this, Type.Fast).register();
    private final SettingEnum<Mode> mode = new SettingEnum<>("Mode", this, Mode.Up).register();
    private final Setting jitterBoundsVertical = register(new Setting("JitterBoundsVertical", this, 22, 0, 50, true).setVisible(mode.getValEnum() == Mode.LimitJitter));
    private final Setting jitterBoundsHorizontal = register(new Setting("JitterBoundsHorizontal", this, 10, 0, 50, true).setVisible(mode.getValEnum() == Mode.LimitJitter));
    private final SettingEnum<Limit> limit = new SettingEnum<>("Limit", this, Limit.None).register();
    private final SettingEnum<Phase> phase = new SettingEnum<>("Phase", this, Phase.None).register();
    private final SettingEnum<Logic> logic = new SettingEnum<>("Logic", this, Logic.PositionBounds).register();
    private final Setting multiAxis = register(new Setting("MultiAxis", this, false));
    private final Setting noPhaseSlow = register(new Setting("NoPhaseSlow", this, false));
    private final Setting speed = register(new Setting("Speed", this, 1, 1, 10, false));
    public final Setting factor = register(new Setting("Factor", this, 1, 1, 10, false).setVisible(() -> type.getValEnum() == Type.Factor || type.getValEnum() == Type.Desync));
    private final SettingEnum<AntiKick> antiKickMode = new SettingEnum<>("AntiKick", this, AntiKick.Normal).register();
    private final Setting strict = register(new Setting("Strict", this, false));
    private final Setting bounds = register(new Setting("Bounds", this, true));
    private final Setting confirm = register(new Setting("Confirm", this, false));
    private final Setting constrict = register(new Setting("Constrict", this, false));
    private final Setting conceal = register(new Setting("Conceal", this, false));
    private final Setting forceConceal = register(new Setting("ForConceal", this, false).setVisible(conceal::getValBoolean));
    private final Setting settingConcealTicks = register(new Setting("ConcealTicks", this, 10, 1, 60, true).setVisible(conceal::getValBoolean));
    private final Setting jitter = register(new Setting("Jitter", this, false));
    private final Setting settingJitterTicks = register(new Setting("JitterTicks", this, 7, 2, 15, true).setVisible(jitter::getValBoolean));
    private final Setting clearTicks = register(new Setting("ClearTicks", this, 20, 2, 50, true));
    private final Setting timerSpeed = register(new Setting("TimerSpeed", this, 1.0, 1.0, 1.4, false));
    private final Setting shrinkBB = register(new Setting("ShrinkBoundingBox", this, false));
    private final Setting facrotize = register(new Setting("Facrotize", this, 0).setVisible(() -> type.getValEnum() == Type.Factor));
    private final Setting motion = register(new Setting("Distance", this, 5, 1, 20, false).setVisible(() -> type.getValEnum() == Type.Factor));

    private static final Random random = new Random();

    private static final double CONCEAL = Double.longBitsToDouble(0x3fE3fffffffffffDL);

    private int teleportID;

    private CPacketPlayer.Position startingOutOfBoundsPos;

    private final List<CPacketPlayer> packets = new ArrayList<>();
    private final Map<Integer, Bind<Vec3d, Long>> posLooks = new ConcurrentHashMap<>();

    private int antiKickTicks;
    private int vDelay = 0;
    private int hDelay = 0;

    private boolean strictLimit = false;
    private int limitTicks = 0;
    private int jitterTicks = 0;

    private boolean jitterSwitch = false;

    private int concealTicks = 0;

    private double speedX = 0;
    private double speedY = 0;
    private double speedZ = 0;

    private int factorCounter = 0;

    private final TimerUtils intervalTimer = new TimerUtils();

    @Override
    public void onEnable(){
        super.onEnable();

        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }

        if(mc.isSingleplayer()){
            ChatUtility.warning().printClientModuleMessage("You are in Singleplayer: toggling off...");
            toggle();
            return;
        }

        packets.clear();
        posLooks.clear();
        teleportID = 0;
        antiKickTicks = 0;
        vDelay = 0;
        hDelay = 0;
        strictLimit = false;
        limitTicks = 0;
        jitterTicks = 0;
        jitterSwitch = false;
        concealTicks = 0;
        speedX = 0;
        speedY = 0;
        speedZ = 0;

        startingOutOfBoundsPos = new CPacketPlayer.Position(randHorizontal(), 1, randHorizontal(), mc.player.onGround);
        packets.add(startingOutOfBoundsPos);
        mc.player.connection.sendPacket(startingOutOfBoundsPos);

        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.timer.tickLength = 50.0f;

        if(mc.player != null){
            mc.player.motionX = 0;
            mc.player.motionY = 0;
            mc.player.motionZ = 0;
            mc.player.noClip = false;
        }

        Kisman.EVENT_BUS.unsubscribe(this);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }

        mc.timer.tickLength = 50.0f / timerSpeed.getValFloat();
    }

    private void clearLooks(){
        posLooks.forEach((tp, bind) -> {
            if(System.currentTimeMillis() - bind.getSecond() > TimeUnit.SECONDS.toMillis(30L))
                posLooks.remove(tp);
        });
    }

    private void preSendPackets(double x, double y, double z, boolean sendCT){
        // unfinished
        /*
        Vec3d curPos = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
        Vec3d nextPos = new Vec3d(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
        List<BlockPos> curBlocks = getPlayerBlocks(curPos);
        List<BlockPos> nextBlocks = getPlayerBlocks(nextPos);
        if(curBlocks.equals(nextBlocks))
            return;
         */
    }

    private List<BlockPos> getPlayerBlocks(Vec3d vec3d){
        return Stream.of(
                new BlockPos(vec3d.x + 0.3, vec3d.y, vec3d.z + 0.3),
                new BlockPos(vec3d.x + 0.3, vec3d.y, vec3d.z - 0.3),
                new BlockPos(vec3d.x - 0.3, vec3d.y, vec3d.z + 0.3),
                new BlockPos(vec3d.x - 0.3, vec3d.y, vec3d.z - 0.3)
        ).distinct().collect(Collectors.toList());
    }

    private void sendPackets(double x, double y, double z, boolean sendCT){
        Vec3d nextPos = new Vec3d(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
        Vec3d bounds = boundVec(x, y, z);

        boolean doLimit = limit.getValEnum() != Limit.None && limitTicks == 0;

        if(logic.getValEnum() == Logic.PositionBounds){
            sendPositionPacket(nextPos);
            if(doLimit)
                return;
            sendBoundsPacket(bounds);
            if(sendCT)
                sendConfirmTeleport(nextPos);
            return;
        }

        if(!doLimit)
            sendBoundsPacket(bounds);
        sendPositionPacket(nextPos);
        if(!doLimit && sendCT)
            sendConfirmTeleport(nextPos);
    }

    private void sendPositionPacket(Vec3d nextPos){
        CPacketPlayer nextPosPacket = new CPacketPlayer.Position(nextPos.x, nextPos.y, nextPos.z, mc.player.onGround);
        packets.add(nextPosPacket);
        mc.player.connection.sendPacket(nextPosPacket);
    }

    private void sendBoundsPacket(Vec3d bounds){
        CPacketPlayer boundsPacket = new CPacketPlayer.Position(bounds.x, bounds.y, bounds.z, mc.player.onGround);
        packets.add(boundsPacket);
        mc.player.connection.sendPacket(boundsPacket);
    }

    private void sendConfirmTeleport(Vec3d nextPos){
        teleportID++;

        if (confirm.getValBoolean()) {
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportID - 1));
        }

        mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportID));

        posLooks.put(teleportID, new Bind<>(new Vec3d(nextPos.x, nextPos.y, nextPos.z), System.currentTimeMillis()));

        if (confirm.getValBoolean()) {
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportID + 1));
        }
    }

    private Vec3d boundVec(double x, double y, double z){
        switch(mode.getValEnum()){
            case Up:
                return new Vec3d(mc.player.posX + x, bounds.getValBoolean() ? (strict.getValBoolean() ? 255 : 256) : mc.player.posY + 420, mc.player.posZ + z);
            case Preserve:
                return new Vec3d(bounds.getValBoolean() ? mc.player.posX + randHorizontal() : randHorizontal(), strict.getValBoolean() ? (Math.max(mc.player.posY, 2D)) : mc.player.posY, bounds.getValBoolean() ? mc.player.posZ + randHorizontal() : randHorizontal());
            case LimitJitter:
                return new Vec3d(mc.player.posX + (strict.getValBoolean() ? x : jitterHorizontal()), mc.player.posY + jitterVertical(), mc.player.posZ + (strict.getValBoolean() ? z : jitterHorizontal()));
            case Bypass:
                if(bounds.getValBoolean()){
                    double rawY = y * 510;
                    return new Vec3d(mc.player.posX + x, mc.player.posY + ((rawY > ((mc.player.dimension == -1) ? 127 : 255)) ? -rawY : (rawY < 1) ? -rawY : rawY), mc.player.posZ + z);
                } else {
                    return new Vec3d(mc.player.posX + (x == 0D ? (random.nextBoolean() ? -10 : 10) : x * 38), mc.player.posY + y, mc.player.posX + (z == 0D ? (random.nextBoolean() ? -10 : 10) : z * 38));
                }
            case Obscure:
                return new Vec3d(mc.player.posX + randHorizontal(), Math.max(1.5D, Math.min(mc.player.posY + y, 253.5D)), mc.player.posZ + randHorizontal());
            default:
                return new Vec3d(mc.player.posX + x, bounds.getValBoolean() ? (strict.getValBoolean() ? 1 : 0) : mc.player.posY - 1337, mc.player.posZ + z);
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> playerUpdateListener = new Listener<>(event -> {
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }

        if(mc.player.ticksExisted % clearTicks.getValInt() == 0)
            clearLooks();

        setDisplayInfo("[" + type.getValEnum().name() + "]");

        mc.player.setVelocity(0, 0, 0);

        if(teleportID <= 0 && type.getValEnum() != Type.Setback){
            startingOutOfBoundsPos = new CPacketPlayer.Position(randHorizontal(), 1, randHorizontal(), mc.player.onGround);
            packets.add(startingOutOfBoundsPos);
            mc.player.connection.sendPacket(startingOutOfBoundsPos);
            return;
        }

        boolean phasing = checkCollisionBoundingBox();

        speedX = 0;
        speedY = 0;
        speedZ = 0;

        if (mc.gameSettings.keyBindJump.isKeyDown() && (hDelay < 1 || (multiAxis.getValBoolean() && phasing))) {
            if (mc.player.ticksExisted % (type.getValEnum() == Type.Setback || type.getValEnum() == Type.Slow || limit.getValEnum() == Limit.Strict ? 10 : 20) == 0) {
                speedY = (antiKickMode.getValEnum() != AntiKick.None) ? -0.032 : 0.062;
            } else {
                speedY = 0.062;
            }
            antiKickTicks = 0;
            vDelay = 5;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown() && (hDelay < 1 || (multiAxis.getValBoolean() && phasing))) {
            speedY = -0.062;
            antiKickTicks = 0;
            vDelay = 5;
        }
        boolean updateConcealTicks = false;
        double[] dir = directionSpeed((phasing && phase.getValEnum() == Phase.NCP ? (noPhaseSlow.getValBoolean() ? (multiAxis.getValBoolean() ? 0.0465 : 0.062) : 0.031) : 0.26) * speed.getValDouble());
        if ((multiAxis.getValBoolean() && phasing) || !(mc.gameSettings.keyBindSneak.isKeyDown() && mc.gameSettings.keyBindJump.isKeyDown())) {
            if (MovementUtil.isMoving()) {
                if ((dir[0] != 0 || dir[1] != 0) && (vDelay < 1 || (multiAxis.getValBoolean() && phasing))) {
                    if(conceal.getValBoolean() && concealTicks == settingConcealTicks.getValInt()){
                        dir = getConceal(dir);
                        updateConcealTicks = true;
                    }
                    speedX = dir[0];
                    speedZ = dir[1];
                    hDelay = 5;
                }
            }
            if (antiKickMode.getValEnum() != AntiKick.None && (limit.getValEnum() == Limit.None || limitTicks != 0)) {
                if (antiKickTicks < (mode.getValEnum() == Mode.Bypass && !bounds.getValBoolean() ? 1 : 3)) {
                    antiKickTicks++;
                } else {
                    antiKickTicks = 0;
                    if (antiKickMode.getValEnum() != AntiKick.Limited || !phasing) {
                        speedY = antiKickMode.getValEnum() == AntiKick.Strict ? -0.08 : -0.04;
                    }
                }
            }
        }

        if (phasing) {
            if (phase.getValEnum() == Phase.NCP && (double) mc.player.moveForward != 0.0 || (double) mc.player.moveStrafing != 0.0 && speedY != 0) {
                speedY /= 2.5;
            }
        }

        if (limit.getValEnum() != Limit.None) {
            if (limitTicks == 0) {
                speedX = 0;
                speedY = 0;
                speedZ = 0;
            } else if (limitTicks == 2 && jitter.getValBoolean()) {
                if (jitterSwitch) {
                    speedX = 0;
                    speedY = 0;
                    speedZ = 0;
                }
                jitterSwitch = !jitterSwitch;
            }
        } else if (jitter.getValBoolean() && jitterTicks == settingJitterTicks.getValInt()) {
            speedX = 0;
            speedY = 0;
            speedZ = 0;
        }

        if(conceal.getValBoolean() && forceConceal.getValBoolean() && concealTicks == settingConcealTicks.getValInt()){
            dir = getConceal(dir);
            speedX = dir[0];
            speedZ = dir[1];
        }

        switch (type.getValEnum()) {
            case Fast:
                preSendPackets(speedX, speedY, speedZ, true);
                mc.player.setVelocity(speedX, speedY, speedZ);
                sendPackets(speedX, speedY, speedZ, true);
                break;
            case Slow:
                preSendPackets(speedX, speedY, speedZ, true);
                sendPackets(speedX, speedY, speedZ, true);
                break;
            case Setback:
                preSendPackets(speedX, speedY, speedZ, false);
                mc.player.setVelocity(speedX, speedY, speedZ);
                sendPackets(speedX, speedY, speedZ, false);
                break;
            case Factor:
            case Desync:
                float rawFactor = factor.getValFloat();
                if (Keyboard.isKeyDown(facrotize.getKey()) && intervalTimer.passedMillis(3500)) {
                    intervalTimer.reset();
                    rawFactor = motion.getValFloat();
                }
                int factorInt = (int) Math.floor(rawFactor);
                factorCounter++;
                if (factorCounter > (int) (20D / ((rawFactor - (double) factorInt) * 20D))) {
                    factorInt += 1;
                    factorCounter = 0;
                }
                for (int i = 1; i <= factorInt; ++i) {
                    preSendPackets(speedX * i, speedY * i, speedZ * i, true);
                    mc.player.setVelocity(speedX * i, speedY * i, speedZ * i);
                    sendPackets(speedX * i, speedY * i, speedZ * i, true);
                }
                speedX = mc.player.motionX;
                speedY = mc.player.motionY;
                speedZ = mc.player.motionZ;
                break;
        }

        vDelay--;
        hDelay--;

        if(conceal.getValBoolean() && forceConceal.getValBoolean() && concealTicks == settingConcealTicks.getValInt()){
            speedX = dir[0];
            speedZ = dir[1];
            updateConcealTicks = true;
        }

        if (constrict.getValBoolean() && (limit.getValEnum() == Limit.None || limitTicks > 1)) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
        }

        limitTicks++;
        jitterTicks++;

        if(updateConcealTicks || !conceal.getValBoolean()) concealTicks = 0;
        else concealTicks++;

        if (limitTicks > ((limit.getValEnum() == Limit.Strict) ? (strictLimit ? 1 : 2) : 3)) {
            limitTicks = 0;
            strictLimit = !strictLimit;
        }

        if (jitterTicks > settingJitterTicks.getValInt()) {
            jitterTicks = 0;
        }
    });

    private double[] getConceal(double[] speeds){
        double delta = Math.max(speeds[0], speeds[1]) / CONCEAL;
        return new double[]{speeds[0] / delta, speeds[1] / delta};
    }

    private double randHorizontal(){
        int rand = random.nextInt(bounds.getValBoolean() ? 80 : (mode.getValEnum() == Mode.Obscure ? (mc.player.ticksExisted % 2 == 0 ? 480 : 100) : 29000000)) + (bounds.getValBoolean() ? 5 : 500);
        return random.nextBoolean() ? rand : -rand;
    }

    private double jitterVertical(){
        int rand = 70 + random.nextInt(jitterBoundsVertical.getValInt());
        return random.nextBoolean() ? rand : -rand;
    }

    private double jitterHorizontal(){
        int rand = random.nextInt(jitterBoundsHorizontal.getValInt());
        return random.nextBoolean() ? rand : -rand;
    }

    private boolean checkCollisionBoundingBox(){
        double offset = shrinkBB.getValBoolean() ? -0.0625 : 0;
        if(!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(offset, offset, offset)).isEmpty())
            return true;
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, 2, 0).contract(0, 1.99, 0)).isEmpty();
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
        if(!(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        if(mc.currentScreen instanceof GuiDownloadTerrain){
            teleportID = 0;
            return;
        }
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
        ifStatement: {
            if(!mc.player.isEntityAlive())
                break ifStatement;
            if(teleportID <= 0){
                this.teleportID = packet.getTeleportId();
                break ifStatement;
            }
            if(!mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) || type.getValEnum() == Type.Setback)
                break ifStatement;
            if(type.getValEnum() == Type.Desync){
                posLooks.remove(packet.getTeleportId());
                event.cancel();
                if(type.getValEnum() == Type.Slow)
                    mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
                return;
            } else if(posLooks.containsKey(packet.getTeleportId())){
                Bind<Vec3d, Long> vec = posLooks.get(packet.getTeleportId());
                if(vec.getFirst().x == packet.getX() && vec.getFirst().y == packet.getY() && vec.getFirst().z == packet.getZ()){
                    posLooks.remove(packet.getTeleportId());
                    event.cancel();
                    if(type.getValEnum() == Type.Slow)
                        mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
                    return;
                }
            }
        }
        packet.yaw = mc.player.rotationYaw;
        packet.pitch = mc.player.rotationPitch;
        packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
        packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
        teleportID = packet.getTeleportId();
    });

    @EventHandler
    private final Listener<EventPlayerMove> playerMoveListener = new Listener<>(event -> {
        if(type.getValEnum() != Type.Setback && teleportID <= 0)
            return;

        if(type.getValEnum() != Type.Slow){
            event.x = speedX;
            event.y = speedY;
            event.z = speedZ;
        }

        if(phase.getValEnum() != Phase.None && phase.getValEnum() == Phase.Vanilla || checkCollisionBoundingBox())
            mc.player.noClip = true;
    });

    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
            event.cancel();
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            if (this.packets.contains(packet)) {
                this.packets.remove(packet);
                return;
            }
            event.cancel();
        }
    });

    @SubscribeEvent
    public void onPlayerPushOutBlock(PlayerSPPushOutOfBlocksEvent event){
        event.setCanceled(true);
    }

    private static double[] directionSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    private enum Type {
        Fast,
        Slow,
        Setback,
        Factor,
        Desync
    }

    private enum Mode {
        Up,
        Down,
        LimitJitter,
        Preserve,
        Bypass,
        Obscure
    }

    private enum Limit {
        None,
        Strict,
        Ticks
    }

    private enum Phase {
        None,
        Vanilla,
        NCP,
        //Bypass // not finished
    }

    private enum AntiKick {
        None,
        Normal,
        Limited,
        Strict
    }

    private enum Logic {
        PositionBounds,
        BoundsPosition
    }
}
