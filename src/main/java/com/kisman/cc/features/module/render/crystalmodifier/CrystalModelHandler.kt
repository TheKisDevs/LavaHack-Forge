package com.kisman.cc.features.module.render.crystalmodifier

import com.kisman.cc.features.module.render.CrystalModifier
import com.kisman.cc.features.module.render.crystalmodifier.RubiksCrystalUtil.*
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.math.toRadians
import net.minecraft.client.model.ModelEnderCrystal
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.util.math.MathHelper
import org.lwjgl.util.vector.Quaternion
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author _kisman_
 * @since 13:02 of 27.07.2022
 */
class CrystalModelHandler(
    private val renderBase : Boolean
) : ModelEnderCrystal(0f, renderBase) {
    private val insideCube = ModelRenderer(this, "cube").setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val insideGlass = ModelRenderer(this, "glass").setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val outsideCube = ModelRenderer(this, "cube").setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val outsideGlass = ModelRenderer(this, "glass").setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val bottom = ModelRenderer(this, "base").setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12)

    override fun render(
        entity : Entity,
        limbSwing : Float,
        limbSwingAmount : Float,
        ageInTicks : Float,
        netHeadYaw : Float,
        headPitch : Float,
        scale : Float
    ) {
        if(CrystalModifier.instance.isToggled && entity is EntityEnderCrystal) {
            if(CrystalModifier.scaleTimes.containsKey(entity.entityId)) {

            }

            val ticks = mc.renderPartialTicks

            val defaultSpinSpeed = entity.innerRotation + ticks
            var defaultBounceSpeed = MathHelper.sin(defaultSpinSpeed * 0.2f) / 2.0f + 0.5f
            defaultBounceSpeed += defaultBounceSpeed * defaultBounceSpeed

            pushMatrix()
            scale(2.0f * getScaleX() * getScaleModifier(entity.entityId, 0), 2.0f * getScaleY() * getScaleModifier(entity.entityId, 0), 2.0f * getScaleZ() * getScaleModifier(entity.entityId, 0))
            translate(getTranslateX(), -0.5f + getTranslateY(), getTranslateZ())
            if (needToRenderBase()) bottom.render(scale)
            scale(getScaleX() * getScaleModifier(entity.entityId, 2), getScaleY() * getScaleModifier(entity.entityId, 2), getScaleZ() * getScaleModifier(entity.entityId, 2))
            rotate(defaultSpinSpeed * CrystalModifier.instance.outsideSpinSpeed.valFloat, 0.0f, 1.0f, 0.0f)
            translate(getTranslateX(), 0.8f + defaultBounceSpeed * CrystalModifier.instance.bounce.valFloat + getTranslateY(), getTranslateZ())
            rotate(60.0f, 0.7071f, 0.0f, 0.7071f)
            if (CrystalModifier.instance.outsideCube.valEnum != CrystalModifier.CubeModes.Off) drawCube(getOutsideBox()!!, 2, scale)
            scale(0.875f * getScaleX() * getScaleModifier(entity.entityId, 3), 0.875f * getScaleY() * getScaleModifier(entity.entityId, 3), 0.875f * getScaleZ() * getScaleModifier(entity.entityId, 3))
            rotate(60.0f, 0.7071f, 0.0f, 0.7071f)
            rotate(defaultSpinSpeed * CrystalModifier.instance.outsideSpinSpeed2.valFloat, 0.0f, 1.0f, 0.0f)
            if (CrystalModifier.instance.outsideCube2.valEnum != CrystalModifier.CubeModes.Off) drawCube(getOutsideBox2()!!, 3, scale)
            scale(0.875f * getScaleX() * getScaleModifier(entity.entityId, 1), 0.875f * getScaleY() * getScaleModifier(entity.entityId, 1), 0.875f * getScaleZ() * getScaleModifier(entity.entityId, 1))
            rotate(60.0f, 0.7071f, 0.0f, 0.7071f)
            rotate(defaultSpinSpeed * CrystalModifier.instance.insideSpinSpeed.valFloat, 0.0f, 1.0f, 0.0f)
            if (CrystalModifier.instance.insideCube.valEnum != CrystalModifier.CubeModes.Off) drawCube(getInsideBox()!!, 1, scale)
            popMatrix()

            if(CrystalModifier.scaleTimes.containsKey(entity.entityId)) {
                CrystalModifier.scaleTimes[entity.entityId]!![0]!!.add(CrystalModifier.instance.baseFadeOutDelay.valLong)
                CrystalModifier.scaleTimes[entity.entityId]!![1]!!.add(CrystalModifier.instance.insideFadeOutDelay.valLong)
                CrystalModifier.scaleTimes[entity.entityId]!![2]!!.add(CrystalModifier.instance.outsideFadeOutDelay.valLong)
                CrystalModifier.scaleTimes[entity.entityId]!![3]!!.add(CrystalModifier.instance.outsideFadeOutDelay2.valLong)
            }
        } else {
            super.render(
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scale
            )
        }
    }

    private fun getInsideBox() : ModelRenderer? = getRenderer(CrystalModifier.instance.insideCube.valEnum as CrystalModifier.CubeModes, CrystalModifier.instance.insideModel.valEnum as CrystalModifier.ModelModes)

    private fun getOutsideBox() : ModelRenderer? = getRenderer(CrystalModifier.instance.outsideCube.valEnum as CrystalModifier.CubeModes, CrystalModifier.instance.outsideModel.valEnum as CrystalModifier.ModelModes)

    private fun getOutsideBox2() : ModelRenderer? = getRenderer(CrystalModifier.instance.outsideCube2.valEnum as CrystalModifier.CubeModes, CrystalModifier.instance.outsideModel2.valEnum as CrystalModifier.ModelModes)


    /**
     * cubeID:
     *
     * 1 - inside cube
     *
     * 2 - first outside cube
     *
     * 3 - second outside cube
     */
    private fun drawCube(cube : ModelRenderer, cubeID : Int, scale : Float) {
        if(CrystalModifier.instance.rubiksCrystal.valBoolean && (if(cubeID == 2) CrystalModifier.instance.rubiksCrystalOutside.valBoolean else if(cubeID == 3) CrystalModifier.instance.rubiksCrystalOutside2.valBoolean else CrystalModifier.instance.rubiksCrystalInside.valBoolean)) {
            drawRubiksBox(cube, scale)
        } else {
            cube.render(scale)
        }
    }

    private fun drawRubiksBox(cube : ModelRenderer, scale : Float) {
        if (CrystalModifier.instance.rubiksCrystal.valBoolean) {
            scale(
                CrystalModifier.CUBELET_SCALE,
                CrystalModifier.CUBELET_SCALE,
                CrystalModifier.CUBELET_SCALE
            )
            val scaleNew = scale * (CrystalModifier.CUBELET_SCALE * 2).toFloat()

            val currentTime = System.currentTimeMillis()
            if (currentTime - CrystalModifier.ANIMATION_LENGTH > CrystalModifier.lastTime) {
                val currentSide = cubeSides[CrystalModifier.rotatingSide]
                val cubletsTemp = arrayOf(
                    cubeletStatus[currentSide[0]],
                    cubeletStatus[currentSide[1]],
                    cubeletStatus[currentSide[2]],
                    cubeletStatus[currentSide[3]],
                    cubeletStatus[currentSide[4]],
                    cubeletStatus[currentSide[5]],
                    cubeletStatus[currentSide[6]],
                    cubeletStatus[currentSide[7]],
                    cubeletStatus[currentSide[8]]
                )

                // rotation direction
                if (CrystalModifier.instance.rubiksCrystalRotationDirection.valEnum === CrystalModifier.RubiksCrystalRotationDirection.Left) {
                    cubeletStatus[currentSide[0]] = cubletsTemp[6]
                    cubeletStatus[currentSide[1]] = cubletsTemp[3]
                    cubeletStatus[currentSide[2]] = cubletsTemp[0]
                    cubeletStatus[currentSide[3]] = cubletsTemp[7]
                    cubeletStatus[currentSide[4]] = cubletsTemp[4]
                    cubeletStatus[currentSide[5]] = cubletsTemp[1]
                    cubeletStatus[currentSide[6]] = cubletsTemp[8]
                    cubeletStatus[currentSide[7]] = cubletsTemp[5]
                    cubeletStatus[currentSide[8]] = cubletsTemp[2]
                } else if (CrystalModifier.instance.rubiksCrystalRotationDirection.valEnum === CrystalModifier.RubiksCrystalRotationDirection.Right) {
                    cubeletStatus[currentSide[0]] = cubletsTemp[2]
                    cubeletStatus[currentSide[1]] = cubletsTemp[5]
                    cubeletStatus[currentSide[2]] = cubletsTemp[8]
                    cubeletStatus[currentSide[3]] = cubletsTemp[1]
                    cubeletStatus[currentSide[4]] = cubletsTemp[4]
                    cubeletStatus[currentSide[5]] = cubletsTemp[7]
                    cubeletStatus[currentSide[6]] = cubletsTemp[0]
                    cubeletStatus[currentSide[7]] = cubletsTemp[3]
                    cubeletStatus[currentSide[8]] = cubletsTemp[6]
                }
                val trans = cubeSideTransforms[CrystalModifier.rotatingSide]
                for (x in -1..1) for (y in -1..1) for (z in -1..1) if (x != 0 || y != 0 || z != 0) applyCubeletRotation(
                    x, y, z,
                    trans[0],
                    trans[1],
                    trans[2]
                )
                CrystalModifier.rotatingSide = ThreadLocalRandom.current().nextInt(0, 5 + 1)
                CrystalModifier.lastTime = currentTime
            }

            // Draw non-rotating cubes
            for (x in -1..1) for (y in -1..1) for (z in -1..1) if (x != 0 || y != 0 || z != 0) drawCubeletStatic(
                cube,
                scaleNew,
                x, y, z
            )


            // Draw rotating cubes
            val trans = cubeSideTransforms[CrystalModifier.rotatingSide]
            pushMatrix()
            translate(
                trans[0] * CrystalModifier.CUBELET_SCALE,
                trans[1] * CrystalModifier.CUBELET_SCALE,
                trans[2] * CrystalModifier.CUBELET_SCALE
            )
            val rotationAngle = toRadians(easeInOutCubic(((currentTime - CrystalModifier.lastTime).toFloat() / CrystalModifier.ANIMATION_LENGTH).toDouble()) * 90).toFloat()
            val xx = (trans[0] * sin((rotationAngle / 2).toDouble())).toFloat()
            val yy = (trans[1] * sin((rotationAngle / 2).toDouble())).toFloat()
            val zz = (trans[2] * sin((rotationAngle / 2).toDouble())).toFloat()
            val ww = cos((rotationAngle / 2).toDouble()).toFloat()
            val q = Quaternion(xx, yy, zz, ww)
            rotate(q)
            for (x in -1..1) for (y in -1..1) for (z in -1..1) if (x != 0 || y != 0 || z != 0) drawCubeletRotating(
                cube,
                scaleNew,
                x, y, z
            )
            popMatrix()
        } else cube.render(scale)
    }

    private fun getRenderer(tex : CrystalModifier.CubeModes, model : CrystalModifier.ModelModes) : ModelRenderer? {
        return when(tex) {
            CrystalModifier.CubeModes.In -> if(model == CrystalModifier.ModelModes.Cube) insideCube else insideGlass
            CrystalModifier.CubeModes.Out -> if(model == CrystalModifier.ModelModes.Cube) outsideCube else outsideGlass
            CrystalModifier.CubeModes.Off -> null
        }
    }

    private fun needToRenderBase() : Boolean = if (CrystalModifier.instance.base.valBoolean) CrystalModifier.instance.alwaysBase.valBoolean || renderBase else false

    private fun getTranslateX() : Double = if (CrystalModifier.instance.translate.valBoolean) CrystalModifier.instance.translateX.valDouble else 0.0
    private fun getTranslateY() : Double = if (CrystalModifier.instance.translate.valBoolean) CrystalModifier.instance.translateY.valDouble else 0.0
    private fun getTranslateZ() : Double = if (CrystalModifier.instance.translate.valBoolean) CrystalModifier.instance.translateZ.valDouble else 0.0

    private fun getScaleX() : Double = if (CrystalModifier.instance.scale.valBoolean) CrystalModifier.instance.scaleX.valDouble else 1.0
    private fun getScaleY() : Double = if (CrystalModifier.instance.scale.valBoolean) CrystalModifier.instance.scaleY.valDouble else 1.0
    private fun getScaleZ() : Double = if (CrystalModifier.instance.scale.valBoolean) CrystalModifier.instance.scaleZ.valDouble else 1.0

    private fun getScaleModifier(
            entityID : Int,
            cubeID : Int
    ) : Double = if(CrystalModifier.scaleTimes.containsKey(entityID)) {
        when(cubeID) {
            0 -> if(CrystalModifier.instance.baseFadeOutDelay.valInt == 0) 1.0 else CrystalModifier.scaleTimes[entityID]!![0]!!.current()
            1 -> if(CrystalModifier.instance.insideFadeOutDelay.valInt == 0) 1.0 else CrystalModifier.scaleTimes[entityID]!![1]!!.current()
            2 -> if(CrystalModifier.instance.outsideFadeOutDelay.valInt == 0) 1.0 else CrystalModifier.scaleTimes[entityID]!![2]!!.current()
            3 -> if(CrystalModifier.instance.outsideFadeOutDelay2.valInt == 0) 1.0 else CrystalModifier.scaleTimes[entityID]!![3]!!.current()
            else -> 1.0
        }
    } else {
        1.0
    }
}