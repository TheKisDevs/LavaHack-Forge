package com.kisman.cc.module.render

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.module.combat.autorer.util.mask.EnumFacingMask
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.BoxRenderModes
import com.kisman.cc.util.render.objects.Box
import com.kisman.cc.util.render.objects.BoxObject
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class BlockHighlight : Module("BlockHighlight", "Highlights object you are looking at", Category.RENDER) {
    private val mode = Setting("Mode", this, BoxRenderModes.Filled)
    private val entities = Setting("Entities", this, false)
    private val hitSideOnly = Setting("Hit Side Only", this, false)
    private val depth = Setting("Depth", this, false)
    private val color = Setting("Color", this, "Color", Colour(255, 255, 255))
    private val width = Setting("Width", this, 2.0, 0.25, 5.0, false)
    private val offset = Setting("Offset", this, 0.002, 0.002, 0.2, false)

    init {
        setmgr.rSetting(mode)
        setmgr.rSetting(entities)
        setmgr.rSetting(hitSideOnly)
        setmgr.rSetting(depth)
        setmgr.rSetting(color)
        setmgr.rSetting(width)
        setmgr.rSetting(offset)
    }

    @SubscribeEvent fun onRenderWorld(event: RenderWorldLastEvent) {
        super.setDisplayInfo("[${mode.valString}]")
        if (mc.objectMouseOver == null) return
        val hitObject = mc.objectMouseOver
        var box: Box? = null

        when (hitObject.typeOfHit) {
            RayTraceResult.Type.ENTITY -> {
                if (entities.valBoolean) {
                    val viewEntity = mc.renderViewEntity ?: mc.player
                    val eyePos = viewEntity.getPositionEyes(event.partialTicks)
                    val entity = hitObject.entityHit ?: return
                    val lookVec = viewEntity.lookVec
                    val sightEnd = eyePos.add(lookVec.scale(6.0))
                    val hitSide = entity.entityBoundingBox.calculateIntercept(eyePos, sightEnd)?.sideHit ?: return
                    box = Box.byAABB(hitObject.entityHit.entityBoundingBox)
                    if (hitSideOnly.valBoolean) box = Box.byAABB(EnumFacingMask.toAABB(box.toAABB(), hitSide))
                }
            }
            RayTraceResult.Type.BLOCK -> {
                box = Box.byAABB(
                    mc.world.getBlockState(hitObject.blockPos).getSelectedBoundingBox(mc.world, hitObject.blockPos)
                        .grow(offset.valDouble)
                )
                if (hitSideOnly.valBoolean) box = Box.byAABB(EnumFacingMask.toAABB(box.toAABB(), hitObject.sideHit))
            }
            else -> return
        }

        if (box == null) return

        BoxObject(
            box,
            color.colour,
            mode.valEnum as BoxRenderModes,
            width.valFloat,
            depth.valBoolean
        ).draw(event.partialTicks)
    }
}