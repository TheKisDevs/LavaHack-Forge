package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.client.entity.AbstractClientPlayer;

public class EventRenderEntityName extends Event {
    public AbstractClientPlayer entityIn;
    public double x, y, z, distanceSq;
    public String name;

    public EventRenderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq) {
        super();
        this.entityIn = entityIn;
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        this.distanceSq = distanceSq;
    }
}
