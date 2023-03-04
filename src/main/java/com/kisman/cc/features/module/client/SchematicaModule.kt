package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.client.schematica.SchematicaSetting
import com.kisman.cc.features.schematica.schematica.handler.ConfigurationHandler
import com.kisman.cc.settings.Setting

/**
 * @author _kisman_
 * @since 10:56 of 04.03.2023
 */
class SchematicaModule : Module(
    "Schematica",
    "Implementation of Schematica mod",
    Category.CLIENT
) {
    init {
        toggled = true
        toggleable = false

        SchematicaSetting<Boolean>(register(Setting("Dump Block List", this, false))) { ConfigurationHandler.dumpBlockList = it }
        SchematicaSetting<Boolean>(register(Setting("Show Debug Info", this, true))) { ConfigurationHandler.showDebugInfo = it }
        SchematicaSetting<Boolean>(register(Setting("Enable Alpha", this, false))) { ConfigurationHandler.enableAlpha = it }
        SchematicaSetting<Float>(register(Setting("Alpha", this, ConfigurationHandler.ALPHA_DEFAULT, 0.0, 10.0, false))) { ConfigurationHandler.alpha = it }
        SchematicaSetting<Boolean>(register(Setting("Highlight", this, true))) { ConfigurationHandler.highlight = it }
        SchematicaSetting<Boolean>(register(Setting("Highlight Air", this, true))) { ConfigurationHandler.highlightAir = it }
        SchematicaSetting<Double>(register(Setting("Block Delta", this, ConfigurationHandler.blockDelta, 0.0, 10.0, false))) { ConfigurationHandler.blockDelta = it }
        SchematicaSetting<Int>(register(Setting("Render Distance", this, ConfigurationHandler.RENDER_DISTANCE_DEFAULT.toDouble(), 0.0, 10.0, true))) { ConfigurationHandler.renderDistance = it }
        SchematicaSetting<Int>(register(Setting("Place Delay", this, ConfigurationHandler.PLACE_DELAY_DEFAULT.toDouble(), 0.0, 10.0, true))) { ConfigurationHandler.placeDelay = it }
        SchematicaSetting<Int>(register(Setting("Place Time Out", this, ConfigurationHandler.TIMEOUT_DEFAULT.toDouble(), 0.0, 10.0, true))) { ConfigurationHandler.timeout = it }
        SchematicaSetting<Boolean>(register(Setting("Destroy Blocks", this, false))) { ConfigurationHandler.destroyBlocks = it }
        SchematicaSetting<Boolean>(register(Setting("Destroy Instantly", this, false))) { ConfigurationHandler.destroyInstantly = it }
        SchematicaSetting<Boolean>(register(Setting("Place Adjacent", this, true))) { ConfigurationHandler.placeAdjacent = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 1", this, false))) { ConfigurationHandler.swapSlots[0] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 2", this, false))) { ConfigurationHandler.swapSlots[1] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 3", this, false))) { ConfigurationHandler.swapSlots[2] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 4", this, false))) { ConfigurationHandler.swapSlots[3] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 5", this, false))) { ConfigurationHandler.swapSlots[4] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 6", this, false))) { ConfigurationHandler.swapSlots[5] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 7", this, true))) { ConfigurationHandler.swapSlots[6] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 8", this, true))) { ConfigurationHandler.swapSlots[7] = it }
        SchematicaSetting<Boolean>(register(Setting("Swap Slot 9", this, true))) { ConfigurationHandler.swapSlots[8] = it }
    }
}