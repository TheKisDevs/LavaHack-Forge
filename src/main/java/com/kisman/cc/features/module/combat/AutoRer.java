package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.PingBypassModule;
import com.kisman.cc.features.module.combat.autorer.*;
import com.kisman.cc.features.module.combat.autorer.render.AutoRerRenderer;
import com.kisman.cc.mixin.accessors.IEntityPlayer;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.DamageSyncPattern;
import com.kisman.cc.settings.util.MovableRendererPattern;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.collections.Bind;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.RotationSaver;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.AutoRerTargetFinderLogic;
import com.kisman.cc.util.enums.RotationLogic;
import com.kisman.cc.util.enums.dynamic.RotationEnum;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.thread.kisman.ThreadHandler;
import com.kisman.cc.util.world.CrystalUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author _kisman_(Logic, Renderer logic), Cubic(Renderer)
 */
@PingBypassModule
@SuppressWarnings({"ForLoopReplaceableByForEach", "ConstantConditions", "JavaDoc"})
public class AutoRer extends Module {
    private final SettingGroup main = register(new SettingGroup(new Setting("Main", this)));
    private final SettingGroup ranges = register(new SettingGroup(new Setting("Ranges", this)));
    private final SettingGroup calc = register(new SettingGroup(new Setting("Calc", this)));
    private final SettingGroup helpers = register(new SettingGroup(new Setting("Helpers", this)));
    private final SettingGroup place_ = register(new SettingGroup(new Setting("Place", this)));
    private final SettingGroup break__ = register(new SettingGroup(new Setting("Break", this)));
    private final SettingGroup delay = register(new SettingGroup(new Setting("Delay", this)));
    private final SettingGroup damage = register(new SettingGroup(new Setting("Damage", this)));
    private final SettingGroup thread_ = register(new SettingGroup(new Setting("Thread", this)));
    private final SettingGroup render_ = register(new SettingGroup(new Setting("Render", this)));
    private final SettingGroup optimization = register(new SettingGroup(new Setting("Optimization", this)));

    private final SettingGroup multiThreadGroup = register(optimization.add(new SettingGroup(new Setting("Multi Thread", this))));
    private final SettingGroup multiThreadGettersGroup = register(multiThreadGroup.add(new SettingGroup(new Setting("Getters", this))));
    public final Setting multiThreaddedSphereGetter = register(multiThreadGettersGroup.add(new Setting("MT Sphere Getter", this, false).setTitle("Sphere")));
    public final Setting multiThreaddedTargetGetter = register(multiThreadGettersGroup.add(new Setting("MT Target Getter", this, false).setTitle("Target")));
    private final Setting multiThreaddedCrystalGetter = register(multiThreadGettersGroup.add(new Setting("MT Crystal Getter", this, false).setTitle("Break Pos")));
    private final Setting multiThreaddedExtrapolation = register(multiThreadGroup.add(new Setting("MT Extrapolation", this, true).setTitle("Extrapolation")));
    private final Setting mtcgDelay = register(multiThreadGroup.add(new Setting("MT CG Delay", this, 15.0, 0.0, 100.0, NumberType.TIME).setTitle("Delay")));
    private final Setting wallRangeUsage = register(optimization.add(new Setting("Wall Range Usage", this, true)));

    public final Setting lagProtect = register(main.add(new Setting("Lag Protect", this, false)));
    public final Setting placeRange = register(ranges.add(new Setting("Place Range", this, 6, 0, 6, false).setTitle("Place")));
    private final Setting placeWallRange = register(ranges.add(new Setting("Place Wall Range", this, 4.5f, 0, 6, false).setTitle("Place Wall")));
    private final Setting breakRange = register(ranges.add(new Setting("Break Range", this, 6, 0, 6, false).setTitle("Break")));
    private final Setting breakWallRange = register(ranges.add(new Setting("Break Wall Range", this, 4.5f, 0, 6, false).setTitle("Break Wall")));
    public final Setting targetRange = register(ranges.add(new Setting("Target Range", this, 9, 0, 20, false).setTitle("Target")));
    private final Setting logic = register(main.add(new Setting("Logic", this, LogicMode.PlaceBreak)));
    public final SettingEnum<AutoRerTargetFinderLogic> targetLogic = new SettingEnum<>("Target Logic", this, AutoRerTargetFinderLogic.Distance).group(main).register();
    public final Setting terrain = register(main.add(new Setting("Terrain", this, false)));
    private final Setting switch_ = register(main.add(new Setting("Switch", this, SwitchMode.None)));
    private final Setting fastCalc = register(calc.add(new Setting("Fast Calc", this, true)));
    private final Setting cubicCalc = register(calc.add(new Setting("Cubic Calc", this, false)));
    private final Setting heuristics = register(calc.add(new Setting("Heuristic", this, Heuristics.Damage)));
    private final Setting safetyBalance = register(calc.add(new Setting("Safety Balance", this, 0, 0, 20, false).setVisible(heuristics.getValEnum() == Heuristics.Safety)));
    private final SettingGroup motionGroup = register(new SettingGroup(new Setting("Motion", this)));
    private final Setting motionCrystal = register(motionGroup.add(new Setting("Motion Crystal", this, false).setTitle("State")));
    private final Setting motionCalc = register(motionGroup.add(new Setting("Motion Calc", this, false).setVisible(motionCrystal::getValBoolean)).setTitle("Calc"));
    private final Setting timingMode = register(helpers.add(new Setting("Timings", this, TimingMode.Adaptive)));
    private final Setting advancedSequential = register(helpers.add(new Setting("Advanced Sequential", this, false).setTitle("Advaned Seq")));
    private final Setting swing = register(main.add(new Setting("Swing", this, SwingMode.PacketSwing)));
    private final Setting swingLogic = register(main.add(new Setting("Swing Logic", this, SwingLogic.Pre).setVisible(() -> swing.getValEnum() != SwingMode.None)));
    private final Setting instant = register(helpers.add(new Setting("Instant", this, true)));
    private final Setting instantCalc = register(helpers.add(new Setting("Instant Calc", this, true).setVisible(instant::getValBoolean)));
    private final Setting instantRotate = register(helpers.add(new Setting("Instant Rotate", this, true).setVisible(instant::getValBoolean)));
    private final Setting inhibit = register(helpers.add(new Setting("Inhibit", this, false)));
    private final Setting sound = register(helpers.add(new Setting("Sound", this, false)));
    public final Setting sync = register(helpers.add(new Setting("Sync", this, false)));
    private final Setting syncMode = register(helpers.add(new Setting("Sync Mode", this, SyncMode.None).setTitle("Sync")));
    private final Setting rotate = register(helpers.add(new Setting("Rotate", this, Rotate.Off)));
    private final SettingEnum<RotationEnum.Rotation> rotateMode = new SettingEnum<>("Rotate Mode", this, RotationEnum.Rotation.None).setVisible(() -> !rotate.checkValString("None")).group(helpers).register();
    private final Setting calcDistSort = register(helpers.add(new Setting("Calc Dist Sort", this, false)));
    private final DamageSyncPattern damageSync = new DamageSyncPattern(this).group(helpers).preInit().init();
    private final Setting damageSyncPlace = register(damageSync.getGroup_().add(new Setting("Damage Sync Place", this, DamageSyncMode.None).setTitle("Place")));
    private final Setting damageSyncBreak = register(damageSync.getGroup_().add(new Setting("Damage Sync Break", this, DamageSyncMode.None).setTitle("Break")));
    private final Setting damageSyncSelf = register(damageSync.getGroup_().add(new Setting("Damage Sync Self", this, false).setTitle("Self")));
    private final SettingGroup extrapolationGroup = register(helpers.add(new SettingGroup(new Setting("Extrapolation", this))));
    private final Setting extrapolationState = register(extrapolationGroup.add(new Setting("Extrapolation State", this, false).setTitle("State")));
    private final Setting extrapolationSelf = register(extrapolationGroup.add(new Setting("Extrapolation Self", this, false).setTitle("Self")));
    private final Setting extrapolationTicks = register(extrapolationGroup.add(new Setting("Extrapolation Ticks", this, 1.0, 1.0, 50.0, true).setTitle("Ticks")));
    private final Setting extrapolationOutOfBlocks = register(extrapolationGroup.add(new Setting("Extrapolation Out Of Blocks", this, false).setTitle("Out Of Blocks")));
    private final Setting extrapolationShrink = register(extrapolationGroup.add(new Setting("Extrapolation Shrink", this, false).setTitle("Shrink")));
    private final Setting extrapolationRender = register(extrapolationGroup.add(new Setting("Extrapolation Render", this, false).setTitle("Render")));

