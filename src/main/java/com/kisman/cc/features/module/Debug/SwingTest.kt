package com.kisman.cc.features.module.Debug

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventItemRenderer
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumHandSide

/**
 * @author _kisman_
 * @since 10:48 of 19.08.2022
 */
object SwingTest : Module(
    "SwingTest",
    "I need this test because i want to make custom swing.",
    Category.DEBUG
) {
    private val translateX = register(Setting("Translate X", this, 0.0, -3.0, 3.0, false))
    private val translateY = register(Setting("Translate Y", this, 0.0, -3.0, 3.0, false))
    private val translateZ = register(Setting("Translate Z", this, 0.0, -3.0, 3.0, false))

    private val itemRotateX = register(Setting("Item Rotate X", this, 0.0, -360.0, 360.0, true))
    private val itemRotateY = register(Setting("Item Rotate Y", this, 0.0, -360.0, 360.0, true))
    private val itemRotateZ = register(Setting("Item Rotate Z", this, 0.0, -360.0, 360.0, true))

    private val handRotateX = register(Setting("Hand Rotate X", this, 0.0, -360.0, 360.0, true))
    private val handRotateY = register(Setting("Hand Rotate Y", this, 0.0, -360.0, 360.0, true))
    private val handRotateZ = register(Setting("Hand Rotate Z", this, 0.0, -360.0, 360.0, true))
    
    private val offhandFix = register(Setting("OffHand Fix", this, false))

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(swing)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(swing)
    }
    
    fun renderItems(
        side : EnumHandSide,
        progress : Float
    ) {
        GlStateManager.translate(
            translateX.valDouble * progress * (if(side == EnumHandSide.LEFT && offhandFix.valBoolean) -1 else 1),
            translateY.valDouble * progress,
            translateZ.valDouble * progress
        )

        GlStateManager.rotate(
            itemRotateX.valFloat * progress,
            1f, 0f, 0F
        )

        GlStateManager.rotate(
            itemRotateY.valFloat * progress,
            0f, 1f, 0F
        )

        GlStateManager.rotate(
            itemRotateZ.valFloat * progress,
            0f, 0f, 1F
        )
    }

    private val swing = Listener<EventItemRenderer>(EventHook {
        /*GlStateManager.translate(
            translateX.valDouble * it.progress * (if(it.side == EnumHandSide.LEFT && offhandFix.valBoolean) -1 else 1),
            translateY.valDouble * it.progress,
            translateZ.valDouble * it.progress
        )*/

        GlStateManager.rotate(
            handRotateX.valFloat * it.progress,
            1f, 0f, 0F
        )

        GlStateManager.rotate(
            handRotateY.valFloat * it.progress,
            0f, 1f, 0F
        )

        GlStateManager.rotate(
            handRotateZ.valFloat * it.progress,
            0f, 0f, 1F
        )
    })
}