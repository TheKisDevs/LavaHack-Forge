package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEntitySpawn;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.PingBypassModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.world.BlockUtil;
import com.kisman.cc.util.world.CrystalUtils;
import com.kisman.cc.util.world.RotationUtils;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.cubic.dynamictask.AbstractTask;

import java.util.*;

/**
 * @author Cubic
 */
@PingBypassModule
public class SurroundRewrite extends Module {
    private final MultiThreaddableModulePattern threads = new MultiThreaddableModulePattern(this).init();
    private final Setting eventMode = register(threads.getGroup_().add(new Setting("Event Mode", this, RunMode.Update)));
    private final Setting syncronized = register(new Setting("Syncronized", this, false));
    private final SettingEnum<Vectors> mode = new SettingEnum<Vectors>("Mode", this, Vectors.Normal).register();
    private final Setting rangeCheck = register(new Setting("RangeCheck", this, false));
    private final Setting placeRange = register(new Setting("PlaceRange", this, 5.5, 1, 10, false).setVisible(rangeCheck::getValBoolean));
    private final Setting extension = register(new Setting("Extension", this, false).setVisible(() -> mode.getValEnum() == Vectors.Dynamic));
    private final Setting allEntities = register(new Setting("AllEntities", this, false).setVisible(extension::getValBoolean));
    private final Setting block = register(new Setting("Block", this, "Obsidian", Arrays.asList("Obsidian", "EnderChest")));
    private final SettingEnum<SwapEnum.Swap> swap = new SettingEnum<SwapEnum.Swap>("Switch", this, SwapEnum.Swap.Silent).register();
    private final Setting swapWhen = register(new Setting("SwitchWhen", this, SwapWhen.Place));
    private final Setting center = register(new Setting("Center", this, false));
    private final Setting smartCenter = register(new Setting("SmartCenter", this, false));
    private final Setting fightCA = register(new Setting("FightCA", this, false));
    private final Setting detectSound = register(new Setting("DetectSound", this).setVisible(fightCA::getValBoolean));
    private final SettingEnum<FightCAEntityMode> detectEntity = new SettingEnum<>("DetectEntity", this, FightCAEntityMode.Off).setVisible(fightCA::getValBoolean).register();
    private final Setting antiCity = register(new Setting("AntiCity", this, false));
    private final Setting toggle = register(new Setting("Toggle", this, Toggle.OffGround));
    private final Setting toggleHeight = register(new Setting("ToggleHeight", this, 0.4, 0.0, 1.0, false).setVisible(() -> toggle.getValEnum() == Toggle.PositiveYChange || toggle.getValEnum() == Toggle.Combo));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting feetBlocks = register(new Setting("FeetBlocks", this, false));
    private final Setting heightLimit = register(new Setting("HeightLimit", this, 256, 0, 256, true));
    private final Setting down = register(new Setting("Down", this, false));

    private final SettingGroup inAirGroup = register(new SettingGroup(new Setting("In Air", this)));

    private final Setting inAir = register(inAirGroup.add(new Setting("In Air", this, false).setTitle("State")));
    private final Setting inAirMotionStop = register(inAirGroup.add(new Setting("In Air Motion Stop", this, false).setTitle("Motion Stop")));

    private final Setting breakCrystals = register(new Setting("BreakCrystals", this, false));

    private final SettingGroup crystalBreaker = register(new SettingGroup(new Setting("Crystal Breaker", this)));

