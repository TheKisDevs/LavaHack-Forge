package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.features.module.render.blockhighlight.BlockHighlightRenderer
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.MovableRendererPattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.enums.BoxRenderModes
import com.kisman.cc.util.render.RenderUtil3.toAABB
import com.kisman.cc.util.render.objects.world.Box
import com.kisman.cc.util.render.objects.world.BoxObject
import com.kisman.cc.util.render.objects.world.TextOnBlockObject
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class BlockHighlight : Module("BlockHighlight", "Highlights object you are looking at", Category.RENDER) {
    private val mode = register(Setting("Mode", this, BoxRenderModes.Filled))
    private val entities = register(Setting("Entities", this, false))
    private val hitSideOnly = register(Setting("Hit Side Only", this, false))
    private val depth = register(Setting("Depth", this, false))
    private val alpha = register(Setting("Alpha", this, true))
    private val color = register(Setting("Color", this, "Color", Colour(255, 255, 255)))
    private val width = register(Setting("Width", this, 2.0, 0.25, 5.0, false).setVisible { !mode.valEnum.equals(BoxRenderModes.Filled) })
    private val offset = register(Setting("Offset", this, 0.002, 0.002, 0.2, false))

    //Crystal info
    private val ciGroup = register(SettingGroup(Setting("Crystal Info", this)))
    private val crystalInfo = register(ciGroup.add(Setting("Crystal Info", this, false)))
    private val crystalInfoColor = register(ciGroup.add(Setting("Crystal Info Color", this, "Crystal Info Color", Colour(255, 255, 255, 255)).setVisible { crystalInfo.valBoolean }))
    private val crystalInfoTerrain = register(ciGroup.add(Setting("Crystal Info Terrain", this, false).setVisible { crystalInfo.valBoolean }))
    private val crystalInfoTargetRange = register(ciGroup.add(Setting("Crystal Info Target Range", this, 15.0, 0.0, 20.0, true).setVisible { crystalInfo.valBoolean }))
    
    //Advanced renderer
    private val adGroup = register(SettingGroup(Setting("Advanced Renderer", this)))
    private val advancedRenderer = register(adGroup.add(Setting("Advanced Renderer", this, false)))
    private val movable = MovableRendererPattern(this).group(adGroup).preInit().init()

    private val renderer = BlockHighlightRenderer()

    companion object {
        var instance : BlockHighlight? = null
    }

    init {
        super.setDisplayInfo { "[${mode.valString}]" }

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
                    if (hitSideOnly.valBoolean && !advancedRenderer.valBoolean) box = Box.byAABB(toAABB(box.toAABB(), facing))
                }
            }
            RayTraceResult.Type.BLOCK -> {
                box = Box.byAABB(mc.world.getBlockState(hitObject.blockPos).getSelectedBoundingBox(mc.world, hitObject.blockPos).grow(offset.valDouble))
                if (hitSideOnly.valBoolean) {
                    facing = hitObject.sideHit
                    if(!advancedRenderer.valBoolean) box = Box.byAABB(toAABB(box.toAABB(), facing))
                }
                bb = box.toAABB()
            }
            else -> {
                shouldReturn = true
                bb = null
            }
        }

        if (box == null || (bb == null && !shouldReturn)) return

        if(advancedRenderer.valBoolean) {
            renderer.onRenderWorld(
                movable.movingLength.valFloat,
                movable.fadeLength.valFloat,
                bb,
                color.colour,
                mode.valEnum as BoxRenderModes,
                width.valFloat,
                depth.valBoolean,
                alpha.valBoolean,
                event.partialTicks,
                offset.valDouble,
                facing
            )

            if(shouldReturn) {
                return
            }
        } else {
            BoxObject(
                box,
                color.colour,
                mode.valEnum as BoxRenderModes,
                width.valFloat,
                depth.valBoolean,
                alpha.valBoolean
            ).draw(event.partialTicks)
        }

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
}