    private final Setting place = register(place_.add(new Setting("Place", this, true)));
    public final Setting secondCheck = register(place_.add(new Setting("Second Check", this, false).setVisible(place::getValBoolean)));
    public final Setting thirdCheck = register(place_.add(new Setting("Third Check", this, false).setVisible(place::getValBoolean)));
    private final Setting multiPlace = register(place_.add(new Setting("Multi Place", this, MultiPlaceMode.None).setTitle("Multi").setVisible(place::getValBoolean)));
    public final Setting firePlace = register(place_.add(new Setting("Fire Place", this, false).setTitle("Fire").setVisible(place::getValBoolean)));
    private final Setting packetPlace = register(place_.add(new Setting("Packet Place", this, true).setTitle("Packet").setVisible(place::getValBoolean)));
    private final SettingGroup facePlaceGroup = register(place_.add(new SettingGroup(new Setting("Face", this))));
    private final Setting facePlace = register(facePlaceGroup.add(new Setting("Face Place", this, FacePlaceMode.None).setTitle("Mode")));
    private final SettingGroup facePlaceTriggersGroup = register(facePlaceGroup.add(new SettingGroup(new Setting("Triggers", this))));
    private final SettingGroup facePlaceArmorBreakerGroup = register(facePlaceTriggersGroup.add(new SettingGroup(new Setting("Armor", this))));
    private final Setting armorBreakerState = register(facePlaceArmorBreakerGroup.add(new Setting("Armor Breaker State", this, false).setTitle("State")));
    public final Setting armorBreaker = register(facePlaceArmorBreakerGroup.add(new Setting("Armor Breaker", this, 100, 0, 100, NumberType.PERCENT).setTitle("Value")));
    private final SettingGroup facePlaceMinFacePlaceDamageGroup = register(facePlaceTriggersGroup.add(new SettingGroup(new Setting("Damage", this))));
    private final Setting minFacePlaceDamageState = register(facePlaceMinFacePlaceDamageGroup.add(new Setting("Min Damage State", this, false).setTitle("State")));
    private final Setting minFacePlaceDMG = register(facePlaceMinFacePlaceDamageGroup.add(new Setting("Min Face Place DMG", this, 7.0, 1.0, 37.0, true).setTitle("Min")));

    private final Setting break_ = register(break__.add(new Setting("Break", this, true)));
    private final Setting breakPriority = register(break__.add(new Setting("Break Priority", this, BreakPriority.Damage).setTitle("Priority").setVisible(break_::getValBoolean)));
    private final Setting friend_ = register(break__.add(new Setting("Friend", this, FriendMode.AntiTotemPop).setVisible(break_::getValBoolean)));
    private final Setting clientSide = register(break__.add(new Setting("Client Side", this, ClientSideMode.None).setVisible(break_::getValBoolean)));
    private final Setting manualBreaker = register(break__.add(new Setting("Manual Breaker", this, false).setTitle("Manual").setVisible(break_::getValBoolean)));
    private final Setting removeAfterAttack = register(break__.add(new Setting("Remove After Attack", this, false).setVisible(break_::getValBoolean)));
    private final Setting antiCevBreakerMode = register(break__.add(new Setting("Anti Cev Breaker", this, AntiCevBreakerMode.None).setTitle("Anti Cev Break").setVisible(break_::getValBoolean)));
    private final Setting packetBreak = register(break__.add(new Setting("Packet Break", this, false).setTitle("Packet").setVisible(break_::getValBoolean)));

