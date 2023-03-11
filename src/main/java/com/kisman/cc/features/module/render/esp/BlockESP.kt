package com.kisman.cc.features.module.render.esp

import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.ShaderableModule
import com.kisman.cc.features.module.render.blockesp.BlockImplementation
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.enums.BlockESPBlocks
import com.kisman.cc.util.thread.kisman.defaultScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

//TODO: range check
@ModuleInfo(
    name = "BlockESP",
    display = "Blocks",
    desc = "Highlights some blocks",
    submodule = true
)
class BlockESP : ShaderableModule(
    true
) {
    private val rangeXZ = /*register*/(Setting("Range XZ", this, 50.0, 0.0, 200.0, true))
    private val topLimitY = register(Setting("Top Limit Y", this, 0.0, 0.0, 255.0, true))
    private val bottomLimitY = register(Setting("Bottom Limit Y", this, 256.0, 0.0, 255.0, true))

    private val implementations = mutableListOf<BlockImplementation>()

    private var map = mutableMapOf<BlockPos, BlockImplementation>()

    init {
        for((index, block) in BlockESPBlocks.values().withIndex()) {
            implementations.add(BlockImplementation(block, this, index))
        }
    }

    override fun onEnable() {
        super.onEnable()
        map.clear()
    }

    @SubscribeEvent
    fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        defaultScope.launch {
            val map0 = mutableMapOf<BlockPos, BlockImplementation>()

            coroutineScope {
                launch {
                    val distance = mc.gameSettings.renderDistanceChunks

                    val chunk0 = ChunkPos(mc.player.position)
                    val chunk1 = ChunkPos(chunk0.x - distance, chunk0.z - distance)
                    val chunk2 = ChunkPos(chunk0.x + distance, chunk0.z + distance)

                    coroutineScope {
                        for(chunkX in chunk1.x..chunk2.x) {
                            for(chunkZ in chunk1.z..chunk2.z) {
                                val chunk = mc.world.getChunkFromChunkCoords(chunkX, chunkZ)

                                //TODO: range check should be here
                                if(chunk.isLoaded) {
                                    launch {
                                        for(x in (chunk.x shl 4)..(chunk.x shl 4) + 16) {
                                            for(y in bottomLimitY.valInt..topLimitY.valInt) {
                                                for(z in (chunk.z shl 4)..(chunk.z shl 4) + 16) {
                                                    val pos = BlockPos(x, y, z)

                                                    for(implementation in implementations) {
                                                        if(implementation.valid(pos)) {
                                                            synchronized(map0) {
                                                                map0[pos] = implementation
                                                            }

                                                            continue
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    delay(1L)
                                }
                            }
                        }
                    }
                }
            }

            synchronized(map) {
                map = map0
            }
        }

        handleDraw()
    }

    override fun draw0(
        flags : Array<Boolean>
    ) {
        for(pos in map.entries) {
            if(flags[pos.value.flag]) {
                pos.value.process(pos.key)
            }
        }
    }
}