    private final Setting cbTimings = register(crystalBreaker.add(new Setting("CB Timings", this, Timings.Adaptive).setTitle("Timings")));
    private final Setting cbSequentialDelay = register(crystalBreaker.add(new Setting("CB Sequential Delay", this, 1, 0, 10, true).setTitle("Sequential Delay")));
    private final Setting cbMode = register(crystalBreaker.add(new Setting("CbMode", this, "SurroundBlocks", Arrays.asList("SurroundBlocks", "Area")).setTitle("Mode")));
    private final Setting cbRange = register(crystalBreaker.add(new Setting("CBRange", this, 3.0, 1.0, 6.0, false).setVisible(() -> cbMode.getValString().equals("Area")).setTitle("Range")));
    private final Setting cbDelay = register(crystalBreaker.add(new Setting("CBDelay", this, 60, 0, 500, true).setTitle("Delay")));
    private final Setting cbRotate = register(crystalBreaker.add(new Setting("CBRotate", this, false).setTitle("Rotate")));
    private final Setting cbRotateMode = register(crystalBreaker.add(new Setting("CBRotateMode", this, CBRotateMode.Packet)).setVisible(cbRotate).setTitle("Rotate Mode"));
    private final Setting cbPacket = register(crystalBreaker.add(new Setting("CBPacket", this, false).setTitle("Packet")));
    private final Setting clientSide = register(crystalBreaker.add(new Setting("ClientSide", this, false).setTitle("Client Side")));
    private final Setting cbNoSuicide = register(crystalBreaker.add(new Setting("CbNoSuicide", this, true).setTitle("No Suicide")));
    private final Setting cbTerrain = register(crystalBreaker.add(new Setting("CbTerrain", this, true).setVisible(cbNoSuicide::getValBoolean)));

    public static SurroundRewrite instance;

    private final TimerUtils timer = new TimerUtils();

    private double lastY = -1;

