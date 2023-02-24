package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.MultiThreaddableModulePattern
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.state
import com.kisman.cc.util.math.distance
import com.kisman.cc.util.world.CrystalUtils
import com.kisman.cc.util.world.sphere
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Explosion
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 22:44 of 09.08.2022
 */
@Suppress("LocalVariableName")
class CrystalSafeBlocks : Module(
    "CrystalSafeBlocks",
    "Shows safe/unsafe blocks by nearest crystals with different colors.",
    Category.RENDER
) {
    private val range = register(Setting("Range", this, 20.0, 1.0, 30.0, true))
    private val terrain = register(Setting("Terrain", this, false))

    private val safeGroup = register(SettingGroup(Setting("Safe", this)))
    private val safeRenderGroup = register(safeGroup.add(SettingGroup(Setting("Render", this))))
    private val safeRenderer = RenderingRewritePattern(this).group(safeRenderGroup).prefix("Safe").preInit().init()
    private val safeDown = register(safeGroup.add(Setting("Safe Down", this, false).setTitle("Down")))

    private val unsafeGroup = register(SettingGroup(Setting("Unsafe", this)))
    private val unsafeRenderGroup = register(unsafeGroup.add(SettingGroup(Setting("Render", this))))
    private val unsafeRenderer = RenderingRewritePattern(this).group(unsafeRenderGroup).prefix("Unsafe").preInit().init()
    private val unsafeDown = register(unsafeGroup.add(Setting("Unsafe Down", this, false).setTitle("Down")))

    private val threads = MultiThreaddableModulePattern(this).init()

    private val map = HashMap<BlockPos, Boolean>()

    override fun onEnable() {
        super.onEnable()
        threads.reset()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        threads.update(Runnable { doIt() })
    }

    private fun doIt() {
        map.clear()

        for(entity in mc.world.loadedEntityList) {
            if(entity is EntityEnderCrystal) {
                for(pos in sphere(entity, 12)) {
                    if(valid(pos)) {
                        val damage = calculate(
                            entity.posX,
                            entity.posY,
                            entity.posZ,
                            pos.x + 0.5,
                            pos.y.toDouble(),
                            pos.z + 0.5,
                            0,
                            terrain.valBoolean
                        )

                        map[pos] = damage < mc.player.health + mc.player.absorptionAmount
                    }
                }
            }
        }

        for(pos in map.keys) {
            mc.addScheduledTask {
                if(map[pos] == true && safeRenderer.isActive()) {
                    safeRenderer.draw(if(safeDown.valBoolean) pos.down() else pos)
                } else if(unsafeRenderer.isActive()) {
                    unsafeRenderer.draw(if(unsafeDown.valBoolean) pos.down() else pos)
                }
            }
        }
    }

    private fun valid(pos : BlockPos) : Boolean {
        return state(pos).block == Blocks.AIR
                && state(pos.down()).block != Blocks.AIR
                && mc.player.getDistance(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5) <= range.valInt
    }

    fun calculate(
        posX : Double, posY : Double, posZ:  Double,
        posXEntity : Double, posYEntity : Double, posZEntity : Double,
        interlopedAmount : Int,
        terrain : Boolean
    ) : Float {
        val doubleExplosionSize = 12.0f
        var dist = distance(posX, posY, posZ, posXEntity, posYEntity, posZEntity)
        if (dist > doubleExplosionSize) return 0f
        if (interlopedAmount > 0) {
            val l_Interloped = EntityUtil.getInterpolatedAmount(mc.player, interlopedAmount.toDouble())
            dist = EntityUtil.getDistance(l_Interloped.x, l_Interloped.y, l_Interloped.z, posX, posY, posZ)
        }
        val distancedsize = dist / doubleExplosionSize.toDouble()
        val vec3d = Vec3d(posX, posY, posZ)
        var blockDensity = 0.0
        try {
            blockDensity = if (terrain) CrystalUtils.getBlockDensity(vec3d, mc.player.entityBoundingBox).toDouble() else mc.world.getBlockDensity(vec3d, mc.player.entityBoundingBox).toDouble()
        } catch (ignored : Exception) { }
        val v = (1.0 - distancedsize) * blockDensity
        val damage = ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0).toInt().toFloat()
        val finald = CrystalUtils.getBlastReduction(
            mc.player,
            CrystalUtils.getDamageMultiplied(mc.world, damage),
            Explosion(mc.world, null, posX, posY, posZ, 6f, false, true)
        ).toDouble()
        return finald.toFloat()
    }
}