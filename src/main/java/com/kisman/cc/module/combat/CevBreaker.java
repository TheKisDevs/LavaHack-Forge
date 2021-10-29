package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.DestroyBlockEvent;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.EntityUtil;
import com.kisman.cc.util.PlayerUtil;
import com.kisman.cc.util.RotationUtils;
import com.kisman.cc.util.gamesense.CrystalUtil;
import com.kisman.cc.util.gamesense.GameSenseHoleUtil;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.kisman.cc.util.gamesense.SpoofRotationUtil.ROTATION_UTIL;

public class CevBreaker extends Module {
    private Setting target = new Setting("Target", this, "Nearest", new ArrayList<>(Arrays.asList("Nearest", "Looking")));
    private Setting breakCrystal = new Setting("Break", this, "Packet", new ArrayList<>(Arrays.asList("Vanilla", "Packet", "None")));
    private Setting breakBlock = new Setting("BreakBlock", this, "Packet", new ArrayList<>(Arrays.asList("Packet", "Normal")));
    private Setting range = new Setting("Range", this, 4.9f, 0, 6, false);
    private Setting preRotationDelay = new Setting("PreRotstionDelay", this,  0, 0, 20, true);
    private Setting afterRotationDelay = new Setting("AfterRotationDelay", this, 0, 0, 20, true);
    private Setting supDelay = new Setting("SupportDelay", this, 1, 0, 4, true);
    private Setting crystalDelay = new Setting("CrystalDelay", this, 2, 0, 20, true);
    private Setting blockPerTick = new Setting("BlockPerTick", this, 4, 2, 6, true);
    private Setting hitDelay = new Setting("HitDelay", this, 2, 0 ,20, true);
    private Setting midHitDelay = new Setting("MidHitDelay", this, 1, 0, 20, true);
    private Setting endDelay = new Setting("EndDelay", this, 1, 0, 20, true);
    private Setting pickSwitchTick = new Setting("PickSwitchTick", this, 100, 0, 500, true);
    private Setting rotate = new Setting("Rotate", this, false);
    private Setting confirmBreak = new Setting("NoGlitchBreak", this, true);
    private Setting confirmPlace = new Setting("NoGlitchPlace", this, true);
    private Setting antiWeakness = new Setting("AntiWeakness", this, true);
    private Setting switchSword = new Setting("SwitchSword", this, false);
    private Setting fastPlace = new Setting("FastPlace", this, false);
    private Setting fastBreak = new Setting("FastBreak", this, true);
    private Setting trapPlayer = new Setting("TrapPlayer", this, false);
    private Setting antiStep = new Setting("AntiStep", this, false);
    private Setting placeCrystal = new Setting("PlaceCrystal", this, true);
    private Setting forceRotation = new Setting("ForceRotation", this, false);
    private Setting forceBreak = new Setting("ForceBreak", this, false);

    public static int cur_item = -1;
    public static boolean isActive = false;
    public static boolean forceBrk = false;

    private boolean noMaterials = false,
            hasMoved = false,
            isSneaking = false,
            isHole = true,
            enoughSpace = true,
            broken,
            stoppedCa,
            deadPl,
            rotationPlayerMoved,
            prevBreak,
            preRotationBol;

    private int oldSlot = -1,
            stage,
            delayTimeTicks,
            hitTryTick,
            tickPick,
            afterRotationTick,
            preRotationTick;
    private final int[][] model = new int[][]{
            {1, 1, 0},
            {-1, 1, 0},
            {0, 1, 1},
            {0, 1, -1}
    };

    public static boolean isPossible = false;

    private int[] slot_mat,
            delayTable,
            enemyCoordsInt;

    private double[] enemyCoordsDouble;

    private structureTemp toPlace;


    Double[][] sur_block = new Double[4][3];

    private EntityPlayer aimTarget;

    private Vec3d lastHitVec;

