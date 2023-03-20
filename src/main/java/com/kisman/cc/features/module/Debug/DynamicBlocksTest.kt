package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
//import com.kisman.cc.util.Colour
//import com.kisman.cc.util.render.Rendering
//import com.kisman.cc.util.state
import com.kisman.cc.util.world.dynamicBlocks
import com.kisman.cc.util.world.playerPosition
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 13:03 of 20.03.2023
 */
@ModuleInfo(
    name = "DynamicBlocksTest",
    debug = true
)
class DynamicBlocksTest : Module() {
    private val debug = register(Setting("Debug", this, true))
    private val renderer1 = RenderingRewritePattern(this).group(register(SettingGroup(Setting("Renderer 1", this)))).preInit().init()
    private val renderer2 = RenderingRewritePattern(this).group(register(SettingGroup(Setting("Renderer 2", this)))).preInit().init()
    private val renderer3 = RenderingRewritePattern(this).group(register(SettingGroup(Setting("Renderer 3", this)))).preInit().init()
    private val renderer4 = RenderingRewritePattern(this).group(register(SettingGroup(Setting("Renderer 4", this)))).preInit().init()
    private val renderer5 = RenderingRewritePattern(this).group(register(SettingGroup(Setting("Renderer 5", this)))).preInit().init()

    @SubscribeEvent
    fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        val blocks = dynamicBlocks(mc.player)
        val playerPosition = playerPosition()

        fun processDynamicBlocks(
            posses : List<BlockPos>
        ) : Map<EnumFacing?, List<BlockPos?>> {
            val map = mutableMapOf<EnumFacing?, List<BlockPos?>>()

            for(pos in posses) {
                if(pos.x == playerPosition.x || pos.z == playerPosition.z) {
                    var pair : BlockPos? = null

                    pos.north().also { if(posses.contains(it)) pair = it }
                    pos.south().also { if(posses.contains(it)) pair = it }
                    pos.west().also { if(posses.contains(it)) pair = it }
                    pos.east().also { if(posses.contains(it)) pair = it }

                    val diffX = (pos.x - playerPosition.x).coerceIn(-1..1)
                    val diffZ = (pos.z - playerPosition.z).coerceIn(-1..1)
                    val vec = Vec3i(diffX, 0, diffZ)
                    var facing : EnumFacing? = null

                    for(facing0 in EnumFacing.values()) {
                        if(facing0.directionVec == vec) {
                            facing = facing0

                            break
                        }
                    }

                    map[facing] = listOf(pos, pair)
                }
            }

            return map
        }

        val blocks2 = processDynamicBlocks(blocks)

        /*fun processBlock(
            pos : BlockPos,
            color : Colour
        ) {
            Rendering.RenderObject.BOX.draw(state(pos).getSelectedBoundingBox(mc.world, pos), color.color)
        }*/

        fun pattern(
            facing : EnumFacing
        ) : RenderingRewritePattern? = when(facing) {
            EnumFacing.NORTH -> renderer2
            EnumFacing.SOUTH -> renderer3
            EnumFacing.WEST -> renderer4
            EnumFacing.EAST -> renderer5
            else -> null
        }

        if(debug.valBoolean) {

            for(pos in blocks) {
                renderer1.draw(pos)
            }
        } else {
            for(entry in blocks2) {
                val facing = entry.key!!

                for(pos in entry.value) {
                    if(pos != null) {
                        pattern(facing)!!.draw(pos)
                    }
                }
            }
        }
    }
}