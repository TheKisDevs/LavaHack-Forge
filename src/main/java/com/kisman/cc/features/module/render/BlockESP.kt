package com.kisman.cc.features.module.render

//import com.kisman.cc.event.events.EventRenderBlock
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.ShaderableModule
import com.kisman.cc.features.module.render.blockesp.BlockImplementation
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.enums.BlockESPBlocks
import com.kisman.cc.util.world.CrystalUtils
//import me.zero.alpine.listener.EventHook
//import me.zero.alpine.listener.Listener
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class BlockESP : ShaderableModule(
    "BlockESP",
    "",
    Category.RENDER,
    true
) {
    private val range : Setting = register(Setting("Range", this, 30.0, 0.0, 50.0, true))

    private val implementations = listOf(
        BlockImplementation(BlockESPBlocks.Web, this, 0),
        BlockImplementation(BlockESPBlocks.NetherPortal, this, 1),
        BlockImplementation(BlockESPBlocks.EndPortal, this, 2),
        BlockImplementation(BlockESPBlocks.Burrow, this, 3)
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
            val list = ArrayList<BlockPos>(list.size)

            for(pos in CrystalUtils.getSphere(range.valFloat, true, false)) {
                for(implementation in implementations) {
                    if(implementation.valid(pos)) {
                        list.add(pos)
                    }
                }
            }

            mc.addScheduledTask { this.list = list }
        })

        handleDraw()
    }

    /*private val renderBlock = Listener<EventRenderBlock>(EventHook {
        val block = mc.world.getBlockState(it.pos)


    })*/

    override fun draw0(
        flags : Array<Boolean>
    ) {
        for(pos in list) {
            for(implementation in implementations) {
                if(flags[implementation.flag]) {
                    implementation.process(pos)
                }
            }
        }
    }
}