    public CevBreaker() {
        super("CevBreaker", "CevBreaker", Category.COMBAT);

        setmgr.rSetting(target);
        setmgr.rSetting(breakCrystal);
        setmgr.rSetting(breakBlock);
        setmgr.rSetting(range);
        setmgr.rSetting(preRotationDelay);
        setmgr.rSetting(afterRotationDelay);
        setmgr.rSetting(supDelay);
        setmgr.rSetting(crystalDelay);
        setmgr.rSetting(blockPerTick);
        setmgr.rSetting(hitDelay);
        setmgr.rSetting(midHitDelay);
        setmgr.rSetting(endDelay);
        setmgr.rSetting(pickSwitchTick);
        setmgr.rSetting(rotate);
        setmgr.rSetting(confirmBreak);
        setmgr.rSetting(confirmPlace);
        setmgr.rSetting(antiWeakness);
        setmgr.rSetting(switchSword);
        setmgr.rSetting(fastPlace);
        setmgr.rSetting(fastBreak);
        setmgr.rSetting(trapPlayer);
        setmgr.rSetting(antiStep);
        setmgr.rSetting(placeCrystal);
        setmgr.rSetting(forceRotation);
        setmgr.rSetting(forceBreak);
    }

    private static class structureTemp {
        public double distance;
        public int supportBlock;
        public ArrayList<Vec3d> to_place;
        public int direction;

        public structureTemp(double distance, int supportBlock, ArrayList<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }
    }

