package com.kisman.cc.features.module.render

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderBlock
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.ShaderableModule
import com.kisman.cc.features.module.render.blockesp.BlockImplementation
//import com.kisman.cc.settings.Setting
//import com.kisman.cc.util.block
import com.kisman.cc.util.enums.BlockESPBlocks
//import com.kisman.cc.util.world.CrystalUtils
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class BlockESP : ShaderableModule(
    "BlockESP",
    "",
    Category.RENDER,
    true
) {
//    private val range : Setting = register(Setting("Range", this, 30.0, 0.0, 50.0, true))

    private val implementations = listOf(
        BlockImplementation(BlockESPBlocks.Web, this, 0),
        BlockImplementation(BlockESPBlocks.NetherPortal, this, 1),
        BlockImplementation(BlockESPBlocks.EndPortal, this, 2),
        BlockImplementation(BlockESPBlocks.Lever, this, 3),
        BlockImplementation(BlockESPBlocks.Burrow, this, 4),
        BlockImplementation(BlockESPBlocks.CrackedStoneBlocks, this, 5)
    )

    private val threads = threads()

    private var list = ArrayList<BlockPos>()
    private val map = mutableMapOf<BlockPos, BlockImplementation>()

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(renderBlock)
        list.clear()
        map.clear()
        threads.reset()
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(renderBlock)
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        /*threads.update(Runnable {
            val list = ArrayList<BlockPos>(list.size)

            for(pos in CrystalUtils.getSphere(range.valFloat, true, false)) {
                for(implementation in implementations) {
                    if(implementation.valid(pos)) {
                        list.add(pos)
                    }
                }
            }

            mc.addScheduledTask { this.list = list }
        })*/

        handleDraw()
    }

    @SubscribeEvent
    fun onRenderTick(
        event : TickEvent.RenderTickEvent
    ) {
        if(event.phase == TickEvent.Phase.START) {
            map.clear()
        }
    }

    private val renderBlock = Listener<EventRenderBlock>(EventHook {
        for(implementation in implementations) {
            if(implementation.valid(it.pos)) {
                map[it.pos] = implementation
            }
        }
    })

    override fun draw0(
        flags : Array<Boolean>
    ) {
        for(pos in map.entries) {
            if(flags[pos.value.flag]) {
                pos.value.process(pos.key)
            }
            /*for(implementation in implementations) {
                if(flags[implementation.flag]) {
                    implementation.process(pos)
                }
            }*/
        }
    }
}