    private final Setting delayMode = register(delay.add(new Setting("Delay Mode", this, DelayMode.Default).setTitle("Mode")));
    private final Setting pingSmartMultiplier = register(delay.add(new Setting("Ping Smart Multi", this, 0, 0, 3, false)));
    private final SettingGroup defaultDelayGroup = register(delay.add(new SettingGroup(new Setting("Default", this))));
    private final Setting placeDelay = register(defaultDelayGroup.add(new Setting("Place Delay", this, 0, 0, 2000, NumberType.TIME).setTitle("Place")));
    private final Setting breakDelay = register(defaultDelayGroup.add(new Setting("Break Delay", this, 0, 0, 2000, NumberType.TIME).setTitle("Break")));
    private final SettingGroup fromToDelayGroup = register(delay.add(new SettingGroup(new Setting("From To", this))));
    private final Setting fromPlaceToBreakDelay = register(fromToDelayGroup.add(new Setting("From Place To Break Delay", this, 50, 0, 2000, NumberType.TIME).setTitle("From P To B")));
    private final Setting fromBreakToPlaceDelay = register(fromToDelayGroup.add(new Setting("From Place To Break Delay", this, 50, 0, 2000, NumberType.TIME).setTitle("From B To P")));
    private final Setting calcDelay = register(delay.add(new Setting("Calc Delay", this, 0, 0, 20000, NumberType.TIME).setTitle("Calc")));
    private final Setting clearDelay = register(delay.add(new Setting("Clear Delay", this, 500, 0, 2000, NumberType.TIME).setTitle("Clear")));
    private final SettingGroup sequentialGroup = register(delay.add(new SettingGroup(new Setting("Sequential", this))));
    //    private final Setting sequentialPlaceDelay = register(sequentialGroup.add(new Setting("Sequential Place Delay", this, 0, 0, 20, true).setTitle("Place")));
    private final Setting sequentialBreakDelay = register(sequentialGroup.add(new Setting("Sequential Break Delay", this, 0, 0, 20, true).setTitle("Break")));
    private final Setting multiplication = register(delay.add(new Setting("Multiplication", this, 1, 1, 10, true).setTitle("Multi")));

    public final Setting minDMG = register(damage.add(new Setting("Min DMG", this, 6, 0, 37, true).setTitle("Min")));
    public final Setting maxSelfDMG = register(damage.add(new Setting("Max Self DMG", this, 18, 0, 37, true).setTitle("Max Self")));
    private final Setting maxFriendDMG = register(damage.add(new Setting("Max Friend DMG", this, 10, 0, 37, true).setTitle("Max Friend")));
    public final Setting lethalMult = register(damage.add(new Setting("Lethal Mult", this, 0, 0, 6, false)));
    private final Setting noSuicide = register(damage.add(new Setting("No Suicide", this, true)));

    public final Setting threadMode = register(thread_.add(new Setting("Thread Mode", this, ThreadMode.None).setTitle("Mode")));
    public final Setting threadDelay = register(thread_.add(new Setting("Thread Delay", this, 50, 1, 1000, NumberType.TIME).setTitle("Delay").setVisible(() -> !threadMode.checkValString(ThreadMode.None.name()))));
    public final Setting threadSyns = register(thread_.add(new Setting("Thread Syns", this, true).setTitle("Sync").setVisible(() -> !threadMode.checkValString(ThreadMode.None.name()))));
    public final Setting threadSynsValue = register(thread_.add(new Setting("Thread Syns Value", this, 1000, 1, 10000, NumberType.TIME).setTitle("Sync Delay").setVisible(() -> !threadMode.checkValString(ThreadMode.None.name()))));
    private final Setting threadPacketRots = register(thread_.add(new Setting("Thread Packet Rots", this, false).setTitle("Rotations").setVisible(() -> !threadMode.checkValString(ThreadMode.None.name()) && !rotate.checkValString(Rotate.Off.name()))));
    private final Setting threadSoundPlayer = register(thread_.add(new Setting("Thread Sound Player", this, 6, 0, 12, true).setTitle("Sound Player").setVisible(() -> threadMode.checkValString("Sound"))));
    private final Setting threadCalc = register(thread_.add(new Setting("Thread Calc", this, true).setTitle("Calc").setVisible(() -> !threadMode.checkValString("None"))));

    private final RenderingRewritePattern renderer_ = new RenderingRewritePattern(this).group(render_).preInit().init();
    private final MovableRendererPattern movable = new MovableRendererPattern(this).group(render_).preInit().init();

    private final Setting renderTest = register(render_.add(new Setting("Render Test", this, false)));

    private final Setting text = register(render_.add(new Setting("Text", this, true)));

    public static AutoRer instance;

    public final List<PlaceInfo> placedList = new ArrayList<>();
    private final TimerUtils placeTimer = new TimerUtils();
    private final TimerUtils breakTimer = new TimerUtils();
    private final TimerUtils fromPlaceToBreakTimer = new TimerUtils();
    private final TimerUtils fromBreakToPlaceTimer = new TimerUtils();
    private final TimerUtils calcTimer = new TimerUtils();
    private final TimerUtils clearTimer = new TimerUtils();
    private final TimerUtils predictTimer = new TimerUtils();
    private final TimerUtils manualTimer = new TimerUtils();
    private final TimerUtils syncTimer = new TimerUtils();
    private ScheduledExecutorService executor;
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
    public static EntityPlayer currentTarget;
    private Thread thread;
    public PlaceInfo placePos = new PlaceInfo(null, null, 0, 0, null, null, null), renderPos;
    private Entity lastHitEntity = null;
    public boolean rotating;
    private String lastThreadMode = threadMode.getValString();
    private boolean lastBroken = false;
    private BlockPos lastTargetPos = null;


    private final ExtrapolationHelper extrapolationHelper = new ExtrapolationHelper(mtcgDelay.getSupplierLong(), multiThreaddedExtrapolation.getSupplierBoolean(), extrapolationTicks.getSupplierInt(), extrapolationOutOfBlocks.getSupplierBoolean(), extrapolationShrink.getSupplierBoolean());
    private final AutoRerDamageSyncHandler damageSyncHandler = new AutoRerDamageSyncHandler(damageSync.getHandler(), () -> damageSyncPlace.getValEnum() != DamageSyncMode.None, () -> damageSyncBreak.getValEnum() != DamageSyncMode.None);

    private final ThreadHandler crystalTHandler = new ThreadHandler(mtcgDelay.getSupplierLong(), multiThreaddedCrystalGetter.getSupplierBoolean());

    private final AtomicReference<BreakInfo> atomicCrystal = new AtomicReference<>();

    private final AutoRerRenderer renderer = new AutoRerRenderer();

    public AutoRer() {
        super("AutoRer", Category.COMBAT);

        instance = this;
    }