    public void onEnable() {
        ROTATION_UTIL.onEnable();
        initValues();
        if (getAimTarget())
            return;
        playerChecks();

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        try {
            Kisman.EVENT_BUS.unsubscribe(listener2);
        } catch (Exception e) {}

        ROTATION_UTIL.onDisable();
        if (mc.player == null) {
            return;
        }

        String output = "";
        String materialsNeeded = "";
        // No target found
        if (aimTarget == null) {
            output = "No target found...";
        } else
            // H distance not avaible
            if (noMaterials) {
                output = "No Materials Detected...";
                materialsNeeded = getMissingMaterials();
                // No Hole
            } else if (!isHole) {
                output = "The enemy is not in a hole...";
                // No Space
            } else if (!enoughSpace) {
                output = "Not enough space...";
                // Has Moved
            } else if (hasMoved) {
                output = "Out of range...";
            } else if (deadPl) {
                output = "Enemy is dead, gg! ";
            }
        // Output in chat
        ChatUtils.complete(output + "CevBreaker turned OFF!");
        if (!materialsNeeded.equals(""))
            ChatUtils.warning("Materials missing:" + materialsNeeded);

        if (stoppedCa) {
            AutoCrystalBypass.instance.setToggled(false);
            stoppedCa = false;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        AutoCrystalBypass.instance.setToggled(false);
        noMaterials = isPossible = isActive = forceBrk = false;
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if (event.getEra() != Event.Era.PRE || !rotate.getValBoolean() || lastHitVec == null || !forceRotation.getValBoolean()) return;
        Vec2f rotation = RotationUtils.getRotationTo(lastHitVec);
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        if(enemyCoordsInt == null) return;

        // If the explosion is on the enemy's idea
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                // Reset
                if ((int) packet.getX() == enemyCoordsInt[0] && (int) packet.getZ() == enemyCoordsInt[2])
                    stage = 1;
            }
        }
    });

    @EventHandler
    private final Listener<DestroyBlockEvent> listener2 = new Listener<>(event -> {
        try {
            if(enemyCoordsInt == null) return;

            // If the destruction is on the enemy's idea
            if (event.getBlockPos().getX() + (event.getBlockPos().getX() < 0 ? 1 : 0) == enemyCoordsInt[0] && event.getBlockPos().getZ() + (event.getBlockPos().getZ() < 0 ? 1 : 0) == enemyCoordsInt[2]) {
                // Destroy
                destroyCrystalAlgo();
            }
        } catch (Exception e) {}

    });

    private String getMissingMaterials() {
        /*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => Crystal
			2 => Pick
			3 => Sword
		 */
        StringBuilder output = new StringBuilder();

        if (slot_mat[0] == -1)
            output.append(" Obsidian");
        if (slot_mat[1] == -1)
            output.append(" Crystal");
        if ((antiWeakness.getValBoolean() || switchSword.getValBoolean()) && slot_mat[3] == -1)
            output.append(" Sword");
        if (slot_mat[2] == -1)
            output.append(" Pick");

        return output.toString();
    }

    private boolean getAimTarget() {
        /// Get aimTarget
        // If nearest, get it

        if (target.getValString().equalsIgnoreCase("Nearest"))
            aimTarget = PlayerUtil.findClosestTarget(range.getValDouble(), aimTarget);
            // if looking
        else
            aimTarget = PlayerUtil.findLookingPlayer(range.getValDouble());

        // If we didnt found a target
        if (aimTarget == null || !target.getValString().equalsIgnoreCase("Looking")) {
            // if it's not looking and we didnt found a target
            if (!target.getValString().equalsIgnoreCase("Looking") && aimTarget == null)
                super.onDisable();
            // If not found a target
            if (aimTarget == null)
                return true;
        }
        return false;
    }

    private boolean createStructure() {

        // Check position of the crystal
        if ((Objects.requireNonNull(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 2, enemyCoordsDouble[2]).getRegistryName()).toString().toLowerCase().contains("bedrock"))
                || !(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 3, enemyCoordsDouble[2]) instanceof BlockAir)
                || !(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 4, enemyCoordsDouble[2]) instanceof BlockAir))
            return false;

        // Iterate for every blocks around, find the closest
        double distance_now;
        double max_found = Double.MIN_VALUE;
        int cor = 0;
        int i = 0;
        // Find closest
        for (Double[] cord_b : sur_block) {
            if ((distance_now = mc.player.getDistanceSq(new BlockPos(cord_b[0], cord_b[1], cord_b[2]))) > max_found) {
                max_found = distance_now;
                cor = i;
            }
            i++;
        }

        // Create support blocks
        toPlace.to_place.add(new Vec3d(model[cor][0], 1, model[cor][2]));
        toPlace.to_place.add(new Vec3d(model[cor][0], 2, model[cor][2]));
        toPlace.supportBlock = 2;

        // Create antitrap + antiStep
        if (trapPlayer.getValBoolean() || antiStep.getValBoolean()) {
            for (int high = 1; high < 3; high++) {
                if (high != 2 || antiStep.getValBoolean())
                    for (int[] modelBas : model) {
                        Vec3d toAdd = new Vec3d(modelBas[0], high, modelBas[2]);
                        if (!toPlace.to_place.contains(toAdd)) {
                            toPlace.to_place.add(toAdd);
                            toPlace.supportBlock++;
                        }
                    }
            }
        }


        // Create structure
        // Obsidian
        toPlace.to_place.add(new Vec3d(0, 2, 0));
        // Crystal
        toPlace.to_place.add(new Vec3d(0, 2, 0));
        return true;
    }

    // Get all the materials
    private boolean getMaterialsSlot() {
		/*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => Crystal
			2 => Pick
			3 => Sword
		 */

        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            slot_mat[1] = 11;
        }
        // Iterate for all the inventory
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            // If there is no block
            if (stack == ItemStack.EMPTY) {
                continue;
            }
            // If endCrystal
            if (slot_mat[1] == -1 && stack.getItem() instanceof ItemEndCrystal) {
                slot_mat[1] = i;
                // If sword
            } else if ((antiWeakness.getValBoolean() || switchSword.getValBoolean()) && stack.getItem() instanceof ItemSword) {
                slot_mat[3] = i;
            } else
                // If Pick
                if (stack.getItem() instanceof ItemPickaxe) {
                    slot_mat[2] = i;
                }
            if (stack.getItem() instanceof ItemBlock) {

                // If yes, get the block
                Block block = ((ItemBlock) stack.getItem()).getBlock();

                // Obsidian
                if (block instanceof BlockObsidian) {
                    slot_mat[0] = i;
                }
            }
        }
        // Count what we found
        int count = 0;
        for (int val : slot_mat) {
            if (val != -1)
                count++;
        }

        // If we have everything we need, return true
        return count >= 3 + ((antiWeakness.getValBoolean() || switchSword.getValBoolean()) ? 1 : 0);

    }

    private boolean is_in_hole() {
        sur_block = new Double[][]{
                {aimTarget.posX + 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX - 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ + 1},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ - 1}
        };

        // Check if the guy is in a hole
        return GameSenseHoleUtil.isHole(EntityUtil.getPosition(aimTarget), true, true).getType() != GameSenseHoleUtil.HoleType.NONE;
    }

    // Make some checks for startup
    private void playerChecks() {
        // Get all the materials
        if (getMaterialsSlot()) {
            // check if the enemy is in a hole
            if (is_in_hole()) {
                // Get enemy coordinates
                enemyCoordsDouble = new double[]{aimTarget.posX, aimTarget.posY, aimTarget.posZ};
                enemyCoordsInt = new int[]{(int) enemyCoordsDouble[0], (int) enemyCoordsDouble[1], (int) enemyCoordsDouble[2]};
                // Start choosing where to place what
                enoughSpace = createStructure();
                // Is not in a hoke
            } else {
                isHole = false;
            }
            // No materials
        } else noMaterials = true;
    }

    private void initValues() {
        preRotationBol = false;
        afterRotationTick = preRotationTick = 0;
        isPossible = false;
        // Reset aimtarget
        aimTarget = null;
        lastHitVec = null;
        // Create new delay table
        delayTable = new int[]{
                (int) supDelay.getValDouble(),
                (int) crystalDelay.getValDouble(),
                (int) hitDelay.getValDouble(),
                (int) endDelay.getValDouble()
        };
        // Default values reset
        toPlace = new structureTemp(0, 0, new ArrayList<>());
        isHole = isActive = true;
        hasMoved = rotationPlayerMoved = deadPl = broken = false;
        slot_mat = new int[]{-1, -1, -1, -1};
        stage = delayTimeTicks = 0;

        if (mc.player == null) {
            super.onDisable();
            return;
        }

        oldSlot = mc.player.inventory.currentItem;

        stoppedCa = false;

        cur_item = -1;

        if (AutoCrystalBypass.instance.isToggled()) {
            AutoCrystalBypass.instance.setToggled(false);
            stoppedCa = true;
        }

        forceBrk = forceBreak.getValBoolean();

    }

    public void destroyCrystalAlgo() {
        isPossible = false;
        // Get the crystal
        Entity crystal = getCrystal();
        // If we have confirmBreak, we have found 0 crystal and we broke a crystal before
        if (confirmBreak.getValBoolean() && broken && crystal == null) {
            /// That means the crystal was broken 100%
            // Reset
            stage = 1;
            broken = false;

        }
        // If found the crystal
        if (crystal != null) {
            // Break it
            breakCrystalPiston(crystal);
            // If we have to check
            if (confirmBreak.getValBoolean())
                broken = true;
                // If not, left
            else {
                stage = 1;
            }
        } else stage = 1;
    }

    private Entity getCrystal() {
        // Check if the crystal exist
        for (Entity t : mc.world.loadedEntityList) {
            // If it's a crystal
            if (t instanceof EntityEnderCrystal) {
                /// Check if the crystal is in the enemy
                // One coordinate is going to be always the same, the other is going to change (because we are pushing it)
                // We have to check if that coordinate is the same as the enemy. Ww add "crystalDeltaBreak" so we can break the crystal before
                // It go to the hole, for a better speed (we find the frame perfect for every servers)
                if ((int) t.posX == enemyCoordsInt[0] && (int) t.posZ == enemyCoordsInt[2] && t.posY - enemyCoordsInt[1] == 3)
                    // If found, yoink
                    return t;
            }
        }
        return null;
    }

    private void breakCrystalPiston(Entity crystal) {
        // HitDelay
        if (hitTryTick++ < midHitDelay.getValDouble())
            return;
        else
            hitTryTick = 0;
        // If weaknes
        if (antiWeakness.getValBoolean())
            mc.player.inventory.currentItem = slot_mat[3];
        /// Break type
        // Swing
        Vec3d vecCrystal = crystal.getPositionVector().add(new Vec3d(0.5, 0.5, 0.5));;

        // If it's not none, then allow the rotation
        if (!breakCrystal.getValString().equalsIgnoreCase("None")) {
            if (rotate.getValBoolean()) {
                // Look at that packet
                ROTATION_UTIL.lookAtPacket(vecCrystal.x, vecCrystal.y, vecCrystal.z, mc.player);
                // If force rotation, lets start straight looking into it
                if (forceRotation.getValBoolean())
                    lastHitVec = vecCrystal;
            }
        }
        try {
            switch (breakCrystal.getValString()) {
                case "Vanilla":
                    CrystalUtil.breakCrystal(crystal);
                    // Packet
                    break;
                case "Packet":
                    CrystalUtil.breakCrystalPacket(crystal);
                    break;
                case "None":

                    break;
            }
        } catch (NullPointerException e) {
            // For some reasons, sometimes it gives a nullPointerException because, the crystal get broken before (?) I dunno
            // This is for preventing a crash
        }
        // Rotate
        if (rotate.getValBoolean())
            ROTATION_UTIL.resetRotation();
    }
}
