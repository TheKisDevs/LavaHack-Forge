package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.render.blockesp.BlockImplementation
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.enums.BlockESPBlocks
import com.kisman.cc.util.world.CrystalUtils
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class BlockESP : Module(
        "BlockESP",
        Category.RENDER
) {
    private val range : Setting = register(Setting("Range", this, 30.0, 0.0, 50.0, true))

    private val implementations = listOf(
        BlockImplementation(BlockESPBlocks.Web, this),
        BlockImplementation(BlockESPBlocks.NetherPortal, this),
        BlockImplementation(BlockESPBlocks.EndPortal, this),
        BlockImplementation(BlockESPBlocks.Burrow, this)
    )

    private val threads = threads()

    private var list = ArrayList<BlockPos>()

    override fun onEnable() {
        super.onEnable()
        list.clear()
        threads.reset()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        threads.update(Runnable {
            doBlockESPLogic()
        })
        doBlockESP(event.partialTicks)
    }

    private fun doBlockESP(ticks : Float) {
        for(pos in list) {
            for(implementation in implementations) {
                implementation.process(pos)
            }
        }
    }

    private fun doBlockESPLogic() {
        val list = ArrayList<BlockPos>(list.size)
        for(pos in CrystalUtils.getSphere(range.valFloat, true, false)) {
            for(implementation in implementations) {
                if(implementation.valid(pos)) {
                    list.add(pos)
                }
            }
        }
        mc.addScheduledTask {
            this.list = list
        }
    }
}