    public void onEnable() {
        super.onEnable();
        AutoRerUtil.Companion.onEnable();
        reset();

        if(!threadMode.checkValString("None")) processMultiThreading();

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(motion);
    }

    private void reset() {
        renderer.reset();
        extrapolationHelper.reset();
        damageSyncHandler.reset();
        placedList.clear();
        placeTimer.reset();
        breakTimer.reset();
        fromPlaceToBreakTimer.reset();
        fromBreakToPlaceTimer.reset();
        clearTimer.reset();
        predictTimer.reset();
        manualTimer.reset();
        currentTarget = null;
        rotating = false;
        renderPos = null;
        lastBroken = true;
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(motion);

        if(thread != null) shouldInterrupt.set(false);
        if(executor != null) executor.shutdown();

        reset();
    }

    private void processMultiThreading() {
        if(threadMode.checkValString("While")) handleWhile();
        else if(!threadMode.checkValString("None")) handlePool(false);
    }

    private ScheduledExecutorService getExecutor() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoRer.getInstance(this), 0L, this.threadDelay.getValLong(), TimeUnit.MILLISECONDS);
        return service;
    }

    private void handleWhile() {
        if(thread == null || thread.isInterrupted() || thread.isAlive() || (syncTimer.passedMillis(threadSynsValue.getValLong()) &&threadSyns.getValBoolean())) {
            if(thread == null) thread = newThread();
            else if(syncTimer.passedMillis(threadSynsValue.getValLong()) && !shouldInterrupt.get() && threadSyns.getValBoolean()) {
                shouldInterrupt.set(true);
                syncTimer.reset();
                return;
            }
            if(thread != null && (thread.isInterrupted() || !thread.isAlive())) thread = newThread();
            if(thread != null && thread.getState().equals(Thread.State.NEW)) {
                try {thread.start();} catch (Exception ignored) {}
                syncTimer.reset();
            }
        }
    }

    private Thread newThread() {
        Thread thread = new Thread(RAutoRer.getInstance(this));
        thread.setName("AutoReR-Thread-" + new AtomicLong(0).getAndIncrement());
        return thread;
    }

    private void handlePool(boolean justDoIt) {
        if(justDoIt || executor == null || executor.isTerminated() || executor.isShutdown() || (syncTimer.passedMillis(threadSynsValue.getValLong()) && threadSyns.getValBoolean())) {
            if(executor != null) executor.shutdown();
            executor = getExecutor();
            syncTimer.reset();
        }
    }

    public void update() {
        if(mc.player == null || mc.world == null || mc.isGamePaused) return;

        if(clearTimer.passedMillis(clearDelay.getValLong())) {
            placedList.clear();
            clearTimer.reset();
            lastBroken = true;
        }

        AutoRerUtil.Companion.getTargetFinder().update();
        currentTarget = AutoRerUtil.Companion.getTargetFinder().getTarget();

        if(!lastThreadMode.equalsIgnoreCase(threadMode.getValString())) {
            if (this.executor != null) this.executor.shutdown();
            if (this.thread != null) this.shouldInterrupt.set(true);
            lastThreadMode = threadMode.getValString();
        }

        if(currentTarget == null) {
            placePos.setBlockPos(null);
            return;
        } else super.setDisplayInfo("[" + currentTarget.getName() + "]");

        if(extrapolationState.getValBoolean()) extrapolationHelper.update();

        calc: {
            if (fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
                if (threadCalc.getValBoolean() && !threadMode.checkValString("None")) break calc;
                doCalculatePlace();
                if (placePos.getBlockPos() != null) if (!getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.OBSIDIAN) && !getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.BEDROCK)) placePos.setBlockPos(null);
                calcTimer.reset();
            }
        }

        if(threadMode.checkValString("None")) {
            if (motionCrystal.getValBoolean()) return;
            else if (motionCalc.getValBoolean() && fastCalc.getValBoolean()) return;
            if (manualBreaker.getValBoolean()) manualBreaker();
            if (multiplication.getValInt() == 1) doAutoRerLogic(null, false);
            else for (int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(null, false);
        } else processMultiThreading();
    }

    private IBlockState getBlockState(BlockPos pos) {
        try {
            return mc.world.getBlockState(pos);
        } catch(Exception e) {
            return Blocks.AIR.getDefaultState();
        }
    }

    /**
     * fromPlaceToBreakTimer like breakTimer
     *
     * fromBreakToPlaceTimer like placeTimer
     *
     * @param break_
     * @return current timer
     */
    private TimerUtils getTimer(boolean break_) {
        return break_ ? (delayMode.getValEnum() == DelayMode.Default ? breakTimer : fromPlaceToBreakTimer) : (delayMode.getValEnum() == DelayMode.Default ? placeTimer : fromBreakToPlaceTimer);
    }

    /**
     *
     * @param break_
     * @return current delay
     */
    private int getDelay(boolean break_) {
        return pingSmartMultiplier.getValInt() == 0 ? getNonSmartDelay(break_) : (int) (UtilityKt.getPing() * pingSmartMultiplier.getValDouble());
    }

    /**
     * fromPlaceToBreakTimer like breakTimer
     *
     * fromBreakToPlaceTimer like placeTimer
     *
     * @param break_
     * @return current non smart delay
     */
    private int getNonSmartDelay(boolean break_) {
        return delayMode.getValEnum() == DelayMode.Default ? (break_ ? breakDelay.getValInt() : placeDelay.getValInt()) : (break_ ? fromPlaceToBreakDelay.getValInt() : fromBreakToPlaceDelay.getValInt());
    }

    public synchronized void doAutoRerForThread() {
        if(mc.player == null || mc.world == null) return;
        if(manualBreaker.getValBoolean()) manualBreaker();
        if(fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
            doCalculatePlace();
            calcTimer.reset();
        }

        if(multiplication.getValInt() == 1) doAutoRerLogic(null, true);
        else for(int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(null, true);
    }

    private void manualBreaker() {
        RayTraceResult result = mc.objectMouseOver;
        if(manualTimer.passedMillis(200) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.BOW && mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && result != null) {
            if(result.typeOfHit.equals(RayTraceResult.Type.ENTITY) && result.entityHit instanceof EntityEnderCrystal) {
                mc.player.connection.sendPacket(new CPacketUseEntity(result.entityHit));
                manualTimer.reset();
            } else if(result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                for (Entity target : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY() + 1.0, mc.objectMouseOver.getBlockPos().getZ())))) {
                    if(!(target instanceof EntityEnderCrystal)) continue;
                    mc.player.connection.sendPacket(new CPacketUseEntity(target));
                    manualTimer.reset();
                }
            }
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> motion = new Listener<>(event -> {
        if(!motionCrystal.getValBoolean() || currentTarget == null) return;
        if(motionCalc.getValBoolean() && fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
            doCalculatePlace();
            calcTimer.reset();
        }
        if(multiplication.getValInt() == 1) doAutoRerLogic(event, false);
        else for(int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(event, false);
    });

    private void doAutoRerLogic(EventPlayerMotionUpdate event, boolean thread) {
        try {
            if (logic.checkValString("PlaceBreak")) {
                doPlace(event, thread);
                doBreak();
            } else {
                doBreak();
                doPlace(event, thread);
            }
        } catch(NullPointerException ignored) {}
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(currentTarget != null && extrapolationState.getValBoolean() && extrapolationRender.getValBoolean() && ((IEntityPlayer) currentTarget).getPredictor() != null) {
            renderer_.draw(((IEntityPlayer) currentTarget).getPredictor().getEntityBoundingBox());
        }

        if(placePos.getBlockPos() != null) renderer.onRenderWorld(
                movable.movingLength.getValFloat(),
                movable.fadeLength.getValFloat(),
                renderer_,
                placePos,
                text.getValBoolean(),
                renderTest.getValBoolean()
        );
    }

    private void attackCrystalPredict(int entityID) {
        boolean flag = instantRotate.getValBoolean() && !motionCrystal.getValBoolean() && (rotate.checkValString("Break") || rotate.checkValString("All"));
        RotationSaver saver = new RotationSaver().save();
        if(flag) rotateToEntity(entityID);
        CPacketUseEntity packet = new CPacketUseEntity();
        packet.entityId = entityID;
        packet.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(packet);
        breakTimer.reset();
        predictTimer.reset();
        if(flag) loadSaver(saver);
    }

    private void rotateToEntity(int entityID) {
        rotateMode.getValEnum().getTaskR().doTask(rotateMode.getValEnum().getTaskCEntity().doTask(entityID, RotationLogic.Default), false);
    }

    private void loadSaver(RotationSaver saver) {
        rotateMode.getValEnum().getTaskRFromSaver().doTask(saver, true);
    }

    private BlockPos doInstant(int entityID, BlockPos pos) {
        if (inhibit.getValBoolean()) try {lastHitEntity = mc.world.getEntityByID(entityID);} catch (Exception ignored) {}
        if(swingLogic.getValEnum() == SwingLogic.Pre) swing();
        attackCrystalPredict(entityID);
        if(swingLogic.getValEnum() == SwingLogic.Post) swing();
        lastBroken = true;
        if(syncMode.getValEnum() == SyncMode.Merge) getTimer(false).reset();
        return pos;
    }

    private float calculateDamage(
            double x,
            double y,
            double z,
            EntityPlayer entity
    ) {
        AxisAlignedBB bb = extrapolationState.getValBoolean() && (entity != mc.player || extrapolationSelf.getValBoolean()) ? extrapolationHelper.predictor(entity).getEntityBoundingBox() : entity.getEntityBoundingBox();

        return CrystalUtils.calculateDamage(
                mc.world,
                x,
                y,
                z,
                entity,
                bb,
                0,
                terrain.getValBoolean()
        );
    }

    private float calculateDamage(
            BlockPos pos,
            EntityPlayer entity
    ) {
        return calculateDamage(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                entity
        );
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSpawnObject && instant.getValBoolean()) {
            SPacketSpawnObject packet =  (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                if(!(mc.world.getEntityByID(packet.getEntityID()) instanceof EntityEnderCrystal)) return;
                BlockPos toRemove = null;
                for (PlaceInfo placeInfo : placedList) {
                    BlockPos pos = placeInfo.getBlockPos();
                    boolean canSee = EntityUtil.canSee(pos);
                    if (mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) >= (canSee ? breakRange.getValDouble() : breakWallRange.getValDouble())) break;

                    if(instantCalc.getValBoolean() && currentTarget != null) {
                        float targetDamage = CrystalUtils.calculateDamage(pos, currentTarget, terrain.getValBoolean());
                        if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                            float selfDamage = CrystalUtils.calculateDamage(pos, mc.player, terrain.getValBoolean());
                            if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) toRemove = doInstant(packet.getEntityID(), pos);
                        }
                    } else toRemove = doInstant(packet.getEntityID(), pos);

                    break;
                }
                if (toRemove != null) placedList.remove(PlaceInfo.Companion.getElementFromListByPos(placedList, toRemove));
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && ((inhibit.getValBoolean() && lastHitEntity != null) || (sound.getValBoolean()))) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) if (lastHitEntity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) lastHitEntity.setDead();
            if(threadMode.checkValString(ThreadMode.Sound.name()) && isRightThread() && mc.player != null && mc.player.getDistanceSq(new BlockPos(packet.getX(), packet.getY(), packet.getZ())) < MathUtil.square(threadSoundPlayer.getValInt())) handlePool(true);
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) try {
            PlaceInfo info = AutoRerUtil.Companion.getPlaceInfo(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos(), currentTarget, terrain.getValBoolean());
            placedList.add(info);
        } catch (Exception ignored) {}
        if(removeAfterAttack.getValBoolean() && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if(packet.getAction().equals(CPacketUseEntity.Action.ATTACK) && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).setDead();
                try {mc.world.removeEntityFromWorld(packet.entityId);} catch(Exception ignored) {}
            }
        }
    });

    private boolean isRightThread() {
        return mc.isCallingFromMinecraftThread() || (!this.threadOngoing.get());
    }

    private void doCalculatePlace() {
        try {
            if(cubicCalc.getValBoolean()) {
                placePos = AutoRerUtil.Companion.getPlacePos(
                        placeRange.getValFloat(),
                        placeWallRange.getValFloat(),
                        currentTarget,
                        needToMultiPlace(),
                        firePlace.getValBoolean(),
                        secondCheck.getValBoolean(),
                        thirdCheck.getValBoolean(),
                        minDMG.getValInt(),
                        maxSelfDMG.getValInt(),
                        lethalMult.getValFloat(),
                        terrain.getValBoolean(),
                        wallRangeUsage.getValBoolean(),
                        noSuicide.getValBoolean()
                );
            } else calculatePlace();
            if(placePos.getBlockPos() == null && Crystals.INSTANCE.getState()) placePos.setBlockPos(Crystals.INSTANCE.getPos());
            else Crystals.INSTANCE.setState(false);
        } catch (Exception e) {if(lagProtect.getValBoolean())  super.setToggled(false);}
    }

    public boolean needToMultiPlace() {
        return multiPlace.getValEnum() != MultiPlaceMode.None && (multiPlace.getValEnum() == MultiPlaceMode.Stupid || isTargetMoving());
    }

    public boolean needToFacePlace() {
        return facePlace.getValEnum() != FacePlaceMode.None && (facePlace.getValEnum() == FacePlaceMode.Stupid || isTargetMoving());
    }

    private boolean isTargetMoving() {
        return currentTarget != null && !lastTargetPos.equals(currentTarget.getPosition());
    }

    public boolean facePlaceDamageCheck(float currentDamage) {
        return minFacePlaceDamageState.getValBoolean() && currentDamage <= minFacePlaceDMG.getValInt();
    }

    public boolean facePlaceArmorBreakerCheck() {
        return armorBreakerState.getValBoolean() && InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt());
    }

    private void calculatePlace() {
        double maxDamage = 0.5;
        double selfDamage_ = 0;
        BlockPos placePos = null;
        List<BlockPos> sphere = CrystalUtils.getSphere(placeRange.getValFloat(), true, false);

        if(calcDistSort.getValBoolean()) {
            Collections.sort(sphere);
            Collections.reverse(sphere);
        }

        for(int size = sphere.size(), i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);

            if(thirdCheck.getValBoolean() && !isPosValid(pos)) continue;
            if(CrystalUtils.canPlaceCrystal(pos, secondCheck.getValBoolean(), true, needToMultiPlace(), firePlace.getValBoolean())) {
                float targetDamage = calculateDamage(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget);

                Bind<Boolean, Float> targetResult = damageSyncHandler.canPlace(targetDamage, currentTarget);

                if(damageSyncPlace.getValEnum() == DamageSyncMode.Smart) targetDamage = targetResult.getSecond();
                if(targetResult.getFirst() && ((needToFacePlace() && (facePlaceDamageCheck(targetDamage) || facePlaceArmorBreakerCheck())) || targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount())) {
                    float selfDamage = calculateDamage(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player);

                    Bind<Boolean, Float> selfResult = damageSyncHandler.canPlace(targetDamage, currentTarget);

                    if(damageSyncPlace.getValEnum() == DamageSyncMode.Smart && damageSyncSelf.getValBoolean()) selfDamage = selfResult.getSecond();
                    if(selfResult.getFirst() && selfDamage <= maxSelfDMG.getValInt() && (selfDamage + 2 < mc.player.getHealth() + mc.player.getAbsorptionAmount() || !noSuicide.getValBoolean()) && selfDamage < targetDamage) {
                        targetDamage = getHeuristic(targetDamage, selfDamage);
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            selfDamage_ = selfDamage;
                            placePos = pos;
                        }
                    }
                }
            }
        }
        this.placePos = new PlaceInfo(currentTarget, placePos, (float) selfDamage_, (float) maxDamage, null, null, null);
    }

    private float getHeuristic(float targetDamage, float selfDamage){
        switch((Heuristics) heuristics.getValEnum()){
            case Damage:
                return targetDamage;
            case MinMax:
                return targetDamage - selfDamage;
            case Safety:
                return targetDamage - safetyBalance.getValFloat();
        }
        return targetDamage;
    }

    private boolean placeStrictSync() {
        return syncMode.getValEnum() == SyncMode.StrictFull && !lastBroken;
    }

    public boolean isPosValid(BlockPos pos) {
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= (EntityUtil.canSee(pos) ?  placeRange.getValDouble() : placeWallRange.getValDouble());
    }

    private void doPlace(EventPlayerMotionUpdate event, boolean thread) {
        if(!place.getValBoolean() || !getTimer(false).passedMillis(getDelay(false)) || (placePos.getBlockPos() == null && fastCalc.getValBoolean() || placeStrictSync())) return;
        if(!fastCalc.getValBoolean() || (thread && threadCalc.getValBoolean())) doCalculatePlace();
        if(placePos.getBlockPos() == null || (!getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.OBSIDIAN) && !getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.BEDROCK)) || (sync.getValBoolean() && placedList.contains(placePos)) || !damageSyncHandler.canPlace(placePos.getTargetDamage(), currentTarget).getFirst()) return;

        EnumHand hand = null;
        boolean offhand = mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL);
        int oldSlot = mc.player.inventory.currentItem;
        int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);

        if(crystalSlot == -1 && !offhand) return;

        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !offhand) {
            switch (switch_.getValString()) {
                case "None": return;
                case "Normal":
                    InventoryUtil.switchToSlot(crystalSlot, false);
                    break;
                case "Silent":
                    InventoryUtil.switchToSlot(crystalSlot, true);
                    break;
            }
        }

        if(mc.player == null) return;
        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;
        if(mc.player.isHandActive()) hand = mc.player.getActiveHand();

        RotationSaver saver = new RotationSaver().save();

        if(rotate.checkValString("Place") || rotate.checkValString("All")) {
            try {
                float[] rots = rotateMode.getValEnum().getTaskCBlock().doTask(placePos.getBlockPos());//RotationUtils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((placePos.getBlockPos().getX() + 0.5f), (placePos.getBlockPos().getY() - 0.5f), (placePos.getBlockPos().getZ() + 0.5f)));
                if (!thread) {
                    if (!motionCrystal.getValBoolean()) {
                        rotateMode.getValEnum().getTaskR().doTask(rots, false);
                    } else if (event != null) {
                        event.setYaw(rots[0]);
                        event.setPitch(rots[1]);
                    }
                } else if (threadPacketRots.getValBoolean()) mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
            } catch (Exception ignored) {}
        }

        RayTraceResult result = null;
        try {
            result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + ( double ) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(( double ) placePos.getBlockPos().getX() + 0.5, ( double ) placePos.getBlockPos().getY() - 0.5, ( double ) placePos.getBlockPos().getZ() + 0.5));
        } catch(Exception ignored) {}
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        if(placePos.getBlockPos() != null && mc.player.connection != null) {
            if(swingLogic.getValEnum() == SwingLogic.Pre) swing();
            if(packetPlace.getValBoolean() && mc.player.connection != null) mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos.getBlockPos(), facing, getPlaceHand(offhand), 0, 0, 0));
            else mc.playerController.processRightClickBlock(mc.player, mc.world, placePos.getBlockPos(), facing, new Vec3d(0, 0, 0), getPlaceHand(offhand));
            if(swingLogic.getValEnum() == SwingLogic.Post) swing();
        }
        getTimer(false).reset();

        renderPos = placePos;

        if((rotate.checkValString("Place") || rotate.checkValString("All"))) loadSaver(saver);
        if(hand != null) mc.player.setActiveHand(hand);
        if(oldSlot != -1 && switch_.checkValString(SwitchMode.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);

        lastBroken = false;
    }

    private EnumHand getPlaceHand(boolean offhand) {
        return offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    private BreakInfo getCrystalForAntiCevBreaker() {
        Entity crystal = null;

        if(!antiCevBreakerMode.checkValString("None")) {
            if(antiCevBreakerMode.checkValString("Cev") || antiCevBreakerMode.checkValString("Both")) {
                for(Vec3i vec : AntiCevBreakerVectors.Cev.vectors) {
                    BlockPos pos = mc.player.getPosition().add(vec);
                    for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                        if(entity instanceof EntityEnderCrystal) {
                            crystal = entity;
                        }
                    }
                }
            }
            if(antiCevBreakerMode.checkValString("Civ") || antiCevBreakerMode.checkValString("Both")) {
                for(Vec3i vec : AntiCevBreakerVectors.Civ.vectors) {
                    BlockPos pos = mc.player.getPosition().add(vec);
                    for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                        if(entity instanceof EntityEnderCrystal) {
                            crystal = entity;
                        }
                    }
                }
            }
        }

        return crystal != null ? new BreakInfo((EntityEnderCrystal) crystal, 0, 0, true) : null;
    }

    private BreakInfo getCrystalWithMaxDamage() {
        if(currentTarget == null) return null;

        Entity crystal = null;
        float maxDamage = 0.5f;
        float selfDamage_ = 0.5f;

        try {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal) || (advancedSequential.getValBoolean() && timingMode.getValEnum() == TimingMode.Sequential && entity.ticksExisted < sequentialBreakDelay.getValInt())) continue;

                if (mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? breakRange.getValDouble() : breakWallRange.getValDouble())) {
                    Friend friend = getNearFriendWithMaxDamage(entity);
                    float targetDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());

                    Bind<Boolean, Float> targetResult = damageSyncHandler.canPlace(targetDamage, currentTarget);

                    if(damageSyncBreak.getValEnum() == DamageSyncMode.Smart) targetDamage = targetResult.getSecond();
                    if (friend != null && !friend_.checkValString(FriendMode.None.name())) {
                        if (friend_.checkValString(FriendMode.AntiTotemPop.name()) && friend.isTotemPopped) return null;
                        else if (friend.isTotemFailed) return null;
                        if (friend.damage >= maxFriendDMG.getValInt()) return null;
                    }

                    if (targetResult.getFirst() && (targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt()))) {
                        float selfDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, mc.player, terrain.getValBoolean());

                        Bind<Boolean, Float> selfResult = damageSyncHandler.canPlace(targetDamage, currentTarget);

                        if(damageSyncBreak.getValEnum() == DamageSyncMode.Smart && damageSyncSelf.getValBoolean()) selfDamage = selfResult.getSecond();
                        if (selfResult.getFirst() && selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                            if (maxDamage <= targetDamage) {
                                maxDamage = targetDamage;
                                selfDamage_ = selfDamage;
                                crystal = entity;
                            }
                        }
                    }
                }
            }
        } catch(NullPointerException ignored) {
            if(lagProtect.getValBoolean()) super.setToggled(false);
        }

        return crystal == null ? null : new BreakInfo((EntityEnderCrystal) crystal, selfDamage_, maxDamage, false);
    }

    private boolean breakStrictSync() {
        return (syncMode.getValEnum() == SyncMode.Strict || syncMode.getValEnum() == SyncMode.StrictFull) && lastBroken;
    }

    private void doBreak() {
        if(
                !break_.getValBoolean()
                        || !getTimer(true).passedMillis(getDelay(true))
                        || breakStrictSync()
        ) return;

        AtomicReference<BreakInfo> crystal = new AtomicReference<>();
        AtomicReference<BreakInfo> crystalWithMaxDamage = new AtomicReference<>();

        crystalTHandler.update(
                () -> mc.addScheduledTask(
                        () -> {
                            if(crystalTHandler.getThreadded().get()) atomicCrystal.set(getCrystalWithMaxDamage());
                            else crystalWithMaxDamage.set(getCrystalWithMaxDamage());
                        }
                )
        );

        if(crystalTHandler.getThreadded().get()) crystalWithMaxDamage.set(atomicCrystal.get());

        if(breakPriority.checkValString("Damage")) crystal.set(crystalWithMaxDamage.get());
        else {
            crystalTHandler.update(() -> mc.addScheduledTask(() -> crystal.set(getCrystalForAntiCevBreaker())));
            crystal.set(getCrystalForAntiCevBreaker());
            if(crystal.get() == null) crystal.set(crystalWithMaxDamage.get());
        }

        if(crystal.get() == null || (timingMode.getValEnum() != TimingMode.Adaptive && crystal.get().getCrystal().ticksExisted < sequentialBreakDelay.getValInt()) || !damageSyncHandler.canBreak(crystal.get().getTargetDamage(), currentTarget).getFirst()) return;

        RotationSaver saver = new RotationSaver().save();

        if(rotate.checkValString("Break") || rotate.checkValString("All")) rotateToEntity(crystal.get().getCrystal().getEntityId());

        lastHitEntity = crystal.get().getCrystal();

        if(swingLogic.getValEnum() == SwingLogic.Pre) swing();

        if(packetBreak.getValBoolean()) mc.player.connection.sendPacket(new CPacketUseEntity(crystal.get().getCrystal()));
        else mc.playerController.attackEntity(mc.player, crystal.get().getCrystal());

        if(swingLogic.getValEnum() == SwingLogic.Post) swing();
        doClientSide(crystal.get().getCrystal());
        try {if(clientSide.getValBoolean()) mc.world.removeEntityFromWorld(crystal.get().getCrystal().entityId);} catch (Exception ignored) {}
        getTimer(true).reset();

        if((rotate.checkValString("Break") || rotate.checkValString("All"))) loadSaver(saver);

        if(sync.getValBoolean()) {
            BlockPos toRemove = null;

            for(int i = 0; i < placedList.size(); i++) if(placedList.get(i).getBlockPos() != null && crystal.get().getCrystal().getDistanceSq(placedList.get(i).getBlockPos()) <= (3 * 3)) toRemove = placedList.get(i).getBlockPos();

            if(toRemove != null) placedList.remove(PlaceInfo.Companion.getElementFromListByPos(placedList, toRemove));
        }

        lastBroken = true;
    }

    private void doClientSide(Entity entity) {
        try {
            if(clientSide.getValEnum() == ClientSideMode.RemoveEntity || clientSide.getValEnum() == ClientSideMode.Both) mc.world.removeEntityFromWorld(entity.entityId);
            else if(clientSide.getValEnum() == ClientSideMode.SetDead || clientSide.getValEnum() == ClientSideMode.Both) entity.setDead();
        } catch (Exception ignored) {}
    }

    private void swing() {
        if(swing.checkValString(SwingMode.None.name())) return;
        if(swing.checkValString(SwingMode.PacketSwing.name())) mc.player.connection.sendPacket(new CPacketAnimation(swing.checkValString(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        else mc.player.swingArm(swing.checkValString(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
    }

    private Friend getNearFriendWithMaxDamage(Entity entity) {
        ArrayList<Friend> friendsWithMaxDamage = new ArrayList<>();

        for(EntityPlayer player : mc.world.playerEntities) {
            if(mc.player == player) continue;
            if(FriendManager.instance.isFriend(player)) {
                double friendDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());
                if(friendDamage <= maxFriendDMG.getValInt() || friendDamage * lethalMult.getValDouble() >= player.getHealth() + player.getAbsorptionAmount()) friendsWithMaxDamage.add(new Friend(player, friendDamage, friendDamage * lethalMult.getValDouble() >= player.getHealth() + player.getAbsorptionAmount()));
            }
        }

        Friend nearFriendWithMaxDamage = null;
        double maxDamage = 0.5;

        for(Friend friend : friendsWithMaxDamage) {
            double friendDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());
            if(friendDamage > maxDamage) {
                maxDamage = friendDamage;
                nearFriendWithMaxDamage = new Friend(friend.friend, friendDamage);
            }
        }

        return nearFriendWithMaxDamage;
    }

    public enum ThreadMode {None, Pool, Sound, While}
    public enum Render {None, Default, Advanced}
    public enum Rotate {Off, Place/*, Break, All*/}
    public enum SwitchMode {None, Normal, Silent}
    public enum SwingMode {MainHand, OffHand, PacketSwing, None}
    public enum SwingLogic {Pre, Post}
    public enum FriendMode {None, AntiTotemFail, AntiTotemPop}
    public enum LogicMode {PlaceBreak, BreakPlace}
    public enum RotateMode {Normal, Silent}
    public enum AntiCevBreakerMode {None, Cev, Civ, Both}
    public enum BreakPriority {Damage, CevBreaker}
    public enum DelayMode {Default, FromTo}
    public enum TimingMode {Sequential, Adaptive}
    public enum SyncMode {None, Merge, Strict, StrictFull}
    public enum MultiPlaceMode {None, Stupid, Smart}
    public enum FacePlaceMode {None, Stupid, Smart}
    public enum DamageSyncMode {None, Stupid, Smart}
    public enum ClientSideMode {None, RemoveEntity, SetDead, Both}
    public enum Heuristics {Damage, MinMax, Safety}

    public enum AntiCevBreakerVectors {
        Cev(Collections.singletonList(new Vec3i(0, 2, 0))),
        Civ(Arrays.asList(new Vec3i(1, 2, 0), new Vec3i(-1, 2, 0), new Vec3i(0, 2, 1), new Vec3i(0, 2, -1), new Vec3i(1, 2, 1), new Vec3i(-1, 2, -1), new Vec3i(1, 2, -1), new Vec3i(-1, 2, 1)));

        public final List<Vec3i> vectors;

        AntiCevBreakerVectors(List<Vec3i> vectors) {
            this.vectors = vectors;
        }
    }

    private static class Friend {
        public final EntityPlayer friend;
        public double damage;
        public boolean isTotemPopped;
        public boolean isTotemFailed = false;

        public Friend(EntityPlayer friend, double damage) {
            this.friend = friend;
            this.damage = damage;
            this.isTotemPopped = false;
        }

        public Friend(EntityPlayer friend, double damage, boolean isTotemPopped) {
            this.friend = friend;
            this.damage = damage;
            if(isTotemPopped) isTotemFailed = !(mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING));
            this.isTotemPopped = isTotemPopped;
        }
    }

    @SuppressWarnings("BusyWait")
    public static class RAutoRer implements Runnable {
        private static RAutoRer instance;
        private AutoRer autoRer;

        public static RAutoRer getInstance(AutoRer autoRer) {
            if(instance == null) {
                instance = new RAutoRer();
                instance.autoRer = autoRer;
            }
            return instance;
        }

        @Override
        public void run() {
            if(autoRer.threadMode.checkValString("While")) {
                while (autoRer.isToggled() && autoRer.threadMode.checkValString("While")) {
                    if(autoRer.shouldInterrupt.get()) {
                        autoRer.shouldInterrupt.set(false);
                        autoRer.syncTimer.reset();
                        autoRer.thread.interrupt();
                    }
                    autoRer.threadOngoing.set(true);
                    try {autoRer.doAutoRerForThread();} catch(Exception ignored) {}
                    autoRer.threadOngoing.set(false);
                    try {Thread.sleep(autoRer.threadDelay.getValLong());} catch (InterruptedException e) {autoRer.thread.interrupt();}
                }
            } else if(!autoRer.threadMode.checkValString("None")) {
                autoRer.threadOngoing.set(true);
                autoRer.doAutoRerForThread();
                autoRer.threadOngoing.set(false);
            }
        }
    }
}