    public SurroundRewrite(){
        super("SurroundRewrite", Category.COMBAT, true);
        instance = this;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTick(TickEvent event){
        if(eventMode.getValEnum() != RunMode.Tick)
            return;

        if(syncronized.getValBoolean())
            doThreaddedSynchronizedSurround();
        else
            doThreaddedSurround();
    }

    @Override
    public void onEnable(){
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(eventEntitySpawnListener);
        Kisman.EVENT_BUS.subscribe(packetListener);
        timer.reset();
        if(mc.player == null || mc.world == null) return;
        lastY = mc.player.posY;
        if(center.getValBoolean() && !centerPlayer()) {
            setToggled(false);
        }
    }

    @Override
    public void update(){
        if(eventMode.getValEnum() != RunMode.Update)
            return;

        if(syncronized.getValBoolean())
            doThreaddedSynchronizedSurround();
        else
            doThreaddedSurround();
    }

    private void doThreaddedSurround() {
        threads.update(this::doSurround);
    }

    private synchronized void doThreaddedSynchronizedSurround(){
        threads.update(this::doSurround);
    }

    private void doSurround(){
        if(mc.player == null || mc.world == null) return;

        double y = mc.player.posY;

        boolean ground = !mc.player.onGround;

        if(inAir.getValBoolean()) {
            ground = false;

            if(inAirMotionStop.getValBoolean()) {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
                mc.player.moveForward = 0;
                mc.player.moveStrafing = 0;
            }
        }

        if(
                (toggle.getValEnum() == Toggle.OffGround && ground)
                        || (toggle.getValEnum() == Toggle.YChange && y > lastY)
                        || (toggle.getValEnum() == Toggle.PositiveYChange && y - toggleHeight.getValDouble() > lastY)
                        || (toggle.getValEnum() == Toggle.Combo && (y - toggleHeight.getValDouble() > lastY || ground))
        ){
            toggle();
            return;
        }

        if(breakCrystals.getValBoolean())
            breakCrystals(mode.getValEnum().getBlocks());

        int slot = getBlockSlot();
        if(slot == -1) return;
        int oldSlot = mc.player.inventory.currentItem;

        swap.getValEnum().doSwap(slot, false, SwapWhen.RunSurround);

        placeBlocks(mode.getValEnum().getBlocks());

        swap.getValEnum().doSwap(oldSlot, true, SwapWhen.RunSurround);

        lastY = y;

        if(toggle.getValEnum() == Toggle.OnComplete) toggle();
    }

    @Override
    public void onDisable(){
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(eventEntitySpawnListener);
        Kisman.EVENT_BUS.unsubscribe(packetListener);
        lastY = -1;
        timer.reset();
    }

    private void breakCrystals(List<BlockPos> blocks){
        if(!timer.passedMillis(cbDelay.getValInt()))
            return;
        float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};
        Set<EntityEnderCrystal> alreadyHit = new HashSet<>(64);
        if(cbMode.getValString().equals("Area")) {
            double range = cbRange.getValDouble();
            double x1 = mc.player.posX - range;
            double y1 = mc.player.posY - range;
            double z1 = mc.player.posZ - range;
            double x2 = mc.player.posX + range;
            double y2 = mc.player.posY + range;
            double z2 = mc.player.posZ + range;
            AxisAlignedBB aabb = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
            for(EntityEnderCrystal crystal : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, aabb)){
                if(!validCrystal(crystal)) return;
                breakCrystal(crystal, oldRots);
            }
            return;
        }
        for(BlockPos pos : blocks){
            for(EntityEnderCrystal crystal : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos))){
                if(alreadyHit.contains(crystal) || !validCrystal(crystal)) continue;
                breakCrystal(crystal, oldRots);
                alreadyHit.add(crystal);
            }
        }
    }

    private boolean validCrystal(EntityEnderCrystal crystal) {
        if(cbTimings.checkValString("Sequential") && crystal.ticksExisted < cbSequentialDelay.getValInt()) return false;
        if(!cbNoSuicide.getValBoolean()) return true;

        float damage = CrystalUtils.calculateDamage(
                mc.world,
                crystal.posX,
                crystal.posY,
                crystal.posZ,
                mc.player,
                cbTerrain.getValBoolean()
        );

        return damage < mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    private void breakCrystal(EntityEnderCrystal crystal, float[] oldRots){
        if(cbRotate.getValBoolean()){
            float[] rots = RotationUtils.getRotation(crystal);
            cbRotate(rots);
        }
        if(cbPacket.getValBoolean())
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        else
            mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if(clientSide.getValBoolean())
            mc.world.removeEntityFromWorld(crystal.entityId);
        if(cbRotate.getValBoolean()){
            cbRotate(oldRots);
        }
    }

    private void cbRotate(float[] rots){
        if(cbRotateMode.getValEnum() == CBRotateMode.Packet || cbRotateMode.getValEnum() == CBRotateMode.Both)
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
        if(cbRotateMode.getValEnum() == CBRotateMode.Client || cbRotateMode.getValEnum() == CBRotateMode.Both) {
            mc.player.rotationYaw = rots[0];
            mc.player.rotationPitch = rots[1];
        }
    }

    private BlockPos findClosestSolid(BlockPos pos){
        List<BlockPos> possiblePositions = new ArrayList<>();
        if(mc.world.getBlockState(pos.north().down()).getMaterial().isSolid())
            possiblePositions.add(pos.north());
        if(mc.world.getBlockState(pos.east().down()).getMaterial().isSolid())
            possiblePositions.add(pos.east());
        if(mc.world.getBlockState(pos.south().down()).getMaterial().isSolid())
            possiblePositions.add(pos.south());
        if(mc.world.getBlockState(pos.west().down()).getMaterial().isSolid())
            possiblePositions.add(pos.west());
        return possiblePositions.stream().min(Comparator.comparingDouble(o -> mc.player.getDistance(o.getX() + 0.5, o.getY(), o.getZ() + 0.5))).orElse(null);
    }

    private boolean centerPlayer(){
        if(smartCenter.getValBoolean()){
            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            if(isReplaceable(pos))
                pos = findClosestSolid(pos);
            if(pos == null)
                return false;
            centerAt(pos);
            return true;
        }
        centerAt(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ));
        return true;
    }

    private void centerAt(BlockPos pos){
        Vec3d setCenter = new Vec3d(pos.getX() + 0.5, mc.player.posY, pos.getZ() + 0.5);
        mc.player.motionX = 0;
        mc.player.motionZ = 0;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(setCenter.x, setCenter.y, setCenter.z, true));
        mc.player.setPosition(setCenter.x, setCenter.y, setCenter.z);
    }

    private void placeBlocks(List<BlockPos> blocks){
        int slot = getBlockSlot();
        if(slot == -1) return;
        int oldSlot = mc.player.inventory.currentItem;
        if(swap.getValEnum() == SwapEnum.Swap.None){
            ItemStack stack = mc.player.inventory.getStackInSlot(oldSlot);
            Item item = stack.getItem();
            if(!(item instanceof ItemBlock))
                return;
            Block block = ((ItemBlock) item).getBlock();
            Block swapBlock = getSwapBlock();
            if(block != swapBlock)
                return;
        }
        for(BlockPos pos : blocks){
            if(pos.getY() > heightLimit.getValInt())
                continue;
            if(!isReplaceable(pos)) continue;
            if(checkEntities(pos)) continue;
            if(rangeCheck.getValBoolean() && mc.player.getDistanceSq(pos) > placeRange.getValDouble())
                continue;
            swap.getValEnum().doSwap(slot, false, SwapWhen.Place);
            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
            swap.getValEnum().doSwap(oldSlot, true, SwapWhen.Place);
        }
    }

    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(!fightCA.getValBoolean())
            return;

        if(!detectSound.getValBoolean())
            return;

        if(!(event.getPacket() instanceof SPacketSoundEffect))
            return;

        SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
        if(packet.getSound() != SoundEvents.ENTITY_GENERIC_EXPLODE)
            return;

        Vec3d vec3d = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        List<BlockPos> blocks = mode.getValEnum().getBlocks();
        if(!isInAnyBlocks(vec3d, blocks))
            return;
        if(syncronized.getValBoolean())
            doThreaddedSynchronizedSurround();
        else
            doThreaddedSurround();
    });

    private final Listener<EventEntitySpawn> eventEntitySpawnListener = new Listener<>(event -> {
        if(!fightCA.getValBoolean())
            return;

        if(detectEntity.getValEnum() == FightCAEntityMode.Off)
            return;

        FightCAEntityMode entityMode = detectEntity.getValEnum();

        Entity entity = event.getEntity();

        List<BlockPos> blocks = mode.getValEnum().getBlocks();
        if(!isInAnyBlocks(entity.getEntityBoundingBox(), blocks))
            return;

        if(entityMode == FightCAEntityMode.SetDead || entityMode == FightCAEntityMode.Off)
            entity.setDead();

        if(entityMode == FightCAEntityMode.RemoveEntity || entityMode == FightCAEntityMode.Both)
            mc.world.removeEntity(entity);

        if(syncronized.getValBoolean())
            doThreaddedSynchronizedSurround();
        else
            doThreaddedSurround();
    });

    private final Listener<PacketEvent.Receive> packetListener = new Listener<>(event -> {
        if(!antiCity.getValBoolean())
            return;

        if(!(event.getPacket() instanceof SPacketBlockChange))
            return;

        SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();

        BlockPos pos = packet.getBlockPosition();

        if(!packet.getBlockState().getBlock().isReplaceable(mc.world, pos))
            return;

        List<BlockPos> blocks = mode.getValEnum().getBlocks();
        if(!blocks.contains(pos))
            return;

        if(syncronized.getValBoolean())
            doThreaddedSynchronizedSurround();
        else
            doThreaddedSurround();
    });

    private boolean isInAnyBlocks(Vec3d vec, List<BlockPos> list){
        for(BlockPos pos : list)
            if(new AxisAlignedBB(pos).contains(vec))
                return true;
        return false;
    }

    private boolean isInAnyBlocks(AxisAlignedBB aabb, List<BlockPos> list){
        for(BlockPos pos : list)
            if(new AxisAlignedBB(pos).intersects(aabb))
                return true;
        return false;
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private int getBlockSlot(){
        if(block.getValString().equals("Obsidian"))
            return InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        return InventoryUtil.getBlockInHotbar(Blocks.ENDER_CHEST);
    }

    private Block getSwapBlock(){
        if(block.getValString().equals("Obsidian"))
            return Blocks.OBSIDIAN;
        return Blocks.ENDER_CHEST;
    }

    private List<BlockPos> getActualDynamicBlocks(){
        if(extension.getValBoolean())
            return getDynamicBlocksExtension();
        return getDynamicBlocks();
    }

    private List<BlockPos> getDynamicBlocks(){
        List<BlockPos> upperBlocks = getDynamicUpperBlocks();
        List<BlockPos> blocks = new ArrayList<>(16);
        if(feetBlocks.getValBoolean())
            blocks.addAll(getDynamicBlocksOffset(-1));
        for(BlockPos pos : upperBlocks){
            List<BlockPos> helpingBlocks = getHelpingBlocks(pos);
            blocks.addAll(helpingBlocks);
            blocks.add(pos);
        }
        return blocks;
    }

    private List<BlockPos> getDynamicBlocksExtension(){
        List<BlockPos> upperBlocks = getDynamicUpperBlocks();
        List<BlockPos> blocks = new ArrayList<>(16);
        if(feetBlocks.getValBoolean())
            blocks.addAll(getDynamicBlocksOffset(-1));
        for(BlockPos pos : upperBlocks){
            List<BlockPos> helpingBlocks = getHelpingBlocks(pos);
            blocks.addAll(helpingBlocks);
            blocks.add(pos);
        }
        List<Entity> entities = new ArrayList<>();
        Class<? extends Entity> entityClass =  allEntities.getValBoolean() ? Entity.class : EntityPlayer.class;
        for(BlockPos pos : blocks){
            List<Entity> playersInside = mc.world.getEntitiesWithinAABB(entityClass, new AxisAlignedBB(pos));
            if(entities.isEmpty())
                entities = mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.down()));
            entities.addAll(playersInside);
        }
        List<BlockPos> actualBlocks = new ArrayList<>(blocks);
        for(Entity entity : entities){
            if(entity.equals(mc.player))
                continue;
            List<BlockPos> curUpperBlocks = getDynamicUpperBlocks(entity, mc.player.posY);
            List<BlockPos> positions = new ArrayList<>(16);
            if(feetBlocks.getValBoolean())
                positions.addAll(getDynamicBlocksOffset(entity, mc.player.posY, -1));
            for(BlockPos pos : curUpperBlocks){
                List<BlockPos> helpingBlocks = getHelpingBlocks(pos);
                positions.addAll(helpingBlocks);
                positions.add(pos);
            }
            List<Entity> newEntities = new ArrayList<>(entities);
            newEntities.add(mc.player);
            List<BlockPos> remove = new ArrayList<>();
            for(Entity e : newEntities){
                List<BlockPos> raw = getDynamicBlocksOffset(e, mc.player.posY, 0);
                for(BlockPos pos : positions){
                    if(raw.contains(pos))
                        remove.add(pos);
                }
            }
            positions.removeAll(remove);
            actualBlocks.addAll(positions);
        }
        return actualBlocks;
    }

    private List<BlockPos> getHelpingBlocks(BlockPos pos){
        // this will change in the future
        return Arrays.asList(pos.down());
    }

    private List<BlockPos> getDynamicUpperBlocks(){
        List<BlockPos> rawBlocks = getDynamicBlocksOffset(0);
        List<BlockPos> blocks = new ArrayList<>(16);
        for(BlockPos pos : rawBlocks){
            BlockPos b1 = pos.north();
            BlockPos b2 = pos.east();
            BlockPos b3 = pos.south();
            BlockPos b4 = pos.west();
            if(!rawBlocks.contains(b1)) blocks.add(b1);
            if(!rawBlocks.contains(b2)) blocks.add(b2);
            if(!rawBlocks.contains(b3)) blocks.add(b3);
            if(!rawBlocks.contains(b4)) blocks.add(b4);
        }
        return blocks;
    }

    private List<BlockPos> getDynamicBlocksOffset(int offset){
        List<BlockPos> list = new ArrayList<>(16);
        Vec3d vec1 = new Vec3d(mc.player.posX + 0.3, mc.player.posY + offset, mc.player.posZ + 0.3);
        Vec3d vec2 = new Vec3d(mc.player.posX + 0.3, mc.player.posY + offset, mc.player.posZ - 0.3);
        Vec3d vec3 = new Vec3d(mc.player.posX - 0.3, mc.player.posY + offset, mc.player.posZ + 0.3);
        Vec3d vec4 = new Vec3d(mc.player.posX - 0.3, mc.player.posY + offset, mc.player.posZ - 0.3);
        addIfChecks(vec1, list);
        addIfChecks(vec2, list);
        addIfChecks(vec3, list);
        addIfChecks(vec4, list);
        return list;
    }

    private List<BlockPos> getDynamicUpperBlocks(Entity entity, double y){
        List<BlockPos> rawBlocks = getDynamicBlocksOffset(entity, y, 0);
        List<BlockPos> blocks = new ArrayList<>(16);
        for(BlockPos pos : rawBlocks){
            BlockPos b1 = pos.north();
            BlockPos b2 = pos.east();
            BlockPos b3 = pos.south();
            BlockPos b4 = pos.west();
            if(!rawBlocks.contains(b1)) blocks.add(b1);
            if(!rawBlocks.contains(b2)) blocks.add(b2);
            if(!rawBlocks.contains(b3)) blocks.add(b3);
            if(!rawBlocks.contains(b4)) blocks.add(b4);
        }
        return blocks;
    }

    private List<BlockPos> getDynamicBlocksOffset(Entity entity, double y, int offset){
        List<BlockPos> list = new ArrayList<>(16);
        AxisAlignedBB aabb = entity.getEntityBoundingBox();
        double oX = (aabb.maxX - aabb.minX) / 2.0;
        double oZ = (aabb.maxZ - aabb.minZ) / 2.0;
        Vec3d vec1 = new Vec3d(entity.posX + oX, y + offset, entity.posZ + oZ);
        Vec3d vec2 = new Vec3d(entity.posX + oX, y + offset, entity.posZ - oZ);
        Vec3d vec3 = new Vec3d(entity.posX - oX, y + offset, entity.posZ + oZ);
        Vec3d vec4 = new Vec3d(entity.posX - oX, y + offset, entity.posZ - oZ);
        addIfChecks(vec1, list);
        addIfChecks(vec2, list);
        addIfChecks(vec3, list);
        addIfChecks(vec4, list);
        return list;
    }

    private void addIfChecks(Vec3d vec, List<BlockPos> list){
        BlockPos pos = new BlockPos(vec);
        if(isReplaceable(pos) && !list.contains(pos))
            list.add(pos);
    }

    private boolean isReplaceable(BlockPos pos){
        return pos != null && mc.world != null && mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    private List<BlockPos> getAntiFacePlaceBlocks(){
        List<BlockPos> blocks = new ArrayList<>(16);
        blocks.addAll(Vectors.Normal.getBlocks());
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<BlockPosOffset> surroundingBlocks = getSurroundingBlocks(playerPos.up());
        for(BlockPosOffset posOffset : surroundingBlocks){
            BlockPos pos = posOffset.getPos();
            BlockPos up = pos.up();
            BlockPos offset = pos.offset(posOffset.getFacing());
            if(getBlock(up) == Blocks.AIR){
                blocks.add(pos);
                continue;
            }
            if(getBlock(offset) == Blocks.AIR)
                blocks.add(pos);
        }
        return blocks;
    }

    private List<BlockPosOffset> getSurroundingBlocks(BlockPos pos){
        List<BlockPosOffset> list = new ArrayList<>(16);
        list.add(new BlockPosOffset(pos.north(), EnumFacing.NORTH));
        list.add(new BlockPosOffset(pos.east(), EnumFacing.EAST));
        list.add(new BlockPosOffset(pos.south(), EnumFacing.SOUTH));
        list.add(new BlockPosOffset(pos.west(), EnumFacing.WEST));
        return list;
    }

    private Block getBlock(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock();
    }

    private enum Vectors {
        Normal(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1)
        }),
        Strict(new Vec3d[]{
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1)
        }),
        Safe(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 1),
                new Vec3d(1, -1, -1),
                new Vec3d(-1, -1, 1),
                new Vec3d(-1, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 1),
                new Vec3d(1, 0, -1),
                new Vec3d(-1, 0, 1),
                new Vec3d(-1, 0, -1),
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2)
        }),
        Cubic(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 1),
                new Vec3d(1, -1, -1),
                new Vec3d(-1, -1, 1),
                new Vec3d(-1, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 1),
                new Vec3d(1, 0, -1),
                new Vec3d(-1, 0, 1),
                new Vec3d(-1, 0, -1)
        }),
        High(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 1, 0),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(0, 1, -1)
        }),
        AntiFacePlace(null),
        Dynamic(null);

        private final Vec3d[] vec3d;

        Vectors(Vec3d[] vec3d){
            this.vec3d = vec3d;
        }

        public List<BlockPos> getBlocks(){
            List<BlockPos> list = new ArrayList<>(64);
            if(this == Dynamic)
                return instance.getActualDynamicBlocks();
            if(this == AntiFacePlace)
                return instance.getAntiFacePlaceBlocks();
            if(instance.feetBlocks.getValBoolean())
                list.addAll(instance.getDynamicBlocksOffset(-1));
            if(instance.down.getValBoolean()) 
                list.addAll(instance.getDynamicBlocksOffset(-2));
            Vec3d posVec = mc.player.getPositionVector();
            for(Vec3d vec : vec3d) {
                list.add(new BlockPos(vec.add(posVec)));
            }
            return list;
        }
    }



    private static class SwapEnum {
        private static final AbstractTask.DelegateAbstractTask<Void> task = AbstractTask.types(
                Void.class,
                Integer.class,//Slot
                Boolean.class//Swap back(Silent)
        );

        private enum Swap {
            None(task.task(arg -> null)),
            Vanilla(task.task(arg -> {
                if(arg.fetch(1)) return null;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(arg.fetch(0)));
                mc.player.inventory.currentItem = arg.fetch(0);
                return null;
            })),
            Packet(task.task(arg -> {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(arg.fetch(0)));
                if(arg.fetch(1)) mc.playerController.updateController();
                return null;
            })),
            Silent(task.task(arg -> {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(arg.fetch(0)));
                mc.player.inventory.currentItem = arg.fetch(0);
                if(arg.fetch(1)) mc.playerController.updateController();
                return null;
            }));

            private final AbstractTask<Void> abstractTask;
            Swap(AbstractTask<Void> task) {
                this.abstractTask = task;}

            public void doSwap(int slot, boolean swapBack, SwapWhen when){
                if(instance.swapWhen.getValEnum() != when) return;
                if(mc.player.inventory.currentItem == slot) return;
                abstractTask.doTask(slot, swapBack);
            }
        }
    }

    private static class BlockPosOffset {
        private final BlockPos pos;

        private final EnumFacing facing;

        public BlockPosOffset(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }

        public BlockPos getPos() {
            return pos;
        }

        public EnumFacing getFacing() {
            return facing;
        }
    }

    private enum Toggle {
        Never,
        OffGround,
        YChange,
        PositiveYChange,
        Combo,
        OnComplete,
    }

    private enum CBRotateMode {
        Client,
        Packet,
        Both
    }

    private enum SwapWhen {
        Place,
        RunSurround
    }

    private enum RunMode {
        Update,
        Tick
    }

    private enum FightCAEntityMode {
        Off,
        RemoveEntity,
        SetDead,
        Both
    }

    private enum Timings {
        Adaptive,
        Sequential
    }
}
