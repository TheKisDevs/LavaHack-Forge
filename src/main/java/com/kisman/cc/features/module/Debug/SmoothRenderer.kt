package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.features.module.combat.autorer.PlaceInfo
import com.kisman.cc.features.module.combat.autorer.util.Easing
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.math.vectors.Vec3dColored
import com.kisman.cc.util.math.vectors.VectorUtils
import com.kisman.cc.util.render.RenderUtil3
import com.kisman.cc.util.render.objects.world.Abstract3dObject
import com.kisman.cc.util.render.objects.world.Object3d
import com.kisman.cc.util.render.objects.world.Vectors
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 19:19 of 07.07.2022
 */
class SmoothRenderer : Module(
    "SmoothRenderer",
    "idk",
    Category.DEBUG
) {
    private val movingLength = register((Setting("Moving Length", this, 400.0, 0.0, 1000.0, NumberType.TIME)))
    private val fadeLength = register((Setting("Fade Length", this, 200.0, 0.0, 1000.0, NumberType.TIME)))
    private val depth = register(Setting("Depth", this, false))
    private val alpha = register(Setting("Alpha", this, true))

    private val renderer = Renderer()

    override fun onEnable() {
        super.onEnable()
        renderer.reset()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        if(mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) return

        renderer.onRenderWorld(
            movingLength.valFloat,
            fadeLength.valFloat,
            PlaceInfo(mc.objectMouseOver.blockPos, Vectors.byAABB(mc.world.getBlockState(mc.objectMouseOver.blockPos).getSelectedBoundingBox(mc.world, mc.objectMouseOver.blockPos), Colour(255, 255, 255, 150))),
            depth.valBoolean,
            alpha.valBoolean
        )
    }

    class PlaceInfo(
        val blockPos : BlockPos?,
        val vectors : Vectors?
    )

    class Renderer : Object3d() {
        @JvmField
        var lastPlaceInfo: PlaceInfo? = null

        @JvmField
        var prevPlaceInfo: PlaceInfo? = null

        @JvmField
        var currentPlaceInfo: PlaceInfo? = null

        @JvmField
        var lastRenderPos: BlockPos? = null

        @JvmField
        var lastUpdateTime = 0L

        @JvmField
        var startTime = 0L

        @JvmField
        var scale = 0.0f

        @JvmField
        var lastSelfDamage = 0.0f

        @JvmField
        var lastTargetDamage = 0.0f

        fun reset() {
            lastPlaceInfo = null
            prevPlaceInfo = null
            currentPlaceInfo = null
            lastRenderPos = null
            lastUpdateTime = 0L
            startTime = 0L
            scale = 0.0f
            lastSelfDamage = 0.0f
            lastTargetDamage = 0.0f
        }

        fun onRenderWorld(
            movingLength: Float,
            fadeLength: Float,
            placeInfo : PlaceInfo,
            depth : Boolean,
            alpha : Boolean
        ) {
            update(placeInfo)

            prevPlaceInfo?.let { prevPos ->
                currentPlaceInfo?.let { currentPos ->
                    val multiplier = Easing.OUT_QUART.inc(Easing.toDelta(lastUpdateTime, movingLength))
                    val renderPos = if(prevPos.vectors != null && currentPos.vectors != null)
                        prevPos.vectors.transform(
                            { vec1/*prevPos*/, vec2/*currentPos*/ ->
                                Vec3dColored(
                                    vec1.vec.add(
                                        VectorUtils.subtract(vec2.vec, vec1.vec)
                                    ).scale(multiplier.toDouble()),
                                    vec2.color
                                )
                            },
                            currentPos.vectors
                        )

//                        Vectors(
//                        Vec3dColored(prevPos.vectors.vectors[0].vec.add(VectorUtils.subtract(currentPos.vectors.vectors[0].vec, prevPos.vectors.vectors[0].vec).scale(multiplier.toDouble())), Colour(255, 255, 255, 150)),
//                        Vec3dColored(prevPos.vectors.vectors[1].vec.add(VectorUtils.subtract(currentPos.vectors.vectors[1].vec, prevPos.vectors.vectors[1].vec).scale(multiplier.toDouble())), Colour(255, 255, 255, 150)),
//                        Vec3dColored(prevPos.vectors.vectors[2].vec.add(VectorUtils.subtract(currentPos.vectors.vectors[2].vec, prevPos.vectors.vectors[2].vec).scale(multiplier.toDouble())), Colour(255, 255, 255, 150)),
//                        Vec3dColored(prevPos.vectors.vectors[3].vec.add(VectorUtils.subtract(currentPos.vectors.vectors[3].vec, prevPos.vectors.vectors[3].vec).scale(multiplier.toDouble())), Colour(255, 255, 255, 150)),
//                        Vec3dColored(prevPos.vectors.vectors[4].vec.add(VectorUtils.subtract(currentPos.vectors.vectors[4].vec, prevPos.vectors.vectors[4].vec).scale(multiplier.toDouble())), Colour(255, 255, 255, 150)),
//                        Vec3dColored(prevPos.vectors.vectors[5].vec.add(VectorUtils.subtract(currentPos.vectors.vectors[5].vec, prevPos.vectors.vectors[5].vec).scale(multiplier.toDouble())), Colour(255, 255, 255, 150))
//                    )
                    else null

                    scale = if (placeInfo.blockPos != null) {
                        Easing.OUT_CUBIC.inc(Easing.toDelta(startTime, fadeLength))
                    } else {
                        Easing.IN_CUBIC.dec(Easing.toDelta(startTime, fadeLength))
                    }

                    if(renderPos != null) {
                        GlStateManager.pushMatrix()
                        prepare(depth,alpha)


                        RenderUtil3.drawBox(renderPos, 150)
                        RenderUtil3.drawBoundingBox(renderPos, 1.0, 150)

                        GlStateManager.color(1F, 1F, 1F, 1F);

                        release(alpha)
                        GlStateManager.popMatrix()
                    }

                    lastRenderPos = placeInfo.blockPos
                }
            }
        }

        fun update(placeInfo: PlaceInfo?) {
            val newBlockPos = placeInfo?.blockPos
            if (newBlockPos != lastRenderPos) {
                prevPlaceInfo = lastPlaceInfo ?: currentPlaceInfo
                lastUpdateTime = System.currentTimeMillis()
                if (lastPlaceInfo == null) startTime = System.currentTimeMillis()

                lastRenderPos = newBlockPos
            }
        }
    }
}