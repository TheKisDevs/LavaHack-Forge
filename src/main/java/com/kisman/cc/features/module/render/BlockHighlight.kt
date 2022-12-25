package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.render.objects.world.Box
import com.kisman.cc.util.render.objects.world.TextOnBlockObject
import com.kisman.cc.util.render.pattern.SlideRendererPattern
import com.kisman.cc.util.toAABB
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.max
import kotlin.math.min

@Suppress("PrivatePropertyName")
class BlockHighlight : Module("BlockHighlight", "Highlights object you are looking at", Category.RENDER) {
    private val entities = register(Setting("Entities", this, false))
    private val hitSideOnly = register(Setting("Hit Side Only", this, false))

    private val renderer_ = SlideRenderingRewritePattern(this).preInit().init()

    //Crystal info
    private val ciGroup = register(SettingGroup(Setting("Crystal Info", this)))
    private val crystalInfo = register(ciGroup.add(Setting("Crystal Info", this, false)))
    private val crystalInfoColor = register(ciGroup.add(Setting("Crystal Info Color", this, "Crystal Info Color", Colour(255, 255, 255, 255)).setVisible { crystalInfo.valBoolean }))
    private val crystalInfoTerrain = register(ciGroup.add(Setting("Crystal Info Terrain", this, false).setVisible { crystalInfo.valBoolean }))
    private val crystalInfoTargetRange = register(ciGroup.add(Setting("Crystal Info Target Range", this, 15.0, 0.0, 20.0, true).setVisible { crystalInfo.valBoolean }))
    
    private val renderer : IRenderer = object : SlideRendererPattern(), IRenderer {
        private var vec : Vec3d? = null
        private var lastVec : Vec3d? = null
        private var lastBB: AxisAlignedBB? = null
        private var bb : AxisAlignedBB? = null
        private var facing : EnumFacing? = null

        override fun reset() {
            super.reset()
            vec = null
            lastVec = null
            lastBB = null
            bb = null
            facing = null
        }

        override fun onRenderWorld(
            bb : AxisAlignedBB,
            facing : EnumFacing?,
            renderer : SlideRenderingRewritePattern
        ) {
            update(bb)
            this.facing = facing
            renderWorld(
                renderer.movingLength.valFloat,
                renderer.fadeLength.valFloat,
                renderer.alphaFadeLength.valFloat,
                renderer,
                null
            )
        }

        override  fun toRenderBox(
            vec3d : Vec3d,
            scale : Double
        ) : AxisAlignedBB {
            val bb = this.bb ?: lastBB!!

            val halfSizeX = getSizeCoefficient(bb, 1) * scale
            val halfSizeY = getSizeCoefficient(bb, 2) * scale
            val halfSizeZ = getSizeCoefficient(bb, 3) * scale

            var modifiedBB = AxisAlignedBB(
                vec3d.x - halfSizeX + getSizeCoefficient(bb, 1), vec3d.y - halfSizeY + getSizeCoefficient(bb, 2), vec3d.z - halfSizeZ + getSizeCoefficient(bb, 3),
                vec3d.x + halfSizeX + getSizeCoefficient(bb, 1), vec3d.y + halfSizeY + getSizeCoefficient(bb, 2), vec3d.z + halfSizeZ + getSizeCoefficient(bb, 3)
            )

            if(facing != null) {
                modifiedBB = toAABB(modifiedBB, facing!!)
            }

            return modifiedBB
        }

        private fun getVec(bb_ : AxisAlignedBB?) : Vec3d {
            val bb = bb_ ?: return Vec3d(0.0, 0.0, 0.0)
            return Vec3d(min(bb.minX, bb.maxX), min(bb.minY, bb.maxY), min(bb.minZ, bb.maxZ))
        }

        private fun getSizeCoefficient(bb : AxisAlignedBB?, axis : Int) : Double {
            return when(axis) {
                1 -> (max(bb?.maxX!!, bb.minX) - min(bb.minX, bb.maxX)) / 2 // X
                2 -> (max(bb?.maxY!!, bb.minY) - min(bb.minY, bb.maxY)) / 2 // Y
                3 -> (max(bb?.maxZ!!, bb.minZ) - min(bb.minZ, bb.maxZ)) / 2 // Z
                else -> 0.5
            }
        }

        private fun update(bb : AxisAlignedBB?) {
            this.bb = bb
            vec = getVec(bb)
            if (vec != lastVec) {
                currentPos = vec
                prevPos = lastRenderPos ?: currentPos
                lastUpdateTime = System.currentTimeMillis()
                if (lastBB == null) {
                    startTime = System.currentTimeMillis()
                }

                lastBB = bb
                lastVec = vec
            }
        }
    }

    companion object {
        var instance : BlockHighlight? = null
    }

    init {
        instance = this
    }

    override fun onEnable() {
        super.onEnable()
        renderer.reset()
    }

    @SubscribeEvent fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        if (mc.objectMouseOver == null) return
        val hitObject = mc.objectMouseOver
        var box: Box? = null
        var bb : AxisAlignedBB? = null
        var shouldReturn = false

        var facing : EnumFacing? = null

        when (hitObject.typeOfHit) {
            RayTraceResult.Type.ENTITY -> {
                if (entities.valBoolean) {
                    val viewEntity = mc.renderViewEntity ?: mc.player
                    val eyePos = viewEntity.getPositionEyes(event.partialTicks)
                    val entity = hitObject.entityHit ?: return
                    val lookVec = viewEntity.lookVec
                    val sightEnd = eyePos.add(lookVec.scale(6.0))
                    facing = entity.entityBoundingBox.calculateIntercept(eyePos, sightEnd)?.sideHit ?: return
                    box = Box.byAABB(entity.entityBoundingBox.also { bb = it })
                }
            }
            RayTraceResult.Type.BLOCK -> {
                box = Box.byAABB(mc.world.getBlockState(hitObject.blockPos).getSelectedBoundingBox(mc.world, hitObject.blockPos))
                facing = hitObject.sideHit
                bb = box.toAABB()
            }
            else -> {
                shouldReturn = true
                bb = null
            }
        }

        if (box == null || bb == null) return

        renderer.onRenderWorld(
            bb!!,
            if(hitSideOnly.valBoolean) facing else null,
            renderer_
        )

        if(crystalInfo.valBoolean && hitObject.typeOfHit == RayTraceResult.Type.BLOCK) {
            val target = EntityUtil.getTarget(crystalInfoTargetRange.valFloat)
            val text = "${
                String.format("%.1f", AutoRerUtil.getSelfDamageByCrystal(crystalInfoTerrain.valBoolean, hitObject.blockPos))
            }/${
                if(target != null) String.format("%.1f", AutoRerUtil.getDamageByCrystal(target, crystalInfoTerrain.valBoolean, hitObject.blockPos))
                else "0.0"
            }"

            TextOnBlockObject(
                    text,
                    hitObject.blockPos,
                    crystalInfoColor.colour
            ).draw(event.partialTicks)
        }
    }

    private interface IRenderer {
        fun onRenderWorld(
            bb : AxisAlignedBB,
            facing : EnumFacing?,
            renderer : SlideRenderingRewritePattern
        )

        fun reset()
    }
}