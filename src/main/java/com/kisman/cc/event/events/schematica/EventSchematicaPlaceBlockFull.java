package com.kisman.cc.event.events.schematica;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

/**
 * @author _kisman_
 * @since 23:18 of 08.07.2022
 */
public class EventSchematicaPlaceBlockFull extends EventSchematicaPlaceBlock {
    public boolean result;
    public Item item;

    public EventSchematicaPlaceBlockFull(BlockPos pos, Item item) {
        super(pos);
        this.item = item;
    }
}
