package com.kisman.cc.event.events.schematica;

import com.kisman.cc.event.Event;
import net.minecraft.util.math.BlockPos;

/**
 * @author _kisman_
 * @since 23:16 of 08.07.2022
 */
public class EventSchematicaPlaceBlock extends Event {
    public BlockPos pos;

    public EventSchematicaPlaceBlock(BlockPos pos) {
        this.pos = pos;
    }
}
