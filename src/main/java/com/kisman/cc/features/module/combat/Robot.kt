package com.kisman.cc.features.module.combat

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.features.module.movement.MoveModifier
import com.kisman.cc.features.module.movement.Speed
import com.kisman.cc.features.subsystem.subsystems.HoleProcessor
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.enums.RobotHoleOperation
import com.kisman.cc.util.render.pattern.SlideRendererPattern
import com.kisman.cc.util.world.BlockUtil
import com.kisman.cc.util.world.Holes
import com.kisman.cc.util.world.entityPosition
import com.kisman.cc.util.world.playerPosition
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap
import kotlin.math.pow


/**
 * @author _kisman_
 * @since 11:10 of 04.12.2022
 */
@WorkInProgress
class Robot : Module(
    "Robot",
    "crystalpvp.cc goes dead",
    Category.COMBAT
) {
    private val crystalpvpccMode = register(Setting("CrystalPvPcc Mode", this, true).setTitle("crystalpvp.cc"))
    private val range = register(Setting("Range", this, 200.0, 1.0, 200.0, true))
    private val healthMovingTrigger = register(Setting("Health Moving Trigger", this, 17.5, 1.0, 37.0, false))
    private val dpsMovingTrigger = register(Setting("DPS Moving Trigger", this, 10.0, 0.0, 40.0, false))
    private val strafing = register(Setting("Strafing", this, false))
    private val renderers = register(SettingGroup(Setting("Renderers", this)))
    private val nextHoleRendererPattern = SlideRenderingRewritePattern(this).group(renderers).preInit().init()

    private val threads = threads()

    private val targets = TargetFinder(range.supplierDouble, threads)
    private val nextHoleRenderer = SlideRendererPattern()

    private var target : EntityPlayer? = null

    private var prevHealth = 0.0
    private var totalDamagePerSecond1 = 0.0
    private var prevPosX = 0.0
    private var prevPosZ = 0.0
    private var prevGapples = 0
    private var packets = 1
    private var targetObsidian = 11
    private var targetExpPercent = 0
    private var prevChorus = 0
    private var xt = 0
    private var zt = 0
    private var st = 0
    private var bowTicks = 0
    private var isEating = false
    private var isSafe = false
    private var isExping = false
    private var enabledFastExp = false
    private var handOnly = false
    private var hasMined = false
    private var enabledPacketMine = false
    private var enabledAura = false
    private var isMoving = false
    private var enabledStep = false
    private var enabledTrap = false
    private var value = false
    private var didChorus = false
    private var switchedToSword = false
    private var switchedPickaxe = false
    private var preGappled = false
    private var needsUnSneak = false
    private var needsOnGround = false
    private var dropped = false
    private var landed = false
    private var forcedHole = false
    private var cantStep = false
    private var mode = ""
    private var triggerMode = ""

    private val mineTimer = TimerUtils()
    private val announceTimer = TimerUtils()
    private val holeTimer = TimerUtils()

    private var minedPos : BlockPos? = null
    private var lastHole : BlockPos? = null
    private var nextHole : BlockPos? = null

    private val damagePerSecond = HashMap<Long, Double>()

    private var holeOperation : RobotHoleOperation? = null

    init {
        super.setDisplayInfo { "[${if(target == null) "no target no fun" else "${target!!.name} | Operation: ${holeOperation?.name ?: "nothing"}"}]" }
    }

    private fun reset() {
        prevHealth = (mc.player.health + mc.player.getAbsorptionAmount()).toDouble()
        totalDamagePerSecond1 = 0.0
        prevPosX = 0.0
        prevPosZ = 0.0
        prevGapples = mc.player.inventory.mainInventory.stream()
            .filter { it: ItemStack -> it.getItem() === Items.GOLDEN_APPLE }
            .mapToInt { it: ItemStack -> it.count }
            .sum() + if (mc.player.heldItemOffhand.getItem() === Items.GOLDEN_APPLE) mc.player.heldItemOffhand.count else 0
        isEating = false
        isSafe = false
        isExping = false
        enabledFastExp = false
        handOnly = false
        hasMined = false
        enabledPacketMine = false
        isMoving = false
        enabledStep = false
        enabledTrap = false
        didChorus = false
        switchedToSword = false
        switchedPickaxe = false
        preGappled = false
        needsUnSneak = false
        needsOnGround = false
        dropped = false
        landed = false
        forcedHole = false
        cantStep = false
        mode = ""
        triggerMode = ""
        damagePerSecond.clear()
        packets = 1
        targetObsidian = 11
        prevChorus = 0
        xt = 0
        zt = 0
        st = 0
        bowTicks = 0

        mineTimer.reset()
        announceTimer.reset()
        holeTimer.reset()

        minedPos = null
        lastHole = null
        nextHole = null

        targets.reset()

        toggleSurround()
    }

    override fun onEnable() {
        super.onEnable()

        Kisman.EVENT_BUS.subscribe(send)
        Kisman.EVENT_BUS.subscribe(receive)

        if(mc.player == null || mc.world == null) {
            isToggled = false
            return
        }

        if(!AutoRer.instance.isToggled) {
            AutoRer.instance.isToggled = true
        }

        reset()
    }

    override fun onDisable() {
        super.onDisable()

        Kisman.EVENT_BUS.unsubscribe(send)
        Kisman.EVENT_BUS.unsubscribe(receive)

        if(mc.player == null || mc.world == null) {
            return
        }

        if(AutoRer.instance.isToggled) {
            AutoRer.instance.isToggled = false
        }

        mc.gameSettings.keyBindUseItem.pressed = false
    }
    
    @SubscribeEvent
    fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        nextHoleRenderer.update(nextHole, nextHoleRendererPattern)
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

//        stopMoving()

        mc.player.rotationYaw = 180f
        mc.player.rotationPitch = 0f

        if(crystalpvpccMode.valBoolean) {
            if(mc.player.ticksExisted > 20 && mc.player.posY >= 122) {
                if(mc.player.heldItemOffhand.item == Items.AIR) {
                    mc.player.sendChatMessage("/kit bot")
                }

                if(mc.player.posZ > -5.4) {
                    mc.gameSettings.keyBindForward.pressed = true
                } else {
                    mc.gameSettings.keyBindForward.pressed = false

                    if(mc.player.collidedHorizontally) {
                        enableStep()
                    }

                    mc.gameSettings.keyBindRight.pressed = true

                    if(mc.world.getBlockState(mc.player.position.down()).block == Blocks.AIR) {
                        dropped = true
                    }
                }

                return
            } else if(dropped) {
                resetMovement()

                if(mc.player.onGround) {
                    landed = true
                }
            }
        } else {
            landed = true
        }

        if(landed) {
            if(mc.player.onGround) {
                center()
            }

            toggleSurround()

            landed = false
            dropped = false
        }

        if(needsUnSneak) {
            st++

            if(st >= 10) {
                mc.gameSettings.keyBindSneak.pressed = false
                needsUnSneak = false
            }
        }

        cantStep = SurroundRewrite.instance.isToggled && holeOperation != RobotHoleOperation.RunOut
        isSafe = isPlayerSafe(mc.player)

        val health = mc.player.health + mc.player.absorptionAmount

        target = targets.target

        handleDamage()

        if(isEating) {
            setUseItemUnpressedIfNeeded()
        }

        if(target == null) {
            resetMovement()
            return
        }

        if(mc.player.getDistance(target!!) > 7) {
            preGappled = false
        }

        handleEat()

        totalDamagePerSecond1 = damagePerSecond.values.stream().mapToDouble { it }.sum()

        if(isMoving) {
            if(!stepState() && !cantStep) {
                enableStep()
                enabledStep = true
            }
        } else {
            if(enabledStep) {
                disableStep()
                enabledStep = false
            }
        }

        val needsToGoIntoHole = (mc.player.getDistance(target!!) < 10 && !isPlayerSafe(target!!) && health < healthMovingTrigger.valDouble) || totalDamagePerSecond1 > dpsMovingTrigger.valDouble
        val isTowering = isTowering(target!!)

        if(isSafe) {
            forcedHole = false
            xt = 0
            zt = 0
            holeOperation = getHoleOperation(target!!)
            lastHole = mc.player.position
            performHoleOperation(holeOperation!!, target!!)
        } else {
            if(prevPosX != 0.0 && prevPosZ != 0.0) {
                var i = 0

                if(mc.player.posX == prevPosX) {
                    xt++
                    i++
                }

                if(mc.player.posZ == prevPosZ) {
                    zt++
                    i++
                }

                if(i == 2 && xt >= 5 && zt >= 5) {
                    toggleSurround() //maybe disableSurround()????
                    xt = 0
                    zt = 0
                }
            }

            holeTimer.reset()

            if(KillAuraRewrite.instance!!.isToggled && enabledAura) {
                disableAura()
                enabledAura = false
            }

            if(AutoTrap.instance.isToggled && enabledTrap) {
                disableTrap()
                enabledTrap = false
            }

            if(HoleFillerRewrite.instance.isToggled) {
                disableFiller()
            }

            needsOnGround = health <= healthMovingTrigger.valDouble

            if(needsOnGround) {
                //TODO: instant speed
            }

            val isFilled = nextHole != null && mc.world.getBlockState(nextHole!!).block != Blocks.AIR
            nextHole = getNextHole(target!!, false, 10.0)

            if(needsToGoIntoHole || forcedHole || isTowering) {
                val newNextHole = getNextHole(target!!, true, 10.0)

                if(newNextHole != null) {
                    nextHole = newNextHole
                    forcedHole = true
                }
            }

            if(nextHole != null) {
                if(isFilled || nextHole == mc.player.position) {
                    toggleSurround()
                    //TODO: toggleSurround()
                    resetMovement()

                } else {
                    moveToNextHole(nextHole!!)
                }
            }
        }

        prevPosX = mc.player.posX
        prevPosZ = mc.player.posZ
    }

    private fun setUseItemUnpressedIfNeeded() {
        val currentGapples = mc.player.inventory.mainInventory.stream().filter { it.item == Items.GOLDEN_APPLE }.mapToInt { it.count }.sum() + (if(mc.player.heldItemOffhand.item == Items.GOLDEN_APPLE) mc.player.heldItemOffhand.count else 0)
        val currentChorus = mc.player.inventory.mainInventory.stream().filter { it.item == Items.CHORUS_FRUIT }.mapToInt { it.count }.sum() + (if(mc.player.heldItemOffhand.item == Items.CHORUS_FRUIT) mc.player.heldItemOffhand.count else 0)

        if(currentGapples < prevGapples || currentChorus < prevChorus || (mc.player.heldItemMainhand.item != Items.GOLDEN_APPLE && mc.player.heldItemMainhand.item != Items.CHORUS_FRUIT)) {
            mc.gameSettings.keyBindUseItem.pressed = false
            isEating = false
        }
    }

    private fun moveToNextHole(
        pos1 : BlockPos
    ) {
        val pos = pos1.up()
        val excludeYPos = BlockPos(pos.x, playerPosition().y, pos.z)

        if(strafing.valBoolean) {
            //TODO: squared value < non squared value
            if(mc.player.getDistanceSq(excludeYPos) < 6) {
                //TODO: instant(ground) speed
            } else if(!needsOnGround) {
                Speed.instance.isToggled = true
                //TODO: speed usage
            }
        }

        val bb = AxisAlignedBB(pos).shrink(0.5)

        //TODO: squared value < non squared value
        if(mc.player.getDistanceSq(excludeYPos) < 4 && stepState()) {
            disableStep()
        }

        if(mc.player.posZ > bb.minZ + 0.125) {
            mc.gameSettings.keyBindForward.pressed = true
            isMoving = true
        } else {
            mc.gameSettings.keyBindForward.pressed = true
        }

        if(mc.player.posZ < bb.minZ - 0.125) {
            mc.gameSettings.keyBindBack.pressed = true
            isMoving = true
        } else {
            mc.gameSettings.keyBindBack.pressed = true
        }

        if(mc.player.posX > bb.minX + 0.125) {
            mc.gameSettings.keyBindLeft.pressed = true
            isMoving = true
        } else {
            mc.gameSettings.keyBindLeft.pressed = true
        }

        if(mc.player.posX < bb.minX - 0.125) {
            mc.gameSettings.keyBindRight.pressed = true
            isMoving = true
        } else {
            mc.gameSettings.keyBindRight.pressed = true
        }
    }

    //TODO: rewrite it cuz xprestige made really shitty code fr
    private fun canEnter(
        pos : BlockPos
    ) : Boolean = isAir(pos.up()) && isAir(pos.up(2)) && isAir(pos.up(3)) && isAir(pos.up(4))

    private fun isAir(
        pos : BlockPos
    ) : Boolean = mc.world.getBlockState(pos).block == Blocks.AIR

    private fun getNextHole(
        target : EntityPlayer,
        force : Boolean,
        forceRadius : Double
    ) : BlockPos? = HoleProcessor.holes.keys.stream().filter {
                canEnter(it)
                && lastHole != it
                && if(force) {
                    mc.player.getDistanceSq(it) < forceRadius.pow(2.0)
                } else {
                    target.getDistanceSq(it) > 4 && target.getDistanceSq(it) < 25
                }
    }.collect(Collectors.toMap( {
        if(force) {
            mc.player.getDistanceSq(it)
        } else {
            target.getDistanceSq(it)
        }
    }, {
        it
    }, {
            _,
            b ->
        b
    }, {
        TreeMap<Double, BlockPos>()
    })).values.firstOrNull()

    private fun performHoleOperation(
        operation : RobotHoleOperation,
        target : EntityPlayer
    ) {
        enableSurround()

        if(isExping && operation != RobotHoleOperation.Exp) {
            mc.gameSettings.keyBindUseItem.pressed = false
            isExping = false
        }

        if(operation != RobotHoleOperation.RunOut) {
            resetMovement()

            if(enabledStep) {
                disableStep()
            }

            if(mc.player.onGround) {
                center()
                isMoving = false
            }
        }

        if(operation != RobotHoleOperation.MineEchest) {
            targetObsidian = 11
        }

        if(operation != RobotHoleOperation.Exp) {
            targetExpPercent = 50
        }

        if(operation != RobotHoleOperation.Sword || mc.player.heldItemMainhand.item !is ItemSword) {
            switchedToSword = false
        }

        if(operation != RobotHoleOperation.Sword) {
            if(enabledTrap) {
                disableTrap()
                enabledTrap = false
            }

            if(enabledAura) {
                disableAura()
                enabledAura = false
            }
        }

        //TODO: нахуй это тут бля
        enableFiller()

        if(isBeingCevBreakered()) {
            //TODO: multi threading
            mc.world.loadedEntityList.stream().filter { it is EntityEnderCrystal && it.getDistanceSq(mc.player.position.up(3)) < 10 }.forEach(this::breakCrystal)
        }

        val pos = isBeingRussianed()

        if(pos != null) {
            mc.world.loadedEntityList.stream().filter { it is EntityEnderCrystal && it.getDistanceSq(pos) < 1 }.forEach(this::breakCrystal)
        }

        when(operation) {
            RobotHoleOperation.Exp -> {
                if(isEating) {
                    return
                }

                val slot = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9)

                if(slot != -1) {
                    InventoryUtil.switchToSlot(slot, false)

                    mc.gameSettings.keyBindUseItem.pressed = true
                    mc.player.rotationPitch = 90f
                    isExping = true
                    targetExpPercent = 80
                }
            }

            RobotHoleOperation.MineEchest -> {
                if(isEating) {
                    handleMineEchest()
                }
            }

            RobotHoleOperation.Sword -> {
                val slot = InventoryUtil.findWeaponSlot(0, 9, false)

                if(slot != -1 && !switchedToSword && !isEating) {
                    InventoryUtil.switchToSlot(slot, false)

                    if(!enabledAura) {
                        enableAura()
                        enabledAura = true
                    }

                    switchedToSword = true
                }

                if(isEnemyInSameHole(target) && !isTrapped(target) && !AutoTrap.instance.isToggled) {
                    enableTrap()
                    enabledTrap = true
                }
            }

            RobotHoleOperation.Quiver -> {
                doQuiver()
            }

            RobotHoleOperation.RunOut -> {
                if(holeTimer.passedMillis(500)) {
                    return
                }

                val health = mc.player.health + mc.player.absorptionAmount

                if(health >= 20) {
                    //TODO: on-ground speed

                    if(moveOutHole() && !(needsObsidian() || needsMending() || needsEffect(target))) {
                        moveOutHole()
                        isMoving = true
                    } else {
                        if(isEnemyInSameHole(target) && !AutoTrap.instance.isToggled) {
                            enableTrap()
                            enabledTrap = true
                        }

                        handleChorus()
                    }
                } else {
                    if(!isEating) {
                        doEat()
                    }
                }
            }

            RobotHoleOperation.CounterTower -> {
                handleTower()
            }
        }
    }

    private fun doQuiver() {
        //TODO: we need to finish quiver before adding it!!!
    }

    private fun handleTower() {
        //TODO: we need scaffoldtest3 usage here!!!!
    }

    private fun breakCrystal(
        crystal : Entity
    ) {
        //TODO: anti weakness

        mc.player.connection.sendPacket(CPacketUseEntity(crystal))
    }

    private fun handleEat() {
        val totalDamagePerSecond = damagePerSecond.values.stream().mapToDouble { it }.sum()
        val health = mc.player.health + mc.player.absorptionAmount

        if(!isEating && (!isPlayerSafe(mc.player) || (totalDamagePerSecond >= 10 || health < (if(isSafe) (if(isBeingCevBreakered()) 20 else 10) else 15)))) {
            doEat()
        }

    }

    private fun handleChorus() {
        if(!isEating) {
            val slot = InventoryUtil.findItem(Items.CHORUS_FRUIT, 0, 9)

            if(slot != -1) {
                mc.player.inventory.currentItem = slot
                mc.gameSettings.keyBindUseItem.pressed = true
                prevChorus = mc.player.inventory.mainInventory.stream().filter { it.item == Items.CHORUS_FRUIT }.mapToInt { it.count }.sum() + (if(mc.player.heldItemOffhand.item == Items.CHORUS_FRUIT) mc.player.heldItemOffhand.count else 0)
                isEating = true
            }
        }
    }

    private fun moveOutHole() : Boolean {
        val lastHole = playerPosition().up()

        fun valid(
            pos : BlockPos,
            facing : EnumFacing
        ) : Boolean = isAir(pos.up()) && isAir(pos.offset(facing).up())
                && ((isAir(pos.offset(facing)) && isAir(pos.offset(facing).up()))
                || (!isAir(pos.offset(facing)) && isAir(pos.offset(facing).up(2)) && isAir(pos.up(2))))

        if(canEnter(lastHole.down())) {
            valid(lastHole, EnumFacing.NORTH).also { if(it) { mc.gameSettings.keyBindForward.pressed = true ; return true } }
            valid(lastHole, EnumFacing.EAST).also { if(it) { mc.gameSettings.keyBindRight.pressed = true ; return true } }
            valid(lastHole, EnumFacing.WEST).also { if(it) { mc.gameSettings.keyBindLeft.pressed = true ; return true } }
            valid(lastHole, EnumFacing.SOUTH).also { if(it) { mc.gameSettings.keyBindBack.pressed = true ; return true } }
        }

        return false
    }

    private fun needsObsidian() : Boolean = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9) == -1

    private fun needsMending() : Boolean = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9) != -1 && mc.player.inventory.armorInventory.stream().filter { !it.isEmpty() }.mapToInt { 100 - ((1 - ((it.maxDamage - it.itemDamage) / it.maxDamage)) * 100) }.anyMatch { it < targetExpPercent }

    private fun needsEffect(
        player : EntityPlayer
    ) : Boolean = false //TODO: rewrite it

    private fun doEat() {
        val slot = InventoryUtil.findItem(Items.GOLDEN_APPLE, 0, 9)

        if(slot != -1) {
            mc.player.inventory.currentItem = slot
            mc.gameSettings.keyBindUseItem.pressed = true
            prevGapples = mc.player.inventory.mainInventory.stream().filter { it.item == Items.GOLDEN_APPLE }.mapToInt { it.count }.sum() + (if(mc.player.heldItemOffhand.item == Items.GOLDEN_APPLE) mc.player.heldItemOffhand.count else 0)
            isEating = true
        }
    }

    private fun placeEchest(
        pos : BlockPos
    ) {
        val slot = InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9)
        val oldSlot = mc.player.inventory.currentItem

        //TODO: move this check to usage of place() method
        if(slot != -1) {
//            InventoryUtil.switchToSlot(slot, false)
            mc.player.inventory.currentItem = slot
            BlockUtil.placeBlock(pos)
            mc.player.inventory.currentItem = oldSlot
        }
    }

    //TODO: rewrite it!!!!
    private fun handleMineEchest() {
        val pos = playerPosition().up()

        fun mine(
            pos : BlockPos
        ) : Boolean {
            fun shouldMine(
                pos : BlockPos
            ) : Boolean = !isAir(pos) && !isEchest(pos)

            shouldMine(pos.north()).also { if(it) { mineBlockForEchest(pos.north(), true) ; return true } }
            shouldMine(pos.east()).also { if(it) { mineBlockForEchest(pos.east(), true) ; return true } }
            shouldMine(pos.west()).also { if(it) { mineBlockForEchest(pos.west(), true) ; return true } }
            shouldMine(pos.south()).also { if(it) { mineBlockForEchest(pos.south(), true) ; return true } }

            return false
        }

        fun place(
            pos : BlockPos
        ) : Boolean {
            isAir(pos.north()).also { if(it) { placeEchest(pos.north()) ; return true } }
            isAir(pos.east()).also { if(it) { placeEchest(pos.east()) ; return true } }
            isAir(pos.west()).also { if(it) { placeEchest(pos.west()) ; return true } }
            isAir(pos.south()).also { if(it) { placeEchest(pos.south()) ; return true } }

            return false
        }

        if(!mine(pos)) {
            place(pos)
        }
    }

    private fun mineBlockForEchest(
        pos : BlockPos,
        obby : Boolean
    ) {
        fun findBlock(
            centre : BlockPos,
            obby : Boolean
        ) : BlockPos? {
            fun valid(
                pos : BlockPos,
                obby : Boolean
            ) : Boolean = if(obby) isObby(pos) else isEchest(pos)

            valid(centre.north(), obby).also { if(it) return centre.north() }
            valid(centre.west(), obby).also { if(it) return centre.west() }
            valid(centre.east(), obby).also { if(it) return centre.east() }
            valid(centre.south(), obby).also { if(it) return centre.south() }

            return null
        }

        val blockPos = findBlock(pos, obby)

        if(blockPos != null) {
            mineBlock(blockPos, obby)
        }
    }

    //TODO: really shit code by zprestige
    private fun mineBlock(
        pos : BlockPos,
        obby : Boolean
    ) {
        if(hasMined) {
            if(mineTimer.passedMillis(if(obby) 2000 else 1000)) {
                //TODO: pick switch is useless

                mc.playerController.onPlayerDamageBlock(minedPos!!, mc.player.horizontalFacing)
                //TODO: swing
                hasMined = false

            }
        } else {
            //TODO: usage of nearest facing
            //TODO: pick switch again

            mc.playerController.onPlayerDamageBlock(pos, mc.player.horizontalFacing)
            //TODO: swing
            minedPos = pos
            mineTimer.reset()
            targetObsidian = 48 //wtf is this field
            hasMined = true
        }
    }

    //i rewrote it
    private fun isTrapped(
        player : EntityPlayer
    ) : Boolean {
        val pos = entityPosition(player)

        return !isAir(pos.north()) && !isAir(pos.west()) && !isAir(pos.east()) && !isAir(pos.south()) && !isAir(pos.down()) && !isAir(pos.up(2))
    }

    private fun isBeingCevBreakered() : Boolean = mc.world.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB(playerPosition().up().up().up())).isNotEmpty()

    /*private fun isBeingRussianed() : EnumFacing? = if(!isPlayerSafe(mc.player)) {
        null
    } else {
        fun check(
            pos : BlockPos
        ) : Boolean = isObby(pos) && mc.world.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB(pos.up())).isEmpty()

        for(entry in mc.renderGlobal.damagedBlocks) {
            val pos = entry.value.position
            var facing : EnumFacing? = null

            if(
                check(pos.north()).also { if(it) facing = EnumFacing.NORTH }
                || check(pos.east()).also { if(it) facing = EnumFacing.EAST }
                || check(pos.south()).also { if(it) facing = EnumFacing.SOUTH }
                || check(pos.west()).also { if(it) facing = EnumFacing.WEST }
            ) {
                facing
            }
        }

        null
    }*/

    private fun isBeingRussianed() : BlockPos? = if(!isPlayerSafe(mc.player)) {
        null
    } else {
        fun check(
            pos : BlockPos
        ) : Boolean = isObby(pos) && mc.world.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB(pos.up())).isEmpty()

        for(entry in mc.renderGlobal.damagedBlocks) {
            val pos = entry.value.position

            check(pos.north()).also { if(it) return pos.north() }
            check(pos.east()).also { if(it) return pos.east() }
            check(pos.south()).also { if(it) return pos.south() }
            check(pos.west()).also { if(it) return pos.west() }
        }

        null
    }

    private fun getHoleOperation(
        target : EntityPlayer
    ) : RobotHoleOperation {
        val isEnemyInSameHole = isEnemyInSameHole(target)

        //TODO: we will uncomment it when i will make my own handleTower() method
        /*if(isTowering(target)) {
            return RoboPvPHoleOperation.CounterTower
        }*/

        if(!isEnemyInSameHole) {
            if(needsMending()) {
                return RobotHoleOperation.Exp
            }

            if(needsObsidian()) {//TODO: echest check
                return RobotHoleOperation.MineEchest
            }

            //TODO: i will uncomment it when someone will fix/finish quiver
            /*if(needsEffect(target)) {
                return RoboPvPHoleOperation.Quiver
            }*/
        } else {
            if(needsObsidian() || needsMending() || needsEffect(target)) {
                return RobotHoleOperation.RunOut
            }

            if(mc.player.getDistance(target) < mc.playerController.blockReachDistance) {
                return RobotHoleOperation.Sword
            }
        }

        if(isPlayerSafe(target) && mc.player.getDistance(target) < mc.playerController.blockReachDistance) {
            return RobotHoleOperation.Sword
        } else if(isPlayerSafe(target)) {
            return RobotHoleOperation.RunOut
        }

        if(!isPlayerSafe(target) && mc.player.getDistance(target) > mc.playerController.blockReachDistance) {
            return RobotHoleOperation.RunOut
        }

        if(!isPlayerSafe(target) && AutoRer.instance.placePos.blockPos == null) {
            if(mc.player.getDistance(target) < mc.playerController.blockReachDistance) {
                return RobotHoleOperation.Sword
            }
        }

        return RobotHoleOperation.Await
    }

    private fun handleDamage() {
        val health = mc.player.health + mc.player.absorptionAmount

        for(entry in damagePerSecond) {
            if(entry.key < System.currentTimeMillis()) {
                damagePerSecond.remove(entry.key)
                return
            }
        }

        if(health < prevHealth) {
            damagePerSecond[System.currentTimeMillis() + 1000L] = prevHealth - health
        }

        prevHealth = health.toDouble()
    }

    private fun center() {
        //TODO: from surround rewrite
    }

    private fun enableTrap() {
        AutoTrap.instance.isToggled = true
    }

    private fun disableTrap() {
        AutoTrap.instance.isToggled = false
    }

    private fun enableFiller() {
        HoleFillerRewrite.instance.isToggled = true
    }

    private fun disableFiller() {
        HoleFillerRewrite.instance.isToggled = false
    }

    private fun enableAura() {
        KillAuraRewrite.instance!!.isToggled = true
    }

    private fun disableAura() {
        KillAuraRewrite.instance!!.isToggled = false
    }

    private fun enableSurround() {
        SurroundRewrite.instance!!.isToggled = true
    }

    private fun disableSurround() {
        SurroundRewrite.instance!!.isToggled = false
    }

    private fun toggleSurround() {
        //TODO: instant speed
        mc.player.setVelocity(0.0, mc.player.motionY, 0.0)
        disableStep()
        if(mc.player.onGround && SurroundRewrite.instance.isToggled) {
            resetMovement()
            SurroundRewrite.instance.isToggled = true
        }
    }

    private fun stepState() : Boolean = MoveModifier.instance!!.stepVal.valBoolean

    private fun enableStep() {
        MoveModifier.instance!!.stepVal.valBoolean = true
    }

    private fun disableStep() {
        MoveModifier.instance!!.stepVal.valBoolean = false
    }

    private fun resetMovement() {
        mc.gameSettings.keyBindForward.pressed = false
        mc.gameSettings.keyBindBack.pressed = false
        mc.gameSettings.keyBindLeft.pressed = false
        mc.gameSettings.keyBindRight.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
        mc.gameSettings.keyBindJump.pressed = false
    }


    private fun isEnemyInSameHole(
        player : EntityPlayer
    ) : Boolean = isSafe && isPlayerSafe(player) && mc.player.getDistance(player) < 1f

    private fun isPlayerSafe(
        player : EntityPlayer
    ) : Boolean = Holes.getHole(player.position) != null

    private fun isTowering(
        player : EntityPlayer
    ) : Boolean = player.posY - mc.player.posY > 15

    private fun isObby(
        pos : BlockPos
    ) : Boolean = mc.world.getBlockState(pos).block == Blocks.OBSIDIAN

    private fun isEchest(
        pos : BlockPos
    ) : Boolean = mc.world.getBlockState(pos).block == Blocks.ENDER_CHEST

    private val send = Listener<PacketEvent.Send>(EventHook {
        val packet = it.packet

        try {
            if(packet is CPacketPlayerTryUseItemOnBlock) {
                if(mc.player.heldItemMainhand.item == Items.GOLDEN_APPLE || mc.player.heldItemMainhand.item == Items.CHORUS_FRUIT) {
                    if(mc.gameSettings.keyBindUseItem.isKeyDown && mc.world.getBlockState(packet.pos).block == Blocks.ENDER_CHEST) {
                        it.cancel()
                        mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                    }
                }
            }
        } catch(_ : Exception) { }
    })

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        if(it.packet is SPacketPlayerPosLook) {
            mc.gameSettings.keyBindSneak.pressed = true
            needsUnSneak = true
        }
    })
}