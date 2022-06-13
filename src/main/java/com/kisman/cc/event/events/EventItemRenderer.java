package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.util.EnumHandSide;

/**
 * @author _kisman_
 * @since 19:14 of 09.06.2022
 */
public class EventItemRenderer extends Event {
    public final EnumHandSide side;

    public EventItemRenderer(EnumHandSide side) {
        this.side = side;
    }
}
